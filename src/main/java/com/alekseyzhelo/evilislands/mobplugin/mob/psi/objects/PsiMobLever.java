package com.alekseyzhelo.evilislands.mobplugin.mob.psi.objects;

import com.alekseyzhelo.eimob.objects.MobLever;
import com.alekseyzhelo.evilislands.mobplugin.EIMessages;
import com.alekseyzhelo.evilislands.mobplugin.icon.Icons;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class PsiMobLever extends PsiMobObjectBase<MobLever> {

    public PsiMobLever(PsiElement parent, MobLever object) {
        super(parent, object);
    }

    @Override
    @NotNull
    public String getObjectKind() {
        return EIMessages.message("mob.lever");
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return Icons.Objects.LEVER;
    }
}
