package com.alekseyzhelo.evilislands.mobplugin.script.psi.impl;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.references.GlobalVariableReference;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.references.ScriptDefinitionReference;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptResolveUtil;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiReference;

import java.util.ArrayList;
import java.util.List;

public class EIScriptPsiImplUtil {

    public static PsiReference getReference(EIScriptImplementation impl) {
        EIScriptIdentifier ident = impl.getScriptIdentifier();
        ScriptFile file = (ScriptFile) impl.getContainingFile();

        EIScriptDeclaration declaration = EIScriptResolveUtil.findScriptDeclaration(file, ident.getText());
        return declaration != null
                ? new ScriptDefinitionReference(ident, TextRange.create(0, ident.getTextLength()))
                : null;
    }

    // TODO: rewrite?
    public static PsiReference getReference(EIVariableAccess variable) {
        List<PsiReference> references = new ArrayList<>();
        ScriptFile file = (ScriptFile) variable.getContainingFile();
        List<EIGlobalVar> globals = EIScriptResolveUtil.findGlobalVars(file);
        for (EIGlobalVar global : globals) {
            String text = global.getName();
            if (text != null && text.equals(variable.getText())) {
                references.add(
                        new GlobalVariableReference(variable, new TextRange(0, text.length()))
                );
            }
        }

        return references.size() == 1 ? references.get(0) : null;
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