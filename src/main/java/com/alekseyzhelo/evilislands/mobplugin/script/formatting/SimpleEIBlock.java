package com.alekseyzhelo.evilislands.mobplugin.script.formatting;

import com.alekseyzhelo.evilislands.mobplugin.script.EIScriptLanguage;
import com.google.common.collect.Lists;
import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.intellij.psi.TokenType.WHITE_SPACE;

// TODO: alignment?
// TODO: add a function block with nice argument indentation?
class SimpleEIBlock extends AbstractBlock implements BlockEx {

    final SpacingBuilder mySpacingBuilder;
    private final Indent myIndent;

    SimpleEIBlock(@NotNull ASTNode node, @Nullable Wrap wrap, @Nullable Alignment alignment, SpacingBuilder spacingBuilder) {
        super(node, wrap, alignment);
        mySpacingBuilder = spacingBuilder;
        myIndent = EIIndentProcessor.getIndent(node);
    }

    @Override
    protected List<Block> buildChildren() {
        List<Block> blocks = Lists.newArrayList();
//        Alignment childAlignment = Alignment.createChildAlignment(getAlignment());
        Alignment childAlignment = Alignment.createAlignment(false);

        for (ASTNode child = getNode().getFirstChildNode(); child != null; child = child.getTreeNext()) {
            IElementType childType = child.getElementType();

            if (child.getTextLength() == 0 || childType == WHITE_SPACE) {
                continue;
            }
            blocks.add(
                    EIBlockFactory.createEIBlock(
                            child,
                            myWrap,
                            null,
//                            EIIndentProcessor.NO_INDENT_ELEMENTS.contains(childType) || childType.equals(ScriptTypes.COMMENT)
//                                    ? null
//                                    : null, // : childAlignment is bad, don't understand why
                            mySpacingBuilder
                    )
            );
        }
        return blocks;
    }

    @Override
    // TODO:?
    // TODO: detect incomplete function call?
    public boolean isIncomplete() {
        return super.isIncomplete();
    }

    @Nullable
    @Override
    public Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
        return mySpacingBuilder.getSpacing(this, child1, child2);
    }

    @Override
    public Indent getIndent() {
        return myIndent;
    }

    @Override
    public boolean isLeaf() {
        return myNode.getFirstChildNode() == null;
    }

    @Nullable
    @Override
    // TODO:
    protected Indent getChildIndent() {
        return EIIndentProcessor.getChildIndent(getElementType());
    }

    @NotNull
    @Override
    public ChildAttributes getChildAttributes(int newChildIndex) {
        return new ChildAttributes(getChildIndent(), null);
    }

    @Nullable
    @Override
    public Language getLanguage() {
        return EIScriptLanguage.INSTANCE;
    }

    public IElementType getElementType() {
        return myNode.getElementType();
    }
}
