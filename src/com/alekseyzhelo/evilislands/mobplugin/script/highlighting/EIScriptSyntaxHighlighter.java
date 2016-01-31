package com.alekseyzhelo.evilislands.mobplugin.script.highlighting;

import com.alekseyzhelo.evilislands.mobplugin.script.lexer.EILexer;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptTypes;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.alekseyzhelo.evilislands.mobplugin.script.highlighting.EIScriptSyntaxHighlightingColors.*;

// GoSyntaxHighlighter is just so much more succinct, investigate

public class EIScriptSyntaxHighlighter extends SyntaxHighlighterBase {

    private static final IElementType[] KEYWORDS_ARRAY = {
            ScriptTypes.GLOBALVARS,
            ScriptTypes.DECLARESCRIPT,
            ScriptTypes.SCRIPT,
            ScriptTypes.IF,
            ScriptTypes.THEN,
            ScriptTypes.FOR,
            ScriptTypes.WORLDSCRIPT
    };

    private static final IElementType[] TYPES_ARRAY = {
            ScriptTypes.OBJECT,
            ScriptTypes.GROUP,
            ScriptTypes.STRING,
            ScriptTypes.FLOAT,
    };

    private static final Set<IElementType> KEYWORDS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(KEYWORDS_ARRAY)));
    private static final Set<IElementType> TYPES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(TYPES_ARRAY)));

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
        } else if (tokenType.equals(ScriptTypes.LPAREN) || tokenType.equals(ScriptTypes.RPAREN)) {
            return PARENTHESES_KEYS;
        } else if (KEYWORDS.contains(tokenType)) {
            return KEYWORD_KEYS;
        } else if (TYPES.contains(tokenType)) {
            return TYPE_KEYS;
        } else if (tokenType.equals(ScriptTypes.CHARACTER_STRING)) {
            return STRING_KEYS;
        } else if (tokenType.equals(ScriptTypes.FLOATNUMBER)) {
            return NUMBER_KEYS;
        } else if (tokenType.equals(ScriptTypes.SCRIPT_IDENTIFIER)) {
            return IDENTIFIER_KEYS;
        } else if (tokenType.equals(ScriptTypes.COMMENT)) {
            return COMMENT_KEYS;
        } else if (tokenType.equals(TokenType.BAD_CHARACTER)) {
            return BAD_CHAR_KEYS;
        } else {
            return EMPTY_KEYS;
        }
    }
}