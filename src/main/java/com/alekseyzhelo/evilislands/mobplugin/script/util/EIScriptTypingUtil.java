package com.alekseyzhelo.evilislands.mobplugin.script.util;


import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.base.EICallableDeclaration;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public final class EIScriptTypingUtil {


    private EIScriptTypingUtil() {

    }

    public static EITypeToken getExpectedType(EIExpression expression) {
        if (expression.getParent() instanceof EIAssignment) {
            PsiElement resolved = expression.getParent().getReference().resolve();
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

    private static EITypeToken getVariableExpectedTypeInner(@NotNull EIVariableAccess variableAccess) {
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
            EIAssignment assignment = (EIAssignment) parent;
            int myIndex = assignment.indexOf(variableAccess);
            if (myIndex == 0) { // left side
                // if the assignment is incomplete any type is OK
                return assignment.isComplete()
                        ? Objects.requireNonNull(assignment.getRightSide()).getType()
                        : EITypeToken.ANY;
            } else if (myIndex == 1) {
                return assignment.getLeftSide().getType();
            } else {
                return null;
            }
        }

        if (parent instanceof EIParams) {
            return getExpectedType((EIParams) parent, variableAccess);
        }

        return null;  // shouldn't happen
    }

    @NotNull
    public static EITypeToken getVariableExpectedType(@NotNull EIVariableAccess variableAccess) {
        EITypeToken expectedType = EIScriptTypingUtil.getVariableExpectedTypeInner(variableAccess);
        return expectedType != null && expectedType != EITypeToken.VOID
                ? expectedType
                : EITypeToken.ANY;
    }

    /**
     * Get the expected type of an expression in a parameters sequence.
     *
     * @param paramOwner the parameters containing the expression
     * @param param      the expression to process
     * @return the expected type token
     */
    private static EITypeToken getExpectedTypeInner(EIParams paramOwner, EIExpression param) {
        if (paramOwner != null && param != null) {
            int index = paramOwner.getExpressionList().indexOf(param);
            if (index >= 0) {
                EIFunctionCall call = PsiTreeUtil.getParentOfType(paramOwner, EIFunctionCall.class);
                assert call != null;
                PsiElement resolved = call.getReference().resolve();
                if (resolved instanceof EICallableDeclaration) {
                    List<EIFormalParameter> formalParameters =
                            ((EICallableDeclaration) resolved).getCallableParams();
                    if (index < formalParameters.size()) {
                        EIType paramType = formalParameters.get(index).getType();
                        return paramType != null ? paramType.getTypeToken() : null;
                    }
                }
            }
        }
        return null;
    }

    public static EITypeToken getExpectedType(EIParams paramOwner, EIExpression param) {
        EITypeToken expectedType = EIScriptTypingUtil.getExpectedTypeInner(paramOwner, param);
        return expectedType != null && expectedType != EITypeToken.VOID
                ? expectedType
                : EITypeToken.ANY;
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
