package com.alekseyzhelo.evilislands.mobplugin.script.util;


import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIScriptIdentifier;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;

public final class EIScriptRenameUtil {

    public static PsiElement renameElement(PsiElement element, String newElementName) {
        final EIScriptIdentifier identifierNew =
                EIScriptElementFactory.createIdentifierFromText(element.getProject(), newElementName);
        if (element instanceof EIScriptIdentifier && element.getParent() != null) {
            element.getParent().getNode().replaceChild(element.getNode(), identifierNew.getNode());
        } else {
            final EIScriptIdentifier identifier = PsiTreeUtil.getChildOfType(element, EIScriptIdentifier.class);
            if (identifier != null && identifierNew != null) {
                element.getNode().replaceChild(identifier.getNode(), identifierNew.getNode());
            }
        }
        return element;
    }

    private EIScriptRenameUtil() {

    }

}
