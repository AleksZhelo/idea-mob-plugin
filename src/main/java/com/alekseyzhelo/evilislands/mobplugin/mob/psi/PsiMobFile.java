package com.alekseyzhelo.evilislands.mobplugin.mob.psi;

import com.alekseyzhelo.eimob.MobException;
import com.alekseyzhelo.eimob.MobFile;
import com.alekseyzhelo.eimob.MobVisitor;
import com.alekseyzhelo.evilislands.mobplugin.mob.EIMobLanguage;
import com.intellij.lang.Language;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.impl.PsiManagerImpl;
import com.intellij.psi.impl.file.PsiBinaryFileImpl;
import com.intellij.psi.search.PsiElementProcessor;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;

// TODO: use PsiFileGist or VirtualFileGist?
public class PsiMobFile extends PsiBinaryFileImpl {

    private static final Logger LOG = Logger.getInstance(PsiMobFile.class);

    private PsiElement[] children = new PsiElement[1];

    public PsiMobFile(@NotNull PsiManagerImpl manager, @NotNull FileViewProvider viewProvider) {
        super(manager, viewProvider);
    }

    public MobFile getMobFile() {
        return CachedValuesManager.getCachedValue(this, new CachedMobFileProvider());
    }

    public void acceptMobVisitor(MobVisitor visitor) {
        getMobFile().accept(visitor);
    }

    public byte[] getScriptBytes() {
        return getMobFile().getScriptBytes();
    }

    public void setScriptBytes(byte[] bytes) {
        MobFile mobFile = getMobFile();
        if (mobFile == null) {
            LOG.error("Mob file is null for " + toString());
            return;
        }
        mobFile.setScriptBytes(bytes);
        ApplicationManager.getApplication().runWriteAction(() -> {
            try (OutputStream out = getVirtualFile().getOutputStream(PsiMobFile.this)) {
                mobFile.serialize(out);
                out.flush();
            } catch (IOException e) {
                LOG.error("Failed to update script bytes for " + getVirtualFile().getPath(), e);
            }
        });
    }

    @NotNull
    @Override
    public PsiElement[] getChildren() {
        if (children[0] == null) {
            // TODO: should really be done in background (with "indexing" progressbar)?
            children[0] = PsiBuildingMobVisitor.createPsiMobObjectsBlock(this);
        }
        return children;
    }

    @Override
    public boolean processChildren(@NotNull PsiElementProcessor<PsiFileSystemItem> processor) {
        // TODO:
        return super.processChildren(processor);
    }

    @Override
    public boolean isValid() {
        return super.isValid();
    }

    @NotNull
    @Override
    public Language getLanguage() {
        return EIMobLanguage.INSTANCE;
    }

    @Override
    public String toString() {
        return "MobFile: " + getName();
    }

    @Override
    public void onContentReload() {
        super.onContentReload();
        System.out.println("Attempted content reload on " + toString());
    }

    private class CachedMobFileProvider implements CachedValueProvider<MobFile> {
        @Nullable
        @Override
        public Result<MobFile> compute() {
            try {
                VirtualFile virtualFile = getViewProvider().getVirtualFile();
                MobFile file = new MobFile(virtualFile.getPath(), virtualFile.contentsToByteArray());
                return new Result<>(file, virtualFile);
            } catch (MobException | IOException e) {
                LOG.error(e);
                return null;
            }
        }
    }
}
