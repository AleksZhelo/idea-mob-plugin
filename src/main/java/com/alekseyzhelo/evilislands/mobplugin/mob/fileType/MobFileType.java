package com.alekseyzhelo.evilislands.mobplugin.mob.fileType;

import com.alekseyzhelo.evilislands.mobplugin.icon.Icons;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class MobFileType implements FileType {

    public static final MobFileType INSTANCE = new MobFileType();

    private MobFileType() {
//        super(EIMobLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Mob";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Evil Islands composite map description file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "mob";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return Icons.MOB_FILE;
    }

    @Override
    public boolean isBinary() {
        return true;
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public String getCharset(@NotNull VirtualFile file, @NotNull byte[] content) {
        return null;
    }

}