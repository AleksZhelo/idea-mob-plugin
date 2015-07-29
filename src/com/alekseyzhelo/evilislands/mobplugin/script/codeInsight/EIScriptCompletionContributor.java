package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFunctionCall;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIScriptReference;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptNativeFunctionsUtil;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

// apparently this does not work
public class EIScriptCompletionContributor extends CompletionContributor {

    private List<LookupElement> functionLookupElements;

    public EIScriptCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(EIScriptReference.class)
                        .inside(EIFunctionCall.class),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet) {
                        if(functionLookupElements == null) {
                            functionLookupElements = initFunctionLookup(parameters.getOriginalFile().getProject());
                        }
                        resultSet.addAllElements(functionLookupElements);
                    }
                }
        );
    }

    private List<LookupElement> initFunctionLookup(Project project) {
        List<String> functions = EIScriptNativeFunctionsUtil.getAllFunctions(project);
        List<LookupElement> lookupElements = new ArrayList<>(functions.size());
        for(String functionName : functions) {
            lookupElements.add(LookupElementBuilder.create(functionName).withCaseSensitivity(false));
        }
        return lookupElements;
    }
}