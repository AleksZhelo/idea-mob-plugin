package com.alekseyzhelo.evilislands.mobplugin.script.psi.impl;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.base.EIScriptPsiElement;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Aleks on 25-07-2015.
 */
public class EIScriptPsiElementImpl extends ASTWrapperPsiElement implements EIScriptPsiElement {

    public EIScriptPsiElementImpl(ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public SearchScope getUseScope() {
        return new LocalSearchScope(getContainingFile());
    }
}
