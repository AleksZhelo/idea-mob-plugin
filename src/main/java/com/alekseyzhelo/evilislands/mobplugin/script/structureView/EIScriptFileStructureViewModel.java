package com.alekseyzhelo.evilislands.mobplugin.script.structureView;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptPsiFile;
import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.TextEditorBasedStructureViewModel;
import com.intellij.ide.util.treeView.smartTree.Grouper;
import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.NotNull;

// TODO: units/etc via groupers?  | will try to do this in the project view window
// TODO: filters: global vars, scripts, gsSet vars
public class EIScriptFileStructureViewModel extends TextEditorBasedStructureViewModel implements StructureViewModel.ElementInfoProvider {

    EIScriptFileStructureViewModel(Editor editor, ScriptPsiFile file) {
        super(editor, file);
    }

    @NotNull
    @Override
    public StructureViewTreeElement getRoot() {
        return new EIScriptFileStructureViewElement((ScriptPsiFile) getPsiFile());
    }

    @Override
    public boolean isAlwaysShowsPlus(StructureViewTreeElement element) {
        return element instanceof EIScriptFileStructureViewElement;
    }

    @Override
    public boolean isAlwaysLeaf(StructureViewTreeElement element) {
        return element instanceof EIScriptImplStructureViewElement;
    }

    @NotNull
    @Override
    public Grouper[] getGroupers() {
        return super.getGroupers();
    }
    
}
