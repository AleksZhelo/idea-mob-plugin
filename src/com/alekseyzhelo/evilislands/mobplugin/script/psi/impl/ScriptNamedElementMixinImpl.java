package com.alekseyzhelo.evilislands.mobplugin.script.psi.impl;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIScriptIdentifier;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptNamedElementMixin;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptTypes;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptElementFactory;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiKeyword;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.PsiMethodImpl;
import com.intellij.psi.impl.source.tree.java.PsiKeywordImpl;
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

    public PsiElement setName(@NotNull String newName) {
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

    @NotNull
    @Override
    public PsiElement getNavigationElement() {
        return getNameIdentifier() != null ? getNameIdentifier() : this;
    }

    @Override
    public int getTextOffset() {
        return getNameIdentifier() != null ? getNameIdentifier().getTextOffset() : super.getTextOffset();
    }
}