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

    private volatile List<EIExpression> expressionCache;

    EIAssignmentMixinImpl(ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public EIVariableAccess getLeftSide() {
        return (EIVariableAccess) getExpressionsCached().get(0);
    }

    @Nullable
    @Override
    public EIExpression getRightSide() {
        List<EIExpression> expressions = getExpressionsCached();
        return expressions.size() > 1 ? expressions.get(1) : null;
    }

    @Override
    public int indexOf(EIExpression expression) {
        return getExpressionsCached().indexOf(expression);
    }

    @Nullable
    @Override
    public PsiElement getEquals() {
        return findChildByType(ScriptTypes.EQUALS);
    }

    @Override
    public boolean isComplete() {
        final PsiElement equals = getEquals();
        final PsiElement rightSide = getRightSide();
        return equals != null && rightSide != null
                && equals.getStartOffsetInParent() < rightSide.getStartOffsetInParent();
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();
        expressionCache = null;
    }

    private List<EIExpression>  getExpressionsCached() {
        List<EIExpression> expressions = expressionCache;
        if (expressions == null) {
            expressionCache = expressions = getExpressionList();
        }
        return expressions;
    }
}
