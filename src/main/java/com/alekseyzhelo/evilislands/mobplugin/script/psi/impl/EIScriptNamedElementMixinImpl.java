package com.alekseyzhelo.evilislands.mobplugin.script.psi.impl;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptTypes;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.base.EIScriptNamedElementMixin;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptElementFactory;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class EIScriptNamedElementMixinImpl extends EIScriptPsiElementImpl implements EIScriptNamedElementMixin {

    EIScriptNamedElementMixinImpl(@NotNull ASTNode node) {
        super(node);
    }

    private ASTNode getNameNode() {
        return getNode().findChildByType(ScriptTypes.IDENTIFIER);
    }

    @NotNull
    @Override
    public String getName() {
        ASTNode nameNode = getNameNode();
        if (nameNode != null) {
            return nameNode.getText();
        } else {
            return "";
        }
    }

    @Override
    public PsiElement setName(@NotNull String newName) {
        ASTNode nameNode = getNameNode();
        if (nameNode != null) {
            PsiElement identifier = EIScriptElementFactory.createIdentifier(getProject(), newName);
            ASTNode newNameNode = identifier.getNode();
            getNode().replaceChild(nameNode, newNameNode);
        }
        return this;
    }

    @Override
    @Nullable
    public PsiElement getNameIdentifier() {
        ASTNode nameNode = getNameNode();
        if (nameNode != null) {
            return nameNode.getPsi();
        } else {
            return null;
        }
    }

    @NotNull
    @Override
    public PsiElement getNavigationElement() {
        PsiElement nameIdentifier = getNameIdentifier();
        return nameIdentifier != null ? nameIdentifier : this;
    }

    @Override
    public int getTextOffset() {
        PsiElement nameIdentifier = getNameIdentifier();
        return nameIdentifier != null ? nameIdentifier.getTextOffset() : super.getTextOffset();
    }
}