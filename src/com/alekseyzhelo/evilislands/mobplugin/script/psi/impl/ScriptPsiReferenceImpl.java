package com.alekseyzhelo.evilislands.mobplugin.script.psi.impl;

import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.util.EICallLookupElementRenderer;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptElementFactory;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptNativeFunctionsUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptResolveUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIType;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.EmptySubstitutor;
import com.intellij.psi.JavaResolveResult;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.infos.CandidateInfo;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    private static Map<EIType, LookupElement[]> typedFunctionLookups = new HashMap<>();

    public ScriptPsiReferenceImpl(ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        if (getParent() instanceof EIFunctionCall) {
            if (getParent().getParent() instanceof EIScriptIfBlock) {
                if (typedFunctionLookups.get(EIType.FLOAT) == null) {
                    initLookupFor(EIType.FLOAT);
                }
                return typedFunctionLookups.get(EIType.FLOAT);
            }
            if (getParent().getParent() instanceof EIExpression) {
                EIExpression expression = (EIExpression) getParent().getParent();
                if (expression.getParent() instanceof EIScriptThenBlock) {
                    if (typedFunctionLookups.get(EIType.VOID) == null) {
                        initLookupFor(EIType.VOID);
                    }
                    return typedFunctionLookups.get(EIType.VOID);
                } else if (expression.getParent() instanceof EIParams) {
                    EIParams params = (EIParams) expression.getParent();
                    int position = params.getExpressionList().indexOf(expression);
                    // TODO type checking using script or function declaration
                }
            }

            if (allFunctionLookupElements == null) {
                List<LookupElement> elements = initFunctionLookup(getProject());
                allFunctionLookupElements = elements.toArray(new LookupElement[elements.size()]);
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

    private LookupElement[] initTypedFunctionLookup(@NotNull Project project, @NotNull EIType type) {
        List<EIFunctionDeclaration> functions = EIScriptNativeFunctionsUtil.getAllFunctions(project, type);
        List<LookupElement> lookupElements = new ArrayList<>(functions.size());
        lookupElements.addAll(
                functions.stream()
                        .map(function -> LookupElementBuilder.create(function)
                                .withCaseSensitivity(false)
                                .withRenderer(new EICallLookupElementRenderer<>()))
                        .collect(Collectors.toList())
        );
        return lookupElements.toArray(new LookupElement[lookupElements.size()]);
    }

    private void initLookupFor(EIType type) {
        typedFunctionLookups.put(
                type,
                initTypedFunctionLookup(getProject(), type)
        );
    }
}
