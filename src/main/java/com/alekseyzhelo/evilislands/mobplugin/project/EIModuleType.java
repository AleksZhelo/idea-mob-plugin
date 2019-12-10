package com.alekseyzhelo.evilislands.mobplugin.project;

import com.alekseyzhelo.evilislands.mobplugin.EIMessages;
import com.alekseyzhelo.evilislands.mobplugin.icon.Icons;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class EIModuleType extends ModuleType<EIScriptModuleBuilder> {

    private static final String ID = "EI_MODULE_TYPE";

    public EIModuleType() {
        super(ID);
    }

    public static EIModuleType getInstance() {
        return (EIModuleType) ModuleTypeManager.getInstance().findByID(ID);
    }

    @NotNull
    @Override
    public EIScriptModuleBuilder createModuleBuilder() {
        return new EIScriptModuleBuilder();
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @NotNull
    @Override
    public String getName() {
        return EIMessages.message("ei.module.name");
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getDescription() {
        return "";
    }

    @NotNull
    @Override
    public Icon getNodeIcon(boolean isOpened) {
        return Icons.MODULE;
    }
}