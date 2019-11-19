package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.fixes;

import com.alekseyzhelo.evilislands.mobplugin.EIMessages;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIScriptThenBlock;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptPsiFile;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptElementFactory;
import com.intellij.codeInsight.FileModificationService;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CallKillScriptFix extends LocalQuickFixAndIntentionActionOnPsiElement {

    public CallKillScriptFix(@Nullable EIScriptThenBlock element) {
        super(element);
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiFile file, @Nullable Editor editor, @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
        EIScriptThenBlock thenBlock = (EIScriptThenBlock) startElement;
        ScriptPsiFile psiFile = (ScriptPsiFile) startElement.getContainingFile();

        if (!FileModificationService.getInstance().preparePsiElementForWrite(psiFile)) return;

        PsiElement addBefore = thenBlock.getScriptStatementList().size() > 0
                ? thenBlock.getScriptStatementList().get(0)
                : thenBlock.getLastChild();
        thenBlock.addBefore(EIScriptElementFactory.createKillScriptCall(psiFile.getProject()), addBefore);
    }

    @NotNull
    @Override
    public String getText() {
        return EIMessages.message("fix.call.killScript.text");
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return getText();
    }
}
