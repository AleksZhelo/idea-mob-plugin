package com.alekseyzhelo.evilislands.mobplugin.script.psi.impl;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIGlobalVar;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptNamedElement;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptTypes;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptElementFactory;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;

public class EIScriptPsiImplUtil {
    public static String getName(ScriptNamedElement element) {
        ASTNode keyNode = element.getNode().findChildByType(ScriptTypes.IDENTIFIER);
        if (keyNode != null) {
            return keyNode.getText();
        } else {
            return null;
        }
    }

    public static PsiElement setName(ScriptNamedElement element, String newName) {
        ASTNode keyNode = element.getNode().findChildByType(ScriptTypes.IDENTIFIER);
        if (keyNode != null) {
            EIGlobalVar globalVar = EIScriptElementFactory.createGlobalVar(element.getProject(), newName);
            ASTNode newKeyNode = globalVar.getFirstChild().getNode();
            element.getNode().replaceChild(keyNode, newKeyNode);
        }
        return element;
    }

    public static PsiElement getNameIdentifier(ScriptNamedElement element) {
        ASTNode keyNode = element.getNode().findChildByType(ScriptTypes.IDENTIFIER);
        if (keyNode != null) {
            return keyNode.getPsi();
        } else {
            return null;
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