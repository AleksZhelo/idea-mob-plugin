package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.intentions;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptElementFactory;
import com.intellij.codeInsight.FileModificationService;
import com.intellij.codeInsight.editorActions.smartEnter.PlainEnterProcessor;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;


public class ImplementScriptQuickFix implements LocalQuickFix {

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getName() {
        return "Implement script";
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return getName();
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        PsiElement element = descriptor.getEndElement();
        ScriptPsiFile file = (ScriptPsiFile) element.getContainingFile();

        // TODO: needed?
        if (!FileModificationService.getInstance().preparePsiElementForWrite(file)) return;

        EIScripts implementations = file.findChildByClass(EIScripts.class);
        if (implementations != null) {
            implementations.add(EIScriptElementFactory.createScriptImplementation(project, element.getText()));

            FileEditor selectedEditor = FileEditorManager.getInstance(project).getSelectedEditor(file.getVirtualFile());
            if (selectedEditor instanceof TextEditor) {
                placeCaretInsideIfBlock(project, implementations, ((TextEditor) selectedEditor).getEditor());
            }
        }
    }

    private void placeCaretInsideIfBlock(@NotNull Project project, EIScripts implementations, Editor editor) {
        EIScriptImplementation newImpl = (EIScriptImplementation) implementations.getLastChild();
        EIScriptIfBlock ifBlock = newImpl.getScriptBlockList().get(0).getScriptIfBlock();
        PsiElement target = ifBlock.getNode().findChildByType(ScriptTypes.LPAREN).getPsi();

        final int offset = target.getTextRange().getEndOffset();
        editor.getCaretModel().moveToOffset(offset);
        editor.getScrollingModel().scrollToCaret(ScrollType.CENTER);
        editor.getSelectionModel().removeSelection();

        PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(editor.getDocument());
        new PlainEnterProcessor().doEnter(editor, target, false);
    }
}
