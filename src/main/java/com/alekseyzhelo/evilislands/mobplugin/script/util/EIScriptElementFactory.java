package com.alekseyzhelo.evilislands.mobplugin.script.util;

import com.alekseyzhelo.evilislands.mobplugin.script.fileType.ScriptFileType;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
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

    private static EIScripts createScripts(Project project, String name, boolean reformat) {
        final ScriptPsiFile file = createDummyScriptFile(project, EIScriptGenerationUtil.scriptImplementationText(name));
        EIScripts scripts = (EIScripts) file.getFirstChild();
        return reformat ? (EIScripts) CodeStyleManager.getInstance(project).reformat(scripts) : scripts;
    }

    public static EIScripts createScripts(Project project, String name) {
        return createScripts(project, name, true);
    }

    public static EIScriptImplementation createScriptImplementation(Project project, String name) {
        EIScripts scripts = createScripts(project, name, false);
        EIScriptImplementation scriptImpl = scripts.getScriptImplementationList().get(0);
        return (EIScriptImplementation) CodeStyleManager.getInstance(project).reformat(scriptImpl);
    }

    private static EIDeclarations createDeclarations(Project project, String name, boolean reformat) {
        final ScriptPsiFile file = createDummyScriptFile(project, EIScriptGenerationUtil.scriptDeclarationText(name));
        EIDeclarations declarations = (EIDeclarations) file.getFirstChild();
        return reformat ? (EIDeclarations) CodeStyleManager.getInstance(project).reformat(declarations) : declarations;
    }

    public static EIDeclarations createDeclarations(Project project, String name) {
        return createDeclarations(project, name, true);
    }

    public static EIScriptDeclaration createScriptDeclaration(Project project, String name) {
        EIScriptDeclaration declaration = createDeclarations(project, name, false).getScriptDeclarationList().get(0);
        return (EIScriptDeclaration) CodeStyleManager.getInstance(project).reformat(declaration);
    }

    public static ScriptPsiFile createDummyScriptFile(Project project, String text) {
        String name = "dummy.eiscript";
        return (ScriptPsiFile) PsiFileFactory.getInstance(project).
                createFileFromText(name, ScriptFileType.INSTANCE, text);
    }
}