package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.lookup;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFormalParameter;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.template.Expression;
import com.intellij.codeInsight.template.ExpressionContext;
import com.intellij.codeInsight.template.Result;
import com.intellij.codeInsight.template.TextResult;
import org.jetbrains.annotations.Nullable;

class EIParamExpression extends Expression {

    private final EIFormalParameter parameter;

    EIParamExpression(EIFormalParameter param) {
        parameter = param;
    }

    @Nullable
    @Override
    public Result calculateResult(ExpressionContext context) {
        return new TextResult(parameter.getName());
    }

    @Nullable
    @Override
    public Result calculateQuickResult(ExpressionContext context) {
        return new TextResult(parameter.getName());
    }

    @Nullable
    @Override
    public LookupElement[] calculateLookupItems(ExpressionContext context) {
        return null;
        // TODO: the below is likely not necessary, standard lookup rules should apply
//                    ScriptPsiFile file = (ScriptPsiFile) context.getPsiElementAtStartOffset().getContainingFile();
//                    return file.findGlobalVars().stream()
//                            .filter((x) -> x.getType().getTypeToken() == parameter.getType().getTypeToken())
//                            .map(EILookupElementFactory::create)
//                            .toArray(LookupElement[]::new);
    }
}