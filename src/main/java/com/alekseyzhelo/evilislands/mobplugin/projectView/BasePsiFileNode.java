package com.alekseyzhelo.evilislands.mobplugin.projectView;

import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.PsiFileNode;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BasePsiFileNode extends PsiFileNode {

    public BasePsiFileNode(Project project, @NotNull PsiFile value, ViewSettings viewSettings) {
        super(project, value, viewSettings);
    }

    protected Collection<AbstractTreeNode> getChildrenForVirtualFile(@Nullable VirtualFile rootFile) {
        Project project = getProject();
        if (project != null && rootFile != null) {
            PsiDirectory psiDirectory = PsiManager.getInstance(project).findDirectory(rootFile);
            if (psiDirectory != null) {
                return processChildren(psiDirectory);
            }
        }
        return ContainerUtil.emptyList();
    }

    private Collection<AbstractTreeNode> processChildren(PsiDirectory dir) {
        List<AbstractTreeNode> children = new ArrayList<>();
        for (PsiElement child : dir.getChildren()) {
            if (child instanceof PsiFileSystemItem) {
                if (child instanceof PsiFile) {
                    children.add(new PsiFileNode(child.getProject(), (PsiFile) child, getSettings()));
                }
//                else if (child instanceof PsiDirectory) {
//                    children.add(new PsiGenericDirectoryNode(child.getProject(), child, viewSettings))
//                }
            }
        }
        return children;
    }
}
