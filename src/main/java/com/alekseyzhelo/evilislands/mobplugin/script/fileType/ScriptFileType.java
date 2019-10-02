package com.alekseyzhelo.evilislands.mobplugin.script.fileType;

import com.alekseyzhelo.evilislands.mobplugin.icon.Icons;
import com.alekseyzhelo.evilislands.mobplugin.script.EIScriptLanguage;
import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ScriptFileType extends LanguageFileType {
    public static final ScriptFileType INSTANCE = new ScriptFileType();

    private ScriptFileType() {
        super(EIScriptLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Script text file";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Evil islands scripting language file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "eiscript";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return Icons.FILE;
    }
}