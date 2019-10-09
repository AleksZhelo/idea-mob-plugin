package com.alekseyzhelo.evilislands.mobplugin.mob;

import com.intellij.lang.Language;

public class EIMobLanguage extends Language {
    public static final EIMobLanguage INSTANCE = new EIMobLanguage();

    private EIMobLanguage() {
        super("EIMobLanguage");
    }
}