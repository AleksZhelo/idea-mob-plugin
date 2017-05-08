package com.alekseyzhelo.evilislands.mobplugin.script.psi.references;

import com.alekseyzhelo.evilislands.mobplugin.icon.Icons;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFormalParameter;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptRenameUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptResolveUtil;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FormalParameterReference extends PsiReferenceBase<PsiElement> {
    private String name;

    public FormalParameterReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
        name = element.getText().substring(textRange.getStartOffset(), textRange.getEndOffset());
    }

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        return EIScriptRenameUtil.renameElement(myElement, newElementName);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        return EIScriptResolveUtil.matchByName(name, EIScriptResolveUtil.findEnclosingScriptParams(myElement));
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        List<LookupElement> variants = VariableReference.getGlobalVarVariants(myElement);
        EIFormalParameter[] params = EIScriptResolveUtil.findEnclosingScriptParams(myElement);
        if (params != null) {
            for (final EIFormalParameter param : params) {
                if (param.getName() != null && param.getName().length() > 0) {
                    variants.add(LookupElementBuilder.create(param).
                            withIcon(Icons.FILE).
                            withTypeText(param.getType().toString())
                    );
                }
            }
        }

        return variants.toArray();
    }

}
