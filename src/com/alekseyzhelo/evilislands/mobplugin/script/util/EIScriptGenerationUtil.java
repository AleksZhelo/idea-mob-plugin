package com.alekseyzhelo.evilislands.mobplugin.script.util;


import com.intellij.codeInsight.completion.CompletionInitializationContext;

public final class EIScriptGenerationUtil {

    public static final String GLOBAL_VARS_PREFIX = "GlobalVars(";
    public static final String GLOBAL_VARS_SUFFIX = ")";
    public static final String DUMMY_TYPE = "group";

    public static String wrapGlobalVars(String identifier) {
        identifier = trimDummy(identifier);
        return GLOBAL_VARS_PREFIX + identifier + ":" + DUMMY_TYPE + GLOBAL_VARS_SUFFIX;
    }

    private static String trimDummy(String text) {
        if (text.endsWith(CompletionInitializationContext.DUMMY_IDENTIFIER_TRIMMED)) {
            text = text.substring(0, text.length() - CompletionInitializationContext.DUMMY_IDENTIFIER_TRIMMED.length());
        }
        return text;
    }

    private EIScriptGenerationUtil() {

    }

}
