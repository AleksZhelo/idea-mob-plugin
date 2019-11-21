package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.base.EICallableDeclaration;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.lang.parameterInfo.*;
import com.intellij.openapi.project.DumbAware;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class EIFunctionParameterInfoHandler implements ParameterInfoHandlerWithTabActionSupport<EIParams, List<EIFormalParameter>, EIExpression>, DumbAware {

    private static final Set<Class> ourArgumentListAllowedParentClassesSet
            = ContainerUtil.newHashSet(EIFunctionCall.class);
    private static final Set<Class> ourStopSearch = Collections.singleton(EIScriptBlock.class);

    @Override
    public boolean couldShowInLookup() {
        return true;
    }

    @Nullable
    @Override
    public Object[] getParametersForLookup(LookupElement item, ParameterInfoContext context) {
        return null;  // TODO v2: should implement?
    }

    @Nullable
    @Override
    public EIParams findElementForParameterInfo(@NotNull CreateParameterInfoContext context) {
        EIFunctionCall call = findCall(context.getFile(), context.getOffset());
        context.setItemsToShow(getFormalParameters(call));
        return call != null ? call.getParams() : null;
    }

    @Override
    public void showParameterInfo(@NotNull EIParams element, @NotNull CreateParameterInfoContext context) {
        context.showHint(element, context.getOffset(), this);
    }

    @Nullable
    @Override
    public EIParams findElementForUpdatingParameterInfo(@NotNull UpdateParameterInfoContext context) {
        EIFunctionCall call = findCall(context.getFile(), context.getOffset());
        return call != null ? call.getParams() : null;
    }

    @Override
    public void updateParameterInfo(@NotNull EIParams parameterOwner, @NotNull UpdateParameterInfoContext context) {
        if (context.getParameterOwner() != parameterOwner) {
            context.removeHint();
            return;
        }

        context.setCurrentParameter(ParameterInfoUtils.getCurrentParameterIndex(
                parameterOwner.getNode(), context.getOffset(), ScriptTypes.COMMA));
    }

    @Override
    public void updateUI(List<EIFormalParameter> parameters, @NotNull ParameterInfoUIContext context) {
        // seems like "parameterIndex" here should refer to the function overload variant index,
        // but I will (mis)use it as the following
        if (parameters.isEmpty()) {
            return;
        }

        int paramIndex = context.getCurrentParameterIndex();

        if (context.isSingleParameterInfo()) {
            if (paramIndex != -1 && paramIndex < parameters.size()) {
                String hint = parameters.get(paramIndex).getText().trim();
                context.setupUIComponentPresentation(hint, 0, hint.length(), false,
                        false, false, context.getDefaultParameterColor());
            }
        } else {
            String hint = parameters.stream()
                    .reduce("", (u, x) -> u + x.getText().trim() + ", ", String::concat);
            hint = hint.substring(0, hint.length() - 2);
            String currentParamText = paramIndex != -1 && paramIndex < parameters.size()
                    ? parameters.get(paramIndex).getText().trim()
                    : "";
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
            if (declaration instanceof EICallableDeclaration) {
                return new List[]{((EICallableDeclaration) declaration).getCallableParams()};
            }
        }
        return null;
    }

    @NotNull
    @Override
    public EIExpression[] getActualParameters(@NotNull EIParams params) {
        return params.getExpressionList().toArray(new EIExpression[0]);
    }

    @NotNull
    @Override
    public IElementType getActualParameterDelimiterType() {
        return ScriptTypes.COMMA;
    }

    @NotNull
    @Override
    public IElementType getActualParametersRBraceType() {
        return ScriptTypes.RPAREN;
    }

    @NotNull
    @Override
    public Set<Class> getArgumentListAllowedParentClasses() {
        return ourArgumentListAllowedParentClassesSet;
    }

    @NotNull
    @Override
    public Set<? extends Class> getArgListStopSearchClasses() {
        return ourStopSearch;
    }

    @NotNull
    @Override
    public Class<EIParams> getArgumentListClass() {
        return EIParams.class;
    }
}
