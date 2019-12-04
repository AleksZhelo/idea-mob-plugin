package com.alekseyzhelo.evilislands.mobplugin.actions;

import com.alekseyzhelo.evilislands.mobplugin.EIMessages;
import com.alekseyzhelo.evilislands.mobplugin.mob.fileType.MobFileType;
import com.alekseyzhelo.evilislands.mobplugin.mob.psi.PsiMobFile;
import com.alekseyzhelo.evilislands.mobplugin.mob.util.MobPackingUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

public class UnpackMobAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiFile psiFile = e.getData(PlatformDataKeys.PSI_FILE);
        if (psiFile instanceof PsiMobFile) {
            final PsiMobFile mobFile = (PsiMobFile) psiFile;
            if (MobPackingUtil.isPacked(mobFile)) {
                MobPackingUtil.unpack(this, mobFile, false);
            } else {
                MobPackingUtil.pack(mobFile);
            }
        } else {
            VirtualFile vFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
            Project project = e.getData(PlatformDataKeys.PROJECT);
            if (vFile != null && vFile.isDirectory() && project != null) {
                unpackAllInDirectory(vFile, project);
            }
        }
    }

    private void unpackAllInDirectory(@NotNull VirtualFile vFile, @NotNull Project project) {
        PsiDirectory directory = PsiManager.getInstance(project).findDirectory(vFile);
        if (directory != null) {
            final PsiElement[] children = directory.getChildren();
            if (children.length > 0) {
                ProgressManager.getInstance().run(new Task.Backgroundable(project,
                        EIMessages.message("action.unpack.script.directory")) {

                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        for (int i = 0; i < children.length; i++) {
                            final PsiElement child = children[i];
                            if (child instanceof PsiMobFile) {
                                indicator.checkCanceled();
                                ApplicationManager.getApplication().invokeAndWait(() -> {
                                    if (MobPackingUtil.isPacked((PsiMobFile) child)) {
                                        MobPackingUtil.unpack(UnpackMobAction.this, (PsiMobFile) child, true);
                                    }
                                });
                                indicator.setFraction((i + 1d) / children.length);
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    public void update(AnActionEvent e) {
        VirtualFile[] vFiles = e.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY);
        if (vFiles != null && vFiles.length == 1) {
            VirtualFile vFile = vFiles[0];
            if (vFile.getFileType() == MobFileType.INSTANCE) {
                e.getPresentation().setEnabledAndVisible(true);
                e.getPresentation().setText(EIMessages.message(MobPackingUtil.isPacked(vFile) ? "action.unpack.script" : "action.pack.script"));
            } else if (vFile.isDirectory()) {
                e.getPresentation().setEnabledAndVisible(true);
                e.getPresentation().setText(EIMessages.message("action.unpack.script.directory"));
            } else {
                e.getPresentation().setEnabledAndVisible(false);
            }
        } else {
            e.getPresentation().setEnabledAndVisible(false);
        }
    }

    @Override
    public boolean isDumbAware() {
        return false;
    }
}
