package com.alekseyzhelo.evilislands.mobplugin.mob.psi.provider;

import com.alekseyzhelo.eimob.MobException;
import com.alekseyzhelo.evilislands.mobplugin.mob.fileType.MobFileType;
import com.alekseyzhelo.evilislands.mobplugin.mob.psi.PsiMobFile;
import com.intellij.lang.Language;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.PsiManagerImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

class MobFileViewProvider extends SingleRootFileViewProvider {

    private static final Logger LOG = Logger.getInstance(MobFileViewProvider.class);

    MobFileViewProvider(@NotNull PsiManager manager, @NotNull VirtualFile virtualFile, boolean eventSystemEnabled, @NotNull Language language) {
        super(manager, virtualFile, eventSystemEnabled, language);
    }

    @Nullable
    @Override
    protected PsiFile getPsiInner(@NotNull Language target) {
        return super.getPsiInner(target);
    }

    @Override
    public PsiReference findReferenceAt(int offset) {
        return super.findReferenceAt(offset);
    }

    @Override
    public PsiElement findElementAt(int offset) {
        return super.findElementAt(offset);
    }

    @Override
    public PsiElement findElementAt(int offset, @NotNull Class<? extends Language> lang) {
        return super.findElementAt(offset, lang);
    }

    @NotNull
    @Override
    protected PsiFile createFile(@NotNull VirtualFile file, @NotNull FileType fileType, @NotNull Language language) {
        if (fileType instanceof MobFileType) {
            PsiFile psiFile = createFile(language);
            return psiFile != null ? psiFile : super.createFile(file, fileType, language);
        } else {
            return super.createFile(file, fileType, language);
        }
    }

    @Nullable
    @Override
    protected PsiFile createFile(@NotNull Language lang) {
        try {
            return new PsiMobFile((PsiManagerImpl) getManager(), this);
        } catch (MobException | IOException e) {
            // TODO: show in event log?
            LOG.error(e);
            return null;
        }
    }

    @NotNull
    @Override
    public CharSequence getContents() {
        return super.getContents();
    }

    @Override
    public Document getDocument() {
        return super.getDocument();
    }

    @Override
    public FileViewProvider clone() {
        return super.clone();
    }
}
