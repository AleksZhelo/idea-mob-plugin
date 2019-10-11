package com.alekseyzhelo.evilislands.mobplugin.mob.psi.objects;

import com.alekseyzhelo.eimob.objects.MobObjectDataHolder;
import com.alekseyzhelo.eimob.util.Float3;
import com.alekseyzhelo.evilislands.mobplugin.mob.psi.PsiMobElement;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.util.DocumentationFormatter;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

abstract class PsiMobObjectBase<T extends MobObjectDataHolder> extends PsiMobElement {

    protected T value;

    PsiMobObjectBase(PsiElement parent, T object) {
        super(parent);
        value = object;
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
    @NotNull
    protected String getDocHeader() {
        return DocumentationFormatter.bold(getObjectKind());
    }

    @Override
    protected String getDocContent() {
        return DocumentationFormatter.bold("ID: ") + value.getId() + "<br/>" +
                DocumentationFormatter.bold("Name: ") + value.getName() + "<br/>" +
                DocumentationFormatter.bold("Model name: ") + value.getModelName() + "<br/>" +
                DocumentationFormatter.bold("Player: ") + value.getNPlayer() + "<br/>" +
                DocumentationFormatter.bold("Location: ") + value.getLocation() + "<br/>" +
                DocumentationFormatter.bold("Textures: ") + value.getPrimaryTexture() + ", " + value.getSecondaryTexture() + "<br/>" +
                DocumentationFormatter.bold("Quest unit: ") + value.isQuestUnit() + "<br/>";
    }
}
