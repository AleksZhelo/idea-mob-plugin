package com.alekseyzhelo.evilislands.mobplugin.mob.psi.objects;

import com.alekseyzhelo.eimob.objects.MobFlame;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class PsiMobFlame extends PsiMobObjectBase<MobFlame> {

    public PsiMobFlame(PsiElement parent, MobFlame object) {
        super(parent, object);
    }

    @Override
    @NotNull
    public String getType() {
        return "Flame";
    }
}
