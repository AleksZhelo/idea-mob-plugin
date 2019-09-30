package com.alekseyzhelo.evilislands.mobplugin.script.psi.impl;

import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.util.EICallLookupElementRenderer;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptNativeFunctionsUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EITypeToken;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Aleks on 25-07-2015.
 */
// TODO: fuck me this is all wrong
public abstract class ScriptPsiReferenceImpl extends ScriptPsiElementImpl
        implements ScriptPsiReference {

    private static LookupElement[] allFunctionLookupElements;
    private static final Map<EITypeToken, LookupElement[]> typedFunctionLookups = new HashMap<>();

    public ScriptPsiReferenceImpl(ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        if (getParent() instanceof EIFunctionCall) {
            if (getParent().getParent() instanceof EIScriptIfBlock) {
                if (typedFunctionLookups.get(EITypeToken.FLOAT) == null) {
                    initLookupFor(EITypeToken.FLOAT);
                }
                return typedFunctionLookups.get(EITypeToken.FLOAT);
            }
            if (getParent().getParent() instanceof EIExpression) {
                EIExpression expression = (EIExpression) getParent().getParent();
                if (expression.getParent() instanceof EIScriptThenBlock) {
                    if (typedFunctionLookups.get(EITypeToken.VOID) == null) {
                        initLookupFor(EITypeToken.VOID);
                    }
                    return typedFunctionLookups.get(EITypeToken.VOID);
                } else if (expression.getParent() instanceof EIParams) {
                    EIParams params = (EIParams) expression.getParent();
                    int position = params.getExpressionList().indexOf(expression);
                    // TODO type checking using script or function declaration
                }
            }

            if (allFunctionLookupElements == null) {
                List<LookupElement> elements = initFunctionLookup(getProject());
                allFunctionLookupElements = elements.toArray(new LookupElement[0]);
            }
            return allFunctionLookupElements;
        }
        return new Object[0];
    }

    private List<LookupElement> initFunctionLookup(@NotNull Project project) {
        List<EIFunctionDeclaration> functions = EIScriptNativeFunctionsUtil.getAllFunctions(project);
        List<LookupElement> lookupElements = new ArrayList<>(functions.size());
        lookupElements.addAll(
                functions.stream()
                        .map(function -> LookupElementBuilder.create(function)
                                .withCaseSensitivity(false)
                                .withRenderer(new EICallLookupElementRenderer<>()))
                        .collect(Collectors.toList())
        );
        return lookupElements;
    }

    private LookupElement[] initTypedFunctionLookup(@NotNull Project project, @NotNull EITypeToken type) {
        List<EIFunctionDeclaration> functions = EIScriptNativeFunctionsUtil.getAllFunctions(project, type);
        List<LookupElement> lookupElements = new ArrayList<>(functions.size());
        lookupElements.addAll(
                functions.stream()
                        .map(function -> LookupElementBuilder.create(function)
                                .withCaseSensitivity(false)
                                .withRenderer(new EICallLookupElementRenderer<>()))
                        .collect(Collectors.toList())
        );
        return lookupElements.toArray(new LookupElement[0]);
    }

    private void initLookupFor(EITypeToken type) {
        typedFunctionLookups.put(
                type,
                initTypedFunctionLookup(getProject(), type)
        );
    }
}
