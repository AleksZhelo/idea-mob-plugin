package com.alekseyzhelo.evilislands.mobplugin.mob.psi.objects;

import com.alekseyzhelo.eimob.util.Float3;
import com.alekseyzhelo.evilislands.mobplugin.mob.psi.PsiMobElement;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.util.DocumentationFormatter;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public abstract class PsiMobEntityBase extends PsiMobElement {

    PsiMobEntityBase(PsiElement parent) {
        super(parent);
    }

    @NotNull
    public abstract String getObjectKind();

    protected abstract String getDocHeader();

    protected abstract String getDocContent();

    public abstract int getId();

    @NotNull
    public abstract Float3 getLocation();

    @NotNull
    public final String getDoc() {
        return DocumentationFormatter.wrapDefinition(getDocHeader()) +
                DocumentationFormatter.wrapContent(getDocContent());
    }
}
