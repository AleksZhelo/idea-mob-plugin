package com.alekseyzhelo.evilislands.mobplugin.script.psi.impl;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIGlobalVar;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptNamedElement;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptTypes;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptElementFactory;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public abstract class ScriptNamedElementImpl extends ScriptPsiElementImpl implements ScriptNamedElement {
    public ScriptNamedElementImpl(@NotNull ASTNode node) {
        super(node);
    }

    public String getName() {
        ASTNode keyNode = getNode().findChildByType(ScriptTypes.SCRIPT_IDENTIFIER);
        if (keyNode != null) {
            return keyNode.getText();
        } else {
            return null;
        }
    }

    public PsiElement setName(String newName) {
        ASTNode keyNode = getNode().findChildByType(ScriptTypes.SCRIPT_IDENTIFIER);
        if (keyNode != null) {
            EIGlobalVar globalVar = EIScriptElementFactory.createGlobalVar(getProject(), newName);
            ASTNode newKeyNode = globalVar.getFirstChild().getNode();
            getNode().replaceChild(keyNode, newKeyNode);
        }
        return this;
    }

    public PsiElement getNameIdentifier() {
        ASTNode keyNode = getNode().findChildByType(ScriptTypes.SCRIPT_IDENTIFIER);
        if (keyNode != null) {
            return keyNode.getPsi();
        } else {
            return null;
        }
    }

}