package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.util;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIExpression;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFormalParameter;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIType;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EITypeToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EICallArgumentErrorDetector {
    private List<EIFormalParameter> formalParameters;
    private List<EIExpression> actualArguments;
    private int numErrors = 0;
    private int firstWrong = Integer.MAX_VALUE;

    public EICallArgumentErrorDetector(@NotNull List<EIFormalParameter> formalParameters,
                                       @NotNull List<EIExpression> actualArguments) {
        this.formalParameters = formalParameters;
        this.actualArguments = actualArguments;
    }

    public boolean errorsDetected() {
        return numErrors > 0;
    }

    public int getNumErrors() {
        return numErrors;
    }

    public int getFirstWrong() {
        return firstWrong;
    }

    public boolean wrongArgumentCount() {
        return formalParameters.size() != actualArguments.size();
    }

    @Nullable
    public EIFormalParameter getFirstWrongParameter() {
        return numErrors > 0 && firstWrong < formalParameters.size() ? formalParameters.get(firstWrong) : null;
    }

    @Nullable
    public EIExpression getFirstWrongArgument() {
        return numErrors > 0 && firstWrong < actualArguments.size() ? actualArguments.get(firstWrong) : null;
    }

    @NotNull
    public EICallArgumentErrorDetector invoke() {
        for (int i = 0; i < Math.max(formalParameters.size(), actualArguments.size()); i++) {
            EIFormalParameter parameter = i < formalParameters.size() ? formalParameters.get(i) : null;
            EIExpression expression = i < actualArguments.size() ? actualArguments.get(i) : null;
            EIType expectedType = parameter != null ? parameter.getType() : null;
            EITypeToken actualType = expression != null ? expression.getType() : null;
            if (actualType == null || expectedType == null || !expectedType.getTypeToken().equals(actualType)) {
                numErrors++;
                firstWrong = Math.min(firstWrong, i);
            }
        }
        return this;
    }
}
