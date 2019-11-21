package com.alekseyzhelo.evilislands.mobplugin.script.util;


import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.util.IncorrectOperationException;

public final class EIScriptRenameUtil {

    public static PsiNameIdentifierOwner renameElement(PsiNameIdentifierOwner element, String newElementName)
            throws IncorrectOperationException {
        final PsiElement identifierOld = element.getNameIdentifier();
        if (identifierOld != null) {
            final PsiElement identifierNew =
                    EIScriptElementFactory.createIdentifier(element.getProject(), newElementName);
            identifierOld.replace(identifierNew);
            return element;
        } else {
            throw new IncorrectOperationException();
        }
    }

    private EIScriptRenameUtil() {

    }

}
