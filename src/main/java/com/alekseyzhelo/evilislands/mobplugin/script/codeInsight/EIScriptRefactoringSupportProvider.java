package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFunctionCall;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFunctionDeclaration;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.base.EIScriptNamedElementMixin;
import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class EIScriptRefactoringSupportProvider extends RefactoringSupportProvider {
    @Override
    public boolean isInplaceRenameAvailable(@NotNull PsiElement element, PsiElement context) {
        return element instanceof EIScriptNamedElementMixin
                && !(element instanceof EIFunctionDeclaration);
    }

    @Override
    public boolean isMemberInplaceRenameAvailable(@NotNull PsiElement element, PsiElement context) {
        return element instanceof EIScriptNamedElementMixin
                && !(element instanceof EIFunctionDeclaration);
    }
}