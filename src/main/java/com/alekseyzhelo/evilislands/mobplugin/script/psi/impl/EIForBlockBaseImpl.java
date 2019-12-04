package com.alekseyzhelo.evilislands.mobplugin.script.psi.impl;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIExpression;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.base.EIExpressionBase;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.base.EIForBlockBase;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EITypeToken;
import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class EIForBlockBaseImpl extends EIScriptPsiElementImpl implements EIForBlockBase {

    private volatile List<EIExpression> expressionCache;

    public EIForBlockBaseImpl(ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public List<EIExpression> getArguments() {
        return getExpressionsCached();
    }

    @Override
    public int indexOfArgument(@NotNull EIExpression expression) {
        return getExpressionsCached().indexOf(expression);
    }

    @NotNull
    @Override
    public EITypeToken[] getArgumentTypes() {
        return getExpressionsCached().stream().map(EIExpressionBase::getType).toArray(EITypeToken[]::new);
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();
        expressionCache = null;
    }

    @NotNull
    private List<EIExpression> getExpressionsCached() {
        List<EIExpression> expressions = expressionCache;
        if (expressions == null) {
            expressionCache = expressions = getExpressionList();
        }
        return expressions;
    }

    @NotNull
    private List<EIExpression> getExpressionList() {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, EIExpression.class);
    }
}
