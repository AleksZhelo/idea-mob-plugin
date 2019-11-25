package com.alekseyzhelo.evilislands.mobplugin.script.formatting.codeStyle;

import com.intellij.configurationStore.Property;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;

public class EICodeStyleSettings extends CustomCodeStyleSettings {

    public EICodeStyleSettings(CodeStyleSettings container) {
        super("EICodeStyleSettings", container);
    }

    @Property(externalName = "whitespace_in_parentheses")
    public boolean EI_WS_IN_PARENTHESES = false;


}
