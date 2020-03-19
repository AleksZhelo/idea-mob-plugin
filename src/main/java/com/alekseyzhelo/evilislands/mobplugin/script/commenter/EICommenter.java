package com.alekseyzhelo.evilislands.mobplugin.script.commenter;

import com.intellij.lang.Commenter;
import org.jetbrains.annotations.Nullable;

public class EICommenter implements Commenter {

    private static final String COMMENT_PREFIX = "//";
    private static final String BLOCK_COMMENT_PREFIX = "/*";
    private static final String BLOCK_COMMENT_SUFFIX = "*/";

    @Nullable
    @Override
    public String getLineCommentPrefix() {
        return COMMENT_PREFIX;
    }

    @Nullable
    @Override
    public String getBlockCommentPrefix() {
        return BLOCK_COMMENT_PREFIX;
    }

    @Nullable
    @Override
    public String getBlockCommentSuffix() {
        return BLOCK_COMMENT_SUFFIX;
    }

    @Nullable
    @Override
    public String getCommentedBlockCommentPrefix() {
        return null;
    }

    @Nullable
    @Override
    public String getCommentedBlockCommentSuffix() {
        return null;
    }
}
