package com.alekseyzhelo.evilislands.mobplugin.mob;
 
import com.alekseyzhelo.evilislands.mobplugin.icon.Icons;
import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
 
import javax.swing.*;
 
public class MobFileType extends LanguageFileType {
    public static final MobFileType INSTANCE = new MobFileType();
 
    private MobFileType() {
        super(null);
    }
 
    @NotNull
    @Override
    public String getName() {
        return "Mob file";
    }
 
    @NotNull
    @Override
    public String getDescription() {
        return "Evil islands composite map description file";
    }
 
    @NotNull
    @Override
    public String getDefaultExtension() {
        return "mob";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return Icons.FILE;
    }
}