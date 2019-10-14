package com.alekseyzhelo.evilislands.mobplugin.script.psi.impl;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.base.EIScriptImplementationMixin;
import com.intellij.lang.ASTNode;

/**
 * Created by Aleks on 25-07-2015.
 */
// TODO: needed?
public class EIScriptImplementationMixinImpl extends EIScriptNamedElementMixinImpl
        implements EIScriptImplementationMixin {

    public EIScriptImplementationMixinImpl(ASTNode node) {
        super(node);
    }

    @Override
    public String toString() {
        return getName();
    }
}
