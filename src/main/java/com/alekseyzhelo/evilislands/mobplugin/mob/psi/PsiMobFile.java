package com.alekseyzhelo.evilislands.mobplugin.mob.psi;

import com.alekseyzhelo.eimob.MobException;
import com.alekseyzhelo.eimob.MobFile;
import com.alekseyzhelo.eimob.MobVisitor;
import com.alekseyzhelo.eimob.Mob_utilKt;
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

    public MobFile getMobFile() {
        final VirtualFile virtualFile = getViewProvider().getVirtualFile();
        return CachedValuesManager.getCachedValue(this,
                () -> CachedValueProvider.Result.create(getMobFileInner(virtualFile), virtualFile));
    }

    public void acceptMobVisitor(MobVisitor visitor) {
        getMobFile().accept(visitor);
    }

    public byte[] getScriptBytes() {
        ScriptBlock scriptBlock = getMobFile().getScriptBlock();
        return scriptBlock != null
                ? Mob_utilKt.encodeMobString(scriptBlock.getScript())
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
            mobFile.getBlocks().add(0, scriptBlock);
        }
        scriptBlock.setScript(Mob_utilKt.decodeMobString(bytes));

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
