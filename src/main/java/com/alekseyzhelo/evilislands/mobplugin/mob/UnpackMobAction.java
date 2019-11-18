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
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileSystemItem;
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
                unpack(mobFile);
            } else {
                pack(mobFile);
            }
        }
    }

    @Override
    public void update(AnActionEvent e) {
        VirtualFile vFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        if (vFile != null && vFile.getFileType() == MobFileType.INSTANCE) {
            e.getPresentation().setEnabledAndVisible(true);
            e.getPresentation().setText(EIMessages.message(isPacked(vFile) ? "action.unpack.script" : "action.pack.script"));
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

    private void unpack(@NotNull PsiMobFile mobFile) {
        final VirtualFile virtualFile = mobFile.getVirtualFile();
        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                final Project project = mobFile.getProject();
                final VirtualFile script = virtualFile.getParent().createChildData(UnpackMobAction.this, getScriptName(virtualFile));
                script.setBinaryContent(mobFile.getScriptBytes());
                FileEditorManager.getInstance(project).openFile(script, true);
                ApplicationManager.getApplication().invokeLater(() -> {
                    PsiFileSystemItem scriptFSItem = PsiUtilCore.findFileSystemItem(project, script);
                    if (scriptFSItem != null) {
                        ProjectView.getInstance(project).selectPsiElement(scriptFSItem, false);
                    }
                });
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
