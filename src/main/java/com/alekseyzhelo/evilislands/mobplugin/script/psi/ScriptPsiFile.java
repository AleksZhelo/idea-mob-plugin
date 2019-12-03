package com.alekseyzhelo.evilislands.mobplugin.script.psi;

import com.alekseyzhelo.evilislands.mobplugin.mob.psi.PsiMobFile;
import com.alekseyzhelo.evilislands.mobplugin.mob.psi.PsiMobObjectsBlock;
import com.alekseyzhelo.evilislands.mobplugin.script.EIScriptLanguage;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.lookup.EILookupElementFactory;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.impl.EIArea;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.impl.EIGSVar;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EICommonUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EITypeToken;
import com.google.common.base.Stopwatch;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.TimeUnit;

// TODO v2: investigate stubs?
// TODO v2: line markers for objects?
public class ScriptPsiFile extends PsiFileBase {

    private static final Logger LOG = Logger.getInstance(ScriptPsiFile.class);
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
        return "ScriptFile:" + getName();
    }

    @NotNull
    public Map<String, EIGlobalVar> findGlobalVars() {
        return CachedValuesManager.getCachedValue(this,
                () -> {
                    final EIGlobalVars globalVars = PsiTreeUtil.getChildOfType(ScriptPsiFile.this,
                            EIGlobalVars.class);
                    return CachedValueProvider.Result.create(
                            EICommonUtil.toNameMap(PsiTreeUtil.getChildrenOfTypeAsList(globalVars, EIGlobalVar.class)),
                            this
                    );
                }
        );
    }

    @Nullable
    public EIGlobalVar findGlobalVar(String varName) {
        return findGlobalVars().get(varName);
    }

    @NotNull
    public Map<String, EIScriptDeclaration> findScriptDeclarations() {
        return CachedValuesManager.getCachedValue(this,
                () -> {
                    final EIDeclarations declarations = PsiTreeUtil.getChildOfType(ScriptPsiFile.this,
                            EIDeclarations.class);
                    return CachedValueProvider.Result.create(
                            EICommonUtil.toNameMap(PsiTreeUtil.getChildrenOfTypeAsList(declarations,
                                    EIScriptDeclaration.class)),
                            this
                    );
                }
        );
    }

    @Nullable
    public EIScriptDeclaration findScriptDeclaration(String scriptName) {
        return findScriptDeclarations().get(scriptName);
    }

    @NotNull
    public Map<String, EIScriptImplementation> findScriptImplementations() {
        return CachedValuesManager.getCachedValue(this,
                () -> {
                    final EIScripts implementations = PsiTreeUtil.getChildOfType(this, EIScripts.class);
                    return CachedValueProvider.Result.create(
                            EICommonUtil.toNameMap(PsiTreeUtil.getChildrenOfTypeAsList(implementations,
                                    EIScriptImplementation.class)),
                            this
                    );
                }
        );
    }

    @Nullable
    public EIScriptImplementation findScriptImplementation(String scriptName) {
        return findScriptImplementations().get(scriptName);
    }

    @NotNull
    public Map<EITypeToken, List<LookupElement>> getGlobalVarLookupElements() {
        return CachedValuesManager.getCachedValue(this,
                () -> CachedValueProvider.Result.create(getGlobalVarLookupElementsInner(), this)
        );
    }

    @NotNull
    public List<LookupElement> getScriptLookupElements() {
        return CachedValuesManager.getCachedValue(this,
                () -> CachedValueProvider.Result.create(getScriptLookupElementsInner(), this)
        );
    }

    @NotNull
    public Map<String, EIGSVar> findGSVars() {
        return CachedValuesManager.getCachedValue(this,
                () -> CachedValueProvider.Result.create(findGSVarsInner(), this)
        );
    }

    @NotNull
    public Map<Integer, EIArea> findAreas() {
        return CachedValuesManager.getCachedValue(this,
                () -> CachedValueProvider.Result.create(findAreasInner(), this)
        );
    }

    @Nullable
    public PsiMobFile getCompanionMobFile() {
        // TODO v2: proper use of tracking?
        return CachedValuesManager.getManager(getProject()).getCachedValue(this, KEY_PSI_MOB_FILE,
                new CachedCompanionPsiMobFileProvider(), true);
    }

    @Nullable
    public PsiMobObjectsBlock getCompanionMobObjectsBlock() {
        PsiMobFile mobFile = getCompanionMobFile();
        return mobFile != null ? (PsiMobObjectsBlock) mobFile.getChildren()[0] : null;
    }

    @NotNull
    private Map<EITypeToken, List<LookupElement>> getGlobalVarLookupElementsInner() {
        Map<EITypeToken, List<LookupElement>> result = new EnumMap<>(EITypeToken.class);
        for (EITypeToken type : EITypeToken.values()) {
            result.put(type, new ArrayList<>());
        }
        for (final EIGlobalVar globalVar : findGlobalVars().values()) {
            if (globalVar.getName().length() > 0 && globalVar.getType() != null) {
                LookupElement lookupElement = EILookupElementFactory.create(globalVar);
                result.get(globalVar.getType().getTypeToken()).add(lookupElement);
                result.get(EITypeToken.ANY).add(lookupElement);
            }
        }
        for (EITypeToken type : EITypeToken.values()) {
            result.put(type, Collections.unmodifiableList(result.get(type)));
        }
        return result;
    }

    @NotNull
    private List<LookupElement> getScriptLookupElementsInner() {
        List<LookupElement> result = new ArrayList<>();
        for (final EIScriptDeclaration script : findScriptDeclarations().values()) {
            if (script.getName().length() > 0) {
                result.add(EILookupElementFactory.create(script));
            }
        }
        return result;
    }

    // TODO v2: GSVars and Areas could be calculated in one PSI-tree pass, instead of two
    @NotNull
    private Map<String, EIGSVar> findGSVarsInner() {
        Stopwatch watch = Stopwatch.createStarted();
        final Map<String, EIGSVar> result = new LinkedHashMap<>(); // to preserve insertion order for structure view
        acceptChildren(new EIVisitor() {
            @Override
            public void visitFunctionCall(@NotNull EIFunctionCall call) {
                super.visitFunctionCall(call);

                if (EIGSVar.isReadOrWrite(call)) {
                    PsiElement varElement = EIGSVar.getGSVarArgument(call);
                    if (varElement != null &&
                            // TODO v2: can also be an expression, how to handle then?
                            varElement.getNode().getElementType() == ScriptTypes.CHARACTER_STRING) {
                        String varName = EIGSVar.getVarName(varElement.getText());
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
                if (!(element instanceof EIGlobalVars || element instanceof EIDeclarations)) {
                    element.acceptChildren(this);
                }
            }
        });
        LOG.debug(watch.stop().elapsed(TimeUnit.MILLISECONDS) + " findGSVarsInner");
        return result;
    }

    @NotNull
    private Map<Integer, EIArea> findAreasInner() {
        Stopwatch watch = Stopwatch.createStarted();
        Map<Integer, EIArea> result = new LinkedHashMap<>(); // to preserve insertion order for structure view
        acceptChildren(new EIVisitor() {
            @Override
            public void visitFunctionCall(@NotNull EIFunctionCall call) {
                super.visitFunctionCall(call);

                if (EIArea.isReadOrWrite(call)) {
                    PsiElement areaIdElement = EIArea.getAreaIdElement(call);
                    if (areaIdElement != null &&
                            // TODO v2: same as with GSVars
                            areaIdElement.getNode().getElementType() == ScriptTypes.FLOATNUMBER) {
                        try {
                            int areaId = Integer.parseInt(areaIdElement.getText());
                            EIArea stats = result.getOrDefault(areaId, new EIArea(areaId));
                            stats.processCall(call);
                            if (stats.isValid()) {
                                result.put(areaId, stats);
                            }
                        } catch (NumberFormatException ignored) {
                            // don't really care
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
        LOG.debug(watch.stop().elapsed(TimeUnit.MILLISECONDS) + " findAreasInner");
        return result;
    }

    private class CachedCompanionPsiMobFileProvider implements CachedValueProvider<PsiMobFile> {
        @Nullable
        @Override
        public Result<PsiMobFile> compute() {
            final Project project = getProject();
            VirtualFile vFile = getOriginalFile().getViewProvider().getVirtualFile();

            final VirtualFile mob = vFile.findFileByRelativePath("../" + vFile.getNameWithoutExtension() + ".mob");
            PsiFile psiFile = mob != null ? PsiManager.getInstance(project).findFile(mob) : null;
            return new Result<>((PsiMobFile) psiFile, PsiModificationTracker.NEVER_CHANGED);
        }
    }
}