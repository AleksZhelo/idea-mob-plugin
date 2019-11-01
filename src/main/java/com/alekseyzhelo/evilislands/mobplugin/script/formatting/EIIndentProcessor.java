package com.alekseyzhelo.evilislands.mobplugin.script.formatting;

import com.intellij.formatting.Indent;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

import static com.alekseyzhelo.evilislands.mobplugin.script.EIScriptParserDefinition.FILE;
import static com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptTypes.*;

final class EIIndentProcessor {

    private static TokenSet NO_INDENT_ELEMENTS = TokenSet.create(
            LPAREN,
            RPAREN,
            GLOBALVARS,
            SCRIPT,
            WORLDSCRIPT,
            IF,
            THEN,
            FOR
    );
    private static TokenSet BLOCKS = TokenSet.create(
            GLOBAL_VARS,
            SCRIPT_BLOCK, // or SCRIPT_IMPLEMENTATION?
            SCRIPT_IF_BLOCK,
            SCRIPT_THEN_BLOCK,
            WORLD_SCRIPT,
            FOR_BLOCK,
            PARAMS
    );
    private static TokenSet NO_INDENT_BLOCKS = TokenSet.create(
            DECLARATIONS,
            SCRIPTS,
            SCRIPT_DECLARATION,
            SCRIPT_IMPLEMENTATION
    );

    private EIIndentProcessor() {
    }

    static Indent getIndent(ASTNode node) {
        IElementType elementType = node.getElementType();
        ASTNode parent = node.getTreeParent();
        IElementType parentType = parent != null ? parent.getElementType() : null;

        if (parent == null || parentType == FILE) {
            return Indent.getNoneIndent();
        }

        if (elementType.equals(WHOLE_LINE_COMMENT)) {
            return Indent.getAbsoluteNoneIndent();
        }

        if (BLOCKS.contains(parentType)) {
            if (NO_INDENT_ELEMENTS.contains(elementType)) {
                return Indent.getNoneIndent();
            }
            return Indent.getNormalIndent();
        }

        if (NO_INDENT_BLOCKS.contains(parentType)) {
            return Indent.getNoneIndent();
        }

        // TODO: which one?
//        return Indent.getContinuationWithoutFirstIndent();
        return Indent.getNoneIndent();
    }

    static Indent getChildIndent(IElementType parentType) {
        if (BLOCKS.contains(parentType)) {
            return Indent.getNormalIndent();
        }
        return Indent.getNoneIndent();
    }
}
