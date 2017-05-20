package com.alekseyzhelo.evilislands.mobplugin.script.psi.impl;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.references.FunctionCallReference;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.references.VariableReference;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EITypeToken;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EIScriptPsiImplUtil {

    @NotNull
    public static PsiReference getReference(EIAssignment assignment) {
        EIScriptIdentifier identifier = assignment.getScriptIdentifier();
        return new VariableReference(identifier, TextRange.create(0, identifier.getTextLength()));
    }

    @NotNull
    public static PsiReference getReference(EIFunctionCall call) {
        EIScriptIdentifier ident = call.getScriptIdentifier();
        return new FunctionCallReference(ident, TextRange.create(0, ident.getTextLength()));
    }

    @NotNull
    public static PsiReference getReference(EIScriptImplementation impl) {
        EIScriptIdentifier ident = impl.getScriptIdentifier();
        return new FunctionCallReference(ident, TextRange.create(0, ident.getTextLength()), true);
    }

    @NotNull
    public static PsiReference getReference(EIVariableAccess variable) {
        EIScriptIdentifier identifier = variable.getScriptIdentifier();
        return new VariableReference(identifier, new TextRange(0, identifier.getTextLength()));
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
        PsiElement resolved = element.getReference() != null ? element.getReference().resolve() : null;
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
        PsiElement resolved = element.getReference() != null ? element.getReference().resolve() : null;
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
}