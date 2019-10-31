package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.completion;

import com.alekseyzhelo.evilislands.mobplugin.script.EIScriptParserDefinition;
import com.intellij.codeInsight.completion.CompletionConfidence;
import com.intellij.codeInsight.completion.SkipAutopopupInStrings;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.ThreeState;
import org.jetbrains.annotations.NotNull;

public class CompletionConfidenceOverride extends CompletionConfidence {

    @NotNull
    @Override
    public ThreeState shouldSkipAutopopup(@NotNull PsiElement contextElement, @NotNull PsiFile psiFile, int offset) {
        if (SkipAutopopupInStrings.isInStringLiteral(contextElement) || isNumberLiteral(contextElement)) {
            return ThreeState.NO;
        } else {
            return ThreeState.UNSURE;
        }
    }

    // TODO: doesn't work
    private static boolean isNumberLiteral(PsiElement element) {
        return PlatformPatterns.psiElement().withElementType(EIScriptParserDefinition.NUMERIC_LITERALS).accepts(element);
    }

}
