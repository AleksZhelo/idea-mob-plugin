package com.alekseyzhelo.evilislands.mobplugin.script.psi.references;

import com.alekseyzhelo.evilislands.mobplugin.icon.Icons;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFunctionDeclaration;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptNativeFunctionsUtil;
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

public class NativeFunctionReference extends PsiReferenceBase<PsiElement> {
    private String name;

    public NativeFunctionReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
        name = element.getText().substring(textRange.getStartOffset(), textRange.getEndOffset());
    }

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        throw new IncorrectOperationException("Native EI functions cannot be renamed.");
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        PsiFile file = myElement.getContainingFile();
        return EIScriptNativeFunctionsUtil.getFunctionDeclaration(file.getProject(), name);
    }

    // TODO: how do?
    // TODO: finish everywhere, use the stuff from the ScriptPsiReference file
    @NotNull
    @Override
    public Object[] getVariants() {
        List<EIFunctionDeclaration> functions =
                EIScriptNativeFunctionsUtil.getAllFunctions(myElement.getProject());
        List<LookupElement> variants = new ArrayList<>();
        for (final EIFunctionDeclaration global : functions) {
            if (global.getName() != null && global.getName().length() > 0) {
                variants.add(LookupElementBuilder.create(global).
                        withIcon(Icons.FILE).
                        withTypeText(global.getContainingFile().getName())
                );
            }
        }
        return variants.toArray();
    }

}
