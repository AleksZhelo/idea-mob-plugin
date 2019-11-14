package com.alekseyzhelo.evilislands.mobplugin.script.formatting;

import com.alekseyzhelo.evilislands.mobplugin.script.EIScriptLanguage;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptTypes;
import com.intellij.formatting.SpacingBuilder;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.tree.TokenSet;

final class EISpacingBuilderProvider {

    private EISpacingBuilderProvider() {
    }

    // TODO: bother with custom settings?
    static SpacingBuilder createSpacingBuilder(CodeStyleSettings settings) {
        return new SpacingBuilder(settings, EIScriptLanguage.INSTANCE)
                .around(TokenSet.create(ScriptTypes.COLON, ScriptTypes.EQUALS)).spaces(1)
                .before(ScriptTypes.COMMA).spaces(0)
                .after(ScriptTypes.COMMA).spaces(1)
                .after(ScriptTypes.LPAREN).spaces(0)
                .before(ScriptTypes.RPAREN).spaces(0)
                .beforeInside(ScriptTypes.LPAREN,
                        TokenSet.create(ScriptTypes.SCRIPT_DECLARATION, ScriptTypes.GLOBAL_VARS)).spaces(1)
                .before(ScriptTypes.DECLARATIONS).blankLines(1)
                .before(ScriptTypes.SCRIPT_IMPLEMENTATION).blankLines(1)
                .before(ScriptTypes.WORLD_SCRIPT).blankLines(1)
                ;
    }
}
