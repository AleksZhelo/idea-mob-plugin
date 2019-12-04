package com.alekseyzhelo.evilislands.mobplugin.mob.util;

import com.alekseyzhelo.evilislands.mobplugin.EIMessages;
import com.alekseyzhelo.evilislands.mobplugin.mob.psi.PsiMobFile;
import com.alekseyzhelo.evilislands.mobplugin.script.fileType.ScriptFileType;
import com.intellij.ide.projectView.ProjectView;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.util.PsiUtilCore;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public final class MobPackingUtil {

    private static final Logger LOG = Logger.getInstance(MobPackingUtil.class);

    private MobPackingUtil() {

    }

    public static boolean isPacked(@NotNull PsiMobFile psiMobFile) {
        return isPacked(psiMobFile.getVirtualFile());
    }

    public static boolean isPacked(@NotNull VirtualFile mobVirtualFile) {
        return findScriptFile(mobVirtualFile) == null;
    }

    public static VirtualFile findScriptFile(VirtualFile mobVirtualFile) {
        return mobVirtualFile.getParent().findChild(getScriptName(mobVirtualFile));
    }

    @NotNull
    private static String getScriptName(@NotNull VirtualFile mobVirtualFile) {
        return mobVirtualFile.getNameWithoutExtension() + "." + ScriptFileType.INSTANCE.getDefaultExtension();
    }

    public static void unpack(@NotNull Object requester, @NotNull PsiMobFile mobFile, boolean silent) {
        final VirtualFile virtualFile = mobFile.getVirtualFile();
        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                final Project project = mobFile.getProject();
                final VirtualFile script = virtualFile.getParent().createChildData(requester, getScriptName(virtualFile));
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

    public static void pack(@NotNull PsiMobFile mobFile) {
        VirtualFile virtualFile = mobFile.getVirtualFile();
        final String filePath = virtualFile.getPath();
        VirtualFile scriptFile = findScriptFile(virtualFile);
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
