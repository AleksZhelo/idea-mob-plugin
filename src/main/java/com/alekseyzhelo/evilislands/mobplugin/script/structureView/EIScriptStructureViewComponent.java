package com.alekseyzhelo.evilislands.mobplugin.script.structureView;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptPsiFile;
import com.intellij.ide.structureView.newStructureView.StructureViewComponent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.ex.util.EditorUtil;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class EIScriptStructureViewComponent extends StructureViewComponent {

    private final ScriptPsiFile myScriptPsiFile;

    EIScriptStructureViewComponent(@NotNull Project project, @NotNull ScriptPsiFile file, @Nullable FileEditor editor) {
        super(editor, new EIScriptFileStructureViewModel(EditorUtil.getEditorEx(editor), file), project, true);
        myScriptPsiFile = file;
    }

    @Override
    public Object getData(@NotNull String dataId) {
        if (CommonDataKeys.VIRTUAL_FILE.is(dataId)) {
            return myScriptPsiFile.getVirtualFile();
        }
        if (CommonDataKeys.PSI_ELEMENT.is(dataId)) {
            return myScriptPsiFile.getContainingFile();
        }
        return super.getData(dataId);
    }
}
