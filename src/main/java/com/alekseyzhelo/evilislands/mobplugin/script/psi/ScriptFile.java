package com.alekseyzhelo.evilislands.mobplugin.script.psi;

import com.alekseyzhelo.evilislands.mobplugin.script.EIScriptLanguage;
import com.alekseyzhelo.evilislands.mobplugin.script.fileType.ScriptFileType;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptResolveUtil;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;

public class ScriptFile extends PsiFileBase {

    public ScriptFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, EIScriptLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return ScriptFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "Script File";
    }

    @Override
    public Icon getIcon(int flags) {
        return super.getIcon(flags);
    }

    // TODO this and below: find or get?
    //  also: should better be cached? (mechanism definitely exists, worth it to include here? stubIndex?)
    @NotNull
    public List<EIGlobalVar> findGlobalVars() {
        final EIGlobalVars globalVars = PsiTreeUtil.getChildOfType(this, EIGlobalVars.class);
        return globalVars == null
                ? Collections.emptyList()
                : PsiTreeUtil.getChildrenOfTypeAsList(globalVars, EIGlobalVar.class);
    }

    @Nullable
    public EIGlobalVar findGlobalVar(String varName) {
        return EIScriptResolveUtil.matchByName(varName, findGlobalVars());
    }

    @NotNull
    public List<EIScriptDeclaration> findScriptDeclarations() {
        final EIDeclarations declarations = PsiTreeUtil.getChildOfType(this, EIDeclarations.class);
        return declarations == null
                ? Collections.emptyList()
                : PsiTreeUtil.getChildrenOfTypeAsList(declarations, EIScriptDeclaration.class);
    }

    @Nullable
    public EIScriptDeclaration findScriptDeclaration(@NotNull String scriptName) {
        return EIScriptResolveUtil.matchByName(scriptName, findScriptDeclarations());
    }

    @NotNull
    public List<EIScriptImplementation> findScriptImplementations() {
        final EIScripts implementations = PsiTreeUtil.getChildOfType(this, EIScripts.class);
        return implementations == null
                ? Collections.emptyList()
                : PsiTreeUtil.getChildrenOfTypeAsList(implementations, EIScriptImplementation.class);
    }

}