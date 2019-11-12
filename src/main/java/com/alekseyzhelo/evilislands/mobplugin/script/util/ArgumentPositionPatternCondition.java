package com.alekseyzhelo.evilislands.mobplugin.script.util;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIExpression;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFunctionCall;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIParams;
import com.intellij.patterns.PatternCondition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ArgumentPositionPatternCondition extends PatternCondition<PsiElement> {

    public static final ArgumentPositionPatternCondition FIRST_ARGUMENT = new ArgumentPositionPatternCondition(0);
    public static final ArgumentPositionPatternCondition SECOND_ARGUMENT = new ArgumentPositionPatternCondition(1);

    private final int position;

    // TODO: flag whether function call parent is already detected? (different stop element if not)
    public ArgumentPositionPatternCondition(int position) {
        super("ArgumentPositionPatternCondition");
        this.position = position;
    }

    @Override
    // TODO: optimize for calls with guaranteed params super-parent? (as it's always called so?)
    public boolean accepts(@NotNull PsiElement psiElement, ProcessingContext context) {
        EIParams parent = PsiTreeUtil.getParentOfType(psiElement, EIParams.class, true, EIFunctionCall.class);
        if (parent != null) {
            List<EIExpression> expressions = parent.getExpressionList();
            return expressions.size() > position && PsiTreeUtil.isAncestor(expressions.get(position), psiElement, false);
        }
        return false;
    }
}
