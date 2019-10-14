package com.alekseyzhelo.evilislands.mobplugin.mob.psi;

import com.alekseyzhelo.eimob.util.Float3;
import com.alekseyzhelo.evilislands.mobplugin.mob.psi.objects.PsiMobEntityBase;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
            List<LookupElement> container = new ArrayList<>();
            for (PsiMobEntityBase element : childrenMap.values()) {
                container.add(LookupElementBuilder
                        .create(element.getText())
                        .withTypeText(element.getObjectKind())
                        .withPresentableText(lookupText(element))
                );
            }
            objectLookupElements = container.toArray(new LookupElement[0]);
        }
        return objectLookupElements;
    }

    private static String lookupText(PsiMobEntityBase element) {
        Float3 location = element.getLocation();
        return String.format("%-10d %s at (%.2f, %.2f, %.2f)", element.getId(), element.getName(),
                location.getX(), location.getY(), location.getZ());
    }
}
