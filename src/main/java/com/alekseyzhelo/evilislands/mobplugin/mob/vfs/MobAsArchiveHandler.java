package com.alekseyzhelo.evilislands.mobplugin.mob.vfs;

import com.alekseyzhelo.eimob.MobFile;
import com.intellij.openapi.util.io.FileAttributes;
import com.intellij.openapi.vfs.impl.ArchiveHandler;
import com.intellij.util.containers.hash.LinkedHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Map;

public class MobAsArchiveHandler extends ArchiveHandler {

    private static final String SCRIPT_ENTRY = "script.eiscript";

    private final MobFile myMob;

    protected MobAsArchiveHandler(@NotNull String path) {
        super(path);
        myMob = new MobFile(path);
    }

    @NotNull
    @Override
    protected Map<String, EntryInfo> createEntriesMap() throws IOException {
        LinkedHashMap<String, EntryInfo> entryMap = new LinkedHashMap<>();
        entryMap.put("", createRootEntry());
        entryMap.put(SCRIPT_ENTRY, new EntryInfo(SCRIPT_ENTRY, false, DEFAULT_LENGTH, DEFAULT_TIMESTAMP, entryMap.get("")));
        return entryMap;
    }

    @NotNull
    @Override
    public byte[] contentsToByteArray(@NotNull String relativePath) throws IOException {
        if (relativePath.equals(SCRIPT_ENTRY)) {
            return myMob.getScriptBytes();
        }
        return new byte[0];
    }

    @Nullable
    @Override
    public FileAttributes getAttributes(@NotNull String relativePath) {
        if (relativePath.equals(SCRIPT_ENTRY)) {
            return new FileAttributes(false, false, false, false, DEFAULT_LENGTH, DEFAULT_TIMESTAMP, true);
        } else {
            return super.getAttributes(relativePath);
        }
    }

}
