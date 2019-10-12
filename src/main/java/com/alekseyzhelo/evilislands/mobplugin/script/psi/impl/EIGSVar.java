package com.alekseyzhelo.evilislands.mobplugin.script.psi.impl;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFunctionCall;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

// TODO: these ones should be project-wide? Especially in read/write counts?
public class EIGSVar extends EIExtraVarBase<String> {

    public EIGSVar(@NotNull String name) {
        super(name);
    }

    @Override
    protected boolean isVariableRead(@NotNull EIFunctionCall call) {
        return isGSVarRead(call);
    }

    @Override
    protected boolean isVariableWrite(@NotNull EIFunctionCall call) {
        return isGSVarWrite(call);
    }

    // TODO: how to fix this? also maybe add an "isReadOrWrite" method?
    public static boolean isGSVarRead(@NotNull EIFunctionCall call) {
        return call.getName().equalsIgnoreCase("gsgetvar");
    }

    public static boolean isGSVarWrite(@NotNull EIFunctionCall call) {
        return call.getName().equalsIgnoreCase("gssetvar");
    }

    public static PsiElement getVarNameElement(EIFunctionCall call) {
        // TODO: handle
        return call.getParams().getExpressionList().get(1).getFirstChild();
    }

    public static String getVarName(EIFunctionCall call) {
        return getVarName(getVarNameElement(call).getText());
    }

    public static String getVarName(String literal) {
        return literal.substring(1, literal.length() - 1).trim();
    }
}
