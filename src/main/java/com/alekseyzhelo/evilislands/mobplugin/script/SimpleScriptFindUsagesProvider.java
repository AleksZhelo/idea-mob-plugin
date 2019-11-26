package com.alekseyzhelo.evilislands.mobplugin.script;

import com.alekseyzhelo.evilislands.mobplugin.EIMessages;
import com.alekseyzhelo.evilislands.mobplugin.script.lexer.EILexer;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.base.EICallableDeclaration;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.base.EIScriptNamedElementMixin;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.base.EIVariableBase;
import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SimpleScriptFindUsagesProvider implements FindUsagesProvider {

    @Nullable
    @Override
    public WordsScanner getWordsScanner() {
        return new DefaultWordsScanner(
                new EILexer(),
                EIScriptParserDefinition.IDENTIFIERS,
                EIScriptParserDefinition.COMMENTS,
                EIScriptParserDefinition.LITERALS
        );
    }

    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
        return psiElement instanceof EIVariableBase
                || psiElement instanceof EICallableDeclaration;
        // TODO v2: doesn't work
//        || psiElement instanceof PsiMobEntityBase;
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
            return EIMessages.message("fu.element.globalVar");
        } else if (element instanceof EIScriptDeclaration) {
            return EIMessages.message("fu.element.scriptDeclaration");
        } else if (element instanceof EIFormalParameter) {
            return EIMessages.message("fu.element.formalParam");
        } else if (element instanceof EIFunctionDeclaration) {
            return EIMessages.message("fu.element.functionDeclaration");
        } else {
            return "";
        }
    }

    @NotNull
    @Override
    public String getDescriptiveName(@NotNull PsiElement element) {
        if (element instanceof EIScriptNamedElementMixin) {
            return ((EIScriptNamedElementMixin) element).getName();
        } else {
            return "";
        }
    }

    @NotNull
    @Override
    public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        if (element instanceof PsiNamedElement) {
            return StringUtil.notNullize(((PsiNamedElement) element).getName());
        }
        if (element.getNode().getElementType().equals(ScriptTypes.IDENTIFIER)) {
            return element.getText();
        }
        return "";
    }
}