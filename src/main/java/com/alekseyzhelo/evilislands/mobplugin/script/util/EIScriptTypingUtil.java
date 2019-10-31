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
            int myIndex = ((EIForBlock) parent).getVariableAccessList().indexOf(variableAccess);
            if (myIndex == 0) {
                return EITypeToken.OBJECT;
            } else if (myIndex == 1) {
                return EITypeToken.GROUP;
            } else {
                return null;
            }
        }

        if (parent instanceof EIExpression) { // either inside a call or on the right side of an assignment
            PsiElement superParent = parent.getParent();
            if (superParent instanceof EIAssignment) {
                EIVariableAccess leftSide = ((EIAssignment) superParent).getVariableAccess();
                return leftSide.getType();
            } else {  // in a call
                EIParams params = UsefulPsiTreeUtil.getParentOfType(variableAccess, EIParams.class);
                EIExpression expression = UsefulPsiTreeUtil.getParentOfType(variableAccess, EIExpression.class);
                return getExpectedType(params, expression);
            }
        }

        if (parent instanceof EIAssignment) {  // variableAccess on the left side of an assignment
            EIExpression expression = ((EIAssignment) parent).getExpression();
            // if expression == null the assignment is incomplete, any type is OK
            return expression != null ? expression.getType() : null;  // TODO: should be ANY instead of null
        }

        return null;  // shouldn't happen
    }

    public static EITypeToken getAssigneeExpectedType(EIAssignment assignment) {
        EIExpression expression = assignment.getExpression();
        return expression != null ? expression.getType() : null;
    }

    /**
     * Get the expected type of an expression in a parameters sequence.
     *
     * @param params     the parameters containing the expression
     * @param expression the expression to process
     * @return the expected type token
     */
    public static EITypeToken getExpectedType(EIParams params, EIExpression expression) {
        if (params != null && expression != null) {
            int index = params.getExpressionList().indexOf(expression);
            if (index >= 0) {
                EIFunctionCall call = UsefulPsiTreeUtil.getParentOfType(params, EIFunctionCall.class);
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
}
