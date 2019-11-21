package com.alekseyzhelo.evilislands.mobplugin.script.formatting;

import com.alekseyzhelo.evilislands.mobplugin.script.EIScriptLanguage;
import com.google.common.collect.Lists;
import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptTypes.*;
import static com.intellij.psi.TokenType.WHITE_SPACE;

// TODO v2: alignment?
class EIBlock extends AbstractBlock implements BlockEx {

    private static final TokenSet WRAP_TOP_LEVEL = TokenSet.create(
            GLOBAL_VARS,
            GLOBAL_VAR,
            SCRIPT_DECLARATION,
            SCRIPT_IMPLEMENTATION,
            SCRIPT_IF_BLOCK,
            SCRIPT_THEN_BLOCK,
            WORLD_SCRIPT
    );

    private static final TokenSet WRAP_SECOND_LEVEL = TokenSet.create(
            LPAREN,
            RPAREN,
            FUNCTION_CALL,
            ASSIGNMENT,
            CALL_STATEMENT,
            FOR_BLOCK
    );

    final SpacingBuilder mySpacingBuilder;
    private final Indent myIndent;

    EIBlock(@NotNull ASTNode node, @Nullable Wrap wrap, @Nullable Alignment alignment, SpacingBuilder spacingBuilder) {
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
                            shouldWrap(childType) ? Wrap.createWrap(WrapType.ALWAYS, true) : null,
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

    private boolean shouldWrap(IElementType childType) {
        IElementType elementType = getElementType();
        if (GLOBAL_VARS.equals(elementType) && LPAREN.equals(childType)) {
            return false;
        } else {
            return WRAP_TOP_LEVEL.contains(childType) ||
                    (!elementType.equals(SCRIPT_DECLARATION) &&
                            WRAP_TOP_LEVEL.contains(elementType) && WRAP_SECOND_LEVEL.contains(childType));
        }
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
