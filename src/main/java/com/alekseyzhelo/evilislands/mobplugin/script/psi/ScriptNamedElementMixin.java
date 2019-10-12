package com.alekseyzhelo.evilislands.mobplugin.script.psi;

import com.intellij.psi.PsiNameIdentifierOwner;
import org.jetbrains.annotations.NotNull;

public interface ScriptNamedElementMixin extends PsiNameIdentifierOwner, ScriptPsiElement {

    @NotNull
    String getName();
}