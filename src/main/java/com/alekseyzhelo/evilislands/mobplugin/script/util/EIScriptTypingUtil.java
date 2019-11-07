package com.alekseyzhelo.evilislands.mobplugin.script.util;


import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
import com.intellij.psi.PsiElement;

import java.util.List;

public final class EIScriptTypingUtil {


    private EIScriptTypingUtil() {

    }

    public static EITypeToken getExpectedType(EIExpression expression) {
        if (expression.getParent() instanceof EIAssignment) {
            PsiElement resolved = ((EIAssignment) expression.getParent()).getReference().resolve();
            if (resolved instanceof EIFormalParameter) {
                EIType type = ((EIFormalParameter) resolved).getType();
                return type.getTypeToken();
            } else if (resolved instanceof EIGlobalVar) {
                EIType type = ((EIGlobalVar) resolved).getType();
                return type != null ? type.getTypeToken() : null;
            } else {
                return null;
            }
        } else if (expression.getParent() instanceof EIParams) {
            return getExpectedType((EIParams) expression.getParent(), expression);
        } else {
            return null;
        }
    }

    public static EITypeToken getVariableExpectedType(EIVariableAccess variableAccess) {
        PsiElement parent = variableAccess.getParent();

        if (parent instanceof EIForBlock) {
            int myIndex = ((EIForBlock) parent).getExpressionList().indexOf(variableAccess);
            if (myIndex == 0) {
                return EITypeToken.OBJECT;
            } else if (myIndex == 1) {
                return EITypeToken.GROUP;
            } else {
                return null;
            }
        }

        if (parent instanceof EIAssignment) {
            List<EIExpression> expressions = ((EIAssignment) parent).getExpressionList();
            int myIndex = expressions.indexOf(variableAccess);
            if (myIndex == 0) { // left side
                EIExpression rightSide = expressions.size() > 1 ? expressions.get(1) : null;
                // if rightSide == null the assignment is incomplete, any type is OK
                return rightSide != null ? rightSide.getType() : EITypeToken.ANY;
            } else if (myIndex == 1) {
                EIExpression leftSide = expressions.get(0);
                return leftSide.getType();
            } else {
                return null;
            }
        }

        if (parent instanceof EIParams) {
            return getExpectedType((EIParams) parent, variableAccess);
        }

        return null;  // shouldn't happen
    }

    /**
     * Get the expected type of an expression in a parameters sequence.
     *
     * @param paramOwner the parameters containing the expression
     * @param param      the expression to process
     * @return the expected type token
     */
    public static EITypeToken getExpectedType(EIParams paramOwner, EIExpression param) {
        if (paramOwner != null && param != null) {
            int index = paramOwner.getExpressionList().indexOf(param);
            if (index >= 0) {
                EIFunctionCall call = UsefulPsiTreeUtil.getParentOfType(paramOwner, EIFunctionCall.class);
                assert call != null;
                PsiElement resolved = call.getReference().resolve();
                if (resolved != null) {
                    List<EIFormalParameter> formalParameters =
                            resolved instanceof EIFunctionDeclaration
                                    ? ((EIFunctionDeclaration) resolved).getFormalParameterList()
                                    : ((EIScriptDeclaration) resolved).getFormalParameterList();
                    if (index < formalParameters.size()) {
                        EIType paramType = formalParameters.get(index).getType();
                        return paramType != null ? paramType.getTypeToken() : null;
                    }
                }
            }
        }
        return null;
    }

    public static boolean matchingType(EIFormalParameter parameter, EIExpression expression) {
        if (parameter == null || expression == null) return false;
        EIType paramType = parameter.getType();
        return matchingType(paramType != null ? paramType.getTypeToken() : null, expression.getType());
    }

    public static boolean matchingType(EITypeToken lType, EITypeToken rType) {
        return lType != null && lType.equals(rType);
    }
}
