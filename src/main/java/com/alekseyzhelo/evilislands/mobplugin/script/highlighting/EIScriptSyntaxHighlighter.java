package com.alekseyzhelo.evilislands.mobplugin.script.highlighting;

import com.alekseyzhelo.evilislands.mobplugin.script.EIScriptParserDefinition;
import com.alekseyzhelo.evilislands.mobplugin.script.lexer.EILexer;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptTypes;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import static com.alekseyzhelo.evilislands.mobplugin.script.highlighting.EIScriptSyntaxHighlightingColors.*;

public class EIScriptSyntaxHighlighter extends SyntaxHighlighterBase {

    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
    private static final TextAttributesKey[] EQUALS_KEYS = new TextAttributesKey[]{EQUALS};
    private static final TextAttributesKey[] COMMA_KEYS = new TextAttributesKey[]{COMMA};
    private static final TextAttributesKey[] PARENTHESES_KEYS = new TextAttributesKey[]{PARENTHESES};
    private static final TextAttributesKey[] KEYWORD_KEYS = new TextAttributesKey[]{KEYWORD};
    private static final TextAttributesKey[] TYPE_KEYS = new TextAttributesKey[]{TYPE};
    private static final TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{STRING};
    private static final TextAttributesKey[] NUMBER_KEYS = new TextAttributesKey[]{NUMBER};
    private static final TextAttributesKey[] IDENTIFIER_KEYS = new TextAttributesKey[]{IDENTIFIER};
    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new EILexer();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (tokenType.equals(ScriptTypes.EQUALS)) {
            return EQUALS_KEYS;
        } else if (tokenType.equals(ScriptTypes.COMMA)) {
            return COMMA_KEYS;
        } else if (EIScriptParserDefinition.PARENS.contains(tokenType)) {
            return PARENTHESES_KEYS;
        } else if (EIScriptParserDefinition.KEYWORDS.contains(tokenType)) {
            return KEYWORD_KEYS;
        } else if (EIScriptParserDefinition.TYPES.contains(tokenType)) {
            return TYPE_KEYS;
        } else if (EIScriptParserDefinition.STRING_LITERALS.contains(tokenType)) {
            return STRING_KEYS;
        } else if (EIScriptParserDefinition.NUMERIC_LITERALS.contains(tokenType)) {
            return NUMBER_KEYS;
        } else if (tokenType.equals(ScriptTypes.IDENTIFIER)) {
            return IDENTIFIER_KEYS;
        } else if (EIScriptParserDefinition.COMMENTS.contains(tokenType)) {
            return COMMENT_KEYS;
        } else if (tokenType.equals(TokenType.BAD_CHARACTER)) {
            return BAD_CHAR_KEYS;
        } else {
            return EMPTY_KEYS;
        }
    }
}