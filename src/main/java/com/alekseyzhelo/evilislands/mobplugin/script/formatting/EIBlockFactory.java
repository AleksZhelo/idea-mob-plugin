package com.alekseyzhelo.evilislands.mobplugin.script.formatting;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class EIBlockFactory {

    private EIBlockFactory() {
    }

    @NotNull
    static Block createEIBlock(@NotNull PsiElement element, CodeStyleSettings settings) {
        return createEIBlock(
                element.getNode(),
                Wrap.createWrap(WrapType.NONE, false),
                Alignment.createAlignment(),
                EISpacingBuilderProvider.createSpacingBuilder(settings)
        );
    }

    @NotNull
    static Block createEIBlock(
            @NotNull ASTNode node,
            @Nullable Wrap wrap,
            @Nullable Alignment alignment,
            SpacingBuilder spacingBuilder
    ) {
        return new SimpleEIBlock(node, wrap, alignment, spacingBuilder);
    }

}
