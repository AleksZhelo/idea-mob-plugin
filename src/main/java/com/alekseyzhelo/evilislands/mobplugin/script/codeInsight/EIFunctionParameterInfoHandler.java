package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.lang.parameterInfo.*;
import com.intellij.openapi.project.DumbAware;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class EIFunctionParameterInfoHandler implements ParameterInfoHandler<EIFunctionCall, List<EIFormalParameter>>, DumbAware {

    @Override
    public boolean couldShowInLookup() {
        return true;
    }

    @Nullable
    @Override
    // TODO: don't understand this method
    public Object[] getParametersForLookup(LookupElement item, ParameterInfoContext context) {
//        return getFormalParameters(findCall(context.getFile(), context.getOffset()));
        return null;
    }

    @Nullable
    @Override
    public EIFunctionCall findElementForParameterInfo(@NotNull CreateParameterInfoContext context) {
        EIFunctionCall call = findCall(context.getFile(), context.getOffset());
        context.setItemsToShow(getFormalParameters(call));
        return call;
    }

    @Override
    public void showParameterInfo(@NotNull EIFunctionCall element, @NotNull CreateParameterInfoContext context) {
        context.showHint(element, context.getOffset(), this);
        context.setHighlightedElement(element);
    }

    @Nullable
    @Override
    public EIFunctionCall findElementForUpdatingParameterInfo(@NotNull UpdateParameterInfoContext context) {
        return findCall(context.getFile(), context.getOffset());
    }

    @Override
    public void updateParameterInfo(@NotNull EIFunctionCall call, @NotNull UpdateParameterInfoContext context) {
        if (context.getParameterOwner() != call) {
            context.removeHint();
            return;
        }

        EIParams params = call.getParams();
        if (params != null) {
            int paramIndex = calculateCurrentParamIndex(params, context.getFile().findElementAt(context.getOffset()));
            context.setCurrentParameter(paramIndex);
            if (paramIndex != -1) {
                context.setHighlightedParameter(params.getExpressionList().get(paramIndex));
            }
        }
    }

    @Override
    public void updateUI(List<EIFormalParameter> parameters, @NotNull ParameterInfoUIContext context) {
        int paramIndex = context.getCurrentParameterIndex();

        if (context.isSingleParameterInfo()) {
            if (paramIndex != -1) {
                String hint = parameters.get(paramIndex).getText().trim();
                context.setupUIComponentPresentation(hint, 0, hint.length(), false,
                        false, false, context.getDefaultParameterColor());
            }
        } else {
            String hint = parameters.stream()
                    .reduce("", (u, x) -> u + x.getText().trim() + ", ", String::concat);
            hint = hint.substring(0, hint.length() - 2);
            String currentParamText = paramIndex != -1 ? parameters.get(paramIndex).getText().trim() : "";
            int currentParamStart = hint.indexOf(currentParamText);
            context.setupUIComponentPresentation(hint, currentParamStart, currentParamStart + currentParamText.length(),
                    false, false, false, context.getDefaultParameterColor());
        }
    }

    @Nullable
    private EIFunctionCall findCall(PsiFile file, int offset) {
        PsiElement element = file.findElementAt(offset);
        return PsiTreeUtil.getParentOfType(element, EIFunctionCall.class, false);
    }

    private Object[] getFormalParameters(EIFunctionCall call) {
        if (call != null) {
            PsiElement declaration = call.getReference().resolve();
            if (declaration instanceof EIFunctionDeclaration) {
                return new List[]{((EIFunctionDeclaration) declaration).getFormalParameterList()};
            } else if (declaration instanceof EIScriptDeclaration) {
                return new List[]{((EIScriptDeclaration) declaration).getFormalParameterList()};
            }
        }
        return null;
    }

    private int calculateCurrentParamIndex(@NotNull EIParams params, PsiElement element) {
        if (element.getNode().getElementType() == ScriptTypes.RPAREN) {
            element = element.getPrevSibling().getLastChild();
        }
        if (PsiTreeUtil.getParentOfType(element, EIExpression.class, false, EIParams.class) == null) {
            if (element.getNode().getElementType() == ScriptTypes.COMMA) {
                element = PsiTreeUtil.getPrevSiblingOfType(element, EIExpression.class);
            } else {
                element = PsiTreeUtil.getNextSiblingOfType(element, EIExpression.class);
            }
        }
        final PsiElement finalElement = element;

        if (finalElement != null) {
            List<EIExpression> paramList = params.getExpressionList();
            Optional<EIExpression> expression = paramList.stream()
                    .filter((x) -> PsiTreeUtil.isAncestor(x, finalElement, false))
                    .findFirst();

            return expression.map(paramList::indexOf).orElse(-1);
        } else {
            return -1;
        }
    }
}
