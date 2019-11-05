package com.alekseyzhelo.evilislands.mobplugin.script.util;


import com.intellij.codeInsight.completion.CompletionInitializationContext;

final class EIScriptGenerationUtil {

    static String globalVarText(String name) {
        return globalVarText(name, EITypeToken.GROUP);
    }

    static String globalVarText(String name, EITypeToken type) {
        return "GlobalVars(" + trimDummy(name) + ":" + type.getTypeString() + ")";
    }

    static String scriptImplementationText(String name) {
        // TODO: world script is a workaround for script_then_block recoverUntil deficiency
        return "Script " + trimDummy(name) + "(if () then (KillScript()))" + "WorldScript()";
    }

    static String scriptDeclarationText(String name) {
        return "DeclareScript " + trimDummy(name) + " (this : object)";
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
