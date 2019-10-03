package com.alekseyzhelo.evilislands.mobplugin.mob.vfs;

import com.alekseyzhelo.evilislands.mobplugin.mob.fileType.TestMobFileType;
import com.intellij.openapi.fileTypes.FileTypeRegistry;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.impl.ArchiveHandler;
import com.intellij.openapi.vfs.newvfs.ArchiveFileSystem;
import com.intellij.openapi.vfs.newvfs.VfsImplUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;

public class MobAsArchiveFileSystem extends ArchiveFileSystem {

    private static final String PS = "/";
    private static final String SEPARATOR = "!/";
    private static final String PROTOCOL = "mob";
    private static final String PROTOCOL_PREFIX = PROTOCOL + "://";

    public static MobAsArchiveFileSystem INSTANCE = (MobAsArchiveFileSystem) VirtualFileManager.getInstance().getFileSystem(PROTOCOL);

    private void printMsg(String msg) {
        System.out.println(msg);
    }

    @NotNull
    @Override
    protected String extractLocalPath(@NotNull String rootPath) {
        return StringUtil.trimEnd(rootPath, SEPARATOR);
    }

    @NotNull
    @Override
    protected String composeRootPath(@NotNull String localPath) {
        return localPath + SEPARATOR;
    }

    @NotNull
    @Override
    protected ArchiveHandler getHandler(@NotNull VirtualFile entryFile) {
        return VfsImplUtil.getHandler(this, entryFile, MobAsArchiveHandler::new);
    }

    @Nullable
    @Override
    public VirtualFile findFileByPathIfCached(@NotNull String path) {
        printMsg(String.format("Looking cached for %s", path));
        return isValid(path) ? VfsImplUtil.findFileByPathIfCached(this, path) : null;
    }

    @NotNull
    @Override
    protected String extractRootPath(@NotNull String path) {
        final int separatorIndex = path.indexOf(SEPARATOR);
        assert separatorIndex >= 0 : "Path passed to MobAsArchiveFileSystem must have separator '!/': " + path;
        return path.substring(0, separatorIndex + SEPARATOR.length());
    }

    @NotNull
    @Override
    public String getProtocol() {
        return PROTOCOL;
    }

    @Nullable
    @Override
    public VirtualFile findFileByPath(@NotNull String path) {
        printMsg(String.format("Looking for %s", path));
        return isValid(path) ? VfsImplUtil.findFileByPath(this, path) : null;
    }

    @Override
    public void refresh(boolean asynchronous) {
        // TODO?
    }

    @Nullable
    @Override
    public VirtualFile refreshAndFindFileByPath(@NotNull String path) {
        printMsg(String.format("Refresh looking for %s", path));
        return isValid(path) ? VfsImplUtil.refreshAndFindFileByPath(this, path) : null;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public boolean isWritable(@NotNull VirtualFile file) {
        return true;
    }



    @NotNull
    @Override
    public OutputStream getOutputStream(@NotNull VirtualFile file, Object requestor, long modStamp, long timeStamp) throws IOException {
        return super.getOutputStream(file, requestor, modStamp, timeStamp);
    }

    private static boolean isValid(String path) {
        return path.contains(SEPARATOR);
    }

    @Override
    protected boolean isCorrectFileType(@NotNull VirtualFile local) {
        return FileTypeRegistry.getInstance().getFileTypeByFileName(local.getNameSequence()) instanceof TestMobFileType;
    }
}
