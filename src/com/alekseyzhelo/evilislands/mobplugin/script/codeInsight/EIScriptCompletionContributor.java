package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight;

import com.alekseyzhelo.evilislands.mobplugin.script.EIScriptLanguage;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.util.EICallLookupElementRenderer;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFunctionDeclaration;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIGlobalVar;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIScriptIdentifier;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptTypes;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptNamingUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptNativeFunctionsUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.util.UsefulPsiTreeUtil;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
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
                        PsiElement element = parameters.getPosition().getParent().getParent();
                        String errorDescription = UsefulPsiTreeUtil.getChildrenOfType(element, PsiErrorElement.class, null)[0].getErrorDescription();
                        fillSuggestedTokens(result, errorDescription);
                    }
                });
        extend(CompletionType.BASIC,
                PlatformPatterns
                        .psiElement(ScriptTypes.IDENTIFIER)
                        .withParent(
                                PlatformPatterns.psiElement(PsiErrorElement.class)
                                        .withLanguage(EIScriptLanguage.INSTANCE)
                        )
                        .withLanguage(EIScriptLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
                        PsiElement element = parameters.getPosition().getParent();
                        String errorDescription = ((PsiErrorElement) element).getErrorDescription();
                        fillSuggestedTokens(result, errorDescription);
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

    private void fillSuggestedTokens(@NotNull CompletionResultSet result, String errorDescription) {
        errorDescription = errorDescription.substring(0, errorDescription.indexOf(" expected, got "));
        errorDescription = errorDescription.replaceAll("ScriptTokenType\\.", "");
        String[] suggestedTokens = errorDescription.split("(, )|( or )");
        String prefix = "IntellijIdeaRulezzz".equals(result.getPrefixMatcher().getPrefix()) ? "" : result.getPrefixMatcher().getPrefix() + " ";
        for (String suggestedToken : suggestedTokens) {
            if ("<type>".equals(suggestedToken)) {
                result.addElement(LookupElementBuilder.create(prefix + EIScriptNamingUtil.FLOAT));
                result.addElement(LookupElementBuilder.create(prefix + EIScriptNamingUtil.SCRIPT));
                result.addElement(LookupElementBuilder.create(prefix + EIScriptNamingUtil.OBJECT));
                result.addElement(LookupElementBuilder.create(prefix + EIScriptNamingUtil.GROUP));
            } else if (EIScriptNamingUtil.tokenMap.get(suggestedToken) != null) {
                result.addElement(LookupElementBuilder.create(prefix + EIScriptNamingUtil.tokenMap.get(suggestedToken)));
            }
        }
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