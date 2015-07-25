package com.alekseyzhelo.evilislands.mobplugin.script.psi.impl;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIScriptIdentifier;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptNamedElementMixin;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptTypes;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptElementFactory;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public abstract class ScriptNamedElementMixinImpl extends ScriptPsiElementImpl implements ScriptNamedElementMixin {
    public ScriptNamedElementMixinImpl(@NotNull ASTNode node) {
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
            EIScriptIdentifier identifier = EIScriptElementFactory.createIdentifierFromText(getProject(), newName);
            ASTNode newKeyNode = identifier.getNode();
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