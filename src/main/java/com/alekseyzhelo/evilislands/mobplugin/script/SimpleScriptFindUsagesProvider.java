package com.alekseyzhelo.evilislands.mobplugin.script;

import com.alekseyzhelo.evilislands.mobplugin.script.lexer.EILexer;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// TODO improve
public class SimpleScriptFindUsagesProvider implements FindUsagesProvider {

    @Nullable
    @Override
    public WordsScanner getWordsScanner() {
        return new DefaultWordsScanner(
                new EILexer(),
                TokenSet.create(ScriptTypes.IDENTIFIER),
                TokenSet.create(ScriptTypes.COMMENT),
                TokenSet.create(ScriptTypes.CHARACTER_STRING, ScriptTypes.FLOATNUMBER)
        );
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
    // TODO: move these string to naming
    public String getType(@NotNull PsiElement element) {
        if (element instanceof EIGlobalVar) {
            return "global variable";
        } else if (element instanceof EIScriptDeclaration) {
            return "script";
        } else if (element instanceof EIScriptImplementation) {
            return "script";
        } else if (element instanceof EIFunctionCall) {
            return "function call";
        } else if (element instanceof EIFunctionDeclaration) {
            return "function";
        } else {
            return "";
        }
    }

    @NotNull
    @Override
    public String getDescriptiveName(@NotNull PsiElement element) {
        if (element instanceof EIGlobalVar) {
            return ((EIGlobalVar) element).getName();
        } else if (element instanceof EIScriptDeclaration) {
            // TODO: improve?
            return ((EIScriptDeclaration) element).getName();
        } else if (element instanceof EIScriptImplementation) {
            // TODO: improve?
            return ((EIScriptImplementation) element).getName();
        } else if (element instanceof EIFunctionCall) {
            // TODO: improve?
            return ((EIFunctionCall) element).getName();
        } else if (element instanceof EIFunctionDeclaration) {
            return ((EIFunctionDeclaration) element).getName();
        } else {
            return "";
        }
    }

    @NotNull
    @Override
    // TODO: improve?
    public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        if (element instanceof PsiNamedElement) {
            //noinspection ConstantConditions
            return ((PsiNamedElement) element).getName();
        }
        if (element.getNode().getElementType().equals(ScriptTypes.IDENTIFIER)) {
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