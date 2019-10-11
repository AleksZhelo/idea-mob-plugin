package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.util;

import com.alekseyzhelo.evilislands.mobplugin.script.highlighting.EIScriptSyntaxHighlightingColors;
import com.intellij.lang.documentation.DocumentationMarkup;

public final class DocumentationFormatter {

    private DocumentationFormatter() {

    }

    public static String bold(String str) {
        return "<b>" + str + "</b>";
    }

    // TODO: improve?
    public static String wrapKeyword(String keyword) {
        String keywordColor = Integer.toHexString(EIScriptSyntaxHighlightingColors.KEYWORD.getDefaultAttributes().getForegroundColor().getRGB());
        return String.format("<span style=\"color: #%s;\">%s</span>", keywordColor, bold(keyword));
    }

    public static String wrapDefinition(String definition) {
        return DocumentationMarkup.DEFINITION_START + definition + DocumentationMarkup.DEFINITION_END;
    }

    public static String wrapContent(String content) {
        return DocumentationMarkup.CONTENT_START + content + DocumentationMarkup.CONTENT_END;
    }
}
