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
        if(keyNode == null) {
            keyNode = getNode().findChildByType(ScriptTypes.SCRIPT_REFERENCE);
        }
        if (keyNode != null) {
            return keyNode.getText();
        } else {
            return null;
        }
    }

    public PsiElement setName(@NotNull String newName) {
        boolean nameIsReference = false;
        ASTNode keyNode = getNode().findChildByType(ScriptTypes.SCRIPT_IDENTIFIER);
        if(keyNode == null) {
            keyNode = getNode().findChildByType(ScriptTypes.SCRIPT_REFERENCE);
            nameIsReference = true;
        }
        if (keyNode != null) {
            EIScriptIdentifier identifier = EIScriptElementFactory.createIdentifierFromText(getProject(), newName);
            ASTNode newKeyNode = identifier.getNode();
            if(!nameIsReference) {
                getNode().replaceChild(keyNode, newKeyNode);
            } else {
                //noinspection ConstantConditions
                keyNode.replaceChild(
                        keyNode.findChildByType(ScriptTypes.SCRIPT_IDENTIFIER),
                        newKeyNode
                );
            }
        }
        return this;
    }

    public PsiElement getNameIdentifier() {
        ASTNode keyNode = getNode().findChildByType(ScriptTypes.SCRIPT_IDENTIFIER);
        if(keyNode == null) {
            keyNode = getNode().findChildByType(ScriptTypes.SCRIPT_REFERENCE);
            if (keyNode != null) {
                keyNode = keyNode.findChildByType(ScriptTypes.SCRIPT_IDENTIFIER);
            }
        }
        if (keyNode != null) {
            return keyNode.getPsi();
        } else {
            return null;
        }
    }

}