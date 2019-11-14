package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.fixes;

import com.alekseyzhelo.evilislands.mobplugin.EIMessages;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.util.EICaretPlacementUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptElementFactory;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptTypingUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EITypeToken;
import com.intellij.codeInsight.FileModificationService;
import com.intellij.codeInspection.LocalQuickFixOnPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AddScriptParamFix extends LocalQuickFixOnPsiElement {

    public AddScriptParamFix(@NotNull EIVariableAccess variableAccess) {
        super(variableAccess);
    }

    @NotNull
    @Override
    public String getText() {
        return EIMessages.message("fix.add.script.param");
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
        EITypeToken expectedType = EIScriptTypingUtil.getCreatableVariableExpectedType(myElement);

        if (!FileModificationService.getInstance().preparePsiElementForWrite(psiFile)) return;

        EIScriptImplementation script = PsiTreeUtil.getParentOfType(myElement, EIScriptImplementation.class);
        if (script != null) {
            EIFormalParameter newParam = null;
            EIScriptDeclaration declaration = (EIScriptDeclaration) script.getReference().resolve();
            if (declaration != null) {
                newParam = addParam(
                        declaration,
                        EIScriptElementFactory.createFormalParameter(project, myElement.getName(), expectedType)
                );

                if (newParam != null) {
                    CodeStyleManager.getInstance(project).reformat(declaration);
                    if (newParam.getType() == null) {
                        FileEditor selectedEditor = FileEditorManager.getInstance(project).getSelectedEditor(file.getVirtualFile());
                        if (selectedEditor instanceof TextEditor) {
                            EICaretPlacementUtil.placeCaretAtElementEnd(newParam, ((TextEditor) selectedEditor).getEditor());
                        }
                    }
                }
            }

            if (newParam == null) {
                Notifications.Bus.notify(new Notification(
                        EIMessages.message("notification.general.group.id"),
                        EIMessages.message("notification.error.cannot.add.script.param.title"),
                        EIMessages.message("notification.error.cannot.add.script.param.text"),
                        NotificationType.ERROR
                ));
            }
        }
    }

    private EIFormalParameter addParam(EIScriptDeclaration declaration, EIFormalParameter parameter) {
        EIFormalParameter result = null;
        List<EIFormalParameter> params = declaration.getFormalParameterList();
        if (params.size() > 0) {
            result = (EIFormalParameter) declaration.addAfter(parameter, params.get(params.size() - 1));
            declaration.getNode().addLeaf(ScriptTypes.COMMA, ",", result.getNode());
        } else {
            ASTNode lParen = declaration.getNode().findChildByType(ScriptTypes.LPAREN);
            if (lParen != null) {
                result = (EIFormalParameter) declaration.addAfter(parameter, lParen.getPsi());
            }
        }
        return result;
    }
}
