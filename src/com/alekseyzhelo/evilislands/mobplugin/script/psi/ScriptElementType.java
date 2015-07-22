package com.alekseyzhelo.evilislands.mobplugin.script.psi;

import com.alekseyzhelo.evilislands.mobplugin.script.EIScriptLanguage;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class ScriptElementType extends IElementType {
    public ScriptElementType(@NotNull @NonNls String debugName) {
        super(debugName, EIScriptLanguage.INSTANCE);
    }
}