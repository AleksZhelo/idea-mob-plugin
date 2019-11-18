package com.alekseyzhelo.evilislands.mobplugin.mob;

import com.alekseyzhelo.evilislands.mobplugin.EIMessages;
import com.alekseyzhelo.evilislands.mobplugin.mob.psi.PsiMobFile;
import com.alekseyzhelo.evilislands.mobplugin.script.fileType.ScriptFileType;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class UnpackMobAction extends AnAction {

    private static final Logger LOG = Logger.getInstance(UnpackMobAction.class);

    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiFile psiFile = e.getData(PlatformDataKeys.PSI_FILE);
        if (psiFile instanceof PsiMobFile) {
            if (isPacked(psiFile)) {
                unpack((PsiMobFile) psiFile);
            } else {
                pack((PsiMobFile) psiFile);
            }
        }
    }

    @Override
    public void update(AnActionEvent e) {
        PsiFile psiFile = e.getData(PlatformDataKeys.PSI_FILE);
        if (psiFile instanceof PsiMobFile) {
            e.getPresentation().setEnabledAndVisible(true);
            e.getPresentation().setText(EIMessages.message(isPacked(psiFile) ? "action.unpack.script" : "action.pack.script"));
        } else {
            e.getPresentation().setEnabledAndVisible(false);
        }
    }

    @Override
    public boolean isDumbAware() {
        return false;
    }

    private boolean isPacked(@NotNull PsiFile psiFile) {
        VirtualFile file = psiFile.getVirtualFile();
        return file.getParent().findChild(getScriptName(file)) == null;
    }

    private void unpack(@NotNull PsiMobFile file) {
        final VirtualFile virtualFile = file.getVirtualFile();
        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                VirtualFile script = virtualFile.getParent().createChildData(UnpackMobAction.this, getScriptName(virtualFile));
                script.setBinaryContent(file.getScriptBytes());
                FileEditorManager.getInstance(file.getProject()).openFile(script, true);
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
                        file.getProject()
                );
                LOG.error(errorText, e);
            }
        });
    }

    @NotNull
    private String getScriptName(@NotNull VirtualFile file) {
        return file.getNameWithoutExtension() + "." + ScriptFileType.INSTANCE.getDefaultExtension();
    }

    private void pack(@NotNull PsiMobFile file) {
        VirtualFile virtualFile = file.getVirtualFile();
        final String filePath = virtualFile.getPath();
        VirtualFile scriptFile = virtualFile.getParent().findChild(getScriptName(virtualFile));
        if (scriptFile != null) {
            try {
                file.setScriptBytes(scriptFile.contentsToByteArray());
            } catch (IOException e) {
                LOG.error("Failed to pack script into mob " + filePath, e);
            }
        } else {
            LOG.error("Want to pack script into mob " + filePath + ", but the script file does not exist!");
        }
    }
}
