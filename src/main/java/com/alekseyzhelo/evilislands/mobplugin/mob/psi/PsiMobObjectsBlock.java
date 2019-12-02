package com.alekseyzhelo.evilislands.mobplugin.mob.psi;

import com.alekseyzhelo.evilislands.mobplugin.mob.psi.objects.PsiMobMapEntity;
import com.alekseyzhelo.evilislands.mobplugin.mob.psi.objects.PsiMobObject;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.lookup.EILookupElementFactory;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// CachedValue/CachedValuesManager?
public class PsiMobObjectsBlock extends PsiMobElement {

    private static final Logger LOG = Logger.getInstance(PsiMobObjectsBlock.class);

    private Map<Integer, PsiMobMapEntity> childrenMap;
    private Map<String, PsiMobMapEntity> childrenByNameMap;
    private PsiElement[] children;
    private volatile LookupElement[] objectByIdLookupElements = null;
    private volatile LookupElement[] objectByNameLookupElements = null;

    PsiMobObjectsBlock(PsiElement parent) {
        super(parent);
    }

    void setElements(Map<Integer, PsiMobMapEntity> elements) {
        childrenMap = Collections.unmodifiableMap(elements);
        childrenByNameMap = Collections.unmodifiableMap(initByNameMap(elements));
        children = childrenMap.values().toArray(PsiElement.EMPTY_ARRAY);
    }

    @NotNull
    private Map<String, PsiMobMapEntity> initByNameMap(Map<Integer, PsiMobMapEntity> elements) {
        Map<String, PsiMobMapEntity> initByName = new HashMap<>();
        for (PsiMobMapEntity entity : elements.values()) {
            final String name = entity.getName();
            if (!StringUtil.isEmpty(name)) {
                // excluding objects due to too much warning spam
                if (initByName.containsKey(name) && !(entity instanceof PsiMobObject)) {
                    LOG.warn(String.format("Non-unique unit name %s in mob file %s", name, getParent()));
                }
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
    public PsiMobMapEntity getChild(int id) {
        return childrenMap.get(id);
    }

    @Nullable
    public PsiMobMapEntity getChild(@NotNull String name) {
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
