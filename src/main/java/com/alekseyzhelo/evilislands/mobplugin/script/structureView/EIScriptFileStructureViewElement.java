package com.alekseyzhelo.evilislands.mobplugin.script.structureView;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptPsiFile;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.stream.Collectors;

public class EIScriptFileStructureViewElement extends PsiTreeElementBase<ScriptPsiFile> {

    EIScriptFileStructureViewElement(@NotNull ScriptPsiFile psiElement) {
        super(psiElement);
    }

    @NotNull
    @Override
    public Collection<StructureViewTreeElement> getChildrenBase() {
        return getElement().findScriptImplementations().stream()
                .map(EIScriptImplStructureViewElement::new)
                .collect(Collectors.toList());
    }

    @Nullable
    @Override
    public String getPresentableText() {
        return getElement().getName();
    }

}
