package com.alekseyzhelo.evilislands.mobplugin.script.util;

/**
 * @author Aleks on 02-08-2015.
 */
public enum EITypeToken {
    VOID(EIScriptNamingUtil.VOID),
    FLOAT(EIScriptNamingUtil.FLOAT),
    STRING(EIScriptNamingUtil.STRING),
    OBJECT(EIScriptNamingUtil.OBJECT),
    GROUP(EIScriptNamingUtil.GROUP);

    private String typeString;

    EITypeToken(String typeString) {
        this.typeString = typeString;
    }

    public String getTypeString() {
        return typeString;
    }

    public static EITypeToken fromString(String typeString) {
        for (EITypeToken type : values()) {
            if (type.typeString.equals(typeString)) {
                return type;
            }
        }

        return VOID;
    }
}
