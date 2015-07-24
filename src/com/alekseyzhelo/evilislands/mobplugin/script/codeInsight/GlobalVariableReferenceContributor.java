package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIVariableAccess;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

// this does not work and isn't what I need anyway
public class GlobalVariableReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(EIVariableAccess.class),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                        EIVariableAccess variableAccess = (EIVariableAccess) element;
                        String text = variableAccess.getName();
                        if (text != null) {
                            return new PsiReference[]{new GlobalVariableReference(element, new TextRange(0, text.length() + 1))};
                        }
                        return new PsiReference[0];
                    }
                });
    }
}