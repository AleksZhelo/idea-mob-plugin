package com.alekseyzhelo.evilislands.mobplugin.mob.psi.objects;

import com.alekseyzhelo.eimob.objects.MobLight;
import com.alekseyzhelo.eimob.util.Float3;
import com.alekseyzhelo.evilislands.mobplugin.mob.psi.PsiMobElement;
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
    @NotNull
    public String getDoc() {
        return "<b>" + getType() + "</b>" + "<br/>" +
                "ID: " + value.getId() + "<br/>" +
                "Name: " + value.getName() + "<br/>" +
                "Location: " + value.getLocation() + "<br/>" +
                "Color (RGB): " + value.getColor() + "<br/>" +
                "Particle size: " + value.getParticleSize() + "<br/>" +
                "Shadow: " + value.getShowShadow() + "<br/>";
    }
}
