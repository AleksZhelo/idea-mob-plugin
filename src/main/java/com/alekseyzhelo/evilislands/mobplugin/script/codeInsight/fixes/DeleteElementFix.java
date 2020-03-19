package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.fixes;

import com.alekseyzhelo.evilislands.mobplugin.EIMessages;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.ObjectUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DeleteElementFix extends LocalQuickFixAndIntentionActionOnPsiElement {
    private final String myText;

    public DeleteElementFix(@NotNull PsiElement element, @NotNull @Nls String text) {
        super(element);
        myText = text;
    }

    @Nls
    @NotNull
    @Override
    public String getText() {
        return ObjectUtils.notNull(myText, getFamilyName());
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return EIMessages.message("fix.remove.element");
    }

    @Override
    public void invoke(@NotNull Project project,
                       @NotNull PsiFile file,
                       @Nullable Editor editor,
                       @NotNull PsiElement startElement,
                       @NotNull PsiElement endElement) {
        startElement.delete();
    }
}