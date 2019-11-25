package com.alekseyzhelo.evilislands.mobplugin.script.template;

import com.intellij.codeInsight.template.impl.DefaultLiveTemplatesProvider;
import org.jetbrains.annotations.Nullable;

public class EILiveTemplatesProvider implements DefaultLiveTemplatesProvider {
    @Override
    public String[] getDefaultLiveTemplateFiles() {
        return new String[]{"liveTemplates/templates"};
    }

    @Nullable
    @Override
    public String[] getHiddenLiveTemplateFiles() {
        return new String[]{"liveTemplates/hiddenTemplates"};
    }
}
