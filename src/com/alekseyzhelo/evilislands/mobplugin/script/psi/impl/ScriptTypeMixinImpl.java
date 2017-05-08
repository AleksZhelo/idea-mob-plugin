package com.alekseyzhelo.evilislands.mobplugin.script.psi.impl;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptTypeMixin;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EITypeToken;
import com.intellij.lang.ASTNode;

import java.util.Locale;

/**
 * Created by Aleks on 02-08-2015.
 */
public class ScriptTypeMixinImpl extends ScriptPsiElementImpl
        implements ScriptTypeMixin {

    private final EITypeToken type;

    public ScriptTypeMixinImpl(ASTNode node) {
        super(node);
        String typeText = node.getText().toLowerCase(Locale.ENGLISH);
        switch (typeText) {
            case "float":
                type = EITypeToken.FLOAT;
                break;
            case "string":
                type = EITypeToken.STRING;
                break;
            case "object":
                type = EITypeToken.OBJECT;
                break;
            case "group":
                type = EITypeToken.GROUP;
                break;
            default:
                type = EITypeToken.VOID;
        }
    }

    @Override
    public EITypeToken getTypeToken() {
        return type;
    }
}
