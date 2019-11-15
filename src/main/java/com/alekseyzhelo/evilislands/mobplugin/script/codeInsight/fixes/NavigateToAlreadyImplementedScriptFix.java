package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.fixes;

import com.alekseyzhelo.evilislands.mobplugin.EIMessages;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIScriptDeclaration;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIScriptImplementation;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class NavigateToAlreadyImplementedScriptFix extends NavigateToAlreadyDeclaredElementFixBase<EIScriptImplementation> {

    public NavigateToAlreadyImplementedScriptFix(@NotNull EIScriptImplementation element) {
        super(element);
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getText() {
        return EIMessages.message("fix.navigate.script.implementation.text", myElement.getName());
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return EIMessages.message("fix.navigate.script.implementation.family");
    }
}
