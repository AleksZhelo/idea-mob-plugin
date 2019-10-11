package com.alekseyzhelo.evilislands.mobplugin.mob.psi.objects;

import com.alekseyzhelo.eimob.objects.MobFlame;
import com.alekseyzhelo.eimob.util.Float3;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class PsiMobFlame extends PsiMobObjectBase<MobFlame> {

    public PsiMobFlame(PsiElement parent, MobFlame object) {
        super(parent, object);
    }

    @Override
    @NotNull
    public String getObjectKind() {
        return "Flame";
    }

    @NotNull
    @Override
    public Float3 getLocation() {
        return value.getFlameLocation(); // TODO: correct?
    }
}
