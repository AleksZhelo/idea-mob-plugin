package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.fixes;

import com.alekseyzhelo.evilislands.mobplugin.EIMessages;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.util.EICaretPlacementUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptElementFactory;
import com.intellij.codeInsight.FileModificationService;
import com.intellij.codeInspection.LocalQuickFixOnPsiElement;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class DeclareScriptFix extends LocalQuickFixOnPsiElement {

    public DeclareScriptFix(@NotNull EIScriptImplementation scriptImplementation) {
        super(scriptImplementation);
    }

    @NotNull
    @Override
    public String getText() {
        return EIMessages.message("fix.declare.script");
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return getText();
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiFile file, @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
        ScriptPsiFile psiFile = (ScriptPsiFile) startElement.getContainingFile();
        EIScriptImplementation myElement = (EIScriptImplementation) startElement;

        if (!FileModificationService.getInstance().preparePsiElementForWrite(psiFile)) return;

        EIDeclarations declarations = psiFile.findChildByClass(EIDeclarations.class);
        if (declarations != null) {
            declarations.add(EIScriptElementFactory.createScriptDeclaration(project, myElement.getName()));
        } else {
            declarations = EIScriptElementFactory.createDeclarations(project, myElement.getName());
            PsiElement insertAfter = psiFile.findChildByClass(EIGlobalVars.class);
            if (insertAfter != null) {
                declarations = (EIDeclarations) psiFile.addAfter(declarations, insertAfter);
            } else {
                declarations = (EIDeclarations) psiFile.addBefore(declarations, myElement.getParent());
            }
        }
        EIScriptDeclaration newDeclaration = (EIScriptDeclaration) declarations.getLastChild();

        FileEditor selectedEditor = FileEditorManager.getInstance(project).getSelectedEditor(file.getVirtualFile());
        if (selectedEditor instanceof TextEditor) {
            EICaretPlacementUtil.placeCaretInsideDeclaration(newDeclaration, ((TextEditor) selectedEditor).getEditor());
        }
    }
}
