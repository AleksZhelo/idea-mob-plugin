package com.alekseyzhelo.evilislands.mobplugin.script.lexer;

import com.intellij.lexer.FlexAdapter;

import java.io.Reader;

public class EILexer extends FlexAdapter {
    public EILexer() {
        super(new EIScriptLexer((Reader) null));
    }
}