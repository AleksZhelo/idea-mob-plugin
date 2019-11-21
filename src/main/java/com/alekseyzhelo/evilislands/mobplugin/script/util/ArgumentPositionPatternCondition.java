package com.alekseyzhelo.evilislands.mobplugin.script.util;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIExpression;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIParams;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIScriptBlock;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIWorldScript;
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

    public ArgumentPositionPatternCondition(int position) {
        super("ArgumentPositionPatternCondition");
        this.position = position;
    }

    @Override
    public boolean accepts(@NotNull PsiElement psiElement, ProcessingContext context) {
        EIParams parent = PsiTreeUtil.getParentOfType(psiElement, EIParams.class, true,
                EIScriptBlock.class, EIWorldScript.class);
        if (parent != null) {
            List<EIExpression> expressions = parent.getExpressionList();
            return expressions.size() > position && PsiTreeUtil.isAncestor(expressions.get(position), psiElement, false);
        }
        return false;
    }
}
