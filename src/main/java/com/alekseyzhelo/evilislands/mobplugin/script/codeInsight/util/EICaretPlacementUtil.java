package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.util;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIScriptDeclaration;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIScriptIfBlock;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptTypes;
import com.intellij.codeInsight.editorActions.smartEnter.PlainEnterProcessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public final class EICaretPlacementUtil {

    private EICaretPlacementUtil() {

    }


    public static void placeCaretAtElementEnd(PsiElement element, Editor editor) {
        final int offset = element.getTextRange().getEndOffset();
        editor.getCaretModel().moveToOffset(offset);
        editor.getScrollingModel().scrollToCaret(ScrollType.CENTER);
        editor.getSelectionModel().removeSelection();
    }

    public static void placeCaretInsideDeclaration(EIScriptDeclaration newDeclaration, Editor editor) {
        PsiElement target = newDeclaration.getNode().findChildByType(ScriptTypes.RPAREN).getPsi();

        final int offset = target.getTextRange().getStartOffset();
        editor.getCaretModel().moveToOffset(offset);
        editor.getScrollingModel().scrollToCaret(ScrollType.CENTER);
        editor.getSelectionModel().removeSelection();
    }

    public static void placeCaretInsideIfBlock(@NotNull Project project, EIScriptIfBlock ifBlock, Editor editor) {
        PsiElement target = ifBlock.getNode().findChildByType(ScriptTypes.LPAREN).getPsi();

        final int offset = target.getTextRange().getEndOffset();
        editor.getCaretModel().moveToOffset(offset);
        editor.getScrollingModel().scrollToCaret(ScrollType.CENTER);
        editor.getSelectionModel().removeSelection();

        PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(editor.getDocument());
        new PlainEnterProcessor().doEnter(editor, target, false);
    }
}
