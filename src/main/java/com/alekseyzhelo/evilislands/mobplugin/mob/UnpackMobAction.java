package com.alekseyzhelo.evilislands.mobplugin.mob;

import com.alekseyzhelo.evilislands.mobplugin.EIMessages;
import com.alekseyzhelo.evilislands.mobplugin.mob.fileType.MobFileType;
import com.alekseyzhelo.evilislands.mobplugin.mob.psi.PsiMobFile;
import com.alekseyzhelo.evilislands.mobplugin.script.fileType.ScriptFileType;
import com.intellij.ide.projectView.ProjectView;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtilCore;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class UnpackMobAction extends AnAction {

    private static final Logger LOG = Logger.getInstance(UnpackMobAction.class);

    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiFile psiFile = e.getData(PlatformDataKeys.PSI_FILE);
        if (psiFile instanceof PsiMobFile) {
            final PsiMobFile mobFile = (PsiMobFile) psiFile;
            if (isPacked(mobFile)) {
                unpack(mobFile, false);
            } else {
                pack(mobFile);
            }
        } else {
            VirtualFile vFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
            Project project = e.getData(PlatformDataKeys.PROJECT);
            if (vFile != null && vFile.isDirectory() && project != null) {
                PsiDirectory directory = PsiManager.getInstance(project).findDirectory(vFile);
                if (directory != null) {
                    final PsiElement[] children = directory.getChildren();
                    if (children.length > 0) {
                        ProgressManager.getInstance().run(new Task.Backgroundable(project,
                                EIMessages.message("action.unpack.all.scripts")) {

                            @Override
                            public void run(@NotNull ProgressIndicator indicator) {
                                for (int i = 0; i < children.length; i++) {
                                    final PsiElement child = children[i];
                                    if (child instanceof PsiMobFile) {
                                        indicator.checkCanceled();
                                        ApplicationManager.getApplication().invokeAndWait(() -> {
                                            if (isPacked((PsiMobFile) child)) {
                                                unpack((PsiMobFile) child, true);
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
        }
    }

    @Override
    public void update(AnActionEvent e) {
        VirtualFile[] vFiles = e.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY);
        if (vFiles != null && vFiles.length == 1) {
            VirtualFile vFile = vFiles[0];
            if (vFile.getFileType() == MobFileType.INSTANCE) {
                e.getPresentation().setEnabledAndVisible(true);
                e.getPresentation().setText(EIMessages.message(isPacked(vFile) ? "action.unpack.script" : "action.pack.script"));
            } else if (vFile.isDirectory()) {
                e.getPresentation().setEnabledAndVisible(true);
                e.getPresentation().setText(EIMessages.message("action.unpack.all.scripts"));
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

    private boolean isPacked(@NotNull PsiMobFile psiMobFile) {
        return isPacked(psiMobFile.getVirtualFile());
    }

    private boolean isPacked(@NotNull VirtualFile mobVirtualFile) {
        return mobVirtualFile.getParent().findChild(getScriptName(mobVirtualFile)) == null;
    }

    @NotNull
    private String getScriptName(@NotNull VirtualFile file) {
        return file.getNameWithoutExtension() + "." + ScriptFileType.INSTANCE.getDefaultExtension();
    }

    private void unpack(@NotNull PsiMobFile mobFile, boolean silent) {
        final VirtualFile virtualFile = mobFile.getVirtualFile();
        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                final Project project = mobFile.getProject();
                final VirtualFile script = virtualFile.getParent().createChildData(UnpackMobAction.this, getScriptName(virtualFile));
                script.setBinaryContent(mobFile.getScriptBytes());
                if (!silent) {
                    FileEditorManager.getInstance(project).openFile(script, true);
                    ApplicationManager.getApplication().invokeLater(() -> {
                        PsiFileSystemItem scriptFSItem = PsiUtilCore.findFileSystemItem(project, script);
                        if (scriptFSItem != null) {
                            ProjectView.getInstance(project).selectPsiElement(scriptFSItem, false);
                        }
                    });
                }
            } catch (IOException e) {
                final String errorText =
                        EIMessages.message("notification.error.cannot.unpack.mob.text", virtualFile.getPath());
                Notifications.Bus.notify(
                        new Notification(
                                EIMessages.message("notification.general.group.id"),
                                EIMessages.message("notification.error.cannot.unpack.mob.title"),
                                errorText,
                                NotificationType.ERROR
                        ),
                        mobFile.getProject()
                );
                LOG.error(errorText, e);
            }
        });
    }

    private void pack(@NotNull PsiMobFile mobFile) {
        VirtualFile virtualFile = mobFile.getVirtualFile();
        final String filePath = virtualFile.getPath();
        VirtualFile scriptFile = virtualFile.getParent().findChild(getScriptName(virtualFile));
        if (scriptFile != null) {
            try {
                mobFile.setScriptBytes(scriptFile.contentsToByteArray());
            } catch (IOException e) {
                LOG.error("Failed to pack script into mob " + filePath, e);
            }
        } else {
            LOG.error("Want to pack script into mob " + filePath + ", but the script file does not exist!");
        }
    }
}
