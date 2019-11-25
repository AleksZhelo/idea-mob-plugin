package com.alekseyzhelo.evilislands.mobplugin.script.formatting.codeStyle;

import com.intellij.application.options.IndentOptionsEditor;

class EIIndentOptionsEditor extends IndentOptionsEditor {

    @Override
    public void showAllStandardOptions() {
        super.showAllStandardOptions();
    }

    @Override
    public void showStandardOptions(String... optionNames) {
        super.showStandardOptions(optionNames);
        myCbUseTab.setEnabled(false);
        myCbUseTab.setVisible(false);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        myCbUseTab.setEnabled(false);
    }

    @Override
    protected void setVisible(boolean visible) {
        super.setVisible(visible);
        myCbUseTab.setVisible(false);
    }
}
