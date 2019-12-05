package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.annotator;

import com.alekseyzhelo.evilislands.mobplugin.EIMessages;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.util.EICodeInsightUtil;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiNamedElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

import java.util.*;

public abstract class EIRepeatDeclarationHandler<DeclarationType extends PsiNamedElement & Navigatable> {

    private final Map<String, DeclarationType> firstDeclarations = new HashMap<>();
    private final List<DeclarationType> repeatDeclarations = new ArrayList<>();
    private final boolean commaSeparated;

    EIRepeatDeclarationHandler(@NotNull List<DeclarationType> declarations, boolean commaSeparated) {
        this.commaSeparated = commaSeparated;
        for (DeclarationType declaration : declarations) {
            final String nameRaw = declaration.getName();
            if (StringUtil.isNotEmpty(nameRaw)) {
                final String name = nameRaw.toLowerCase();
                if (firstDeclarations.containsKey(name)) {
                    repeatDeclarations.add(declaration);
                }
                firstDeclarations.putIfAbsent(name, declaration);
            }
        }
    }

    protected abstract IntentionAction createNavigateToAlreadyDeclaredElementFix(DeclarationType element);

    void registerErrors(@NotNull AnnotationHolder holder,
                        @NotNull @PropertyKey(resourceBundle = EIMessages.BUNDLE) String errorKey) {
        for (DeclarationType repeatDeclaration : repeatDeclarations) {
            final String nameDisplay = repeatDeclaration.getName();
            final String nameKey = Objects.requireNonNull(repeatDeclaration.getName()).toLowerCase();
            Annotation annotation = AnnotatorUtil.markAsError(
                    holder,
                    repeatDeclaration,
                    EIMessages.message(errorKey, nameDisplay),
                    false
            );
            annotation.registerFix(createNavigateToAlreadyDeclaredElementFix(firstDeclarations.get(nameKey)));
            annotation.registerFix(EICodeInsightUtil.createDeleteElementFix(repeatDeclaration, commaSeparated));
        }
    }
}
