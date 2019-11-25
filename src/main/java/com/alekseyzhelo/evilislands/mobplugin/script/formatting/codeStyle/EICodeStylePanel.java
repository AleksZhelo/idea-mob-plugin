package com.alekseyzhelo.evilislands.mobplugin.script.formatting.codeStyle;

import com.intellij.application.options.TabbedLanguageCodeStylePanel;
import com.intellij.lang.Language;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import org.jetbrains.annotations.Nullable;

class EICodeStylePanel extends TabbedLanguageCodeStylePanel {

    EICodeStylePanel(@Nullable Language language, CodeStyleSettings currentSettings, CodeStyleSettings settings) {
        super(language, currentSettings, settings);
    }

    @Override
    protected void initTabs(CodeStyleSettings settings) {
        addIndentOptionsTab(settings);
    }
}
