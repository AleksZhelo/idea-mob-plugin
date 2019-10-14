package com.alekseyzhelo.evilislands.mobplugin.script.psi.base;

import com.intellij.psi.PsiNameIdentifierOwner;
import org.jetbrains.annotations.NotNull;

public interface EIScriptNamedElementMixin extends PsiNameIdentifierOwner, EIScriptPsiElement {
    @NotNull
    String getName();
}