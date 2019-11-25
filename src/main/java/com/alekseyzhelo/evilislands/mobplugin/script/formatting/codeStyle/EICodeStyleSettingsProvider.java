package com.alekseyzhelo.evilislands.mobplugin.script.formatting.codeStyle;

import com.alekseyzhelo.evilislands.mobplugin.IOUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.EIScriptLanguage;
import com.intellij.application.options.CodeStyleAbstractConfigurable;
import com.intellij.application.options.CodeStyleAbstractPanel;
import com.intellij.application.options.IndentOptionsEditor;
import com.intellij.lang.Language;
import com.intellij.psi.codeStyle.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;


// TODO: finish: spaces, blank lines, etc
public class EICodeStyleSettingsProvider extends LanguageCodeStyleSettingsProvider {
    @Nullable
    @Override
    public String getCodeSample(@NotNull SettingsType settingsType) {
        return IOUtil.readTextFromResources("/code_style_demo.txt");
    }

    @NotNull
    @Override
    public Language getLanguage() {
        return EIScriptLanguage.INSTANCE;
    }

    @NotNull
    @Override
    public CodeStyleConfigurable createConfigurable(@NotNull CodeStyleSettings baseSettings, @NotNull CodeStyleSettings modelSettings) {
        return new CodeStyleAbstractConfigurable(baseSettings, modelSettings, EIScriptLanguage.INSTANCE.getDisplayName()) {
            @Override
            protected CodeStyleAbstractPanel createPanel(CodeStyleSettings settings) {
                return new EICodeStylePanel(getLanguage(), getCurrentSettings(), settings);
            }
        };
    }

    @Override
    protected void customizeDefaults(@NotNull CommonCodeStyleSettings commonSettings, @NotNull CommonCodeStyleSettings.IndentOptions indentOptions) {
        indentOptions.INDENT_SIZE = 2;
        indentOptions.TAB_SIZE = 2;
        indentOptions.USE_TAB_CHARACTER = false;
    }

    @Nullable
    @Override
    public CustomCodeStyleSettings createCustomSettings(CodeStyleSettings settings) {
        return super.createCustomSettings(settings);
    }

    @Nullable
    @Override
    public IndentOptionsEditor getIndentOptionsEditor() {
        // TODO: remove "use tab character"
        return new IndentOptionsEditor();
    }

    @Override
    public Set<String> getSupportedFields() {
        Set<String> supported = super.getSupportedFields();
        supported.remove(CodeStyleSettingsCustomizable.IndentOption.USE_TAB_CHARACTER.toString());
        return supported;
    }

    @Override
    public Set<String> getSupportedFields(SettingsType type) {
        Set<String> supported = super.getSupportedFields(type);
        return supported;
    }
}
