package com.alekseyzhelo.evilislands.mobplugin.script.structureView;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptPsiFile;
import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.lang.PsiStructureViewFactory;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class EIScriptStructureViewBuilderFactory implements PsiStructureViewFactory {
    @Override
    @NotNull
    public StructureViewBuilder getStructureViewBuilder(@NotNull final PsiFile psiFile) {
        return (fileEditor, project) -> new EIScriptStructureViewComponent(project, (ScriptPsiFile) psiFile, fileEditor);
    }
}