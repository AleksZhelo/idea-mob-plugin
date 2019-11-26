package com.alekseyzhelo.evilislands.mobplugin.mob.psi.objects;

import com.alekseyzhelo.eimob.objects.MobUnit;
import com.alekseyzhelo.evilislands.mobplugin.EIMessages;
import com.alekseyzhelo.evilislands.mobplugin.icon.Icons;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.util.DocumentationFormatter;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class PsiMobUnit extends PsiMobObjectDataHolder<MobUnit> {

    public PsiMobUnit(PsiElement parent, MobUnit object) {
        super(parent, object);
    }

    @Override
    @NotNull
    public String getObjectKind() {
        return EIMessages.message("mob.unit");
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return Icons.Objects.UNIT;
    }

    @Override
    protected String getDocContent() {
        return super.getDocContent() + DocumentationFormatter.bold("Use mob info: ") + value.getUseMobInfo() + "<br/>";
    }
}
