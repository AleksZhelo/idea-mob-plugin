package com.alekseyzhelo.evilislands.mobplugin.script.psi;

import com.alekseyzhelo.evilislands.mobplugin.script.EIScriptLanguage;
import com.alekseyzhelo.evilislands.mobplugin.script.file.ScriptFileType;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptResolveUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.util.UsefulPsiTreeUtil;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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


    // TODO experimental (everything below)
    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
        for (PsiElement element : getDeclarationsToProcess(lastParent)) {
            if (!processor.execute(element, state)) {
                return false;
            }
        }
        return super.processDeclarations(processor, state, lastParent, place);
    }

    private List<PsiElement> getDeclarationsToProcess(PsiElement lastParent) {
        final List<PsiElement> result = new ArrayList<PsiElement>();

        addDeclarations(result, EIScriptResolveUtil.findGlobalVars(this));
        addDeclarations(result, EIScriptResolveUtil.findFormalParameters(this));
        addDeclarations(result, EIScriptResolveUtil.findScriptDeclarations(this));

        // TODO something special for For blocks?
//        if (this instanceof HaxeForStatement && ((HaxeForStatement)this).getIterable() != lastParent) {
//            result.add(this);
//        }
        return result;
    }

    private static void addDeclarations(@NotNull List<PsiElement> result, List<? extends PsiElement> items) {
        if (items != null) {
            result.addAll(items);
        }
    }
}