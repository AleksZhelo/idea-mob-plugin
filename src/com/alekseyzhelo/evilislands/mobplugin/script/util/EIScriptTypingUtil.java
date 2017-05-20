package com.alekseyzhelo.evilislands.mobplugin.script.util;


import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
import com.intellij.psi.PsiElement;

public final class EIScriptTypingUtil {


    // TODO: well this is a fucking wreck of a method
    public static EITypeToken getExpectedType(EIVariableAccess variableAccess) {
        if (variableAccess.getParent() instanceof EIForBlock) {
            return EITypeToken.OBJECT;
        } else {
            EIParams params = UsefulPsiTreeUtil.getParentOfType(variableAccess, EIParams.class);
            if (params != null) {
                int index = params.getExpressionList().indexOf(
                        UsefulPsiTreeUtil.getParentOfType(variableAccess, EIExpression.class)
                );
                if (index >= 0) {
                    EIFunctionCall call = UsefulPsiTreeUtil.getParentOfType(params, EIFunctionCall.class);
                    if (call != null) {
                        PsiElement resolved = call.getReference().resolve();
                        if (resolved instanceof EIFunctionDeclaration) {
                            return ((EIFunctionDeclaration) resolved).getFormalParameterList().get(index).getType().getTypeToken();
                        } else if (resolved instanceof EIScriptDeclaration) {
                            return ((EIScriptDeclaration) resolved).getFormalParameterList().get(index).getType().getTypeToken();
                        } else {
                            return null;
                        }
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    private EIScriptTypingUtil() {

    }

}
