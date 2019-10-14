package com.alekseyzhelo.evilislands.mobplugin.mob.psi.objects;

import com.alekseyzhelo.eimob.objects.MobLever;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class PsiMobLever extends PsiMobObjectDataHolder<MobLever> {

    public PsiMobLever(PsiElement parent, MobLever object) {
        super(parent, object);
    }

    @Override
    @NotNull
    public String getObjectKind() {
        return "Lever";
    }
}
