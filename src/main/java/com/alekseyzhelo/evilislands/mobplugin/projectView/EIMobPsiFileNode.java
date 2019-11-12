package com.alekseyzhelo.evilislands.mobplugin.projectView;

import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.PsiFileNode;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

// TODO: show objects/units/particles/etc?
// TODO: red underline for script file when errors detected?
public class EIMobPsiFileNode extends PsiFileNode {

    private final Collection<AbstractTreeNode> children;

    public EIMobPsiFileNode(Project project, @NotNull PsiFile value, ViewSettings viewSettings, PsiFileNode scriptNode) {
        super(project, value, viewSettings);
        if (scriptNode != null) {
            children = Collections.singletonList(scriptNode);
        } else {
            children = Collections.emptyList();
        }
    }

    @Override
    public Collection<AbstractTreeNode> getChildrenImpl() {
        return children;
    }

    // TODO: doesn't work?
    @Override
    public boolean isAlwaysExpand() {
        return children.size() > 0;
    }
}
