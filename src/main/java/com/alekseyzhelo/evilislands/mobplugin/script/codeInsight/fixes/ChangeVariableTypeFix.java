package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.fixes;

import com.alekseyzhelo.evilislands.mobplugin.EIMessages;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptElementFactory;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EITypeToken;
import com.intellij.codeInsight.FileModificationService;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.codeInspection.LocalQuickFixOnPsiElement;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.command.undo.UndoUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class ChangeVariableTypeFix extends LocalQuickFixOnPsiElement {

    private final String varName;
    private final EITypeToken newType;

    public ChangeVariableTypeFix(@NotNull PsiNamedElement element, EITypeToken newType) {
        super(element);
        varName = element.getName();
        this.newType = newType;
    }

    @NotNull
    @Override
    public String getText() {
        return EIMessages.message("fix.change.variable.type", varName, newType.getTypeString());
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return EIMessages.message("fix.change.variable.type.base");
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiFile file, @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
        ScriptPsiFile psiFile = (ScriptPsiFile) startElement.getContainingFile();

        if (!FileModificationService.getInstance().preparePsiElementForWrite(psiFile)) return;

        EIType type = null;
        if (startElement instanceof EIFormalParameter) {
            type = ((EIFormalParameter) startElement).getType();
        } else if (startElement instanceof EIGlobalVar) {
            type = ((EIGlobalVar) startElement).getType();
        }
        if (type != null) {
            EIType finalType = type;
            WriteCommandAction.writeCommandAction(project, file).withName(getText()).run(() -> {
                try {
                    EIType newEIType = EIScriptElementFactory.createType(psiFile.getProject(), newType);
                    finalType.replace(newEIType);
                    UndoUtil.markPsiFileForUndo(psiFile);
                }
                catch (IncorrectOperationException e) {
                    LOG.error(e);
                }
            });
        }
    }
}
