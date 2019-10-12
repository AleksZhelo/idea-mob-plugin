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

    private class CachedGSVarsProvider implements CachedValueProvider<Map<String, EIGSVar>> {
        @Override
        public Result<Map<String, EIGSVar>> compute() {
            Map<String, EIGSVar> result = new LinkedHashMap<>(); // to preserve insertion order for structure view
            acceptChildren(new PsiRecursiveElementVisitor() {
                @Override
                // TODO: do visitFunctionCall instead? benchmark!
                public void visitElement(PsiElement element) {
                    super.visitElement(element);
                    if (element.getNode().getElementType() == ScriptTypes.CHARACTER_STRING) {
                        EIFunctionCall parentCall = UsefulPsiTreeUtil.getParentFunctionCall(element);
                        if (parentCall != null) {
                            String varName = element.getText().substring(1, element.getTextLength() - 1);
                            // TODO: extract common?
                            EIGSVar stats = result.getOrDefault(varName, new EIGSVar(varName));
                            stats.processCall(parentCall);
                            if (stats.isValid()) {
                                result.put(varName, stats);
                            }
                        }
                    }
                }
            });
            return new Result<>(result, ScriptPsiFile.this);
        }
    }

    private class CachedAreasProvider implements CachedValueProvider<Map<Integer, EIArea>> {
        @Override
        public Result<Map<Integer, EIArea>> compute() {
            Map<Integer, EIArea> result = new LinkedHashMap<>(); // to preserve insertion order for structure view
            final int[] functionsVisited = {0};
            final int[] elementsVisited = {0};
            acceptChildren(new EIVisitor() {

                @Override
                public void visitFunctionCall(@NotNull EIFunctionCall o) {
                    super.visitFunctionCall(o);
                    functionsVisited[0]++;
                }

                @Override
                // TODO: do visitFunctionCall instead? benchmark!
                public void visitElement(PsiElement element) {
                    super.visitElement(element);
                    element.acceptChildren(this);

                    if (element.getNode().getElementType() == ScriptTypes.FLOATNUMBER) {
                        elementsVisited[0]++;
                        EIFunctionCall parentCall = UsefulPsiTreeUtil.getParentFunctionCall(element);
                        if (parentCall != null) {
                            // TODO: probably rework
                            List<EIExpression> expressions = parentCall.getParams().getExpressionList();
                            if (expressions.size() > 0) {
                                EIExpression expression = expressions.get(0);
                                if(PsiTreeUtil.isAncestor(expression, element, true)) {
                                    try {
                                        int areaId = Integer.parseInt(element.getText());
                                        EIArea stats = result.getOrDefault(areaId, new EIArea(areaId));
                                        stats.processCall(parentCall);
                                        if (stats.isValid()) {
                                            result.put(areaId, stats);
                                        }
                                    } catch (NumberFormatException ignored) {
                                        //don't really care
                                    }
                                }
                            }
                        }
                    }
                }
            });
            System.out.println(String.format("CachedAreasProvider visited %d functions, %d floats",
                    functionsVisited[0],
                    elementsVisited[0]
            ));
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