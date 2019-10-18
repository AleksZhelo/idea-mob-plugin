package com.alekseyzhelo.evilislands.mobplugin.script.formatting;

import com.alekseyzhelo.evilislands.mobplugin.script.EIScriptLanguage;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptTypes;
import com.intellij.formatting.SpacingBuilder;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.tree.TokenSet;

final class EISpacingBuilderProvider {

    private static final TokenSet other;

    private EISpacingBuilderProvider() {
    }

    static {
        //  test
        other = TokenSet.create(ScriptTypes.COLON, ScriptTypes.EQUALS);
    }

    // TODO: bother with custom settings?
    static SpacingBuilder createSpacingBuilder(CodeStyleSettings settings) {
        return new SpacingBuilder(settings, EIScriptLanguage.INSTANCE)
                .around(other).spaces(1)
                .after(ScriptTypes.COMMA).spaces(1)
                ;
    }
}
