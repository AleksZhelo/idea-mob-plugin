package com.alekseyzhelo.evilislands.mobplugin.script.psi.impl;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFunctionCall;
import com.intellij.psi.PsiElement;

// TODO: these ones should be project-wide? Especially in read/write counts?
public class EIGSVar {

    private String varName;
    private int reads = 0;
    private int writes = 0;

    public EIGSVar(String name) {
        varName = name;
    }

    public String getVarName() {
        return varName;
    }

    public void addRead() {
        reads++;
    }

    public void addWrite() {
        writes++;
    }

    public int getReads() {
        return reads;
    }

    public int getWrites() {
        return writes;
    }

    @Override
    public String toString() {
        return varName;
    }

    public static PsiElement getVarNameElement(EIFunctionCall call) {
        return call.getParams().getExpressionList().get(1).getFirstChild();
    }

    public static String getVarName(EIFunctionCall call) {
        return getVarName(getVarNameElement(call).getText());
    }

    public static String getVarName(String literal) {
        return literal.substring(1, literal.length() - 1).trim();
    }

    public static boolean isGSVarRead(EIFunctionCall call) {
        return call.getScriptIdentifier().getText().equalsIgnoreCase("gsgetvar");
    }

    public static boolean isGSVarWrite(EIFunctionCall call) {
        return call.getScriptIdentifier().getText().equalsIgnoreCase("gssetvar");
    }
}
