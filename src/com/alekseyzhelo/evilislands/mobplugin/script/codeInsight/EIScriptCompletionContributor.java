package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight;

import com.alekseyzhelo.evilislands.mobplugin.script.EIScriptLanguage;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.util.EICallLookupElementRenderer;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptNativeFunctionsUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.util.UsefulPsiTreeUtil;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.impl.source.tree.PsiErrorElementImpl;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

// this does work, actually
// but isn't currently necessary?
public class EIScriptCompletionContributor extends CompletionContributor {

    private List<LookupElement> functionLookupElements;

    public EIScriptCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns
                        .psiElement(ScriptTypes.IDENTIFIER)
                        .withParent(
                                PlatformPatterns.psiElement(EIScriptIdentifier.class)
                                        .withParent(PlatformPatterns.psiElement(EIGlobalVar.class)
                                                .withChild(PlatformPatterns.psiElement(PsiErrorElement.class))
                                                .withLanguage(EIScriptLanguage.INSTANCE))
                                        .withLanguage(EIScriptLanguage.INSTANCE)
                        )
                        .withLanguage(EIScriptLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
                        ScriptTypes
                        PsiElement element = parameters.getPosition().getParent().getParent();
                        String errorDescription = UsefulPsiTreeUtil.getChildrenOfType(element, PsiErrorElement.class, null)[0].getErrorDescription();
                        errorDescription = errorDescription.substring(0, errorDescription.indexOf(" expected, got "));
                        errorDescription = errorDescription.replaceAll("ScriptTokenType\\.", "");
                        String[] suggestedTokens = errorDescription.split("(, )|( or )");
                        for (String suggestedToken : suggestedTokens) {
                            result.addElement(LookupElementBuilder.create(suggestedToken));
                        }
                        result.addElement(LookupElementBuilder.create(result.getPrefixMatcher().getPrefix() + " :"));
                        result.addElement(LookupElementBuilder.create("RAGE"));
                    }
                });
        // XXX: already done by my references
//        extend(CompletionType.BASIC,
//                PlatformPatterns.psiElement(ScriptTypes.IDENTIFIER)
//                        .inside(EIScriptThenBlock.class)
//                        .withLanguage(EIScriptLanguage.INSTANCE),
//                new CompletionProvider<CompletionParameters>() {
//                    public void addCompletions(@NotNull CompletionParameters parameters,
//                                               ProcessingContext context,
//                                               @NotNull CompletionResultSet resultSet) {
//                        if (functionLookupElements == null) {
//                            functionLookupElements = initFunctionLookup(parameters.getOriginalFile().getProject());
//                        }
//                        resultSet.addAllElements(functionLookupElements);
//                    }
//                }
//        );
    }

    private List<LookupElement> initFunctionLookup(Project project) {
        List<EIFunctionDeclaration> functions = EIScriptNativeFunctionsUtil.getAllFunctions(project);
        List<LookupElement> lookupElements = new ArrayList<>(functions.size());
        for (EIFunctionDeclaration function : functions) {
            lookupElements.add(LookupElementBuilder.create(function)
                    .withCaseSensitivity(false)
                    .withRenderer(new EICallLookupElementRenderer<>()));
        }
        return lookupElements;
    }

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        super.fillCompletionVariants(parameters, result);
    }
}