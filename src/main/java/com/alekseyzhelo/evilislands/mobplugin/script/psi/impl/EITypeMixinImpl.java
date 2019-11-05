package com.alekseyzhelo.evilislands.mobplugin.script.psi.impl;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.base.EITypeMixin;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EITypeToken;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * Created by Aleks on 02-08-2015.
 */
public class EITypeMixinImpl extends EIScriptPsiElementImpl
        implements EITypeMixin {

    EITypeMixinImpl(ASTNode node) {
        super(node);
    }

    @Override
    @NotNull
    public EITypeToken getTypeToken() {
        // TODO: cache?
        return EITypeToken.fromString(getText().toLowerCase(Locale.ENGLISH));
    }

    @Override
    public String toString() {
        return getText();
    }
}
