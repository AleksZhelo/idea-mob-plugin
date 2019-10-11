package com.alekseyzhelo.evilislands.mobplugin.script.structureView.elements;

import com.alekseyzhelo.evilislands.mobplugin.icon.Icons;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIScriptImplementation;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;
import java.util.Collections;

public class EIScriptImplStructureViewElement extends PsiTreeElementBase<EIScriptImplementation> {

    EIScriptImplStructureViewElement(@NotNull EIScriptImplementation psiElement) {
        super(psiElement);
    }

    @NotNull
    @Override
    public Collection<StructureViewTreeElement> getChildrenBase() {
        return Collections.emptyList();
    }

    @Nullable
    @Override
    public String getPresentableText() {
        return getElement().getName();
    }

    @Override
    public Icon getIcon(boolean open) {
        return Icons.SCRIPT_IMPL;
    }

}
