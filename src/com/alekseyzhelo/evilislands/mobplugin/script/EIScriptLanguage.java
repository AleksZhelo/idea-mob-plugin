package com.alekseyzhelo.evilislands.mobplugin.script;

import com.intellij.lang.Language;
 
public class EIScriptLanguage extends Language {
    public static final EIScriptLanguage INSTANCE = new EIScriptLanguage();
 
    private EIScriptLanguage() {
        super("EIScriptLanguage");
    }
}