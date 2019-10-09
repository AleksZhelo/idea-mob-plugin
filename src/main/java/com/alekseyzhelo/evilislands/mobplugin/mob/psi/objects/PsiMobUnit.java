package com.alekseyzhelo.evilislands.mobplugin.mob.psi.objects;

import com.alekseyzhelo.eimob.objects.MobUnit;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class PsiMobUnit extends PsiMobObjectBase<MobUnit> {

    public PsiMobUnit(PsiElement parent, MobUnit object) {
        super(parent, object);
    }

    @Override
    @NotNull
    public String getType() {
        return "Unit";
    }

    @Override
    @NotNull
    public String getDoc() {
        String doc = super.getDoc();
        return doc + "<b>Use mob info: </b>" + value.getUseMobInfo() + "<br/>";
    }
}
