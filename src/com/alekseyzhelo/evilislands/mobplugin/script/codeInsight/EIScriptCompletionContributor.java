package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight;

import com.alekseyzhelo.evilislands.mobplugin.script.EIScriptLanguage;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.util.EICallLookupElementRenderer;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFunctionDeclaration;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptTypes;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptNamingUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptNativeFunctionsUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.util.UsefulPsiTreeUtil;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
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
    private final static String QUOTED_DUMMY = "'" + CompletionInitializationContext.DUMMY_IDENTIFIER_TRIMMED + "'";
    private final PsiElementPattern.Capture<PsiElement> hasErrorChild = PlatformPatterns.psiElement()
            .withChild(PlatformPatterns.psiElement(PsiErrorElement.class))
            .withLanguage(EIScriptLanguage.INSTANCE);


    public EIScriptCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns
                        .psiElement()
                        .withTreeParent(hasErrorChild)
                        .withLanguage(EIScriptLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
                        PsiElement parent = UsefulPsiTreeUtil.getParentByPattern(parameters.getOriginalPosition(), hasErrorChild);
                        PsiErrorElement[] errors = UsefulPsiTreeUtil.getChildrenOfType(parent, PsiErrorElement.class, null);
                        String errorDescription = errors[0].getErrorDescription();
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
                        PsiElement element = parameters.getOriginalPosition().getParent();
                        if (!(element instanceof PsiErrorElement)) {  // TODO: makes sense to check the original pos first?
                            // check if non-original has 'dummy' and expected?
                            PsiElement parent = UsefulPsiTreeUtil.getParentByPattern(parameters.getOriginalPosition(), hasErrorChild);
                            if (parent != null) {
                                PsiErrorElement[] errors = UsefulPsiTreeUtil.getChildrenOfType(parent, PsiErrorElement.class, null);
                                element = errors[0];
                            } else {
                                element = parameters.getPosition().getParent();

                            }
                        }
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
        int indexOfSuggestion = errorDescription.indexOf(" expected, got ");
        if (indexOfSuggestion >= 0) {
            errorDescription = errorDescription.substring(0, indexOfSuggestion);
            errorDescription = errorDescription.replaceAll("ScriptTokenType\\.", "");
            String[] suggestedTokens = errorDescription.split("(, )|( or )");
            String prefix = "IntellijIdeaRulezzz".equals(result.getPrefixMatcher().getPrefix()) ? "" : result.getPrefixMatcher().getPrefix();
            for (String suggestedToken : suggestedTokens) {
                if ("<type>".equals(suggestedToken)) {
                    result.addElement(prefixedToken(prefix, EIScriptNamingUtil.FLOAT, true));
                    result.addElement(prefixedToken(prefix, EIScriptNamingUtil.STRING, true));
                    result.addElement(prefixedToken(prefix, EIScriptNamingUtil.OBJECT, true));
                    result.addElement(prefixedToken(prefix, EIScriptNamingUtil.GROUP, true));
                } else if (EIScriptNamingUtil.tokenMap.get(suggestedToken) != null) {
                    result.addElement(prefixedToken(prefix, EIScriptNamingUtil.tokenMap.get(suggestedToken), spaceForToken(suggestedToken)));
                }
            }
        }
    }

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        super.fillCompletionVariants(parameters, result);
    }

    private LookupElementBuilder prefixedToken(String prefix, String token, boolean withWhitespace) {
        return LookupElementBuilder.create(prefix + (withWhitespace ? " " : "") + token);
    }

    private boolean spaceForToken(String suggestedToken) {
        return !("COMMA".equals(suggestedToken) || "LPAREN".equals(suggestedToken) || "RPAREN".equals(suggestedToken));
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
}