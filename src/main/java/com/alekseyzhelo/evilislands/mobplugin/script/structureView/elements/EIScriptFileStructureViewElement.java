package com.alekseyzhelo.evilislands.mobplugin.script.structureView.elements;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptPsiFile;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class EIScriptFileStructureViewElement extends PsiTreeElementBase<ScriptPsiFile> {

    public EIScriptFileStructureViewElement(@NotNull ScriptPsiFile psiElement) {
        super(psiElement);
    }

    @NotNull
    @Override
    public Collection<StructureViewTreeElement> getChildrenBase() {
        List<StructureViewTreeElement> children = new ArrayList<>();
        ScriptPsiFile myFile = getElement();
        if (myFile != null) {
            children.addAll(myFile.findScriptImplementations().stream()
                    .map(EIScriptImplStructureViewElement::new)
                    .collect(Collectors.toList()));
            children.addAll(myFile.findGlobalVars().stream()
                    .map(EIGlobalVarStructureViewElement::new)
                    .collect(Collectors.toList()));
            children.addAll(myFile.findGSVars().values().stream()
                    .map(EIGSVarStructureViewElement::new)
                    .collect(Collectors.toList()));
            children.addAll(myFile.findAreas().values().stream()
                    .map(EIAreaStructureViewElement::new)
                    .collect(Collectors.toList()));
        }
        return children;
    }

    @Nullable
    @Override
    public String getPresentableText() {
        return getElement().getName();
    }

}
