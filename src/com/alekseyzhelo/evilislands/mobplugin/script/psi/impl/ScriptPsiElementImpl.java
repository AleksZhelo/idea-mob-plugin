package com.alekseyzhelo.evilislands.mobplugin.script.psi.impl;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptPsiElement;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import org.apache.commons.lang.ArrayUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Aleks on 25-07-2015.
 */
public class ScriptPsiElementImpl extends ASTWrapperPsiElement implements ScriptPsiElement{

    public ScriptPsiElementImpl(ASTNode node) {
        super(node);
    }

}
