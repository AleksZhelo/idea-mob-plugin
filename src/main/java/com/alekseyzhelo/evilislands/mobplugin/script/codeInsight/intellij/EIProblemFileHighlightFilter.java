package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.intellij;

import com.alekseyzhelo.evilislands.mobplugin.script.fileType.ScriptFileType;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vfs.VirtualFile;

public class EIProblemFileHighlightFilter implements Condition<VirtualFile> {
    @Override
    public boolean value(VirtualFile virtualFile) {
        final FileType fileType = virtualFile.getFileType();
        return fileType == ScriptFileType.INSTANCE;
    }
}