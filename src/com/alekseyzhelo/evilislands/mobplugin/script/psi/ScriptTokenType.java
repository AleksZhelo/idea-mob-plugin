package com.alekseyzhelo.evilislands.mobplugin.script.psi;

import com.alekseyzhelo.evilislands.mobplugin.script.EIScriptLanguage;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class ScriptTokenType extends IElementType {
    public ScriptTokenType(@NotNull @NonNls String debugName) {
        super(debugName, EIScriptLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "ScriptTokenType." + super.toString();
    }
}