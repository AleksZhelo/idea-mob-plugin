package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.completion;

import com.intellij.codeInsight.completion.CompletionConfidence;
import com.intellij.codeInsight.completion.SkipAutopopupInStrings;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.ThreeState;
import org.jetbrains.annotations.NotNull;

public class CompletionConfidenceOverride extends CompletionConfidence {

    @NotNull
    @Override
    public ThreeState shouldSkipAutopopup(@NotNull PsiElement contextElement, @NotNull PsiFile psiFile, int offset) {
        if (SkipAutopopupInStrings.isInStringLiteral(contextElement)) {
            return ThreeState.NO;
        } else {
            return ThreeState.UNSURE;
        }
    }
}
