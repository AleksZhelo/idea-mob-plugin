package com.alekseyzhelo.evilislands.mobplugin.mob.psi.objects;

import com.alekseyzhelo.eimob.objects.MobObjectBase;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.util.DocumentationFormatter;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

abstract class PsiMobObjectBase<T extends MobObjectBase> extends PsiMobMapEntity<T> {

    PsiMobObjectBase(PsiElement parent, T object) {
        super(parent, object);
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
