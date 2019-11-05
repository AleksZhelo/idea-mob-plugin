package com.alekseyzhelo.evilislands.mobplugin.script.util;

import com.alekseyzhelo.evilislands.mobplugin.script.fileType.ScriptFileType;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;


public class EIScriptElementFactory {

    public static PsiElement createIdentifier(@NotNull Project project, @NotNull String name) {
        EIGlobalVar globalVar = createGlobalVar(project, name);
        return globalVar.getNameIdentifier();
    }

    public static EIType createType(@NotNull Project project, @NotNull EITypeToken typeToken) {
        if (EITypeToken.ANY.equals(typeToken)) {
            throw new IncorrectOperationException("Cannot create an 'ANY' PSI EIType");
        }
        final ScriptPsiFile file = createDummyScriptFile(project, EIScriptGenerationUtil.globalVarText("dummy", typeToken));
        return PsiTreeUtil.findChildOfType(file.getFirstChild(), EIGlobalVar.class).getType();
    }

    public static EIGlobalVar createGlobalVar(@NotNull Project project, @NotNull String name) {
        final ScriptPsiFile file = createDummyScriptFile(project, EIScriptGenerationUtil.globalVarText(name));
        return PsiTreeUtil.findChildOfType(file.getFirstChild(), EIGlobalVar.class);
    }

    private static EIScripts createScripts(@NotNull Project project, @NotNull String scriptName, boolean reformat) {
        final ScriptPsiFile file = createDummyScriptFile(project, EIScriptGenerationUtil.scriptImplementationText(scriptName));
        EIScripts scripts = (EIScripts) file.getFirstChild();
        return reformat ? (EIScripts) CodeStyleManager.getInstance(project).reformat(scripts) : scripts;
    }

    public static EIScripts createScripts(@NotNull Project project, @NotNull String scriptName) {
        return createScripts(project, scriptName, true);
    }

    public static EIScriptImplementation createScriptImplementation(@NotNull Project project, @NotNull String name) {
        EIScripts scripts = createScripts(project, name, false);
        EIScriptImplementation scriptImpl = scripts.getScriptImplementationList().get(0);
        return (EIScriptImplementation) CodeStyleManager.getInstance(project).reformat(scriptImpl);
    }

    private static EIDeclarations createDeclarations(@NotNull Project project, @NotNull String scriptName, boolean reformat) {
        final ScriptPsiFile file = createDummyScriptFile(project, EIScriptGenerationUtil.scriptDeclarationText(scriptName));
        EIDeclarations declarations = (EIDeclarations) file.getFirstChild();
        return reformat ? (EIDeclarations) CodeStyleManager.getInstance(project).reformat(declarations) : declarations;
    }

    public static EIDeclarations createDeclarations(@NotNull Project project, @NotNull String scriptName) {
        return createDeclarations(project, scriptName, true);
    }

    public static EIScriptDeclaration createScriptDeclaration(@NotNull Project project, @NotNull String name) {
        EIScriptDeclaration declaration = createDeclarations(project, name, false).getScriptDeclarationList().get(0);
        return (EIScriptDeclaration) CodeStyleManager.getInstance(project).reformat(declaration);
    }

    public static ScriptPsiFile createDummyScriptFile(@NotNull Project project, @NotNull String text) {
        String name = "dummy.eiscript";
        return (ScriptPsiFile) PsiFileFactory.getInstance(project).
                createFileFromText(name, ScriptFileType.INSTANCE, text);
    }
}