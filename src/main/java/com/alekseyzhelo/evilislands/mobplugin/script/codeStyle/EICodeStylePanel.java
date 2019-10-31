package com.alekseyzhelo.evilislands.mobplugin.script.codeStyle;

import com.intellij.application.options.TabbedLanguageCodeStylePanel;
import com.intellij.lang.Language;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import org.jetbrains.annotations.Nullable;

// TODO: finish
class EICodeStylePanel extends TabbedLanguageCodeStylePanel {

    EICodeStylePanel(@Nullable Language language, CodeStyleSettings currentSettings, CodeStyleSettings settings) {
        super(language, currentSettings, settings);
    }

    @Override
    protected void initTabs(CodeStyleSettings settings) {
        super.initTabs(settings);
    }
}
