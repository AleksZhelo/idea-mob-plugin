
package com.alekseyzhelo.evilislands.mobplugin.project;

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.SystemIndependent;

import java.util.Objects;

public class EIScriptModuleBuilder extends ModuleBuilder {

    @Override
    public void setupRootModel(@NotNull ModifiableRootModel modifiableRootModel) {
        @SystemIndependent final String basePath = Objects.requireNonNull(modifiableRootModel.getProject().getBasePath());
        final VirtualFile root = LocalFileSystem.getInstance().findFileByPath(basePath);
        assert root != null;
        ContentEntry entry = modifiableRootModel.addContentEntry(root);
        entry.addSourceFolder(root, false);
    }

    @Override
    public boolean canCreateModule() {
        return true;
    }

    @Override
    public ModuleType<?> getModuleType() {
        return EIModuleType.getInstance();
    }
}
