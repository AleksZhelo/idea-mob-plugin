package com.alekseyzhelo.evilislands.mobplugin.script.psi.references;

import com.alekseyzhelo.evilislands.mobplugin.mob.psi.PsiMobObjectsBlock;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.completion.TokenCompletionHelper;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.fixes.AddScriptParamFix;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.fixes.DeclareGlobalVarFix;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.intellij.EIFunctionsService;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.base.EIForBlockBase;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptResolveUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptTypingUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EITypeToken;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.LocalQuickFixProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.alekseyzhelo.evilislands.mobplugin.script.util.UsefulPsiTreeUtil.HAS_ERROR_CHILD;

public class VariableReference extends PsiReferenceBase<EIVariableAccess> implements LocalQuickFixProvider {

    private final String name;

    public VariableReference(@NotNull EIVariableAccess element, TextRange textRange) {
        super(element, textRange);
        name = element.getText().substring(textRange.getStartOffset(), textRange.getEndOffset());
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        return ResolveCache.getInstance(myElement.getProject()).resolveWithCaching(
                this,
                MyResolver.INSTANCE,
                true,
                false
        );
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        ScriptPsiFile scriptFile = (ScriptPsiFile) myElement.getContainingFile();
        List<LookupElement> variants = new ArrayList<>();
        EITypeToken expectedType = EIScriptTypingUtil.getVariableExpectedType(myElement);

        List<LookupElement> globalVars = scriptFile.getGlobalVarLookupElements().get(expectedType);
        if (globalVars != null) {
            variants.addAll(globalVars);
        }

        List<EIFormalParameter> params = EIScriptResolveUtil.findEnclosingScriptParams(scriptFile, myElement);
        if (params != null) {
            EIScriptResolveUtil.fillParamVariantsOfType(variants, params, expectedType);
        }

        PsiElement parent = myElement.getParent();
        // incomplete assignment -> may be a void function or a script, or a For
        // (global var and param are accounted for above)
        if (parent instanceof EIAssignment) {
            if (!((EIAssignment) parent).isComplete() || HAS_ERROR_CHILD.accepts(parent)) {
                suggestForIncompleteAssignmentLeftSide(scriptFile, variants);
            }
        }
        // not on the left side of assignment, and not the first var in a For -> may be function of the expected type
        if (!(parent instanceof EIAssignment && ((EIAssignment) parent).indexOf(myElement) == 0) &&
                !(parent instanceof EIForBlockBase && (((EIForBlockBase) parent).indexOfArgument(myElement) == 0))) {
            // TODO v2: remove hack
            if (parent instanceof EIParams
                    && ((EIFunctionCall) parent.getParent()).getName().equalsIgnoreCase("getobject") ) {
                PsiMobObjectsBlock block = scriptFile.getCompanionMobObjectsBlock();
                if (block != null) {
                    Collections.addAll(variants, block.getObjectByIdLookupElements());
                }
            } else {
                suggestFunctionsOfType(scriptFile.getProject(), variants, expectedType);
            }
        }

        return variants.toArray();
    }

    private void suggestForIncompleteAssignmentLeftSide(ScriptPsiFile scriptFile, List<LookupElement> variants) {
        suggestFunctionsOfType(scriptFile.getProject(), variants, EITypeToken.VOID);
        variants.addAll(scriptFile.getScriptLookupElements());
        variants.add(TokenCompletionHelper.FOR.getLookupElement(""));
        variants.add(TokenCompletionHelper.FOR_IF.getLookupElement(""));
    }

    private void suggestFunctionsOfType(Project project, List<LookupElement> variants, EITypeToken expectedType) {
        EIFunctionsService service = EIFunctionsService.getInstance(project);
        List<LookupElement> acceptableFunctions = service.getFunctionLookupElements(expectedType);
        if (acceptableFunctions != null) {
            variants.addAll(acceptableFunctions);
        }
    }

    @Nullable
    @Override
    public LocalQuickFix[] getQuickFixes() {
        List<LocalQuickFix> fixes = new ArrayList<>();
        if (PsiTreeUtil.getParentOfType(myElement, EIScriptImplementation.class, true, EIWorldScript.class) != null) {
            // inside a script impl
            fixes.add(new AddScriptParamFix(myElement));
        }
        fixes.add(new DeclareGlobalVarFix(myElement));
        return fixes.toArray(LocalQuickFix.EMPTY_ARRAY);
    }

    private static class MyResolver implements ResolveCache.AbstractResolver<VariableReference, PsiElement> {
        static final MyResolver INSTANCE = new MyResolver();

        @Override
        public PsiElement resolve(@NotNull VariableReference variableReference, boolean incompleteCode) {
            String name = variableReference.name;
            PsiElement myElement = variableReference.myElement;
            ScriptPsiFile file = (ScriptPsiFile) myElement.getContainingFile();

            EIFormalParameter param = EIScriptResolveUtil.matchByName(
                    name,
                    EIScriptResolveUtil.findEnclosingScriptParams(file, myElement)
            );
            if (param != null) {
                return param;
            } else {
                return file.findGlobalVar(name);
            }
        }
    }
}
