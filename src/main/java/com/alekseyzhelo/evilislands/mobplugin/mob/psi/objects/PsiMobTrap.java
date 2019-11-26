package com.alekseyzhelo.evilislands.mobplugin.mob.psi.objects;

import com.alekseyzhelo.eimob.objects.MobTrap;
import com.alekseyzhelo.evilislands.mobplugin.EIMessages;
import com.alekseyzhelo.evilislands.mobplugin.icon.Icons;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class PsiMobTrap extends PsiMobObjectDataHolder<MobTrap> {

    public PsiMobTrap(PsiElement parent, MobTrap object) {
        super(parent, object);
    }

    @Override
    @NotNull
    public String getObjectKind() {
        return EIMessages.message("mob.trap");
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return Icons.Objects.TRAP;
    }
}
