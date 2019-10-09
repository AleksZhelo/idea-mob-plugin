package com.alekseyzhelo.evilislands.mobplugin.mob.psi.objects;

import com.alekseyzhelo.eimob.objects.MobParticle;
import com.alekseyzhelo.eimob.util.Float3;
import com.alekseyzhelo.evilislands.mobplugin.mob.psi.PsiMobElement;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class PsiMobParticle extends PsiMobElement {

    private MobParticle value;

    public PsiMobParticle(PsiElement parent, MobParticle particle) {
        super(parent);
        value = particle;
    }

    @Override
    @NotNull
    public String getType() {
        return "Particle";
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

    @Override
    public String getText() {
        return String.valueOf(value.getId());
    }

    @NotNull
    @Override
    public String getDoc() {
        return "<b>" + getType() + "</b>" + "<br/>" +
                "ID: " + value.getId() + "<br/>" +
                "Name: " + value.getName() + "<br/>" +
                "Location: " + value.getLocation() + "<br/>" +
                "Scale: " + value.getScale() + "<br/>" +
                "Type: " + value.getType() + "<br/>";
    }
}
