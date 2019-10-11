package com.alekseyzhelo.evilislands.mobplugin.script.structureView;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptPsiFile;
import com.alekseyzhelo.evilislands.mobplugin.script.structureView.elements.EIScriptFileStructureViewElement;
import com.alekseyzhelo.evilislands.mobplugin.script.structureView.elements.EIScriptImplStructureViewElement;
import com.alekseyzhelo.evilislands.mobplugin.script.structureView.filters.GSVarsFilter;
import com.alekseyzhelo.evilislands.mobplugin.script.structureView.filters.GlobalVarsFilter;
import com.alekseyzhelo.evilislands.mobplugin.script.structureView.filters.ScriptsFilter;
import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.TextEditorBasedStructureViewModel;
import com.intellij.ide.util.treeView.smartTree.Filter;
import com.intellij.ide.util.treeView.smartTree.Grouper;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.NotNull;

// TODO: units/etc via groupers?  | will try to do this in the project view window
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

    @NotNull
    @Override
    public Filter[] getFilters() {
        return new Filter[]{new ScriptsFilter(), new GlobalVarsFilter(), new GSVarsFilter()};
    }

    @NotNull
    @Override
    public Sorter[] getSorters() {
        return new Sorter[]{Sorter.ALPHA_SORTER};
    }
}
