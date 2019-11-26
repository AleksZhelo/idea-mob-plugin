package com.alekseyzhelo.evilislands.mobplugin.script.psi.impl;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.base.EIVariableBase;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public abstract class EIVariableBaseImpl extends EIScriptNamedElementMixinImpl implements EIVariableBase {
    EIVariableBaseImpl(@NotNull ASTNode node) {
        super(node);
    }
}
