package com.alekseyzhelo.evilislands.mobplugin.script.psi;

import com.alekseyzhelo.evilislands.mobplugin.mob.psi.PsiMobFile;
import com.alekseyzhelo.evilislands.mobplugin.mob.psi.PsiMobObjectsBlock;
import com.alekseyzhelo.evilislands.mobplugin.script.EIScriptLanguage;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.impl.EIGSVar;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptResolveUtil;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.*;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
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

    @NotNull
    public Map<String, EIGSVar> findGSVars() {
        return CachedValuesManager.getCachedValue(this, new CachedGSVarsProvider());
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

    private class CachedGSVarsProvider implements CachedValueProvider<Map<String, EIGSVar>> {
        @Override
        public Result<Map<String, EIGSVar>> compute() {
            Map<String, EIGSVar> result = new LinkedHashMap<>(); // to preserve insertion order
            acceptChildren(new PsiRecursiveElementVisitor() {
                @Override
                public void visitElement(PsiElement element) {
                    super.visitElement(element);
                    if (element.getNode().getElementType() == ScriptTypes.CHARACTER_STRING) {
                        EIFunctionCall parentCall = PsiTreeUtil.getParentOfType(
                                element,
                                EIFunctionCall.class,
                                true,
                                EIScriptBlock.class
                        );
                        if (parentCall != null) {
                            EIGSVar stats = null;
                            String varName = element.getText().substring(1, element.getTextLength() - 1);
                            if (parentCall.getScriptIdentifier().getText().equalsIgnoreCase("gsgetvar")) {
                                stats = result.getOrDefault(varName, new EIGSVar(varName));
                                stats.addRead();
                            } else if (parentCall.getScriptIdentifier().getText().equalsIgnoreCase("gssetvar")) {
                                stats = result.getOrDefault(varName, new EIGSVar(varName));
                                stats.addWrite();
                            }
                            if (stats != null) {
                                result.put(varName, stats);
                            }
                        }
                    }
                }
            });
            return new Result<>(result, ScriptPsiFile.this);
        }
    }
}