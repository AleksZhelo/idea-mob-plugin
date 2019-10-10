package com.alekseyzhelo.evilislands.mobplugin.mob.psi.objects;

import com.alekseyzhelo.eimob.objects.MobObjectDataHolder;
import com.alekseyzhelo.eimob.util.Float3;
import com.alekseyzhelo.evilislands.mobplugin.mob.psi.PsiMobElement;
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
    public String getDoc() {
        return "<b>" + getType() + "</b>" + "<br/>" +
                "<b>ID: </b>" + value.getId() + "<br/>" +
                "<b>Name: </b>" + value.getName() + "<br/>" +
                "<b>Model name: </b> " + value.getModelName() + "<br/>" +
                "<b>Player: </b> " + value.getNPlayer() + "<br/>" +
                "<b>Location: </b> " + value.getLocation() + "<br/>" +
                "<b>Textures: </b> " + value.getPrimaryTexture() + ", " + value.getSecondaryTexture() + "<br/>" +
                "<b>Quest unit: </b> " + value.isQuestUnit() + "<br/>";
    }
}
