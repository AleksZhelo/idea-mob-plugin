package com.alekseyzhelo.evilislands.mobplugin.script.structureView.filters;

import com.alekseyzhelo.evilislands.mobplugin.icon.Icons;
import com.alekseyzhelo.evilislands.mobplugin.script.structureView.elements.EIGSVarStructureViewElement;
import com.intellij.ide.util.treeView.smartTree.ActionPresentation;
import com.intellij.ide.util.treeView.smartTree.ActionPresentationData;
import com.intellij.ide.util.treeView.smartTree.Filter;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class GSVarsFilter implements Filter {
    @NonNls
    public static final String ID = "EI_SHOW_GS_VARS";

    @Override
    public boolean isVisible(TreeElement treeNode) {
        return !(treeNode instanceof EIGSVarStructureViewElement);
    }

    @Override
    @NotNull
    public ActionPresentation getPresentation() {
        return new ActionPresentationData("Show GSVars", null, Icons.GS_VAR);
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
