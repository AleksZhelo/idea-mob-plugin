package com.alekseyzhelo.evilislands.mobplugin.script.psi.impl;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptTypes;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.base.ScriptPsiIdentifierMixin;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;

/**
 * Created by Aleks on 25-07-2015.
 */
public class ScriptPsiIdentifierMixinImpl extends EIScriptPsiElementImpl
        implements ScriptPsiIdentifierMixin {

    public ScriptPsiIdentifierMixinImpl(ASTNode node) {
        super(node);
    }

    @Override
    public IElementType getTokenType() {
        return ScriptTypes.IDENTIFIER;
    }

    @Override
    public String toString() {
        return getText();
    }
}
