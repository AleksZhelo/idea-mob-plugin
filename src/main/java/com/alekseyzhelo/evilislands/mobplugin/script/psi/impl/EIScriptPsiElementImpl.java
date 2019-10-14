package com.alekseyzhelo.evilislands.mobplugin.script.psi.impl;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.base.EIScriptPsiElement;
import com.alekseyzhelo.evilislands.mobplugin.script.util.UsefulPsiTreeUtil;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Aleks on 25-07-2015.
 */
// TODO: use or remove unused code
public class EIScriptPsiElementImpl extends ASTWrapperPsiElement implements EIScriptPsiElement {

    public EIScriptPsiElementImpl(ASTNode node) {
        super(node);
    }

    private List<PsiElement> getDeclarationsToProcess(PsiElement lastParent) {
        if (this instanceof EIGlobalVars) {
            System.out.println("wow");
        }
        final boolean isBlock = this instanceof EIScriptBlock || this instanceof EIGlobalVars;
        final PsiElement stopper = isBlock ? lastParent : null;
        final List<PsiElement> result = new ArrayList<>();

        addDeclarations(result, UsefulPsiTreeUtil.getChildrenOfType(this, EIGlobalVar.class, stopper));
        addDeclarations(result, UsefulPsiTreeUtil.getChildrenOfType(this, EIFormalParameter.class, stopper));
        addDeclarations(result, PsiTreeUtil.getChildrenOfType(this, EIScriptDeclaration.class));

        // TODO something special for For blocks?
//        if (this instanceof HaxeForStatement && ((HaxeForStatement)this).getIterable() != lastParent) {
//            result.add(this);
//        }
        return result;
    }

    private static void addDeclarations(@NotNull List<PsiElement> result, @Nullable PsiElement[] items) {
        if (items != null) {
            result.addAll(Arrays.asList(items));
        }
    }
}
