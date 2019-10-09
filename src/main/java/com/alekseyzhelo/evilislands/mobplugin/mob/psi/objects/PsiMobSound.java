package com.alekseyzhelo.evilislands.mobplugin.mob.psi.objects;

import com.alekseyzhelo.eimob.objects.MobSound;
import com.alekseyzhelo.eimob.util.Float3;
import com.alekseyzhelo.evilislands.mobplugin.mob.psi.PsiMobElement;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class PsiMobSound extends PsiMobElement {

    private MobSound value;

    public PsiMobSound(PsiElement parent, MobSound sound) {
        super(parent);
        value = sound;
    }

    @Override
    @NotNull
    public String getType() {
        return "Sound";
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
                "<b>ID: </b>" + value.getId() + "<br/>" +
                "<b>Name: </b>" + value.getName() + "<br/>" +
                "<b>Location: </b>" + value.getLocation() + "<br/>" +
                "<b>Sounds: </b>" + value.getSoundResources() + "<br/>" +
                "<b>Range: </b>" + value.getRange() + "<br/>" +
                "<b>Range2: </b>" + value.getRange2() + "<br/>" +
                "<b>Volume min: </b>" + value.getVolumeMin() + "<br/>" +
                "<b>Volume max: </b>" + value.getVolumeMax() + "<br/>" +
                "<b>Is ambient: </b>" + value.isAmbient() + "<br/>" +
                "<b>Is music: </b>" + value.isMusic() + "<br/>";
    }
}
