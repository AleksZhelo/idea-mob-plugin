package com.alekseyzhelo.evilislands.mobplugin.actions;

import com.alekseyzhelo.evilislands.mobplugin.EIMessages;
import com.alekseyzhelo.evilislands.mobplugin.mob.psi.PsiMobFile;
import com.alekseyzhelo.evilislands.mobplugin.mob.util.MobPackingUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;

public class PackAllMobsAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        VirtualFile vFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        Project project = e.getData(PlatformDataKeys.PROJECT);
        if (vFile != null && vFile.isDirectory() && project != null) {
            packAllInDirectory(vFile, project);
        }
    }

    private void packAllInDirectory(@NotNull VirtualFile vFile, @NotNull Project project) {
        PsiDirectory directory = PsiManager.getInstance(project).findDirectory(vFile);
        if (directory != null) {
            final PsiElement[] children = directory.getChildren();
            if (children.length > 0) {
                ProgressManager.getInstance().run(new Task.Backgroundable(project,
                        EIMessages.message("action.pack.script.directory")) {

                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        indicator.setIndeterminate(false);
                        for (int i = 0; i < children.length; i++) {
                            final PsiElement child = children[i];
                            if (child instanceof PsiMobFile) {
                                indicator.checkCanceled();
                                ApplicationManager.getApplication().invokeAndWait(() -> {
                                    final PsiMobFile mobFile = (PsiMobFile) child;
                                    if (!MobPackingUtil.isPacked(mobFile)) {
                                        MobPackingUtil.pack(mobFile);
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
            if (vFile.isDirectory()) {
                e.getPresentation().setEnabledAndVisible(true);
                e.getPresentation().setText(EIMessages.message("action.pack.script.directory"));
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
