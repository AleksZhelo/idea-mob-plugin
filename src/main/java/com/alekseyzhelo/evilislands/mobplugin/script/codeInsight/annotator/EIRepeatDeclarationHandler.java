package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.annotator;

import com.alekseyzhelo.evilislands.mobplugin.EIMessages;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiNamedElement;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class EIRepeatDeclarationHandler<DeclarationType extends PsiNamedElement & NavigatablePsiElement> {

    private final List<DeclarationType> repeatDeclarations = new ArrayList<>();
    private final Map<String, DeclarationType> firstDeclarations = new HashMap<>();
    private final boolean commaSeparatedDeclarations;

    // TODO: better name for commaSeparatedDeclarations?
    EIRepeatDeclarationHandler(@NotNull List<DeclarationType> declarations, boolean commaSeparatedDeclarations) {
        this.commaSeparatedDeclarations = commaSeparatedDeclarations;
        for (DeclarationType declaration : declarations) {
            final String name = declaration.getName();
            if (firstDeclarations.containsKey(name)) {
                repeatDeclarations.add(declaration);
            }
            firstDeclarations.putIfAbsent(name, declaration);
        }
    }

    protected abstract IntentionAction createNavigateToAlreadyDeclaredElementFix(DeclarationType element);

    @MagicConstant
    void registerErrors(@NotNull AnnotationHolder holder,
                        @NotNull @PropertyKey(resourceBundle = EIMessages.BUNDLE) String errorKey) {
        for (DeclarationType repeatDeclaration : repeatDeclarations) {
            final String name = repeatDeclaration.getName();
            Annotation annotation = AnnotatorUtil.markAsError(
                    holder,
                    repeatDeclaration,
                    EIMessages.message(errorKey, name),
                    false
            );
            annotation.registerFix(createNavigateToAlreadyDeclaredElementFix(firstDeclarations.get(name)));
            annotation.registerFix(AnnotatorUtil.createDeleteElementFix(repeatDeclaration, commaSeparatedDeclarations));
        }
    }
}
