package com.alekseyzhelo.evilislands.mobplugin.script.highlighting;

import com.alekseyzhelo.evilislands.mobplugin.IOUtil;
import com.alekseyzhelo.evilislands.mobplugin.icon.Icons;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

import static com.alekseyzhelo.evilislands.mobplugin.script.highlighting.EIScriptSyntaxHighlightingColors.*;

public class EIScriptColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Equals sign", EQUALS),
            new AttributesDescriptor("Comma", COMMA),
            new AttributesDescriptor("Parentheses", PARENTHESES),
            new AttributesDescriptor("Keyword", KEYWORD),
            new AttributesDescriptor("Type", TYPE),
            new AttributesDescriptor("String", STRING),
            new AttributesDescriptor("Number", NUMBER),
            new AttributesDescriptor("Identifier", IDENTIFIER),
            new AttributesDescriptor("Function call", FUNCTION_CALL),
            new AttributesDescriptor("Variable access", VARIABLE_ACCESS),
            new AttributesDescriptor("Comment", COMMENT),
    };

    private static final Map<String, TextAttributesKey> ATTRIBUTES_KEY_MAP = new HashMap<>();

    static {
        ATTRIBUTES_KEY_MAP.put("fc", FUNCTION_CALL);
        ATTRIBUTES_KEY_MAP.put("va", VARIABLE_ACCESS);
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return Icons.FILE;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new EIScriptSyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return IOUtil.readTextFromResources("/color_settings_demo.txt");
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return ATTRIBUTES_KEY_MAP;
    }

    @NotNull
    @Override
    public AttributesDescriptor[] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @NotNull
    @Override
    public ColorDescriptor[] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "EIScript";
    }
}