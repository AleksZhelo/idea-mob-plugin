package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.util;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFunctionDeclaration;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.completion.util.ParenthesesInsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;

// TODO: remove unused?
public class EIFunctionParenthesesHandler extends ParenthesesInsertHandler<LookupElement> {
    private final EIFunctionDeclaration function;

    public EIFunctionParenthesesHandler(final EIFunctionDeclaration functionDeclaration) {
        function = functionDeclaration;
    }

    @Override
    protected boolean placeCaretInsideParentheses(final InsertionContext context, final LookupElement item) {
        return !function.getFormalParameterList().isEmpty();
    }
}
