package com.alekseyzhelo.evilislands.mobplugin.script.psi.impl;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptImplementationMixin;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptNamingUtil;
import com.intellij.lang.ASTNode;

/**
 * Created by Aleks on 25-07-2015.
 */
public class ScriptImplementationMixinImpl extends ScriptNamedElementMixinImpl
        implements ScriptImplementationMixin {

    public ScriptImplementationMixinImpl(ASTNode node) {
        super(node);
    }

    @Override
    public String toString() {
        return EIScriptNamingUtil.NAME_SCRIPT_IMPL + getName();
    }

}
