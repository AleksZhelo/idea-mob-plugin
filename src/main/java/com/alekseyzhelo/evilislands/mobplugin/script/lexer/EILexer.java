package com.alekseyzhelo.evilislands.mobplugin.script.lexer;

import com.intellij.lexer.FlexAdapter;

public class EILexer extends FlexAdapter {
    public EILexer() {
        super(new EIScriptLexer(null));
    }
}