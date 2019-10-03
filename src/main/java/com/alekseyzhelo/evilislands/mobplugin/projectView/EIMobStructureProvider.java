package com.alekseyzhelo.evilislands.mobplugin.projectView;

import com.alekseyzhelo.evilislands.mobplugin.mob.fileType.TestMobFileType;
import com.intellij.ide.projectView.TreeStructureProvider;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.PsiFileNode;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.stream.Collectors;

public class EIMobStructureProvider implements TreeStructureProvider {
    @NotNull
    @Override
    public Collection<AbstractTreeNode> modify(@NotNull AbstractTreeNode parent, @NotNull Collection<AbstractTreeNode> children, ViewSettings settings) {
        return children.stream()
                .map(this::convertMobNode)
                .collect(Collectors.toList());
    }

    @Nullable
    @Override
    public Object getData(@NotNull Collection<AbstractTreeNode> selected, @NotNull String dataId) {
        return null;
    }

    private AbstractTreeNode<?> convertMobNode(AbstractTreeNode<?> node) {
        if (node instanceof PsiFileNode) {
            VirtualFile virtualFile = ((PsiFileNode) node).getVirtualFile();
            if (virtualFile != null) {
                try {
                    PsiFile psiFile = ((PsiFileNode) node).getValue();
                    if (virtualFile.getFileType() instanceof TestMobFileType) {
                        return new EIMobPsiFileNode(node.getProject(), psiFile, ((PsiFileNode) node).getSettings());
                    }
                } catch (Exception e) {
                    // return the original node in case of any error
                    return node;
                }
            }
        }
        return node;
    }
}
