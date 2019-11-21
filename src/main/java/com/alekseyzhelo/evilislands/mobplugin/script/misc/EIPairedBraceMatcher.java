package com.alekseyzhelo.evilislands.mobplugin.script.misc;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptTypes;
import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EIPairedBraceMatcher implements PairedBraceMatcher {

    private final BracePair[] pairs = new BracePair[]{
            new BracePair(ScriptTypes.LPAREN, ScriptTypes.RPAREN, false)
    };

    @NotNull
    @Override
    public BracePair[] getPairs() {
        return pairs;
    }

    @Override
    public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType lbraceType, @Nullable IElementType contextType) {
        // TODO v2: implement?
        return true;
    }

    @Override
    public int getCodeConstructStart(PsiFile file, int openingBraceOffset) {
        PsiElement element = file.findElementAt(openingBraceOffset);
        if (element == null || element instanceof PsiFile) return openingBraceOffset;

        PsiElement parent = element.getParent();
        if (parent != null) {
            return parent.getTextOffset();
        } else {
            return openingBraceOffset;
        }
    }
}
