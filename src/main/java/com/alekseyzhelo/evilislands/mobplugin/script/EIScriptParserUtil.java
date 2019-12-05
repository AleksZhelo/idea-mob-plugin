package com.alekseyzhelo.evilislands.mobplugin.script;

import com.intellij.lang.WhitespacesAndCommentsBinder;
import com.intellij.lang.parser.GeneratedParserUtilBase;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

import java.util.List;

public class EIScriptParserUtil extends GeneratedParserUtilBase {

    public static final WhitespacesAndCommentsBinder PRECEDING_COMMENT_BINDER = new PrecedingWhitespacesAndCommentsBinder();
    public static final WhitespacesAndCommentsBinder TRAILING_COMMENT_BINDER = new TrailingWhitespacesAndCommentsBinder();

    private static class PrecedingWhitespacesAndCommentsBinder implements WhitespacesAndCommentsBinder {

        PrecedingWhitespacesAndCommentsBinder() {

        }

        @Override
        public int getEdgePosition(final List<IElementType> tokens, final boolean atStreamEdge, final TokenTextGetter
                getter) {
            if (tokens.size() == 0) return 0;

            int result = tokens.size();
            for (int idx = tokens.size() - 1; idx >= 0; idx--) {
                final IElementType tokenType = tokens.get(idx);
                if (TokenSet.WHITE_SPACE.contains(tokenType)) {
                    if (StringUtil.getLineBreakCount(getter.get(idx)) > 1) break;
                } else if (EIScriptParserDefinition.COMMENTS.contains(tokenType)) {
                    if (atStreamEdge ||
                            (idx > 0 && TokenSet.WHITE_SPACE.contains(tokens.get(idx - 1)) && StringUtil.containsLineBreak(getter.get(idx - 1)))) {
                        result = idx;
                    }
                } else break;
            }

            return result;
        }
    }

    private static class TrailingWhitespacesAndCommentsBinder implements WhitespacesAndCommentsBinder {
        @Override
        public int getEdgePosition(final List<IElementType> tokens, final boolean atStreamEdge, final TokenTextGetter getter) {
            if (tokens.size() == 0) return 0;

            int result = 0;
            for (int idx = 0; idx < tokens.size(); idx++) {
                final IElementType tokenType = tokens.get(idx);
                if (TokenSet.WHITE_SPACE.contains(tokenType)) {
                    if (StringUtil.getLineBreakCount(getter.get(idx)) > 1) break;
                }
                else if (EIScriptParserDefinition.COMMENTS.contains(tokenType)) {
                    result = idx + 1;
                }
                else break;
            }

            return result;
        }
    }
}

