package com.alekseyzhelo.evilislands.mobplugin.script.psi.references;

import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.lookup.EILookupElementFactory;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFormalParameter;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIType;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptPsiFile;
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
public class VariableReference extends PsiReferenceBase<PsiElement> {
    private final String name;

    public VariableReference(@NotNull PsiElement element, TextRange textRange) {
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
        EITypeToken expectedType = EIScriptTypingUtil.getExpectedType(this);
        List<LookupElement> variants = getGlobalVarVariants(myElement, expectedType);
        List<EIFormalParameter> params = EIScriptResolveUtil.findEnclosingScriptParams(myElement);
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

        return variants.toArray();
    }

    @NotNull
    private List<LookupElement> getGlobalVarVariants(PsiElement myElement, EITypeToken expectedType) {
        ScriptPsiFile file = (ScriptPsiFile) myElement.getContainingFile();
        List<LookupElement> variants = new ArrayList<>();
        if (expectedType != null) {
            variants.addAll(file.getGlobalVarLookupElements().get(expectedType));
        }
        return variants;
    }

    private static class MyResolver implements ResolveCache.AbstractResolver<VariableReference, PsiElement> {
        static final MyResolver INSTANCE = new MyResolver();

        @Override
        public PsiElement resolve(@NotNull VariableReference variableReference, boolean incompleteCode) {
            String name = variableReference.name;
            PsiElement myElement = variableReference.myElement;

            EIFormalParameter param = EIScriptResolveUtil.matchByName(name, EIScriptResolveUtil.findEnclosingScriptParams(myElement));
            if (param != null) {
                return param;
            } else {
                ScriptPsiFile file = (ScriptPsiFile) myElement.getContainingFile();
                return file.findGlobalVar(name);
            }
        }
    }
}
