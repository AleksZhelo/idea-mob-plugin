package com.alekseyzhelo.evilislands.mobplugin.script.util;

import com.alekseyzhelo.evilislands.mobplugin.script.fileType.ScriptFileType;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIGlobalVar;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIScriptImplementation;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptPsiFile;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;

public class EIScriptElementFactory {

    // TODO: anything better?
    public static PsiElement createIdentifierFromText(Project project, String name) {
        EIGlobalVar globalVar = createGlobalVar(project, name);
        return globalVar.getNameIdentifier();
    }

    public static EIGlobalVar createGlobalVar(Project project, String name) {
        final ScriptPsiFile file = createDummyScriptFile(project, EIScriptGenerationUtil.globalVarText(name));
        return PsiTreeUtil.findChildOfType(file.getFirstChild(), EIGlobalVar.class);
    }

    public static EIScriptImplementation createScriptImplementation(Project project, String name) {
        final ScriptPsiFile file = createDummyScriptFile(project, EIScriptGenerationUtil.scriptImplementationText(name));
        EIScriptImplementation scriptImpl = PsiTreeUtil.findChildOfType(file.getFirstChild(), EIScriptImplementation.class);
        return (EIScriptImplementation) CodeStyleManager.getInstance(project).reformat(scriptImpl);
    }

    public static ScriptPsiFile createDummyScriptFile(Project project, String text) {
        String name = "dummy.eiscript";
        return (ScriptPsiFile) PsiFileFactory.getInstance(project).
                createFileFromText(name, ScriptFileType.INSTANCE, text);
    }
}