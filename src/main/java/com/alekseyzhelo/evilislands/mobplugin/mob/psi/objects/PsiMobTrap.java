package com.alekseyzhelo.evilislands.mobplugin.mob.psi.objects;

import com.alekseyzhelo.eimob.objects.MobTrap;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class PsiMobTrap extends PsiMobObjectDataHolder<MobTrap> {

    public PsiMobTrap(PsiElement parent, MobTrap object) {
        super(parent, object);
    }

    @Override
    @NotNull
    public String getObjectKind() {
        return "Trap";
    }
}
