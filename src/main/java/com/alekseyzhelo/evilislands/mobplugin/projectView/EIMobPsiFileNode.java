package com.alekseyzhelo.evilislands.mobplugin.projectView;

import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.PsiFileNode;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class EIMobPsiFileNode extends PsiFileNode {

    private final List<AbstractTreeNode<?>> children;

    EIMobPsiFileNode(Project project, @NotNull PsiFile value, ViewSettings viewSettings, PsiFileNode scriptNode) {
        super(project, value, viewSettings);
        if (scriptNode != null) {
            children = Collections.singletonList(scriptNode);
        } else {
            children = Collections.emptyList();
        }
    }

    @Override
    public Collection<AbstractTreeNode<?>> getChildrenImpl() {
        return children;
    }

    @Override
    public boolean contains(@NotNull VirtualFile file) {
        return super.contains(file) || !children.isEmpty() && file.equals(((PsiFileNode) children.get(0)).getVirtualFile());
    }
}
