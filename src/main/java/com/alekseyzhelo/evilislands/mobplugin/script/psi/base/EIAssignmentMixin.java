package com.alekseyzhelo.evilislands.mobplugin.script.psi.base;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIExpression;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIVariableAccess;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface EIAssignmentMixin extends EIScriptPsiElement {
    @NotNull
    List<EIExpression> getExpressionList();

    @NotNull
    EIVariableAccess getLeftSide();

    @Nullable
    EIExpression getRightSide();

    int indexOf(EIExpression expression);

    @Nullable
    PsiElement getEquals();

    boolean isComplete();
}
