package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.fixes;

import com.alekseyzhelo.evilislands.mobplugin.EIMessages;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIGlobalVar;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIScriptDeclaration;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class NavigateToAlreadyDeclaredScriptFix extends NavigateToAlreadyDeclaredElementFixBase<EIScriptDeclaration> {

    public NavigateToAlreadyDeclaredScriptFix(@NotNull EIScriptDeclaration element) {
        super(element);
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getText() {
        return EIMessages.message("fix.navigate.script.declaration.text", myElement.getName());
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return EIMessages.message("fix.navigate.script.declaration.family");
    }
}
