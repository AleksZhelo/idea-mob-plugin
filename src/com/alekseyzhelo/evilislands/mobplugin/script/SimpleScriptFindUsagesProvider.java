package com.alekseyzhelo.evilislands.mobplugin.script;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.lexer.FlexAdapter;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Reader;

// TODO proper
public class SimpleScriptFindUsagesProvider implements FindUsagesProvider {
    private static final DefaultWordsScanner WORDS_SCANNER =
            new DefaultWordsScanner(
                    new FlexAdapter(new EIScriptLexer((Reader) null)),
                    TokenSet.create(ScriptTypes.IDENTIFIER),
                    TokenSet.create(ScriptTypes.COMMENT),
                    TokenSet.create(ScriptTypes.CHARACTER_STRING, ScriptTypes.FLOATNUMBER)
            );

    @Nullable
    @Override
    public WordsScanner getWordsScanner() {
        return WORDS_SCANNER;
    }

    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
        return psiElement instanceof PsiNamedElement;
    }

    @Nullable
    @Override
    public String getHelpId(@NotNull PsiElement psiElement) {
        return null;
    }

    @NotNull
    @Override
    public String getType(@NotNull PsiElement element) {
        if (element instanceof EIGlobalVar) {
            return "global variable";
        } else if (element instanceof EIScriptDeclaration) {
            return "script declaration";
        } else if (element instanceof EIScriptImplementation) {
            return "script implementation";
        } else if (element instanceof EIFunctionCall) {
            return "function call";
        } else {
            return "";
        }
    }

    @NotNull
    @Override
    public String getDescriptiveName(@NotNull PsiElement element) {
        if (element instanceof EIGlobalVar) {
            return "global variable";
        } else if (element instanceof EIScriptDeclaration) {
            return "script declaration";
        } else if (element instanceof EIScriptImplementation) {
            return "script implementation";
        } else if (element instanceof EIFunctionCall) {
            return "function call";
        } else {
            return "";
        }
//        if (element instanceof SimpleProperty) {
//            return ((SimpleProperty) element).getKey();
//        } else {
//            return "";
//        }
    }

    @NotNull
    @Override
    public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        if (element instanceof PsiNamedElement) {
            //noinspection ConstantConditions
            return ((PsiNamedElement) element).getName();
        }
        if (element instanceof EIScriptReference) {
            return element.getText();
        }
        if (element instanceof EIScriptIdentifier) {
            return element.getText();
        }
        return "";
//        if (element instanceof PsiNamedElement) {
//            return ((SimpleProperty) element).getKey() + ":" + ((SimpleProperty) element).getValue();
//        } else {
//            return "";
//        }
    }
}