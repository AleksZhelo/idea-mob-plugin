package com.alekseyzhelo.evilislands.mobplugin.script.util;


import com.intellij.codeInsight.completion.CompletionInitializationContext;
import org.jetbrains.annotations.NotNull;

final class EIScriptGenerationUtil {

    @NotNull
    static String globalVarText(@NotNull String name, @NotNull EITypeToken type) {
        return "GlobalVars(" + trimDummy(name) + ":" + type.getTypeString() + ")";
    }

    @NotNull
    static String scriptImplementationText(@NotNull String name) {
        return "Script " + trimDummy(name) + "(if () then (KillScript()))";
    }

    @NotNull
    static String scriptDeclarationText(@NotNull String name, @NotNull String paramName, @NotNull EITypeToken type) {
        return "DeclareScript " + trimDummy(name) + String.format(" (%s : %s)", paramName, type.getTypeString());
    }

    @NotNull
    private static String trimDummy(@NotNull String text) {
        if (text.endsWith(CompletionInitializationContext.DUMMY_IDENTIFIER_TRIMMED)) {
            text = text.substring(0, text.length() - CompletionInitializationContext.DUMMY_IDENTIFIER_TRIMMED.length());
        }
        return text;
    }

    private EIScriptGenerationUtil() {

    }
}
