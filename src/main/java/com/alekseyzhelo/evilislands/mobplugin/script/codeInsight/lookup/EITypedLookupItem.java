package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.lookup;

import com.alekseyzhelo.evilislands.mobplugin.script.util.EITypeToken;
import org.jetbrains.annotations.NotNull;

public interface EITypedLookupItem {
    @NotNull
    EITypeToken getType();
}
