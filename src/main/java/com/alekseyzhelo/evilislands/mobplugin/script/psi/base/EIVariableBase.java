package com.alekseyzhelo.evilislands.mobplugin.script.psi.base;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIType;
import org.jetbrains.annotations.Nullable;

public interface EIVariableBase extends EIScriptNamedElementMixin {
    @Nullable
    EIType getType();
}
