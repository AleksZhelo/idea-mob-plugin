package com.alekseyzhelo.evilislands.mobplugin.script;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptTypes;
import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;
 
import java.awt.*;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;
 
public class EIScriptSyntaxHighlighter extends SyntaxHighlighterBase {

    public static final TextAttributesKey SEPARATOR =
            createTextAttributesKey("SCRIPT_SEPARATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN);
    public static final TextAttributesKey KEYWORD =
            createTextAttributesKey("SCRIPT_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey STRING =
            createTextAttributesKey("SCRIPT_STRING", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey COMMENT =
            createTextAttributesKey("SCRIPT_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
 
    static final TextAttributesKey BAD_CHARACTER = createTextAttributesKey("SCRIPT_BAD_CHARACTER",
            new TextAttributes(JBColor.RED, null, null, null, Font.BOLD));

    private static final IElementType[] KEYWORDS_ARRAY = {
            ScriptTypes.GLOBALVARS,
            ScriptTypes.DECLARESCRIPT,
            ScriptTypes.SCRIPT,
            ScriptTypes.IF,
            ScriptTypes.THEN,
            ScriptTypes.FOR,
            ScriptTypes.WORLDSCRIPT
    };
    private static final Set<IElementType> KEYWORDS = new HashSet<>(Arrays.asList(KEYWORDS_ARRAY));
 
    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
    private static final TextAttributesKey[] SEPARATOR_KEYS = new TextAttributesKey[]{SEPARATOR};
    private static final TextAttributesKey[] KEYWORD_KEYS = new TextAttributesKey[]{KEYWORD};
    private static final TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{STRING};
    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];
 
    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new FlexAdapter(new EIScriptLexer((Reader) null));
    }
 
    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (tokenType.equals(ScriptTypes.EQUALS)) {
            return SEPARATOR_KEYS;
        } else if (KEYWORDS.contains(tokenType)) {
            return KEYWORD_KEYS;
        } else if (tokenType.equals(ScriptTypes.CHARACTER_STRING)) {
            return STRING_KEYS;
        } else if (tokenType.equals(ScriptTypes.COMMENT)) {
            return COMMENT_KEYS;
        } else if (tokenType.equals(TokenType.BAD_CHARACTER)) {
            return BAD_CHAR_KEYS;
        } else {
            return EMPTY_KEYS;
        }
    }
}