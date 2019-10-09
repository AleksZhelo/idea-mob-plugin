package com.alekseyzhelo.evilislands.mobplugin.script.psi.impl;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.references.FunctionCallReference;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.references.MobObjectReference;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.references.VariableReference;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptNamingUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EITypeToken;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class EIScriptPsiImplUtil {

    @NotNull
    public static PsiReference getReference(EIAssignment assignment) {
        EIScriptIdentifier identifier = assignment.getScriptIdentifier();
        return new VariableReference(assignment, TextRange.create(0, identifier.getTextLength()));
    }

    @NotNull
    public static PsiReference getReference(EIFunctionCall call) {
        EIScriptIdentifier ident = call.getScriptIdentifier();
        return new FunctionCallReference(call, TextRange.create(0, ident.getTextLength()));
    }

    @NotNull
    public static PsiReference getReference(EIScriptImplementation impl) {
        EIScriptIdentifier ident = impl.getScriptIdentifier();
        return new FunctionCallReference(impl, ident != null ? ident.getTextRange().shiftLeft(impl.getTextRange().getStartOffset()) :
                TextRange.create(0, 0), true);
    }

    @NotNull
    public static PsiReference getReference(EIVariableAccess variable) {
        EIScriptIdentifier identifier = variable.getScriptIdentifier();
        return new VariableReference(variable, new TextRange(0, identifier.getTextLength()));
    }

    @Nullable
    public static PsiReference getReference(EIExpression expression) {
        ASTNode firstChild = expression.getNode().getFirstChildNode();
        final IElementType elementType = firstChild.getElementType();
        if (elementType == ScriptTypes.FLOATNUMBER) {
            EIFunctionCall parentCall = PsiTreeUtil.getParentOfType(expression, EIFunctionCall.class);
            if (parentCall != null && parentCall.getScriptIdentifier().getText().equalsIgnoreCase("getobject")) {
                return new MobObjectReference(expression, new TextRange(0, firstChild.getTextLength()));
            } else {
                return null;
            }
        } else if (elementType == ScriptTypes.CHARACTER_STRING) {
            EIFunctionCall parentCall = PsiTreeUtil.getParentOfType(expression, EIFunctionCall.class);
            // TODO: extract comparison logic; extract magic string?
            if (parentCall != null && parentCall.getScriptIdentifier().getText().equalsIgnoreCase("getobjectbyid")) {
                return new MobObjectReference(expression, new TextRange(1, firstChild.getTextLength() - 1));
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @NotNull
    public static EITypeToken getDisplayableType(EIFunctionDeclaration element) {
        if (element.getType() != null) {
            return element.getType().getTypeToken();
        } else {
            return EITypeToken.VOID;
        }
    }

    @Nullable
    public static EITypeToken getType(EIFunctionCall element) {
        PsiElement resolved = element.getReference().resolve();
        if (resolved instanceof EIFunctionDeclaration) {
            return ((EIFunctionDeclaration) resolved).getDisplayableType();
        } else if (resolved instanceof EIScriptDeclaration) {
            return EITypeToken.VOID;
        } else {
            return null;
        }
    }

    @Nullable
    public static EITypeToken getType(EIVariableAccess element) {
        PsiElement resolved = element.getReference().resolve();
        if (resolved instanceof EIGlobalVar && (((EIGlobalVar) resolved).getType() != null)) {
            //noinspection ConstantConditions
            return ((EIGlobalVar) resolved).getType().getTypeToken();
        } else if (resolved instanceof EIFormalParameter) {
            return ((EIFormalParameter) resolved).getType().getTypeToken();
        } else {
            return null;
        }
    }

    @Nullable
    public static EITypeToken getType(EIExpression element) {
        ASTNode firstChild = element.getNode().getFirstChildNode();
        final IElementType elementType = firstChild.getElementType();
        if (elementType == ScriptTypes.FLOATNUMBER) {
            return EITypeToken.FLOAT;
        } else if (elementType == ScriptTypes.CHARACTER_STRING) {
            return EITypeToken.STRING;
        } else if (element.getFunctionCall() != null) {
            return element.getFunctionCall().getType();
        } else if (element.getVariableAccess() != null) {
            return element.getVariableAccess().getType();
        } else {
            return null;
        }
    }

    @Nullable
    public static EITypeToken getType(EIScriptExpression element) {
        if (element.getAssignment() != null) {
            return EITypeToken.VOID;
        } else if (element.getForBlock() != null) {
            return EITypeToken.VOID;
        } else if (element.getFunctionCall() != null) {
            return element.getFunctionCall().getType();
        } else {
            return null;
        }
    }


    // toString block

    public static String toString(EIExpression expression) {
        return EIScriptNamingUtil.NAME_EXPRESSION;
    }

    public static String toString(EIScriptDeclaration declaration) {
//        return EIScriptNamingUtil.NAME_SCRIPT_DECL + declaration.getName();
        return declaration.getName();
    }

    public static String toString(EIGlobalVar globalVar) {
//        return EIScriptNamingUtil.NAME_GLOBAL_VAR + globalVar.getName();
        return globalVar.getName();
    }

    public static String toString(EIFunctionCall functionCall) {
//        return EIScriptNamingUtil.NAME_FUNCTION_CALL + functionCall.getScriptIdentifier().getText();
        return functionCall.getScriptIdentifier().getText();
    }

    public static String toString(EIVariableAccess variableAccess) {
//        return EIScriptNamingUtil.NAME_VAR_ACCESS + variableAccess.getScriptIdentifier().getText();
        return variableAccess.getScriptIdentifier().getText();
    }

    public static String toString(EIAssignment assignment) {
//        return EIScriptNamingUtil.NAME_ASSIGNMENT + assignment.getScriptIdentifier().getText();
        return assignment.getScriptIdentifier().getText();
    }

    public static String toString(EIScriptExpression scriptExpression) {
        return EIScriptNamingUtil.NAME_SCRIPT_EXPRESSION;
    }

    public static String toString(EIScriptBlock scriptBlock) {
        return EIScriptNamingUtil.NAME_SCRIPT_BLOCK;
    }

    public static String toString(EIScriptIfBlock scriptIfBlock) {
        return EIScriptNamingUtil.NAME_SCRIPT_IF_BLOCK;
    }

    public static String toString(EIScriptThenBlock scriptThenBlock) {
        return EIScriptNamingUtil.NAME_SCRIPT_THEN_BLOCK;
    }

    public static String toString(EIGlobalVars globalVars) {
        return EIScriptNamingUtil.NAME_GLOBALVARS;
    }

    public static String toString(EIDeclarations declarations) {
        return EIScriptNamingUtil.NAME_DECLARATIONS;
    }

    public static String toString(EIScripts scripts) {
        return EIScriptNamingUtil.NAME_SCRIPTS;
    }

    public static String toString(EIWorldScript worldScript) {
        return EIScriptNamingUtil.NAME_WORLDSCRIPT;
    }

}