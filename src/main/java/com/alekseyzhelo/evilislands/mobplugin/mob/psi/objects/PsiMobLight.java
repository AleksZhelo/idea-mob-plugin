package com.alekseyzhelo.evilislands.mobplugin.mob.psi.objects;

import com.alekseyzhelo.eimob.objects.MobLight;
import com.alekseyzhelo.eimob.util.Float3;
import com.alekseyzhelo.evilislands.mobplugin.mob.psi.PsiMobElement;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.util.DocumentationFormatter;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class PsiMobLight extends PsiMobElement {

    private MobLight value;

    public PsiMobLight(PsiElement parent, MobLight light) {
        super(parent);
        value = light;
    }

    @Override
    @NotNull
    public String getType() {
        return "Light";
    }

    @Override
    public String getName() {
        return value.getName();
    }

    @Override
    public int getId() {
        return value.getId();
    }

    @NotNull
    @Override
    public Float3 getLocation() {
        return value.getLocation();
    }

    // TODO: extract to base, together with the above
    @Override
    public String getText() {
        return String.valueOf(value.getId());
    }

    @Override
    protected String getDocHeader() {
        return DocumentationFormatter.bold(getType());
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
