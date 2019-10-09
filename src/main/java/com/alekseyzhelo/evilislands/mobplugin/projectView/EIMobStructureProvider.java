package com.alekseyzhelo.evilislands.mobplugin.projectView;

import com.alekseyzhelo.evilislands.mobplugin.mob.fileType.MobFileType;
import com.alekseyzhelo.evilislands.mobplugin.script.fileType.ScriptFileType;
import com.intellij.ide.projectView.TreeStructureProvider;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.PsiFileNode;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

// TODO: show script as mob child
public class EIMobStructureProvider implements TreeStructureProvider {
    @NotNull
    @Override
    public Collection<AbstractTreeNode> modify(@NotNull AbstractTreeNode parent, @NotNull Collection<AbstractTreeNode> children, ViewSettings settings) {
        return children.stream()
                .map((x) -> convertMobNode(x, children))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Nullable
    @Override
    public Object getData(@NotNull Collection<AbstractTreeNode> selected, @NotNull String dataId) {
        return null;
    }

    private AbstractTreeNode<?> convertMobNode(AbstractTreeNode<?> node, Collection<AbstractTreeNode> siblings) {
        if (node instanceof PsiFileNode) {
            VirtualFile virtualFile = ((PsiFileNode) node).getVirtualFile();
            if (virtualFile != null) {
                try {
                    PsiFile psiFile = ((PsiFileNode) node).getValue();
                    if (virtualFile.getFileType() instanceof MobFileType) {
                        PsiFileNode scriptNode = findSameNamedSiblingOfType(siblings, virtualFile, ScriptFileType.class);
                        return scriptNode != null
                                ? new EIMobPsiFileNode(node.getProject(), psiFile, ((PsiFileNode) node).getSettings(), scriptNode)
                                : node;
                    } else {
                        if (virtualFile.getFileType() instanceof ScriptFileType) {
                            PsiFileNode mobNode = findSameNamedSiblingOfType(siblings, virtualFile, MobFileType.class);
                            // filter out script nodes which will be subsumed by the corresponding mob nodes
                            return mobNode == null ? node : null;
                        } else {
                            return node;
                        }
                    }
                } catch (Exception e) {
                    // return the original node in case of any error
                    return node;
                }
            }
        }
        return node;
    }

    @Nullable
    private PsiFileNode findSameNamedSiblingOfType(
            Collection<AbstractTreeNode> siblings,
            VirtualFile file,
            Class<? extends FileType> type
    ) {
        PsiFileNode siblingNode = null;
        for (AbstractTreeNode<?> sibling : siblings) {
            if (sibling instanceof PsiFileNode) {
                VirtualFile siblingFile = ((PsiFileNode) sibling).getVirtualFile();
                if (siblingFile != null && type.isInstance(siblingFile.getFileType()) &&
                        siblingFile.getNameWithoutExtension().equals(file.getNameWithoutExtension())) {
                    siblingNode = (PsiFileNode) sibling;
                    break;
                }
            }
        }
        return siblingNode;
    }
}
