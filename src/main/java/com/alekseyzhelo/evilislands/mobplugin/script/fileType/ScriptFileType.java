package com.alekseyzhelo.evilislands.mobplugin.script.fileType;

import com.alekseyzhelo.eimob.MobFile;
import com.alekseyzhelo.evilislands.mobplugin.EIMessages;
import com.alekseyzhelo.evilislands.mobplugin.icon.Icons;
import com.alekseyzhelo.evilislands.mobplugin.script.EIScriptLanguage;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.vfs.VirtualFile;
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
    // TODO: change?
    public String getName() {
        return "Script text file";
    }

    @NotNull
    @Override
    public String getDescription() {
        return EIMessages.message("file.eiscript.desc");
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "eiscript";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return Icons.SCRIPT_FILE;
    }

    @Override
    public String getCharset(@NotNull VirtualFile file, @NotNull byte[] content) {
        return MobFile.Companion.getEiCharset().toString();
    }

}