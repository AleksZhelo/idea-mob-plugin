package com.alekseyzhelo.evilislands.mobplugin.script.psi.impl;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.references.FunctionCallReference;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.references.VariableReference;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EITypeToken;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiReference;

public class EIScriptPsiImplUtil {

    public static PsiReference getReference(EIAssignment assignment) {
        EIScriptIdentifier identifier = assignment.getScriptIdentifier();
        return new VariableReference(identifier, TextRange.create(0, identifier.getTextLength()));
    }

    public static PsiReference getReference(EIFunctionCall call) {
        EIScriptIdentifier ident = call.getScriptIdentifier();
        return new FunctionCallReference(ident, TextRange.create(0, ident.getTextLength()));
    }

    public static PsiReference getReference(EIScriptImplementation impl) {
        EIScriptIdentifier ident = impl.getScriptIdentifier();
        return new FunctionCallReference(ident, TextRange.create(0, ident.getTextLength()), true);
    }

    public static PsiReference getReference(EIVariableAccess variable) {
        EIScriptIdentifier identifier = variable.getScriptIdentifier();
        return new VariableReference(identifier, new TextRange(0, identifier.getTextLength()));
    }

    public static EITypeToken getDisplayableType(EIFunctionDeclaration element) {
        if (element.getType() != null) {
            return element.getType().getTypeToken();
        } else {
            return EITypeToken.VOID;
        }
    }

    // UNUSED
    public static String getType(EIGlobalVar element) {
        ASTNode valueNode = element.getNode().findChildByType(ScriptTypes.TYPE);
        if (valueNode != null) {
            return valueNode.getText();
        } else {
            return null;
        }
    }
}