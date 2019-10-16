package com.alekseyzhelo.evilislands.mobplugin.script.structureView.filters;

import com.alekseyzhelo.evilislands.mobplugin.icon.Icons;
import com.alekseyzhelo.evilislands.mobplugin.script.structureView.elements.EIScriptImplStructureViewElement;
import com.intellij.ide.util.treeView.smartTree.ActionPresentation;
import com.intellij.ide.util.treeView.smartTree.ActionPresentationData;
import com.intellij.ide.util.treeView.smartTree.Filter;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class ScriptsFilter implements Filter {
    @NonNls
    public static final String ID = "EI_SHOW_SCRIPTS";

    @Override
    public boolean isVisible(TreeElement treeNode) {
        return !(treeNode instanceof EIScriptImplStructureViewElement);
    }

    @Override
    @NotNull
    public ActionPresentation getPresentation() {
        return new ActionPresentationData("Show Scripts", null, Icons.SCRIPT_IMPL);
    }

    @Override
    @NotNull
    public String getName() {
        return ID;
    }

    @Override
    public boolean isReverted() {
        return true;
    }
}
