package com.alekseyzhelo.evilislands.mobplugin.mob.psi;

import com.alekseyzhelo.evilislands.mobplugin.mob.psi.objects.PsiMobEntityBase;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.lookup.EILookupElementFactory;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// CachedValue/CachedValuesManager?
public class PsiMobObjectsBlock extends PsiMobElement {

    private Map<Integer, PsiMobEntityBase> childrenMap;
    private Map<String, PsiMobEntityBase> childrenByNameMap;
    private PsiElement[] children;
    private volatile LookupElement[] objectByIdLookupElements = null;
    private volatile LookupElement[] objectByNameLookupElements = null;

    PsiMobObjectsBlock(PsiElement parent) {
        super(parent);
    }

    void setElements(Map<Integer, PsiMobEntityBase> elements) {
        childrenMap = Collections.unmodifiableMap(elements);
        childrenByNameMap = Collections.unmodifiableMap(initByNameMap(elements));
        children = childrenMap.values().toArray(PsiElement.EMPTY_ARRAY);
    }

    @NotNull
    private Map<String, PsiMobEntityBase> initByNameMap(Map<Integer, PsiMobEntityBase> elements) {
        Map<String, PsiMobEntityBase> initByName = new HashMap<>();
        for (PsiMobEntityBase entity : elements.values()) {
            final String name = entity.getName();
            if (!StringUtil.isEmpty(name)) {
                // TODO: warn about non-unique object names - somewhere in EIMob, actually
                initByName.putIfAbsent(name, entity);
            }
        }
        return initByName;
    }

    @NotNull
    @Override
    public PsiElement[] getChildren() {
        return children;
    }

    @Nullable
    public PsiMobEntityBase getChild(int id) {
        return childrenMap.get(id);
    }

    @Nullable
    public PsiMobEntityBase getChild(@NotNull String name) {
        return childrenByNameMap.get(name);
    }

    @NotNull
    public LookupElement[] getObjectByIdLookupElements() {
        if (objectByIdLookupElements == null) {
            objectByIdLookupElements = childrenMap.values().stream()
                    .map(EILookupElementFactory::create)
                    .toArray(LookupElement[]::new);
        }
        return objectByIdLookupElements;
    }

    @NotNull
    public LookupElement[] getObjectByNameLookupElements() {
        if (objectByNameLookupElements == null) {
            objectByNameLookupElements = childrenByNameMap.values().stream()
                    .map(EILookupElementFactory::createByName)
                    .toArray(LookupElement[]::new);
        }
        return objectByNameLookupElements;
    }
}
