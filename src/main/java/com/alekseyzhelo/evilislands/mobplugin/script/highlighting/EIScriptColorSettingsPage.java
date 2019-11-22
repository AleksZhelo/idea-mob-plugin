package com.alekseyzhelo.evilislands.mobplugin.script.highlighting;

import com.alekseyzhelo.evilislands.mobplugin.EIMessages;
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
            new AttributesDescriptor(EIMessages.message("color.settings.equals"), EQUALS),
            new AttributesDescriptor(EIMessages.message("color.settings.comma"), COMMA),
            new AttributesDescriptor(EIMessages.message("color.settings.parentheses"), PARENTHESES),
            new AttributesDescriptor(EIMessages.message("color.settings.keyword"), KEYWORD),
            new AttributesDescriptor(EIMessages.message("color.settings.type"), TYPE),
            new AttributesDescriptor(EIMessages.message("color.settings.string"), STRING),
            new AttributesDescriptor(EIMessages.message("color.settings.number"), NUMBER),
            new AttributesDescriptor(EIMessages.message("color.settings.identifier"), IDENTIFIER),
            new AttributesDescriptor(EIMessages.message("color.settings.functionCall"), FUNCTION_CALL),
            new AttributesDescriptor(EIMessages.message("color.settings.variable"), VARIABLE_ACCESS),
            new AttributesDescriptor(EIMessages.message("color.settings.comment"), COMMENT),
    };

    private static final Map<String, TextAttributesKey> ATTRIBUTES_KEY_MAP = new HashMap<>();

    static {
        ATTRIBUTES_KEY_MAP.put("fc", FUNCTION_CALL);
        ATTRIBUTES_KEY_MAP.put("va", VARIABLE_ACCESS);
    }

    @Nullable
    @Override
    public Icon getIcon() {
        // TODO v2: draw icon?
        return null;
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
        return EIMessages.message("lang.display.name");
    }
}