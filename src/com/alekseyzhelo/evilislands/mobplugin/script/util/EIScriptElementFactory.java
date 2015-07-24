package com.alekseyzhelo.evilislands.mobplugin.script.util;

import com.alekseyzhelo.evilislands.mobplugin.script.file.ScriptFileType;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIGlobalVar;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptFile;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.util.PsiTreeUtil;

public class EIScriptElementFactory {
    public static EIGlobalVar createGlobalVar(Project project, String name) {
        final ScriptFile file = createFile(project, EIScriptGenerationUtil.wrapGlobalVars(name));
        return PsiTreeUtil.findChildOfType(file.getFirstChild(), EIGlobalVar.class);
    }
 
    public static ScriptFile createFile(Project project, String text) {
        String name = "dummy.eiscript";
        return (ScriptFile) PsiFileFactory.getInstance(project).
                createFileFromText(name, ScriptFileType.INSTANCE, text);
    }
}