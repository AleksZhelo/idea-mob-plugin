package com.alekseyzhelo.evilislands.mobplugin.script.misc;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptTypes;
import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler;

public class EIQuoteHandler extends SimpleTokenSetQuoteHandler {
    
    public EIQuoteHandler() {
        super(ScriptTypes.CHARACTER_STRING);
    }
}
