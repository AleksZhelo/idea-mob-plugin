package com.alekseyzhelo.evilislands.mobplugin.script.highlighting;

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.ui.JBColor;

import java.awt.*;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

/**
 * @author Aleks on 31-01-2016.
 */
public class EIScriptSyntaxHighlightingColors {
    public static final TextAttributesKey EQUALS =
            createTextAttributesKey("SCRIPT_SEPARATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN);
    public static final TextAttributesKey COMMA =
            createTextAttributesKey("SCRIPT_COMMA", DefaultLanguageHighlighterColors.COMMA);
    public static final TextAttributesKey PARENTHESES =
            createTextAttributesKey("SCRIPT_PARENTHESES", DefaultLanguageHighlighterColors.PARENTHESES);
    public static final TextAttributesKey KEYWORD =
            createTextAttributesKey("SCRIPT_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey TYPE =
            //createTextAttributesKey("SCRIPT_TYPE", TextAttributesKey.createTextAttributesKey("SCRIPT_TYPE")); // better something like this?
            createTextAttributesKey("SCRIPT_TYPE", new TextAttributes(JBColor.decode("#b10000"), null, null, null, Font.BOLD));
    public static final TextAttributesKey STRING =
            createTextAttributesKey("SCRIPT_STRING", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey NUMBER =
            createTextAttributesKey("SCRIPT_NUMBER", DefaultLanguageHighlighterColors.NUMBER);
    public static final TextAttributesKey IDENTIFIER =
            createTextAttributesKey("SCRIPT_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER);
    public static final TextAttributesKey FUNCTION_CALL =
            createTextAttributesKey("SCRIPT_FUNCTION_CALL", DefaultLanguageHighlighterColors.FUNCTION_CALL);
    public static final TextAttributesKey VARIABLE_ACCESS =
            createTextAttributesKey("SCRIPT_VARIABLE", DefaultLanguageHighlighterColors.LOCAL_VARIABLE);
    public static final TextAttributesKey COMMENT =
            createTextAttributesKey("SCRIPT_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);

    static final TextAttributesKey BAD_CHARACTER = createTextAttributesKey("SCRIPT_BAD_CHARACTER",
            new TextAttributes(JBColor.RED, null, null, null, Font.BOLD));
}
