package com.alekseyzhelo.evilislands.mobplugin.script.util;


import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptTypes;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;

public final class EIScriptRenameUtil {

    private static final TokenSet IDENTIFIER = TokenSet.create(ScriptTypes.IDENTIFIER);

    public static PsiElement renameElement(PsiElement element, String newElementName) {
        final PsiElement identifierNew =
                EIScriptElementFactory.createIdentifierFromText(element.getProject(), newElementName);
        if (element.getNode().getElementType().equals(ScriptTypes.IDENTIFIER) && element.getParent() != null) {
            element.getParent().getNode().replaceChild(element.getNode(), identifierNew.getNode());
        } else {
            // TODO: EIScriptNamedElementMixin here?
            ASTNode[] identifiers = element.getNode().getChildren(IDENTIFIER);
            if (identifiers.length > 0) {
                ASTNode identifier = identifiers[0];
                element.getNode().replaceChild(identifier, identifierNew.getNode());
            }
        }
        return element;
    }

    private EIScriptRenameUtil() {

    }

}
