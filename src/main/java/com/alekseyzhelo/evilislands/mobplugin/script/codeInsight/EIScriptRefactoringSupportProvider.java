package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptNamedElementMixin;
import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class EIScriptRefactoringSupportProvider extends RefactoringSupportProvider {
    @Override
    public boolean isInplaceRenameAvailable(@NotNull PsiElement element, PsiElement context) {
        return element instanceof ScriptNamedElementMixin;
//        return element instanceof EIScriptIdentifier
//                || element instanceof EIScriptReference
//                || element instanceof ScriptNamedElementMixin;
    }

    @Override
    public boolean isMemberInplaceRenameAvailable(@NotNull PsiElement element, PsiElement context) {
        return false;
        //return element instanceof EIScriptIdentifier || element instanceof EIScriptReference;
        //return false;
    }
}