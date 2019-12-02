package com.alekseyzhelo.evilislands.mobplugin.mob.psi.objects;

import com.alekseyzhelo.eimob.objects.MobSound;
import com.alekseyzhelo.evilislands.mobplugin.EIMessages;
import com.alekseyzhelo.evilislands.mobplugin.icon.Icons;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.util.DocumentationFormatter;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class PsiMobSound extends PsiMobMapEntity<MobSound> {

    public PsiMobSound(PsiElement parent, MobSound sound) {
        super(parent, sound);
    }

    @Override
    @NotNull
    public String getObjectKind() {
        return EIMessages.message("mob.sound");
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return Icons.Objects.SOUND;
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
