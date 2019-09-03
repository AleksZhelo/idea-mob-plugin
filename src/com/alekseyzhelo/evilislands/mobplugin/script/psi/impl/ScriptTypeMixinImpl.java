package com.alekseyzhelo.evilislands.mobplugin.script.psi.impl;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptTypeMixin;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptNamingUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EITypeToken;
import com.intellij.lang.ASTNode;

import javax.annotation.Nonnull;
import java.util.Locale;

/**
 * Created by Aleks on 02-08-2015.
 */
public class ScriptTypeMixinImpl extends ScriptPsiElementImpl
        implements ScriptTypeMixin {

    @Nonnull
    private final EITypeToken type;

    public ScriptTypeMixinImpl(ASTNode node) {
        super(node);
        String typeText = node.getText().toLowerCase(Locale.ENGLISH);
        type = EITypeToken.fromString(typeText);
    }

    @Override
    public EITypeToken getTypeToken() {
        return type;
    }

    @Override
    public String toString() {
//        return EIScriptNamingUtil.NAME_TYPE + getText();
        return getText();
    }

}
