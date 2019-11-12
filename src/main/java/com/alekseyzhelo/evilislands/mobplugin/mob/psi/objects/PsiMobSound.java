package com.alekseyzhelo.evilislands.mobplugin.mob.psi.objects;

import com.alekseyzhelo.eimob.objects.MobSound;
import com.alekseyzhelo.eimob.util.Float3;
import com.alekseyzhelo.evilislands.mobplugin.EIMessages;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.util.DocumentationFormatter;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class PsiMobSound extends PsiMobEntityBase {

    private final MobSound value;

    public PsiMobSound(PsiElement parent, MobSound sound) {
        super(parent);
        value = sound;
    }

    @Override
    @NotNull
    public String getObjectKind() {
        return EIMessages.message("mob.sound");
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

    @Override
    protected String getDocHeader() {
        return DocumentationFormatter.bold(getObjectKind());
    }

    @Override
    protected String getDocContent() {
        return DocumentationFormatter.bold("ID: ") + value.getId() + "<br/>" +
                DocumentationFormatter.bold("Name: ") + value.getName() + "<br/>" +
                DocumentationFormatter.bold("Location: ") + value.getLocation() + "<br/>" +
                DocumentationFormatter.bold("Sounds: ") + value.getSoundResources() + "<br/>" +
                DocumentationFormatter.bold("Range: ") + value.getRange() + "<br/>" +
                DocumentationFormatter.bold("Range2: ") + value.getRange2() + "<br/>" +
                DocumentationFormatter.bold("Volume min: ") + value.getVolumeMin() + "<br/>" +
                DocumentationFormatter.bold("Volume max: ") + value.getVolumeMax() + "<br/>" +
                DocumentationFormatter.bold("Is ambient: ") + value.isAmbient() + "<br/>" +
                DocumentationFormatter.bold("Is music: ") + value.isMusic() + "<br/>";
    }
}
