package com.alekseyzhelo.evilislands.mobplugin.mob;

import com.alekseyzhelo.evilislands.mobplugin.mob.psi.PsiMobFile;
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

// TODO: finish, test, improve
public class UnpackMobAction extends AnAction {

    private static final Logger LOG = Logger.getInstance(UnpackMobAction.class);

    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiFile psiFile = e.getData(PlatformDataKeys.PSI_FILE);
        if (psiFile instanceof PsiMobFile) {
            if (isPacked(psiFile)) {
                unpack((PsiMobFile) psiFile);
                // TODO: correct, needed?
                psiFile.getVirtualFile().getParent().refresh(false, false);
                // TODO: auto-open the mob node in project view
            } else {
                pack((PsiMobFile) psiFile);
                // TODO: correct, needed?
                psiFile.getVirtualFile().refresh(false, false);
            }
        }
    }

    @Override
    public void update(AnActionEvent e) {
        long start = System.currentTimeMillis();
        PsiFile psiFile = e.getData(PlatformDataKeys.PSI_FILE);
        if (psiFile != null && !psiFile.isDirectory() && psiFile instanceof PsiMobFile) {
            e.getPresentation().setEnabledAndVisible(true);
            e.getPresentation().setText(isPacked(psiFile) ? "Unpack Script" : "Pack Script");
        } else {
            e.getPresentation().setEnabledAndVisible(false);
        }
        System.out.println(String.format("UnpackMobAction update took %d ms", System.currentTimeMillis() - start));
    }

    @Override
    public boolean isDumbAware() {
        return false;
    }

    private boolean isPacked(@NotNull PsiFile psiFile) {
        VirtualFile file = psiFile.getVirtualFile();
        return file.getParent().findChild(getScriptName(file)) == null;
    }

    private void unpack(PsiMobFile file) {
        VirtualFile virtualFile = file.getVirtualFile();
        System.out.println("Want to unpack " + virtualFile.getPath());
        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                VirtualFile script = virtualFile.getParent().createChildData(UnpackMobAction.this, getScriptName(virtualFile));
                script.setBinaryContent(file.getScriptBytes());
                FileEditorManager.getInstance(file.getProject()).openFile(script, true);
            } catch (IOException e) {
                // TODO: also display in event log?
                LOG.error("Failed to unpack script for " + virtualFile.getPath(), e);
            }
        });
    }

    @NotNull
    private String getScriptName(VirtualFile file) {
        return file.getNameWithoutExtension() + ".eiscript";
    }

    private void pack(PsiMobFile file) {
        VirtualFile virtualFile = file.getVirtualFile();
        System.out.println("Want to pack " + virtualFile.getPath());
        VirtualFile scriptFile = virtualFile.getParent().findChild(getScriptName(virtualFile));
        if (scriptFile != null) {
            try {
                file.setScriptBytes(scriptFile.contentsToByteArray());
            } catch (IOException e) {
                LOG.error("Failed to pack script into mob " + virtualFile.getPath(), e);
            }
        } else {
            LOG.error("Want to pack script into mob " + virtualFile.getPath() + ", but the script file does not exist!");
        }
    }

}
