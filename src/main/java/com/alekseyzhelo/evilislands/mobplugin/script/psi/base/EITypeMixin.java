package com.alekseyzhelo.evilislands.mobplugin.script.psi.base;

import com.alekseyzhelo.evilislands.mobplugin.script.util.EITypeToken;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Aleks on 25-07-2015.
 */
public interface EITypeMixin extends EIScriptPsiElement {
    @NotNull
    EITypeToken getTypeToken();
}
