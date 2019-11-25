package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIScriptDeclaration;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptPsiFile;
import com.intellij.openapi.application.ReadAction;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.searches.DefinitionsScopedSearch;
import com.intellij.util.Processor;
import com.intellij.util.QueryExecutor;
import org.jetbrains.annotations.NotNull;

public class EIDefinitionsScopedSearch implements QueryExecutor<PsiElement, DefinitionsScopedSearch.SearchParameters> {

    @Override
    public boolean execute(@NotNull DefinitionsScopedSearch.SearchParameters queryParameters, @NotNull Processor<? super PsiElement> consumer) {
        final PsiElement sourceElement = queryParameters.getElement();
        if (sourceElement instanceof EIScriptDeclaration) {
            return ReadAction.compute(() -> {
                EIScriptDeclaration declaration = (EIScriptDeclaration) sourceElement;
                ScriptPsiFile psiFile = (ScriptPsiFile) declaration.getContainingFile();
                return consumer.process(psiFile.findScriptImplementation(declaration.getName()));
            });
        }
        return true;
    }
}
