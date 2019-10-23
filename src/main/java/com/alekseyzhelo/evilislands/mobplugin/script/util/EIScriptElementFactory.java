package com.alekseyzhelo.evilislands.mobplugin.script.util;

import com.alekseyzhelo.evilislands.mobplugin.script.fileType.ScriptFileType;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIGlobalVar;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptPsiFile;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.util.*;

public class EIScriptElementFactory {

    // TODO: anything better?
    public static PsiElement createIdentifierFromText(Project project, String name) {
        EIGlobalVar globalVar = createGlobalVar(project, name);
        return globalVar.getNameIdentifier();
    }

    public static EIGlobalVar createGlobalVar(Project project, String name) {
        final ScriptPsiFile file = createFile(project, EIScriptGenerationUtil.wrapGlobalVars(name));
        return PsiTreeUtil.findChildOfType(file.getFirstChild(), EIGlobalVar.class);
    }

    public static ScriptPsiFile createFile(Project project, String text) {
        String name = "dummy.eiscript";
        return (ScriptPsiFile) PsiFileFactory.getInstance(project).
                createFileFromText(name, ScriptFileType.INSTANCE, text);
    }
}