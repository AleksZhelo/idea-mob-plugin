package com.alekseyzhelo.evilislands.mobplugin.script.psi.references;

import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.intellij.EIFunctionsService;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.lookup.EILookupElementFactory;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptRenameUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptResolveUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptTypingUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EITypeToken;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

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
        if (params != null) {
            for (final EIFormalParameter param : params) {
                EIType paramType = param.getType();
                if (param.getName().length() > 0 && paramType != null) {
                    if (expectedType != null && paramType.getTypeToken() != expectedType) {
                        continue;
                    }
                    variants.add(EILookupElementFactory.create(param));
                }
            }
        }

        PsiElement parent = myElement.getParent();
        // not directly in assignment, i.e. not on the left side of assignment, and not the first var in a For
        if (!(parent instanceof EIAssignment) &&
                !(parent instanceof EIForBlock && ((((EIForBlock) parent).getVariableAccessList().indexOf(myElement) == 0)))) {
            EIFunctionsService service = EIFunctionsService.getInstance(scriptFile.getProject());
            List<LookupElement> acceptableFunctions = service.getFunctionLookupElements(expectedType);
            if (acceptableFunctions != null) {
                variants.addAll(acceptableFunctions);
            }
        }

        return variants.toArray();
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
