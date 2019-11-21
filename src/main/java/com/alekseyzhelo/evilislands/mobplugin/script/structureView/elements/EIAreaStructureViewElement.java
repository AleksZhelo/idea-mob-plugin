package com.alekseyzhelo.evilislands.mobplugin.script.structureView.elements;

import com.alekseyzhelo.evilislands.mobplugin.icon.Icons;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.impl.EIArea;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.NodeDescriptorProvidingKey;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

// TODO v2: navigate to first usage? or highlight all usages?
// TODO v2: merge with GSVar element?
public class EIAreaStructureViewElement implements StructureViewTreeElement, ItemPresentation, NodeDescriptorProvidingKey {

    private final EIArea myValue;

    EIAreaStructureViewElement(@NotNull EIArea gsVar) {
        myValue = gsVar;
    }

    @Nullable
    @Override
    public String getPresentableText() {
        return String.format("Area %d", myValue.getVariable());
    }

    @Nullable
    @Override
    public String getLocationString() {
        return null;
    }

    @Override
    public Icon getIcon(boolean open) {
        return Icons.AREA;
    }

    @Override
    public Object getValue() {
        return myValue;
    }

    @NotNull
    @Override
    public Object getKey() {
        return String.valueOf(myValue);
    }

    @NotNull
    @Override
    public ItemPresentation getPresentation() {
        return this;
    }

    @NotNull
    @Override
    public TreeElement[] getChildren() {
        return new TreeElement[0];
    }

    @Override
    public void navigate(boolean requestFocus) {

    }

    @Override
    public boolean canNavigate() {
        return false;
    }

    @Override
    public boolean canNavigateToSource() {
        return false;
    }
}
