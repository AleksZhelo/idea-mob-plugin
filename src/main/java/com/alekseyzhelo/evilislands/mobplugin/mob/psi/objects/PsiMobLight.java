package com.alekseyzhelo.evilislands.mobplugin.mob.psi.objects;

import com.alekseyzhelo.eimob.objects.MobLight;
import com.alekseyzhelo.evilislands.mobplugin.EIMessages;
import com.alekseyzhelo.evilislands.mobplugin.icon.Icons;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.util.DocumentationFormatter;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class PsiMobLight extends PsiMobMapEntity<MobLight> {

    public PsiMobLight(PsiElement parent, MobLight light) {
        super(parent, light);
    }

    @Override
    @NotNull
    public String getObjectKind() {
        return EIMessages.message("mob.light");
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return Icons.Objects.LIGHT;
    }

    @Override
    protected String getDocHeader() {
        return DocumentationFormatter.bold(getObjectKind());
    }

    @Override
    protected String getDocContent() {
        return DocumentationFormatter.bold("ID: ") + value.getId() + "<br/>" +
                DocumentationFormatter.bold("Name: ") + value.getName() + "<br/>" +
                DocumentationFormatter.bold("Location: ") + value.getLocation() + "<br/>" +
                DocumentationFormatter.bold("Color (RGB): ") + value.getColor() + "<br/>" +
                DocumentationFormatter.bold("Particle size: ") + value.getParticleSize() + "<br/>" +
                DocumentationFormatter.bold("Shadow: ") + value.getShowShadow() + "<br/>";
    }
}
