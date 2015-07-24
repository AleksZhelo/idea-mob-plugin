package com.alekseyzhelo.evilislands.mobplugin.script.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import org.apache.commons.lang.ArrayUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Aleks on 25-07-2015.
 */
public class ScriptPsiElement extends ASTWrapperPsiElement {

    public ScriptPsiElement(ASTNode node) {
        super(node);
    }

}
