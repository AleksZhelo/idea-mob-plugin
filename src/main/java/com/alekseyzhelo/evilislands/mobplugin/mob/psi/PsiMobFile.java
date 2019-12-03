package com.alekseyzhelo.evilislands.mobplugin.mob.psi;

import com.alekseyzhelo.eimob.MobException;
import com.alekseyzhelo.eimob.MobFile;
import com.alekseyzhelo.eimob.MobVisitor;
import com.alekseyzhelo.eimob.Mob_ioKt;
import com.alekseyzhelo.eimob.blocks.EncryptedScriptBlock;
import com.alekseyzhelo.eimob.blocks.ScriptBlock;
import com.alekseyzhelo.evilislands.mobplugin.mob.EIMobLanguage;
import com.intellij.lang.Language;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.PsiManagerImpl;
import com.intellij.psi.impl.file.PsiBinaryFileImpl;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;

// TODO v2: use PsiFileGist or VirtualFileGist?
public class PsiMobFile extends PsiBinaryFileImpl {

    private static final Logger LOG = Logger.getInstance(PsiMobFile.class);

    public PsiMobFile(@NotNull PsiManagerImpl manager, @NotNull FileViewProvider viewProvider) {
        super(manager, viewProvider);
    }

    @Nullable
    public MobFile getMobFile() {
        final VirtualFile virtualFile = getViewProvider().getVirtualFile();
        return CachedValuesManager.getCachedValue(this,
                () -> CachedValueProvider.Result.create(getMobFileInner(virtualFile), virtualFile));
    }

    public void acceptMobVisitor(MobVisitor visitor) {
        final MobFile mobFile = getMobFile();
        if (mobFile != null) {
            mobFile.accept(visitor);
        } else {
            LOG.error("Mob file is null for " + this + ", cannot accept visitor!");
        }
    }

    public byte[] getScriptBytes() {
        final MobFile mobFile = getMobFile();
        if (mobFile == null) {
            LOG.error("Asked for script bytes for " + this + " with a NULL mob file!");
            return new byte[0];
        }
        final ScriptBlock scriptBlock = mobFile.getScriptBlock();
        return scriptBlock != null
                ? Mob_ioKt.encodeMobString(scriptBlock.getScript())
                : new byte[0];
    }

    public void setScriptBytes(byte[] bytes) {
        final MobFile mobFile = getMobFile();
        if (mobFile == null) {
            LOG.error("Mob file is null for " + toString());
            return;
        }

        ScriptBlock scriptBlock = mobFile.getScriptBlock();
        if (scriptBlock == null) {
            scriptBlock = EncryptedScriptBlock.Companion.createWithKey(117637889);
            mobFile.addBlock(scriptBlock,0);
        }
        scriptBlock.setScript(Mob_ioKt.decodeMobString(bytes));

        ApplicationManager.getApplication().runWriteAction(() -> {
            final VirtualFile virtualFile = getViewProvider().getVirtualFile();
            try (OutputStream out = virtualFile.getOutputStream(PsiMobFile.this)) {
                mobFile.serialize(out);
                out.flush();
            } catch (IOException e) {
                LOG.error("Failed to update script bytes for " + virtualFile.getPath(), e);
            }
        });
    }

    @NotNull
    @Override
    public PsiElement[] getChildren() {
        return CachedValuesManager.getCachedValue(this,
                () -> CachedValueProvider.Result.create(
                        new PsiElement[]{PsiBuildingMobVisitor.createPsiMobObjectsBlock(this)},
                        getViewProvider().getVirtualFile()
                ));
    }

    @NotNull
    @Override
    public Language getLanguage() {
        return EIMobLanguage.INSTANCE;
    }

    @Override
    public String toString() {
        return "MobFile:" + getName();
    }

    @Nullable
    private MobFile getMobFileInner(VirtualFile virtualFile) {
        try {
            return new MobFile(virtualFile.getPath(), virtualFile.contentsToByteArray());
        } catch (MobException | IOException e) {
            LOG.error(e);
            return null;
        }
    }
}
