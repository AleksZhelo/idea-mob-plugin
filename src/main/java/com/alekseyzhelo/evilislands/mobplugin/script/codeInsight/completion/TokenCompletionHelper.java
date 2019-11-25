package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.completion;

import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.lookup.EILookupElementFactory;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.lookup.EITemplateLookupElement;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptNamingUtil;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.template.impl.TemplateImpl;
import com.intellij.codeInsight.template.impl.TemplateSettings;
import org.apache.commons.compress.utils.Sets;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;

enum TokenCompletionHelper {
    FLOAT("FLOAT", EIScriptNamingUtil.FLOAT),
    STRING("STRING", EIScriptNamingUtil.STRING),
    OBJECT("OBJECT", EIScriptNamingUtil.OBJECT),
    GROUP("GROUP", EIScriptNamingUtil.GROUP),

    GLOBALVARS("GLOBALVARS", EIScriptNamingUtil.GLOBALVARS),
    DECLARESCRIPT("DECLARESCRIPT", EIScriptNamingUtil.DECLARESCRIPT),
    SCRIPT("SCRIPT", EIScriptNamingUtil.SCRIPT),
    IF("IF", EIScriptNamingUtil.IF),
    THEN("THEN", EIScriptNamingUtil.THEN),
    FOR("FOR", EIScriptNamingUtil.FOR),
    WORLDSCRIPT("WORLDSCRIPT", EIScriptNamingUtil.WORLDSCRIPT),

    EQUALS("EQUALS", EIScriptNamingUtil.EQUALS),
    LPAREN("LPAREN", EIScriptNamingUtil.LPAREN),
    RPAREN("RPAREN", EIScriptNamingUtil.RPAREN),
    COMMA("COMMA", EIScriptNamingUtil.COMMA),
    COLON("COLON", EIScriptNamingUtil.COLON);

    private static Set<TokenCompletionHelper> TO_PREFIX = Collections.unmodifiableSet(Sets.newHashSet(
            COMMA, LPAREN, RPAREN
    ));

    @NotNull
    private String key;
    @NotNull
    private String name;

    TokenCompletionHelper(@NotNull String tokenText, @NotNull String lookupString) {
        key = tokenText;
        name = lookupString;
    }

    public boolean valueStartsWith(@NotNull String string) {
        return name.toLowerCase(Locale.ENGLISH).startsWith(string.toLowerCase(Locale.ENGLISH));
    }

    public LookupElement getLookupElement(@NotNull String prefix) {
        LookupElement element;
        TemplateImpl templateImpl = TemplateSettings.getInstance()
                .getDefaultTemplate(new TemplateImpl(name, "EIScriptHidden"));
        // TODO v2: check template context?  | TemplateManagerImpl.getApplicableContextTypes() ?
        if (templateImpl != null) {
            element = new EITemplateLookupElement(templateImpl, false);
        } else {
            element = EILookupElementFactory.createForToken(shouldPrefix() ? prefix + name : name);
        }
        return element;
    }

    @Nullable
    public static TokenCompletionHelper fromString(String tokenString) {
        for (TokenCompletionHelper token : values()) {
            if (token.key.equals(tokenString)) {
                return token;
            }
        }

        return null;
    }

    private boolean shouldPrefix() {
        return TO_PREFIX.contains(this);
    }
}
