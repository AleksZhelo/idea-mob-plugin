package com.alekseyzhelo.evilislands.mobplugin.script.psi.impl;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.references.FunctionCallReference;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.references.MobObjectReference;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.references.ScriptImplReference;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.references.VariableReference;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptNamingUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EITypeToken;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("unused")
public class EIScriptPsiImplUtil {

    public static final TokenSet COULD_HAVE_MOB_OBJECT_REF = TokenSet.create(
            ScriptTypes.FLOATNUMBER,
            ScriptTypes.CHARACTER_STRING
    );

//    @NotNull
//    public static PsiReference getReference(EIAssignment assignment) {
//        EIScriptIdentifier identifier = assignment.getScriptIdentifier();
//        return new VariableReference(assignment, TextRange.create(0, identifier.getTextLength()));
//    }

    @NotNull
    // TODO: cache for getReference as well?
    public static PsiReference getReference(EIFunctionCall call) {
        PsiElement ident = call.getNameIdentifier();
        return new FunctionCallReference(call, TextRange.create(0, ident.getTextLength()));
    }

    @NotNull
    public static PsiReference getReference(EIScriptImplementation impl) {
        PsiElement ident = impl.getNameIdentifier();
        return new ScriptImplReference(impl, ident != null ? ident.getTextRange().shiftLeft(impl.getTextRange().getStartOffset()) :
                TextRange.create(0, 0));
    }

    @NotNull
    public static PsiReference getReference(EIVariableAccess variable) {
        PsiElement identifier = variable.getNameIdentifier();
        return new VariableReference(variable, new TextRange(0, identifier.getTextLength()));
    }

    @Nullable
    public static PsiReference getReference(EILiteral literal) {
        ASTNode firstChild = literal.getNode().getFirstChildNode();
        final IElementType elementType = firstChild.getElementType();
//
//        if (COULD_HAVE_MOB_OBJECT_REF.contains(elementType)) {
            boolean isFloat = elementType.equals(ScriptTypes.FLOATNUMBER);
            final String functionName = isFloat ? "getobject" : "getobjectbyid";

            EIFunctionCall parentCall = PsiTreeUtil.getParentOfType(literal, EIFunctionCall.class);
            if (parentCall != null && parentCall.getName().equalsIgnoreCase(functionName)) {
                TextRange range = isFloat
                        ? new TextRange(0, firstChild.getTextLength())
                        : new TextRange(1, firstChild.getTextLength() - 1);
                return new MobObjectReference(literal, range);
            }
//        }

        return null;
    }

    @NotNull
    public static EITypeToken getActualType(EIFunctionDeclaration element) {
        if (element.getType() != null) {
            return element.getType().getTypeToken();
        } else {
            return EITypeToken.VOID;
        }
    }

    @NotNull
    public static List<EIFormalParameter> getCallableParams(EIFunctionDeclaration declaration) {
        return declaration.getFormalParameterList();
    }

    @NotNull
    public static List<EIFormalParameter> getCallableParams(EIScriptDeclaration declaration) {
        return declaration.getFormalParameterList();
    }

    @NotNull
    public static EITypeToken getCallableType(EIFunctionDeclaration declaration) {
        return declaration.getActualType();
    }

    @NotNull
    public static EITypeToken getCallableType(EIScriptDeclaration declaration) {
        return EITypeToken.VOID;
    }

    @Nullable
    public static EITypeToken getType(EIFunctionCall element) {
        PsiElement resolved = element.getReference().resolve();
        if (resolved instanceof EIFunctionDeclaration) {
            return ((EIFunctionDeclaration) resolved).getActualType();
        } else if (resolved instanceof EIScriptDeclaration) {
            return EITypeToken.VOID;
        } else {
            return null;
        }
    }

    @Nullable
    public static EITypeToken getType(EIVariableAccess element) {
        PsiElement resolved = element.getReference().resolve();
        EIType type = null;
        if (resolved instanceof EIGlobalVar) {
            type = ((EIGlobalVar) resolved).getType();
        } else if (resolved instanceof EIFormalParameter) {
            type = ((EIFormalParameter) resolved).getType();
        }
        return type != null ? type.getTypeToken() : null;
    }

    @Nullable
    public static EITypeToken getType(EILiteral element) {
        ASTNode firstChild = element.getNode().getFirstChildNode();
        final IElementType elementType = firstChild.getElementType();
        if (elementType == ScriptTypes.FLOATNUMBER) {
            return EITypeToken.FLOAT;
        } else if (elementType == ScriptTypes.CHARACTER_STRING) {
            return EITypeToken.STRING;
        } else {
            return null;
        }
    }

    public static EITypeToken getType(EIAssignment element) {
        return EITypeToken.VOID;
    }

    @Nullable
    public static EITypeToken getType(EIExpressionStatement element) {
        return element.getExpression().getType();
    }

    @NotNull
    public static EITypeToken getType(EIForBlock element) {
        return EITypeToken.VOID;
    }

    @Nullable
    public static PsiElement getNthArgument(EIFunctionCall call, int n) {
        EIParams params = call.getParams();
        if (params != null) {
            List<EIExpression> expressions = params.getExpressionList();
            if (expressions.size() > n) {
                return expressions.get(n);
            }
        }
        return null;
    }

    // toString block

    public static String toString(EIExpression expression) {
        return EIScriptNamingUtil.NAME_EXPRESSION;
    }

    public static String toString(EIScriptDeclaration declaration) {
        return EIScriptNamingUtil.NAME_SCRIPT_DECL + declaration.getName();
//        return declaration.getName();
    }

    public static String toString(EIScriptImplementation implementation) {
        return EIScriptNamingUtil.NAME_SCRIPT_IMPL + implementation.getName();
//        return implementation.getName();
    }

    public static String toString(EIGlobalVar globalVar) {
        return EIScriptNamingUtil.NAME_GLOBAL_VAR + globalVar.getName();
//        return globalVar.getName();
    }

    public static String toString(EIFunctionCall functionCall) {
        return EIScriptNamingUtil.NAME_FUNCTION_CALL + functionCall.getName();
//        return functionCall.getName();
    }

    public static String toString(EIVariableAccess variableAccess) {
        return EIScriptNamingUtil.NAME_VAR_ACCESS + variableAccess.getName();
    }

    public static String toString(EIAssignment assignment) {
        return EIScriptNamingUtil.NAME_ASSIGNMENT + assignment.getExpressionList().get(0).getText();
    }

    public static String toString(EIScriptStatement scriptStatement) {
        return EIScriptNamingUtil.NAME_SCRIPT_STATEMENT;
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