package com.alekseyzhelo.evilislands.mobplugin.script.util;

import com.alekseyzhelo.evilislands.mobplugin.script.file.ScriptFileType;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIGlobalVar;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIScriptIdentifier;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptFile;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.HashMap;
import java.util.Map;

public class EIScriptElementFactory {

    public static EIScriptIdentifier createIdentifierFromText(Project project, String name) {
        final EIGlobalVar globalVar = createGlobalVar(project, name);
        return PsiTreeUtil.findChildOfType(globalVar, EIScriptIdentifier.class);
    }

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