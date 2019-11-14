package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.fixes;

import com.alekseyzhelo.evilislands.mobplugin.EIMessages;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.util.EICaretPlacementUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptElementFactory;
import com.intellij.codeInsight.FileModificationService;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;


public class ImplementScriptFix implements LocalQuickFix {

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getName() {
        return EIMessages.message("fix.implement.script");
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

        if (!FileModificationService.getInstance().preparePsiElementForWrite(file)) return;

        EIScripts implementations = file.findChildByClass(EIScripts.class);
        if (implementations != null) {
            implementations.add(EIScriptElementFactory.createScriptImplementation(project, element.getText()));
        } else {
            PsiElement insertAfter = file.findChildByClass(EIDeclarations.class);
            implementations = (EIScripts) file.addAfter(
                    EIScriptElementFactory.createScripts(project, element.getText()),
                    insertAfter
            );
        }
        EIScriptImplementation newImpl = (EIScriptImplementation) implementations.getLastChild();

        FileEditor selectedEditor = FileEditorManager.getInstance(project).getSelectedEditor(file.getVirtualFile());
        if (selectedEditor instanceof TextEditor) {
            EIScriptIfBlock ifBlock = newImpl.getScriptBlockList().get(0).getScriptIfBlock();
            EICaretPlacementUtil.placeCaretInsideIfBlock(project, ifBlock, ((TextEditor) selectedEditor).getEditor());
        }
    }
}
