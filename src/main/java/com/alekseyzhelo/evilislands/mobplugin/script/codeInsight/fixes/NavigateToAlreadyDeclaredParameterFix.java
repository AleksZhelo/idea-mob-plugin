package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.fixes;

import com.alekseyzhelo.evilislands.mobplugin.EIMessages;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFormalParameter;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class NavigateToAlreadyDeclaredParameterFix extends NavigateToAlreadyDeclaredElementFixBase<EIFormalParameter> {

    public NavigateToAlreadyDeclaredParameterFix(@NotNull EIFormalParameter element) {
        super(element);
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getText() {
        return EIMessages.message("fix.navigate.parameter.declaration.text", myElement.getName());
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return EIMessages.message("fix.navigate.parameter.declaration.family");
    }
}
