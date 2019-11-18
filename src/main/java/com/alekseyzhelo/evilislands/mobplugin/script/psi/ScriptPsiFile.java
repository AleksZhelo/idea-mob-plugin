package com.alekseyzhelo.evilislands.mobplugin.script.psi;

import com.alekseyzhelo.evilislands.mobplugin.mob.psi.PsiMobFile;
import com.alekseyzhelo.evilislands.mobplugin.mob.psi.PsiMobObjectsBlock;
import com.alekseyzhelo.evilislands.mobplugin.script.EIScriptLanguage;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.lookup.EILookupElementFactory;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.impl.EIArea;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.impl.EIGSVar;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptResolveUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EITypeToken;
import com.alekseyzhelo.evilislands.mobplugin.script.util.UsefulPsiTreeUtil;
import com.google.common.base.Stopwatch;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.util.Key;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.TimeUnit;

// TODO: processChildren implementation needed?
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
        return "ScriptFile: " + getName();
    }

    // TODO this and below: find or get?
    //  also: should better be cached? (mechanism definitely exists, worth it to include here? stubIndex?)
    //  | done with CachedValue; investigate stubs?
    // TODO: return Maps instead of lists?
    @NotNull
    public List<EIGlobalVar> findGlobalVars() {
        final EIGlobalVars globalVars = PsiTreeUtil.getChildOfType(ScriptPsiFile.this, EIGlobalVars.class);
        return CachedValuesManager.getCachedValue(this,
                () -> CachedValueProvider.Result.create(PsiTreeUtil.getChildrenOfTypeAsList(globalVars, EIGlobalVar.class),
                        globalVars != null ? globalVars : this));
    }

    @Nullable
    public EIGlobalVar findGlobalVar(String varName) {
        return EIScriptResolveUtil.matchByName(varName, findGlobalVars());
    }

    @NotNull
    public List<EIScriptDeclaration> findScriptDeclarations() {
        final EIDeclarations declarations = PsiTreeUtil.getChildOfType(ScriptPsiFile.this, EIDeclarations.class);
        return CachedValuesManager.getCachedValue(this,
                () -> CachedValueProvider.Result.create(PsiTreeUtil.getChildrenOfTypeAsList(declarations, EIScriptDeclaration.class),
                        declarations != null ? declarations : this));
    }

    @Nullable
    // TODO: implement via Maps for faster access?
    public EIScriptDeclaration findScriptDeclaration(String scriptName) {
        return EIScriptResolveUtil.matchByName(scriptName, findScriptDeclarations());
    }

    @NotNull
    public List<EIScriptImplementation> findScriptImplementations() {
        final EIScripts implementations = PsiTreeUtil.getChildOfType(this, EIScripts.class);
        return CachedValuesManager.getCachedValue(this,
                () -> CachedValueProvider.Result.create(PsiTreeUtil.getChildrenOfTypeAsList(implementations, EIScriptImplementation.class),
                        implementations != null ? implementations : this));
    }

    @Nullable
    public EIScriptImplementation findScriptImplementation(String scriptName) {
        return EIScriptResolveUtil.matchByName(scriptName, findScriptImplementations());
    }

    @NotNull
    public Map<EITypeToken, List<LookupElement>> getGlobalVarLookupElements() {
        EIGlobalVars globalVars = findChildByClass(EIGlobalVars.class);
        return CachedValuesManager.getCachedValue(this,
                () -> CachedValueProvider.Result.create(getGlobalVarLookupElementsInner(),
                        globalVars != null ? globalVars : this));
    }

    @NotNull
    public List<LookupElement> getScriptLookupElements() {
        final EIDeclarations declarations = PsiTreeUtil.getChildOfType(this, EIDeclarations.class);
        return CachedValuesManager.getCachedValue(this,
                () -> CachedValueProvider.Result.create(getScriptLookupElementsInner(),
                        declarations != null ? declarations : this));
    }

    @NotNull
    public Map<String, EIGSVar> findGSVars() {
        return CachedValuesManager.getCachedValue(this,
                () -> CachedValueProvider.Result.create(findGSVarsInner(), this));
    }

    @NotNull
    public Map<Integer, EIArea> findAreas() {
        return CachedValuesManager.getCachedValue(this,
                () -> CachedValueProvider.Result.create(findAreasInner(), this));
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

    @NotNull
    // TODO: improve?
    private Map<EITypeToken, List<LookupElement>> getGlobalVarLookupElementsInner() {
        Map<EITypeToken, List<LookupElement>> result = new EnumMap<>(EITypeToken.class);
        List<LookupElement> floatVars = new ArrayList<>();
        List<LookupElement> stringVars = new ArrayList<>();
        List<LookupElement> objectVars = new ArrayList<>();
        List<LookupElement> groupVars = new ArrayList<>();
        List<LookupElement> allVars = new ArrayList<>();
        for (final EIGlobalVar globalVar : findGlobalVars()) {
            if (globalVar.getName().length() > 0 && globalVar.getType() != null) {
                LookupElement lookupElement = EILookupElementFactory.create(globalVar);
                switch (globalVar.getType().getTypeToken()) {
                    case VOID:
                        break;
                    case FLOAT:
                        floatVars.add(lookupElement);
                        break;
                    case STRING:
                        stringVars.add(lookupElement);
                        break;
                    case OBJECT:
                        objectVars.add(lookupElement);
                        break;
                    case GROUP:
                        groupVars.add(lookupElement);
                        break;
                }
            }
        }
        allVars.addAll(floatVars);
        allVars.addAll(stringVars);
        allVars.addAll(objectVars);
        allVars.addAll(groupVars);
        result.put(EITypeToken.VOID, Collections.emptyList());
        result.put(EITypeToken.FLOAT, Collections.unmodifiableList(floatVars));
        result.put(EITypeToken.STRING, Collections.unmodifiableList(stringVars));
        result.put(EITypeToken.OBJECT, Collections.unmodifiableList(objectVars));
        result.put(EITypeToken.GROUP, Collections.unmodifiableList(groupVars));
        result.put(EITypeToken.ANY, Collections.unmodifiableList(allVars));
        return result;
    }

    @NotNull
    private List<LookupElement> getScriptLookupElementsInner() {
        List<LookupElement> result = new ArrayList<>();
        for (final EIScriptDeclaration script : findScriptDeclarations()) {
            if (script.getName().length() > 0) {
                result.add(EILookupElementFactory.create(script));
            }
        }
        return result;
    }

    // TODO: implement with a HighlightingPass?
    // TODO: GSVars and Areas could be calculated in one PSI-tree pass, instead of two
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
                            // TODO: can also be an expression, how to handle then? a reference goes to a formal param
                            //  for instance, instead of the actual parameter
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
        LOG.warn(watch.stop().elapsed(TimeUnit.MILLISECONDS) + " findGSVarsInner");
        return result;
    }

    // TODO: not sure about this one
//    @NotNull
//    private Map<String, EIGSVar> findGSVarsInnerLiteral() {
//        Stopwatch watch = Stopwatch.createStarted();
//        final Map<String, EIGSVar> result = new LinkedHashMap<>(); // to preserve insertion order for structure view
//        acceptChildren(new EIVisitor() {
//            @Override
//            public void visitLiteral(@NotNull EILiteral literal) {
//                if (literal.getNode().getFirstChildNode().getElementType() == ScriptTypes.CHARACTER_STRING) {
//                    EIFunctionCall call = UsefulPsiTreeUtil.getParentFunctionCall(literal);
//                    if (call != null && EIGSVar.isReadOrWrite(call)
//                            && call.getParams().getExpressionList().indexOf(literal) == 1) {
//                        String varName = EIGSVar.getVarName(literal.getText());
//                        EIGSVar stats = result.getOrDefault(varName, new EIGSVar(varName));
//                        stats.processCall(call);
//                        if (stats.isValid()) {
//                            result.put(varName, stats);
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void visitElement(PsiElement element) {
//                super.visitElement(element);
//                if (!(element instanceof EIGlobalVars || element instanceof EIDeclarations)) {
//                    element.acceptChildren(this);
//                }
//            }
//        });
//        LOG.warn(watch.stop().elapsed(TimeUnit.MILLISECONDS) + " findGSVarsInnerLiteral");
//        return result;
//    }

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
        LOG.warn(watch.stop().elapsed(TimeUnit.MILLISECONDS) + " findAreasInner");
        return result;
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
            LOG.warn("CachedCompanionPsiMobFileProvider");
            return new Result<>(result, PsiModificationTracker.NEVER_CHANGED);
        }
    }
}