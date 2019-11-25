package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.completion;

import com.alekseyzhelo.evilislands.mobplugin.script.EIScriptLanguage;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.impl.EIGSVar;
import com.alekseyzhelo.evilislands.mobplugin.script.util.ArgumentPositionPatternCondition;
import com.alekseyzhelo.evilislands.mobplugin.script.util.UsefulPsiTreeUtil;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class EIScriptCompletionContributor extends CompletionContributor {

    private static final TokenSet ROLL_BACK_TO_FLOAT = TokenSet.create(ScriptTypes.RPAREN, ScriptTypes.COMMA);

    // TODO: DUMMY_IDENTIFIER_TRIMMED?
    public EIScriptCompletionContributor() {
        // TODO: rework?
        // TODO: basically always triggers, as the error is produced by the IntellijIdeaRulezzz dummy identifier, fix!
        //  so far changed the position to originalPosition (in the PSI tree without the dummy and the fake error)
        extend(CompletionType.BASIC,
                PlatformPatterns
                        .psiElement()
                        .withTreeParent(StandardPatterns.or(
                                UsefulPsiTreeUtil.HAS_ERROR_CHILD,
                                StandardPatterns.instanceOf(PsiErrorElement.class)
                        ))
                        .withLanguage(EIScriptLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet result) {
                        PsiElement parent = PsiTreeUtil.findFirstParent(
                                parameters.getOriginalPosition(),
                                psiElement -> psiElement instanceof PsiErrorElement
                                        || UsefulPsiTreeUtil.HAS_ERROR_CHILD.accepts(psiElement)
                        );
                        PsiErrorElement error = parent instanceof PsiErrorElement
                                ? (PsiErrorElement) parent
                                : PsiTreeUtil.getChildOfType(parent, PsiErrorElement.class);
                        if (error != null) {
                            fillSuggestedTokens(result, parent, error.getErrorDescription());
                        }
                    }
                });
        if (EIScriptLanguage.GS_VARS_ENABLED) {
            extend(CompletionType.BASIC, PlatformPatterns
                            .psiElement(ScriptTypes.CHARACTER_STRING)
                            .withLanguage(EIScriptLanguage.INSTANCE)
                            .withSuperParent(3, PlatformPatterns
                                    .psiElement(EIFunctionCall.class)
                                    .withName(StandardPatterns.string().oneOfIgnoreCase(EIGSVar.relevantFunctions.toArray(new String[0])))
                            )
                            .with(ArgumentPositionPatternCondition.SECOND_ARGUMENT),
                    new CompletionProvider<CompletionParameters>() {
                        @Override
                        protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
                            Map<String, EIGSVar> vars = ((ScriptPsiFile) parameters.getOriginalFile()).findGSVars();
                            PsiElement element = parameters.getOriginalPosition();
                            EIFunctionCall call = PsiTreeUtil.getParentOfType(element, EIFunctionCall.class);

                            assert element != null;
                            assert call != null;
                            String varName = EIGSVar.getVarName(element.getText());
                            boolean isRead = EIGSVar.isGSVarRead(call);
                            EIGSVar myVar = vars.get(varName);

                            for (EIGSVar gsVar : vars.values()) {
                                if (gsVar == myVar && ((isRead && (myVar.getReads() == 1 && myVar.getWrites() == 0))
                                        || (!isRead && (myVar.getWrites() == 1 && myVar.getReads() == 0)))) {
                                    continue;
                                }
                                // TODO: proper lookup element with icon and everything
                                result.addElement(LookupElementBuilder.create(gsVar.toString()));
                            }
                        }
                    }
            );
        }
        if (EIScriptLanguage.AREAS_ENABLED) {
            // TODO: as far as I remember this doesn't work anyway  | try again, should work now
//            extend(CompletionType.BASIC, PlatformPatterns
//                            .psiElement()
//                            .withLanguage(EIScriptLanguage.INSTANCE)
//                            .withSuperParent(3, PlatformPatterns
//                                    .psiElement(EIFunctionCall.class)
//                                    .withName(StandardPatterns.string().oneOfIgnoreCase(EIArea.relevantFunctions.toArray(new String[0])))
//                            ),
//                    new CompletionProvider<CompletionParameters>() {
//                        @Override
//                        protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
//                            Map<Integer, EIArea> areas = ((ScriptPsiFile) parameters.getOriginalFile()).findAreas();
//                            PsiElement element = parameters.getOriginalPosition().getPrevSibling().getLastChild();
//                            if (!ArgumentPositionPatternCondition.FIRST_ARGUMENT.accepts(element, context)) {
//                                return;
//                            }
//
//                            EIFunctionCall call = PsiTreeUtil.getParentOfType(element, EIFunctionCall.class);
//
//                            assert element != null;
//                            assert call != null;
//                            try {
//                                int areaId = Integer.parseInt(element.getText());
//                                boolean isRead = EIArea.isAreaRead(call);
//                                EIArea myArea = areas.get(areaId);
//
//                                for (EIArea area : areas.values()) {
//                                    if (area == myArea && ((isRead && (myArea.getReads() == 1 && myArea.getWrites() == 0))
//                                            || (!isRead && (myArea.getWrites() == 1 && myArea.getReads() == 0)))) {
//                                        continue;
//                                    }
//                                    suggestToken(result, area.toString());
//                                }
//                            } catch (NumberFormatException ignored) {
//                                // whatewz
//                            }
//                        }
//                    }
//            );
        }
    }

    @Override
    public void beforeCompletion(@NotNull CompletionInitializationContext context) {
        super.beforeCompletion(context);
        tryRollBackToFloatAndFixDummy(context);
    }

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet
            result) {
        super.fillCompletionVariants(parameters, result);
    }

    // TODO v2: better way?
    private void tryRollBackToFloatAndFixDummy(@NotNull CompletionInitializationContext context) {
        PsiElement element = context.getFile().findElementAt(context.getCaret().getOffset());
        if (element != null && ROLL_BACK_TO_FLOAT.contains(element.getNode().getElementType())) {
            element = context.getFile().findElementAt(context.getCaret().getOffset() - 1);
        }
        if (element != null && element.getNode().getElementType() == ScriptTypes.FLOATNUMBER) {
            context.setDummyIdentifier("1337");
        }
    }

    private void fillSuggestedTokens(@NotNull CompletionResultSet result, PsiElement parent, String
            errorDescription) {
        if (errorDescription.contains("expected, got")) {
            suggestExpectedTerms(result, parent, errorDescription);
        } else if (errorDescription.contains("unexpected")) {
            suggestUnexpected(result, parent, errorDescription);
        }
    }

    private void suggestExpectedTerms(@NotNull CompletionResultSet result, PsiElement parent, String
            errorDescription) {
        int indexOfSuggestion = errorDescription.indexOf(" expected, got ");
        if (indexOfSuggestion >= 0) {
            errorDescription = errorDescription.substring(0, indexOfSuggestion);
            errorDescription = errorDescription.replaceAll("ScriptTokenType\\.", "");
            String[] suggestedTokens = errorDescription.split("(, )|( or )");
            for (String suggestedToken : suggestedTokens) {
                if ("<type>".equals(suggestedToken)) {
                    suggestToken(result, TokenCompletionHelper.FLOAT);
                    suggestToken(result, TokenCompletionHelper.STRING);
                    suggestToken(result, TokenCompletionHelper.OBJECT);
                    suggestToken(result, TokenCompletionHelper.GROUP);
                } else {
                    suggestToken(result, TokenCompletionHelper.fromString(suggestedToken));
                }
            }
        }
    }

    private void suggestUnexpected(@NotNull CompletionResultSet result, PsiElement parent, String errorDescription) {
        String unexpectedTerm = errorDescription.split("'")[1];
        // TODO: should be parent-dependent | complete?
        for (TokenCompletionHelper token : TokenCompletionHelper.values()) {
            if (token.valueStartsWith(unexpectedTerm)) {
                suggestToken(result, token);
            }
        }
        if (parent instanceof EIGlobalVar || parent instanceof EIFormalParameter) {
            suggestToken(result, TokenCompletionHelper.COMMA);
        }
    }

    private void suggestToken(CompletionResultSet result, @Nullable TokenCompletionHelper token) {
        if (token != null) {
            result.addElement(token.getLookupElement(result.getPrefixMatcher().getPrefix()));
        }
    }
}