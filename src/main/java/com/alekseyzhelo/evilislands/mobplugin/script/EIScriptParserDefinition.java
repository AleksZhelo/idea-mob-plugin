package com.alekseyzhelo.evilislands.mobplugin.script;

import com.alekseyzhelo.evilislands.mobplugin.script.lexer.EILexer;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptPsiFile;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptTypes;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;

public class EIScriptParserDefinition implements ParserDefinition {
    @SuppressWarnings("WeakerAccess")
    public static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);
    public static final TokenSet COMMENTS = TokenSet.create(ScriptTypes.WHOLE_LINE_COMMENT, ScriptTypes.COMMENT);
    public static final TokenSet STRING_LITERALS = TokenSet.create(ScriptTypes.CHARACTER_STRING);
    public static final TokenSet NUMERIC_LITERALS = TokenSet.create(ScriptTypes.FLOATNUMBER);
    public static final TokenSet IDENTIFIERS = TokenSet.create(ScriptTypes.IDENTIFIER);
    @SuppressWarnings("WeakerAccess")
    public static final TokenSet LITERALS = TokenSet.orSet(STRING_LITERALS, NUMERIC_LITERALS);
    public static final TokenSet KEYWORDS = TokenSet.create(
            ScriptTypes.GLOBALVARS,
            ScriptTypes.DECLARESCRIPT,
            ScriptTypes.SCRIPT,
            ScriptTypes.IF,
            ScriptTypes.THEN,
            ScriptTypes.FOR,
            ScriptTypes.WORLDSCRIPT
    );
    public static final TokenSet TYPES = TokenSet.create(
            ScriptTypes.OBJECT,
            ScriptTypes.GROUP,
            ScriptTypes.STRING,
            ScriptTypes.FLOAT
    );
    public static final TokenSet PARENS = TokenSet.create(ScriptTypes.LPAREN, ScriptTypes.RPAREN);

    public static final IFileElementType FILE =
            new IFileElementType(Language.findInstance(EIScriptLanguage.class));

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new EILexer();
    }

    @NotNull
    public TokenSet getWhitespaceTokens() {
        return WHITE_SPACES;
    }

    @NotNull
    public TokenSet getCommentTokens() {
        return COMMENTS;
    }

    @NotNull
    public TokenSet getStringLiteralElements() {
        return STRING_LITERALS;
    }

    @NotNull
    public PsiParser createParser(final Project project) {
        return new EIScriptParser();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return FILE;
    }

    public PsiFile createFile(FileViewProvider viewProvider) {
        return new ScriptPsiFile(viewProvider);
    }

    public SpaceRequirements spaceExistenceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }

    @NotNull
    public PsiElement createElement(ASTNode node) {
        return ScriptTypes.Factory.createElement(node);
    }
}