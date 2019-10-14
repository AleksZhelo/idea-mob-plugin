package com.alekseyzhelo.evilislands.mobplugin.script.psi.impl;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFunctionCall;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.base.EIExtraVarBase;
import com.intellij.psi.PsiElement;
import org.apache.commons.compress.utils.Sets;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO: these ones should be project-wide? Especially in read/write counts?
public class EIGSVar extends EIExtraVarBase<String> {

    @SuppressWarnings("SpellCheckingInspection")
    private static final Set<String> readFunctions = Collections.unmodifiableSet(Sets.newHashSet(
            "gsdelvar",
            "gsgetvar"
    ));
    @SuppressWarnings("SpellCheckingInspection")
    private static final Set<String> writeFunctions = Collections.unmodifiableSet(Sets.newHashSet(
            "gssetvar",
            "gssetvarmax"
    ));
    public static final Set<String> relevantFunctions = Collections.unmodifiableSet(
            Stream.concat(readFunctions.stream(), writeFunctions.stream()).collect(Collectors.toSet())
    );

    public static boolean isReadOrWrite(@NotNull EIFunctionCall call) {
        return relevantFunctions.contains(call.getName().toLowerCase(Locale.ENGLISH));
    }

    public static boolean isGSVarRead(@NotNull EIFunctionCall call) {
        return readFunctions.contains(call.getName().toLowerCase(Locale.ENGLISH));
    }

    @Nullable
    public static PsiElement getVarNameElement(EIFunctionCall call) {
        PsiElement element = call.getNthArgument(1);
        return element != null ? element.getFirstChild() : null;
    }

    @Nullable
    public static String getVarName(EIFunctionCall call) {
        PsiElement nameElement = getVarNameElement(call);
        return nameElement != null ? getVarName(nameElement.getText()) : null;
    }

    @NotNull
    public static String getVarName(@NotNull String literal) {
        return literal.substring(1, literal.length() - 1).trim();
    }

    public EIGSVar(@NotNull String name) {
        super(name);
    }

    @Override
    protected boolean isVariableRead(@NotNull EIFunctionCall call) {
        // TODO: is calling isGSVarRead expensive here?
        return isGSVarRead(call);
    }

    @Override
    protected boolean isVariableWrite(@NotNull EIFunctionCall call) {
        return writeFunctions.contains(call.getName().toLowerCase(Locale.ENGLISH));
    }
}
