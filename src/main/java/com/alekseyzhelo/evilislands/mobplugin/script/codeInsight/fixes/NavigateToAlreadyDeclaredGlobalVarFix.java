package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.fixes;

import com.alekseyzhelo.evilislands.mobplugin.EIMessages;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIGlobalVar;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class NavigateToAlreadyDeclaredGlobalVarFix extends NavigateToAlreadyDeclaredElementFixBase<EIGlobalVar> {

    public NavigateToAlreadyDeclaredGlobalVarFix(@NotNull EIGlobalVar element) {
        super(element);
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getText() {
        return EIMessages.message("fix.navigate.global.var.declaration.text", myElement.getName());
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return EIMessages.message("fix.navigate.global.var.declaration.family");
    }
}
