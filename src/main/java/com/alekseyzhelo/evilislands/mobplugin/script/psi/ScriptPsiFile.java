package com.alekseyzhelo.evilislands.mobplugin.script.psi;

import com.alekseyzhelo.evilislands.mobplugin.mob.psi.PsiMobFile;
import com.alekseyzhelo.evilislands.mobplugin.mob.psi.PsiMobObjectsBlock;
import com.alekseyzhelo.evilislands.mobplugin.script.EIScriptLanguage;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.impl.EIArea;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.impl.EIGSVar;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptResolveUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.util.UsefulPsiTreeUtil;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.util.Key;
import com.intellij.psi.*;
import com.intellij.psi.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

// TODO: processChildren implementation needed?
public class ScriptPsiFile extends PsiFileBase {

    private static final Key<CachedValue<PsiMobFile>> KEY_PSI_MOB_FILE = Key.create("KEY_PSI_MOB_FILE");

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
    //  | done with CachedValue; investigate stubs?
    // TODO: return Maps instead of lists?
    @NotNull
    public List<EIGlobalVar> findGlobalVars() {
        return CachedValuesManager.getCachedValue(this, new CacheGlobalVarsProvider());
    }

    @Nullable
    public EIGlobalVar findGlobalVar(String varName) {
        return EIScriptResolveUtil.matchByName(varName, findGlobalVars());
    }

    @NotNull
    public List<EIScriptDeclaration> findScriptDeclarations() {
        return CachedValuesManager.getCachedValue(this, new CachedScriptDeclarationsProvider());
    }

    @Nullable
    public EIScriptDeclaration findScriptDeclaration(String scriptName) {
        return EIScriptResolveUtil.matchByName(scriptName, findScriptDeclarations());
    }

    @NotNull
    public List<EIScriptImplementation> findScriptImplementations() {
        return CachedValuesManager.getCachedValue(this, new CachedScriptImplementationsProvider());
    }

    @Nullable
    public EIScriptImplementation findScriptImplementation(String scriptName) {
        return EIScriptResolveUtil.matchByName(scriptName, findScriptImplementations());
    }

    @NotNull
    public Map<String, EIGSVar> findGSVars() {
        return CachedValuesManager.getCachedValue(this, new CachedGSVarsProvider());
    }

    @NotNull
    public Map<Integer, EIArea> findAreas() {
        return CachedValuesManager.getCachedValue(this, new CachedAreasProvider());
    }

    @Nullable
    public PsiMobFile getCompanionMobFile() {
        return CachedValuesManager.getManager(getProject()).getCachedValue(this, KEY_PSI_MOB_FILE, new CachedCompanionPsiMobFileProvider(), true);
    }

    @Nullable
    public PsiMobObjectsBlock getCompanionMobObjectsBlock() {
        PsiMobFile mobFile = getCompanionMobFile();
        return mobFile != null ? (PsiMobObjectsBlock) mobFile.getChildren()[0] : null;
    }

    private class CacheGlobalVarsProvider implements CachedValueProvider<List<EIGlobalVar>> {
        @Nullable
        @Override
        public Result<List<EIGlobalVar>> compute() {
            final EIGlobalVars globalVars = PsiTreeUtil.getChildOfType(ScriptPsiFile.this, EIGlobalVars.class);
            return new Result<>(
                    globalVars == null
                            ? Collections.emptyList()
                            : PsiTreeUtil.getChildrenOfTypeAsList(globalVars, EIGlobalVar.class),
                    ScriptPsiFile.this
            );
        }
    }

    private class CachedScriptDeclarationsProvider implements CachedValueProvider<List<EIScriptDeclaration>> {
        @Nullable
        @Override
        public Result<List<EIScriptDeclaration>> compute() {
            final EIDeclarations declarations = PsiTreeUtil.getChildOfType(ScriptPsiFile.this, EIDeclarations.class);
            return new Result<>(
                    declarations == null
                            ? Collections.emptyList()
                            : PsiTreeUtil.getChildrenOfTypeAsList(declarations, EIScriptDeclaration.class),
                    ScriptPsiFile.this
            );
        }
    }

    private class CachedScriptImplementationsProvider implements CachedValueProvider<List<EIScriptImplementation>> {
        @Nullable
        @Override
        public Result<List<EIScriptImplementation>> compute() {
            final EIScripts implementations = PsiTreeUtil.getChildOfType(ScriptPsiFile.this, EIScripts.class);
            return new Result<>(
                    implementations == null
                            ? Collections.emptyList()
                            : PsiTreeUtil.getChildrenOfTypeAsList(implementations, EIScriptImplementation.class),
                    ScriptPsiFile.this
            );
        }
    }

    // TODO: GSVars and Areas could be calculated in one PSI-tree pass, instead of two
    private class CachedGSVarsProvider implements CachedValueProvider<Map<String, EIGSVar>> {
        @Override
        public Result<Map<String, EIGSVar>> compute() {
            Map<String, EIGSVar> result = new LinkedHashMap<>(); // to preserve insertion order for structure view
            acceptChildren(new EIVisitor() {
                @Override
                public void visitFunctionCall(@NotNull EIFunctionCall call) {
                    super.visitFunctionCall(call);

                    if (EIGSVar.isReadOrWrite(call)) {
                        PsiElement nameElement = EIGSVar.getVarNameElement(call);
                        if (nameElement != null &&
                                // TODO: can also be an expression, how to handle then? a reference goes to a formal param
                                //  for instance, instead of the actual parameter
                                nameElement.getNode().getElementType() == ScriptTypes.CHARACTER_STRING) {
                            String varName = EIGSVar.getVarName(nameElement.getText());
                            EIGSVar stats = result.getOrDefault(varName, new EIGSVar(varName));
                            stats.processCall(call);
                            if (stats.isValid()) {
                                result.put(varName, stats);
                            }
                        }
                    }
                }

                @Override
                public void visitElement(PsiElement element) {
                    super.visitElement(element);
                    element.acceptChildren(this);
                }
            });
            return new Result<>(result, ScriptPsiFile.this);
        }
    }

    private class CachedAreasProvider implements CachedValueProvider<Map<Integer, EIArea>> {
        @Override
        public Result<Map<Integer, EIArea>> compute() {
            Map<Integer, EIArea> result = new LinkedHashMap<>(); // to preserve insertion order for structure view
            acceptChildren(new EIVisitor() {
                @Override
                public void visitFunctionCall(@NotNull EIFunctionCall call) {
                    super.visitFunctionCall(call);

                    if (EIArea.isReadOrWrite(call)) {
                        PsiElement areaIdElement = EIArea.getAreaIdElement(call);
                        if (areaIdElement != null &&
                                // TODO: same as with GSVars
                                areaIdElement.getNode().getElementType() == ScriptTypes.FLOATNUMBER) {
                            try {
                                // TODO: also extract parsing to EIArea?
                                int areaId = Integer.parseInt(areaIdElement.getText());
                                EIArea stats = result.getOrDefault(areaId, new EIArea(areaId));
                                stats.processCall(call);
                                if (stats.isValid()) {
                                    result.put(areaId, stats);
                                }
                            } catch (NumberFormatException ignored) {
                                //don't really care
                            }
                        }
                    }
                }

                @Override
                public void visitElement(PsiElement element) {
                    super.visitElement(element);
                    element.acceptChildren(this);
                }
            });
            return new Result<>(result, ScriptPsiFile.this);
        }
    }

    private class CachedCompanionPsiMobFileProvider implements CachedValueProvider<PsiMobFile> {
        @Nullable
        @Override
        // TODO: better way to do this?
        public Result<PsiMobFile> compute() {
            PsiDirectory parent = getParent();
            if (parent == null) {
                parent = myOriginalFile.getParent();
            }

            PsiMobFile result = null;
            if (parent != null) {
                PsiFile psiFile = parent.findFile(getViewProvider().getVirtualFile().getNameWithoutExtension() + ".mob");
                if (psiFile instanceof PsiMobFile) {
                    result = (PsiMobFile) psiFile;
                }
            }
            return new Result<>(result, PsiModificationTracker.NEVER_CHANGED);
        }
    }
}