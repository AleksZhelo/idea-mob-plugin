package com.alekseyzhelo.evilislands.mobplugin.script.psi.references;

import com.alekseyzhelo.evilislands.mobplugin.mob.psi.PsiMobObjectsBlock;
import com.alekseyzhelo.evilislands.mobplugin.mob.psi.objects.PsiMobEntityBase;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EILiteral;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class MobObjectReferenceByName extends MobObjectReferenceBase<String> {

    public MobObjectReferenceByName(@NotNull EILiteral element, TextRange rangeInElement) {
        super(element, rangeInElement, "error.wrong.object.name");
    }

    @NotNull
    @Override
    protected LookupElement[] getObjectLookupElements(@NotNull PsiMobObjectsBlock objectsBlock) {
        return objectsBlock.getObjectByNameLookupElements();
    }

    @NotNull
    @Override
    public String getElementId() {
        return getCanonicalText();
    }

    @Nullable
    @Override
    protected PsiMobEntityBase findMobEntity(@NotNull PsiMobObjectsBlock objectsBlock) {
        return objectsBlock.getChild(getElementId());
    }
}
