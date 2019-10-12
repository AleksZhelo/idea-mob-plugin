package com.alekseyzhelo.evilislands.mobplugin.script.psi.impl;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFunctionCall;
import org.apache.commons.compress.utils.Sets;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;

public class EIArea extends EIExtraVarBase<Integer> {

    @SuppressWarnings("SpellCheckingInspection")
    private static final Set<String> readFunctions = Collections.unmodifiableSet(Sets.newHashSet(
            "deletearea",
            "qobjarea",
            "isinarea"
    ));
    @SuppressWarnings("SpellCheckingInspection")
    private static final Set<String> writeFunctions = Collections.unmodifiableSet(Sets.newHashSet(
            "addroundtoarea",
            "addrecttoarea"
    ));

    public EIArea(int value) {
        super(value);
    }

    @Override
    protected boolean isVariableRead(@NotNull EIFunctionCall call) {
        String functionName = call.getName();
        return readFunctions.contains(functionName.toLowerCase(Locale.ENGLISH));
    }

    @Override
    protected boolean isVariableWrite(@NotNull EIFunctionCall call) {
        String functionName = call.getName();
        return writeFunctions.contains(functionName.toLowerCase(Locale.ENGLISH));
    }
}
