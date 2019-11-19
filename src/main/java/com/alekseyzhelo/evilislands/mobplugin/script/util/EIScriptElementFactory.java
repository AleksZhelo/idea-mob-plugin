package com.alekseyzhelo.evilislands.mobplugin.script.util;

import com.alekseyzhelo.evilislands.mobplugin.script.fileType.ScriptFileType;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;


public class EIScriptElementFactory {

    public static EIGlobalVars createGlobalVars(@NotNull Project project,
                                                @Nullable String varName,
                                                @Nullable EITypeToken varType) {
        ScriptPsiFile file;
        if (varName == null || varType == null) {
            file = createDummyScriptFile(project, "GlobalVars()");
        } else {
            file = createDummyScriptFile(project, EIScriptGenerationUtil.globalVarText(varName, varType));
        }
        PsiElement result = file.getFirstChild();
        return (EIGlobalVars) CodeStyleManager.getInstance(project).reformat(result);
    }

    public static EIGlobalVar createGlobalVar(@NotNull Project project,
                                              @NotNull String name,
                                              @NotNull EITypeToken type) {
        if (EITypeToken.VOID.equals(type)) {
            throw new IncorrectOperationException("Cannot create a 'VOID' global variable");
        }
        return createGlobalVars(project, name, type).getGlobalVarList().get(0);
    }

    public static PsiElement createIdentifier(@NotNull Project project, @NotNull String name) {
        return createGlobalVar(project, name, EITypeToken.GROUP).getNameIdentifier();
    }

    public static EIType createType(@NotNull Project project, @NotNull EITypeToken typeToken) {
        if (EITypeToken.ANY.equals(typeToken) || EITypeToken.VOID.equals(typeToken)) {
            throw new IncorrectOperationException("Cannot create an 'ANY' or 'VOID' PSI EIType");
        }
        return createGlobalVar(project, "dummy", typeToken).getType();
    }

    public static EIScripts createScripts(@NotNull Project project,
                                          @NotNull String scriptName) {
        final ScriptPsiFile file = createDummyScriptFile(project, EIScriptGenerationUtil.scriptImplementationText(scriptName));
        EIScripts scripts = (EIScripts) file.getFirstChild();
        return (EIScripts) CodeStyleManager.getInstance(project).reformat(scripts);
    }

    public static EIScriptImplementation createScriptImplementation(@NotNull Project project,
                                                                    @NotNull String name) {
        return createScripts(project, name).getScriptImplementationList().get(0);
    }

    public static EICallStatement createKillScriptCall(@NotNull Project project) {
        return (EICallStatement) Objects.requireNonNull(createScripts(project, "dummy")
                .getScriptImplementationList().get(0)
                .getScriptBlockList().get(0)
                .getScriptThenBlock())
                .getScriptStatementList().get(0);
    }

    private static EIDeclarations createDeclarations(@NotNull Project project,
                                                     @NotNull String scriptName,
                                                     @NotNull String paramName,
                                                     @NotNull EITypeToken paramType) {
        final ScriptPsiFile file = createDummyScriptFile(
                project,
                EIScriptGenerationUtil.scriptDeclarationText(scriptName, paramName, paramType)
        );
        EIDeclarations declarations = (EIDeclarations) file.getFirstChild();
        return (EIDeclarations) CodeStyleManager.getInstance(project).reformat(declarations);
    }

    public static EIDeclarations createDeclarations(@NotNull Project project,
                                                    @NotNull String scriptName) {
        return createDeclarations(project, scriptName, "this", EITypeToken.OBJECT);
    }

    public static EIScriptDeclaration createScriptDeclaration(@NotNull Project project,
                                                              @NotNull String name) {
        return createDeclarations(project, name).getScriptDeclarationList().get(0);
    }

    public static EIFormalParameter createFormalParameter(@NotNull Project project,
                                                          @NotNull String name,
                                                          @NotNull EITypeToken type) {
        if (EITypeToken.VOID.equals(type)) {
            throw new IncorrectOperationException("Cannot create a 'VOID' formal parameter");
        }
        EIDeclarations declarations = createDeclarations(project, "dummy", name, type);
        return declarations.getScriptDeclarationList().get(0).getFormalParameterList().get(0);
    }

    private static ScriptPsiFile createDummyScriptFile(@NotNull Project project, @NotNull String text) {
        String name = "dummy.eiscript";
        return (ScriptPsiFile) PsiFileFactory.getInstance(project).
                createFileFromText(name, ScriptFileType.INSTANCE, text);
    }
}