package com.alekseyzhelo.evilislands.mobplugin.script.psi.references;

import com.alekseyzhelo.evilislands.mobplugin.icon.Icons;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIGlobalVar;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptFile;
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
        PsiFile file = myElement.getContainingFile();
        return EIScriptResolveUtil.findGlobalVar((ScriptFile) file, name);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return getGlobalVarVariants(myElement).toArray();
    }

    @NotNull
    // TODO: string localization, or simply a better string for "unknown"?
    public static List<LookupElement> getGlobalVarVariants(PsiElement myElement) {
        PsiFile file = myElement.getContainingFile();
        List<EIGlobalVar> globalVars = EIScriptResolveUtil.findGlobalVars((ScriptFile) file);
        List<LookupElement> variants = new ArrayList<>();
        for (final EIGlobalVar global : globalVars) {
            if (global.getName() != null && global.getName().length() > 0) {
                variants.add(LookupElementBuilder.create(global).
                        withIcon(Icons.FILE).
                        withTypeText(global.getType() != null ? global.getType().toString() : "unknown")
                );
            }
        }
        return variants;
    }

}
