package com.alekseyzhelo.evilislands.mobplugin.script.psi.references;

import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.intellij.EIFunctionsService;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.lookup.EILookupElementFactory;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptRenameUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptResolveUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptTypingUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EITypeToken;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.alekseyzhelo.evilislands.mobplugin.script.util.UsefulPsiTreeUtil.HAS_ERROR_CHILD;

// TODO: ResolveCache.getInstance(getProject()).resolveWithCaching()?
public class VariableReference extends PsiReferenceBase<EIVariableAccess> {
    private final String name;

    public VariableReference(@NotNull EIVariableAccess element, TextRange textRange) {
        super(element, textRange);
        name = element.getText().substring(textRange.getStartOffset(), textRange.getEndOffset());
    }

    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        return EIScriptRenameUtil.renameElement(myElement, newElementName);
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
        EITypeToken expectedType = EIScriptTypingUtil.getVariableExpectedType(myElement);
        List<LookupElement> variants = new ArrayList<>();

        List<LookupElement> globalVars = scriptFile.getGlobalVarLookupElements().get(expectedType);
        if (globalVars != null) {
            variants.addAll(globalVars);
        }

        List<EIFormalParameter> params = EIScriptResolveUtil.findEnclosingScriptParams(scriptFile, myElement);
        // TODO: extract?
        if (params != null && expectedType != null) {
            for (final EIFormalParameter param : params) {
                EIType paramType = param.getType();
                if (param.getName().length() > 0 && paramType != null) {
                    if (expectedType != EITypeToken.ANY && paramType.getTypeToken() != expectedType) {
                        continue;
                    }
                    // TODO: icon!
                    variants.add(EILookupElementFactory.create(param));
                }
            }
        }

        PsiElement parent = myElement.getParent();
        // incomplete assignment -> may be a void function or a script
        if (parent instanceof EIAssignment) {
            if (!((EIAssignment) parent).isComplete() || HAS_ERROR_CHILD.accepts(parent)) {
                suggestForIncompleteAssignmentLeftSide(scriptFile, variants);
            }
        }
        // not on the left side of assignment, and not the first var in a For -> may be function of the expected type
        if (!(parent instanceof EIAssignment && ((EIAssignment) parent).indexOf(myElement) == 0) &&
                !(parent instanceof EIForBlock && (((EIForBlock) parent).getExpressionList().indexOf(myElement) == 0))) {
            suggestFunctionsOfType(scriptFile.getProject(), variants, expectedType);
        }

        return variants.toArray();
    }

    private void suggestForIncompleteAssignmentLeftSide(ScriptPsiFile scriptFile, List<LookupElement> variants) {
        suggestFunctionsOfType(scriptFile.getProject(), variants, EITypeToken.VOID);
        List<LookupElement> scriptsLookup = scriptFile.getScriptLookupElements();
        variants.addAll(scriptsLookup);
    }

    private void suggestFunctionsOfType(Project project, List<LookupElement> variants, EITypeToken expectedType) {
        EIFunctionsService service = EIFunctionsService.getInstance(project);
        List<LookupElement> acceptableFunctions = service.getFunctionLookupElements(expectedType);
        if (acceptableFunctions != null) {
            variants.addAll(acceptableFunctions);
        }
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
