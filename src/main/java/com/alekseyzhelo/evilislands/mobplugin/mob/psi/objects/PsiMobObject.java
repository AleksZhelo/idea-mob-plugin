package com.alekseyzhelo.evilislands.mobplugin.mob.psi.objects;

import com.alekseyzhelo.eimob.objects.MobObject;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class PsiMobObject extends PsiMobObjectBase<MobObject> {

    public PsiMobObject(PsiElement parent, MobObject object) {
        super(parent, object);
    }

    @Override
    @NotNull
    public String getObjectKind() {
        return "Object";
    }
}
