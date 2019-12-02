package com.alekseyzhelo.evilislands.mobplugin.mob.psi.provider;

import com.alekseyzhelo.evilislands.mobplugin.mob.psi.PsiMobFile;
import com.intellij.lang.Language;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.SingleRootFileViewProvider;
import com.intellij.psi.impl.PsiManagerImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class MobFileViewProvider extends SingleRootFileViewProvider {

    private static final Logger LOG = Logger.getInstance(MobFileViewProvider.class);

    MobFileViewProvider(@NotNull PsiManager manager, @NotNull VirtualFile virtualFile, boolean eventSystemEnabled, @NotNull Language language) {
        super(manager, virtualFile, eventSystemEnabled, language);
    }

    @Nullable
    @Override
    protected PsiFile createFile(@NotNull Project project, @NotNull VirtualFile file, @NotNull FileType fileType) {
        return createFile(getBaseLanguage());
    }

    @Nullable
    @Override
    protected PsiFile createFile(@NotNull Language lang) {
        return new PsiMobFile((PsiManagerImpl) getManager(), this);
    }
}
