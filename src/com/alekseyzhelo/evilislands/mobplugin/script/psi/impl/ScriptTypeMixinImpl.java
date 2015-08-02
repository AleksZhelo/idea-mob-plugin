package com.alekseyzhelo.evilislands.mobplugin.script.psi.impl;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptTypeMixin;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIType;
import com.intellij.lang.ASTNode;

import java.util.Locale;

/**
 * Created by Aleks on 02-08-2015.
 */
public class ScriptTypeMixinImpl extends ScriptPsiElementImpl
        implements ScriptTypeMixin {

    private final EIType type;

    public ScriptTypeMixinImpl(ASTNode node) {
        super(node);
        String typeText = node.getText().toLowerCase(Locale.ENGLISH);
        switch (typeText) {
            case "float":
                type = EIType.FLOAT;
                break;
            case "string":
                type = EIType.STRING;
                break;
            case "object":
                type = EIType.OBJECT;
                break;
            case "group":
                type = EIType.GROUP;
                break;
            default:
                type = EIType.VOID;
        }
    }

    @Override
    public EIType getTypeToken() {
        return type;
    }
}
