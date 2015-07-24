package com.alekseyzhelo.evilislands.mobplugin.script.psi.impl;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptNamedElement;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
 
public abstract class ScriptNamedElementImpl extends ASTWrapperPsiElement implements ScriptNamedElement {
    public ScriptNamedElementImpl(@NotNull ASTNode node) {
        super(node);
    }
}