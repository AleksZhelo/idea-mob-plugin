package com.alekseyzhelo.evilislands.mobplugin.script.util;


import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.references.VariableReference;
import com.intellij.psi.PsiElement;

public final class EIScriptTypingUtil {

    public static EITypeToken getExpectedType(EIExpression expression) {
        if (expression.getParent() instanceof EIAssignment) {
            PsiElement resolved = ((EIAssignment) expression.getParent()).getReference().resolve();
            if (resolved instanceof EIFormalParameter) {
                EIType type = ((EIFormalParameter) resolved).getType();
                return type != null ? type.getTypeToken() : null;
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

    public static EITypeToken getExpectedType(VariableReference reference) {
        PsiElement parent = reference.getElement().getParent();
        if (parent instanceof EIVariableAccess) {
            return EIScriptTypingUtil.getVariableExpectedType((EIVariableAccess) parent);
        } else if (parent instanceof EIAssignment) {
            return EIScriptTypingUtil.getAssigneeExpectedType((EIAssignment) parent);
        } else {
            return null;
        }
    }

    public static EITypeToken getVariableExpectedType(EIVariableAccess variableAccess) {
        if (variableAccess.getParent() instanceof EIForBlock) {
            return EITypeToken.OBJECT;
        } else { // variableAccess is in an EIExpression, which would only happen inside a call
            EIParams params = UsefulPsiTreeUtil.getParentOfType(variableAccess, EIParams.class);
            EIExpression expression = UsefulPsiTreeUtil.getParentOfType(variableAccess, EIExpression.class);
            return getExpectedType(params, expression);
        }
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
                if (call != null) {
                    PsiElement resolved = call.getReference().resolve();
                    if (resolved instanceof EIFunctionDeclaration) {
                        try {
                            return ((EIFunctionDeclaration) resolved).getFormalParameterList().get(index).getType().getTypeToken();
                        } catch (IndexOutOfBoundsException e) { // TODO: better handling?
                            return null;
                        }
                    } else if (resolved instanceof EIScriptDeclaration) {
                        try {
                            return ((EIScriptDeclaration) resolved).getFormalParameterList().get(index).getType().getTypeToken();
                        } catch (IndexOutOfBoundsException e) { // TODO: better handling?
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
        } else {
            return null;
        }
    }

    private EIScriptTypingUtil() {

    }

}
