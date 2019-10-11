package com.alekseyzhelo.evilislands.mobplugin.mob.psi.objects;

import com.alekseyzhelo.eimob.objects.MobTrap;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class PsiMobTrap extends PsiMobObjectBase<MobTrap> {

    public PsiMobTrap(PsiElement parent, MobTrap object) {
        super(parent, object);
    }

    @Override
    @NotNull
    public String getType() {
        return "Trap";
    }
}