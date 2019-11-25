package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.lookup;

import com.intellij.codeInsight.template.impl.LiveTemplateLookupElementImpl;
import com.intellij.codeInsight.template.impl.TemplateImpl;
import org.jetbrains.annotations.NotNull;

public class EITemplateLookupElement extends LiveTemplateLookupElementImpl {

    public EITemplateLookupElement(@NotNull TemplateImpl template, boolean sudden) {
        super(template, sudden);
    }

    @Override
    public boolean isCaseSensitive() {
        return false;
    }
}
