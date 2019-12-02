package com.alekseyzhelo.evilislands.mobplugin.script.psi.references;

import com.alekseyzhelo.evilislands.mobplugin.mob.psi.PsiMobObjectsBlock;
import com.alekseyzhelo.evilislands.mobplugin.mob.psi.objects.PsiMobMapEntity;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EILiteral;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class MobObjectReferenceById extends MobObjectReferenceBase<Integer> {

    public MobObjectReferenceById(@NotNull EILiteral element, TextRange rangeInElement) {
        super(element, rangeInElement, "error.wrong.object.id");
    }

    @Nullable
    @Override
    public Integer getElementId() {
        try {
            return Integer.parseInt(getCanonicalText());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @NotNull
    @Override
    protected LookupElement[] getObjectLookupElements(@NotNull PsiMobObjectsBlock objectsBlock) {
        return objectsBlock.getObjectByIdLookupElements();
    }

    @Nullable
    @Override
    protected PsiMobMapEntity findMobEntity(@NotNull PsiMobObjectsBlock objectsBlock) {
        final Integer id = getElementId();
        return id != null ? objectsBlock.getChild(id) : null;
    }
}
