package com.alekseyzhelo.evilislands.mobplugin.script.structureView.filters;

import com.alekseyzhelo.evilislands.mobplugin.icon.Icons;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.impl.EIArea;
import com.alekseyzhelo.evilislands.mobplugin.script.structureView.elements.EIAreaStructureViewElement;
import com.alekseyzhelo.evilislands.mobplugin.script.structureView.elements.EIGSVarStructureViewElement;
import com.intellij.ide.util.treeView.smartTree.ActionPresentation;
import com.intellij.ide.util.treeView.smartTree.ActionPresentationData;
import com.intellij.ide.util.treeView.smartTree.Filter;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class AreasFilter implements Filter {
    @NonNls
    public static final String ID = "EI_SHOW_AREAS";

    @Override
    public boolean isVisible(TreeElement treeNode) {
        return !(treeNode instanceof EIAreaStructureViewElement);
    }

    @Override
    @NotNull
    public ActionPresentation getPresentation() {
        return new ActionPresentationData("Show Areas", null, Icons.AREA);
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
