package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.util;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptTypes;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;

import java.util.List;

public class EIParenthesisedListInsertUtil {

    @SuppressWarnings("unchecked")
    public static <T extends PsiElement> T insertElement(PsiElement listHolder, List<T> list, T element) {
        T result = null;
        if (list.size() > 0) {
            result = (T) listHolder.addAfter(element, list.get(list.size() - 1));
            listHolder.getNode().addLeaf(ScriptTypes.COMMA, ",", result.getNode());
        } else {
            ASTNode lParen = listHolder.getNode().findChildByType(ScriptTypes.LPAREN);
            if (lParen != null) {
                result = (T) listHolder.addAfter(element, lParen.getPsi());
            }
        }
        return result;
    }
}
