package com.alekseyzhelo.evilislands.mobplugin.script.psi.base;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFormalParameter;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EITypeToken;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface EICallableDeclaration extends EIScriptNamedElementMixin {
    @NotNull
    List<EIFormalParameter> getCallableParams();

    @NotNull
    EITypeToken getCallableType();
}
