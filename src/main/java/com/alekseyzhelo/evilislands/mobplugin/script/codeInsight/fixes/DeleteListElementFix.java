package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.fixes;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptTypes;
import com.intellij.codeInsight.daemon.impl.quickfix.DeleteElementFix;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DeleteListElementFix extends DeleteElementFix {

    public DeleteListElementFix(@NotNull PsiElement element, @Nls @NotNull String text) {
        super(element, text);
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiFile file, @Nullable Editor editor, @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
        PsiElement previous = PsiTreeUtil.skipWhitespacesAndCommentsBackward(startElement);
        if (previous != null && ScriptTypes.COMMA.equals(previous.getNode().getElementType())) {
            if (previous.getNextSibling() instanceof PsiWhiteSpace){
                previous.getNextSibling().delete();
            }
            previous.delete();
        } else {
            PsiElement next = PsiTreeUtil.skipWhitespacesAndCommentsForward(startElement);
            if (next != null && ScriptTypes.COMMA.equals(next.getNode().getElementType())) {
                next.delete();
            }
        }
        super.invoke(project, file, editor, startElement, endElement);
    }
}