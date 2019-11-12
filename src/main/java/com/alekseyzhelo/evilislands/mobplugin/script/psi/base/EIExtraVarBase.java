package com.alekseyzhelo.evilislands.mobplugin.script.psi.base;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFunctionCall;
import org.jetbrains.annotations.NotNull;

public abstract class EIExtraVarBase<T> {

    private final T variable;
    private int reads = 0;
    private int writes = 0;

    public EIExtraVarBase(@NotNull T value) {
        variable = value;
    }

    protected abstract boolean isVariableRead(@NotNull EIFunctionCall call);

    protected abstract boolean isVariableWrite(@NotNull EIFunctionCall call);

    public final T getVariable() {
        return variable;
    }

    public final int getReads() {
        return reads;
    }

    public final int getWrites() {
        return writes;
    }

    public final boolean isValid() {
        return reads != 0 || writes != 0;
    }

    public final void processCall(EIFunctionCall call) {
        if (call != null) {
            if (isVariableRead(call)) {
                reads++;
            } else if (isVariableWrite(call)) {
                writes++;
            }
        }
    }

    @Override
    public String toString() {
        return String.valueOf(variable);
    }
}
