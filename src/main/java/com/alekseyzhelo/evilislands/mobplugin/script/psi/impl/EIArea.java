package com.alekseyzhelo.evilislands.mobplugin.script.psi.impl;

import com.alekseyzhelo.evilislands.mobplugin.icon.Icons;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFunctionCall;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.base.EIExtraVarBase;
import com.intellij.psi.PsiElement;
import org.apache.commons.compress.utils.Sets;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO v2: are these also project-wide? check in engine!
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
    public static final Set<String> relevantFunctions = Collections.unmodifiableSet(
            Stream.concat(readFunctions.stream(), writeFunctions.stream()).collect(Collectors.toSet())
    );

    public static boolean isReadOrWrite(@NotNull EIFunctionCall call) {
        return relevantFunctions.contains(call.getName().toLowerCase(Locale.ENGLISH));
    }

    public static boolean isAreaRead(@NotNull EIFunctionCall call) {
        return readFunctions.contains(call.getName().toLowerCase(Locale.ENGLISH));
    }

    @Nullable
    public static PsiElement getAreaIdElement(@NotNull EIFunctionCall call) {
        PsiElement element = call.getNthArgument(0);
        return element != null ? element.getFirstChild() : null;
    }

    public EIArea(int value) {
        super(value);
    }

    @Override
    public Icon getIcon(int flags) {
        return Icons.AREA;
    }

    @Override
    protected boolean isVariableRead(@NotNull EIFunctionCall call) {
        return isAreaRead(call);
    }

    @Override
    protected boolean isVariableWrite(@NotNull EIFunctionCall call) {
        return writeFunctions.contains(call.getName().toLowerCase(Locale.ENGLISH));
    }
}
