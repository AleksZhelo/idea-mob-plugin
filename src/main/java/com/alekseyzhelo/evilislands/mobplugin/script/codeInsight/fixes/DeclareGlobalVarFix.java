package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.fixes;

import com.alekseyzhelo.evilislands.mobplugin.EIMessages;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.util.EICaretPlacementUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.util.EIParenthesisedListInsertUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIGlobalVar;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIGlobalVars;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIVariableAccess;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptPsiFile;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptElementFactory;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptTypingUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EITypeToken;
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

public class DeclareGlobalVarFix extends LocalQuickFixOnPsiElement {

    public DeclareGlobalVarFix(@NotNull EIVariableAccess variableAccess) {
        super(variableAccess);
    }

    @NotNull
    @Override
    public String getText() {
        return EIMessages.message("fix.declare.global.var");
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
        EIVariableAccess myElement = (EIVariableAccess) startElement;
        EITypeToken expectedType = EIScriptTypingUtil.getVariableExpectedType(myElement);

        if (!FileModificationService.getInstance().preparePsiElementForWrite(psiFile)) return;

        EIGlobalVars globalVars = psiFile.findChildByClass(EIGlobalVars.class);
        EIGlobalVar newVar;
        if (globalVars != null) {
            newVar = EIParenthesisedListInsertUtil.insertElement(
                    globalVars,
                    globalVars.getGlobalVarList(),
                    EIScriptElementFactory.createGlobalVar(project, myElement.getName(), expectedType)
            );
        } else {
            globalVars = (EIGlobalVars) psiFile.addBefore(
                    EIScriptElementFactory.createGlobalVars(project, myElement.getName(), expectedType),
                    psiFile.getFirstChild()
            );
            newVar = globalVars.getGlobalVarList().get(0);
        }

        if (newVar != null && newVar.getType() == null) {
            FileEditor selectedEditor = FileEditorManager.getInstance(project).getSelectedEditor(file.getVirtualFile());
            if (selectedEditor instanceof TextEditor) {
                EICaretPlacementUtil.placeCaretAtElementEnd(newVar, ((TextEditor) selectedEditor).getEditor());
            }
        }
    }
}
