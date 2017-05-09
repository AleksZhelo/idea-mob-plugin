package com.alekseyzhelo.evilislands.mobplugin.script.psi.references;

import com.alekseyzhelo.evilislands.mobplugin.icon.Icons;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFormalParameter;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIGlobalVar;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptFile;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptNamingUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptRenameUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptResolveUtil;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class VariableReference extends PsiReferenceBase<PsiElement> {
    private String name;

    public VariableReference(@NotNull PsiElement element, TextRange textRange) {
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
        PsiElement param = EIScriptResolveUtil.matchByName(name, EIScriptResolveUtil.findEnclosingScriptParams(myElement));
        if (param != null) {
            return param;
        } else {
            PsiFile file = myElement.getContainingFile();
            return EIScriptResolveUtil.findGlobalVar((ScriptFile) file, name);
        }
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        List<LookupElement> variants = getGlobalVarVariants(myElement);
        EIFormalParameter[] params = EIScriptResolveUtil.findEnclosingScriptParams(myElement);
        if (params != null) {
            for (final EIFormalParameter param : params) {
                if (param.getName() != null && param.getName().length() > 0) {
                    variants.add(LookupElementBuilder.create(param).
                            withIcon(Icons.FILE).
                            withTypeText(param.getType().getText())
                    );
                }
            }
        }

        return variants.toArray();
    }

    @NotNull
    // TODO: string localization, or simply a better string for "unknown"?
    private List<LookupElement> getGlobalVarVariants(PsiElement myElement) {
        PsiFile file = myElement.getContainingFile();
        List<EIGlobalVar> globalVars = EIScriptResolveUtil.findGlobalVars((ScriptFile) file);
        List<LookupElement> variants = new ArrayList<>();
        for (final EIGlobalVar global : globalVars) {
            if (global.getName() != null && global.getName().length() > 0) {
                variants.add(LookupElementBuilder.create(global).
                        withIcon(Icons.FILE).
                        withTypeText(
                                global.getType() != null
                                        ? global.getType().getText()
                                        : EIScriptNamingUtil.UNKNOWN
                        )
                );
            }
        }
        return variants;
    }

}
