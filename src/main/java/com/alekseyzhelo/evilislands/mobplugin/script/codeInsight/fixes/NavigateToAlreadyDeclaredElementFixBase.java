package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.fixes;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.pom.Navigatable;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

abstract class NavigateToAlreadyDeclaredElementFixBase<Element extends PsiElement & Navigatable> implements IntentionAction {
    protected final Element myElement;

    NavigateToAlreadyDeclaredElementFixBase(@NotNull Element element) {
        this.myElement = element;
    }

    @Override
    public boolean isAvailable(@NotNull final Project project, final Editor editor, final PsiFile file) {
        if (!myElement.isValid()) {
            return false;
        }
        return BaseIntentionAction.canModify(myElement);
    }

    @Override
    public void invoke(@NotNull final Project project, final Editor editor, final PsiFile file) throws IncorrectOperationException {
        myElement.navigate(true);
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }
}
