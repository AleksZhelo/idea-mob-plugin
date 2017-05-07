package com.alekseyzhelo.evilislands.mobplugin.script;

import com.alekseyzhelo.evilislands.mobplugin.script.lexer.EILexer;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptFile;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptTypes;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;

import java.io.Reader;

public class EIScriptParserDefinition implements ParserDefinition {
    public static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);
    public static final TokenSet COMMENTS = TokenSet.create(ScriptTypes.COMMENT);
 
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
        return TokenSet.EMPTY;
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
        return new ScriptFile(viewProvider);
    }
 
    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }
 
    @NotNull
    public PsiElement createElement(ASTNode node) {
        return ScriptTypes.Factory.createElement(node);
    }
}