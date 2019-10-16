package com.alekseyzhelo.evilislands.mobplugin.mob.psi;

import com.alekseyzhelo.evilislands.mobplugin.mob.psi.objects.PsiMobEntityBase;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.util.EILookupElementFactory;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;

// CachedValue/CachedValuesManager?
public class PsiMobObjectsBlock extends PsiMobElement {

    private Map<Integer, PsiMobEntityBase> childrenMap;
    private PsiElement[] children;
    private LookupElement[] objectLookupElements = null;

    PsiMobObjectsBlock(PsiElement parent) {
        super(parent);
    }

    void setElements(Map<Integer, PsiMobEntityBase> elements) {
        childrenMap = Collections.unmodifiableMap(elements);
        children = childrenMap.values().toArray(PsiElement.EMPTY_ARRAY);
    }

    @NotNull
    @Override
    public PsiElement[] getChildren() {
        return children;
    }

    @Nullable
    public PsiMobElement getChild(int id) {
        return childrenMap.get(id);
    }

    @NotNull
    public LookupElement[] getObjectLookupElements() {
        if (objectLookupElements == null) {
            objectLookupElements = childrenMap.values().stream()
                    .map(EILookupElementFactory::create)
                    .toArray(LookupElement[]::new);
        }
        return objectLookupElements;
    }
}
