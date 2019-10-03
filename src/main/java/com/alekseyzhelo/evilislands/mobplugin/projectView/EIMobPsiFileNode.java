package com.alekseyzhelo.evilislands.mobplugin.projectView;

import com.alekseyzhelo.evilislands.mobplugin.mob.vfs.MobAsArchiveFileSystem;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

public class EIMobPsiFileNode extends BasePsiFileNode {
    public EIMobPsiFileNode(Project project, @NotNull PsiFile value, ViewSettings viewSettings) {
        super(project, value, viewSettings);
    }

    @Override
    public Collection<AbstractTreeNode> getChildrenImpl() {
        VirtualFile vFile = getVirtualFile();
        if (vFile != null) {
            VirtualFile root = MobAsArchiveFileSystem.INSTANCE.getRootByLocal(vFile);
            return getChildrenForVirtualFile(root);
        } else {
            return Collections.emptyList();
        }
    }
}
