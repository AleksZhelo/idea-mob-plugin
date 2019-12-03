package com.alekseyzhelo.evilislands.mobplugin.mob.psi.objects;

import com.alekseyzhelo.eimob.objects.MobMapEntity;
import com.alekseyzhelo.eimob.types.Float3;
import com.alekseyzhelo.evilislands.mobplugin.mob.psi.PsiMobElement;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.util.DocumentationFormatter;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public abstract class PsiMobMapEntity<T extends MobMapEntity> extends PsiMobElement {

    protected final T value;

    PsiMobMapEntity(PsiElement parent, T value) {
        super(parent);
        this.value = value;
    }

    @NotNull
    public abstract String getObjectKind();

    protected abstract String getDocHeader();

    // TODO v2: finalize doc fields, move field names to EIMessages
    protected abstract String getDocContent();

    public final String getName() {
        return value.getName();
    }

    public final int getId() {
        return value.getId();
    }

    @NotNull
    public final Float3 getLocation() {
        return value.getLocation();
    }

    @NotNull
    public final String getDoc() {
        return DocumentationFormatter.wrapDefinition(getDocHeader()) +
                DocumentationFormatter.wrapContent(getDocContent());
    }

    @Override
    public String getText() {
        return String.valueOf(getId());
    }
}
