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

    @NotNull
    private final EITypeToken type;

    EITypeMixinImpl(ASTNode node) {
        super(node);
        String typeText = node.getText().toLowerCase(Locale.ENGLISH);
        type = EITypeToken.fromString(typeText);
    }

    @Override
    @NotNull
    public EITypeToken getTypeToken() {
        return type;
    }

    @Override
    public String toString() {
        return getText();
    }
}
