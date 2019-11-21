package com.alekseyzhelo.evilislands.mobplugin.script.util;

import org.jetbrains.annotations.NotNull;

/**
 * @author Aleks on 02-08-2015.
 */
public enum EITypeToken {
    VOID(EIScriptNamingUtil.VOID),
    FLOAT(EIScriptNamingUtil.FLOAT),
    STRING(EIScriptNamingUtil.STRING),
    OBJECT(EIScriptNamingUtil.OBJECT),
    GROUP(EIScriptNamingUtil.GROUP),
    ANY("");

    @NotNull
    private final String typeString;

    EITypeToken(@NotNull String typeString) {
        this.typeString = typeString;
    }

    @NotNull
    public String getTypeString() {
        return typeString;
    }

    @NotNull
    public static EITypeToken fromString(String typeString) {
        for (EITypeToken type : values()) {
            if (type.typeString.equals(typeString)) {
                return type;
            }
        }

        return VOID;  // XXX: shouldn't happen
    }
}
