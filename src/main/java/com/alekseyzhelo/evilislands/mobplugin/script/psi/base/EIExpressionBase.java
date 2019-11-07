package com.alekseyzhelo.evilislands.mobplugin.script.psi.base;

import com.alekseyzhelo.evilislands.mobplugin.script.util.EITypeToken;
import org.jetbrains.annotations.Nullable;

public interface EIExpressionBase extends EIScriptPsiElement {
    @Nullable
    EITypeToken getType();
}
