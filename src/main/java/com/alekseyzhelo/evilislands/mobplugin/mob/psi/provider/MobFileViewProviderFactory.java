package com.alekseyzhelo.evilislands.mobplugin.mob.psi.provider;

import com.alekseyzhelo.evilislands.mobplugin.mob.EIMobLanguage;
import com.intellij.lang.Language;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.FileViewProviderFactory;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;

public class MobFileViewProviderFactory implements FileViewProviderFactory {
    @NotNull
    @Override
    public FileViewProvider createFileViewProvider(@NotNull VirtualFile file, Language language, @NotNull PsiManager manager, boolean eventSystemEnabled) {
        return new MobFileViewProvider(manager, file, eventSystemEnabled, EIMobLanguage.INSTANCE);
    }
}
