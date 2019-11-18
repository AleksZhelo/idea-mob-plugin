package com.alekseyzhelo.evilislands.mobplugin.script.psi.impl;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIExpression;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIVariableAccess;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptTypes;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.base.EIAssignmentMixin;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class EIAssignmentMixinImpl extends EIScriptPsiElementImpl implements EIAssignmentMixin {

    // TODO: is concurrent access a concern?
    private volatile List<EIExpression> expressionCache;

    EIAssignmentMixinImpl(ASTNode node) {
        super(node);
    }

    private void updateCache() {
        if (expressionCache == null) {
            expressionCache = getExpressionList();
        }
    }

    @NotNull
    @Override
    public EIVariableAccess getLeftSide() {
        updateCache();
        return (EIVariableAccess) expressionCache.get(0);
    }

    @Nullable
    @Override
    public EIExpression getRightSide() {
        updateCache();
        return expressionCache.size() > 1 ? expressionCache.get(1) : null;
    }

    @Override
    public int indexOf(EIExpression expression) {
        updateCache();
        return expressionCache.indexOf(expression);
    }

    @Nullable
    @Override
    public PsiElement getEquals() {
        return findChildByType(ScriptTypes.EQUALS);
    }

    @Override
    public boolean isComplete() {
        return getEquals() != null && getRightSide() != null;
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();
        expressionCache = null;
    }
}
