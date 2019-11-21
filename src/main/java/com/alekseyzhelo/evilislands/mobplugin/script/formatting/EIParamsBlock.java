package com.alekseyzhelo.evilislands.mobplugin.script.formatting;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptTypes;
import com.intellij.formatting.Alignment;
import com.intellij.formatting.SpacingBuilder;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EIParamsBlock extends EIBlock {

    EIParamsBlock(@NotNull ASTNode node, @Nullable Wrap wrap, @Nullable Alignment alignment, SpacingBuilder spacingBuilder) {
        super(node, wrap, alignment, spacingBuilder);
    }

    @Override
    public boolean isIncomplete() {
        return super.isIncomplete() || getNode().findChildByType(ScriptTypes.RPAREN) == null;
    }
}
