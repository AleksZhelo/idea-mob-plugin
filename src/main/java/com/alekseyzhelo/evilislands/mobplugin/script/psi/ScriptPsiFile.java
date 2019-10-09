package com.alekseyzhelo.evilislands.mobplugin.script.psi;

import com.alekseyzhelo.evilislands.mobplugin.mob.psi.PsiMobFile;
import com.alekseyzhelo.evilislands.mobplugin.mob.psi.PsiMobObjectsBlock;
import com.alekseyzhelo.evilislands.mobplugin.script.EIScriptLanguage;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptResolveUtil;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

// TODO: processChildren implementation needed?
public class ScriptPsiFile extends PsiFileBase {

    public ScriptPsiFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, EIScriptLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return getViewProvider().getFileType();
    }

    @Override
    public String toString() {
        return "ScriptFile: " + getName();
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

    // TODO: better way to do this?
    @Nullable
    public PsiMobFile getCompanionMobFile() {
        PsiDirectory parent = getParent();
        if (parent == null) {
            parent = myOriginalFile.getParent();
        }

        if (parent != null) {
            PsiFile psiFile = parent.findFile(getViewProvider().getVirtualFile().getNameWithoutExtension() + ".mob");
            if (psiFile instanceof PsiMobFile) {
                return (PsiMobFile) psiFile;
            }
        }
        return null;
    }

    @Nullable
    public PsiMobObjectsBlock getCompanionMobObjectsBlock() {
        PsiMobFile mobFile = getCompanionMobFile();
        return mobFile != null ? (PsiMobObjectsBlock) mobFile.getChildren()[0] : null;
    }
}