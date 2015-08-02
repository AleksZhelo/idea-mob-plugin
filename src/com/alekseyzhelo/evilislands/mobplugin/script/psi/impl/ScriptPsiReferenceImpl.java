package com.alekseyzhelo.evilislands.mobplugin.script.psi.impl;

import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.util.EICallLookupElementRenderer;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptElementFactory;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptNativeFunctionsUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIType;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.infos.CandidateInfo;
import com.intellij.psi.scope.PsiScopeProcessor;
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
 * Created by Aleks on 25-07-2015.
 */
public class ScriptPsiReferenceImpl extends ScriptPsiElementImpl
        implements ScriptPsiReference {

    private static LookupElement[] allFunctionLookupElements;
    private static Map<EIType, LookupElement[]> typedFunctionLookups = new HashMap<>();

    public ScriptPsiReferenceImpl(ASTNode node) {
        super(node);
    }

    @Override
    public PsiElement getElement() {
        return this;
    }

    @Override
    public PsiReference getReference() {
        return this;
    }

    @Override
    public TextRange getRangeInElement() {
        final TextRange textRange = getTextRange();
        return new TextRange(0, textRange.getEndOffset() - textRange.getStartOffset());
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        return resolve(true);
    }

    @Nullable
    public PsiElement resolve(boolean incompleteCode) {
        final ResolveResult[] resolveResults = multiResolve(incompleteCode);

        return resolveResults.length == 0 ||
                resolveResults.length > 1 ||
                !resolveResults[0].isValidResult() ? null : resolveResults[0].getElement();
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        return getText();
    }

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        final EIScriptIdentifier identifier = PsiTreeUtil.getChildOfType(this, EIScriptIdentifier.class);
        final EIScriptIdentifier identifierNew = EIScriptElementFactory.createIdentifierFromText(getProject(), newElementName);
        if (identifier != null && identifierNew != null) {
            getNode().replaceChild(identifier.getNode(), identifierNew.getNode());
        }
        return this;
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        if (element instanceof ScriptNamedElementMixin) {
            handleElementRename(((ScriptNamedElementMixin) element).getName());
        }
        return this;
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        // TODO
        if (element instanceof ScriptNamedElementMixin) {
            String name = ((ScriptNamedElementMixin) element).getName();
            if (name != null
                    && name.equals(getText())
                    && element.getContainingFile().equals(getContainingFile())) {
                return true;
            }
        }
        return false;
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

    @Override
    public boolean isSoft() {
        return false;
    }

    @Override
    public void processVariants(@NotNull PsiScopeProcessor processor) {
        // TODO
        // TODO needed at all?
    }

    @NotNull
    @Override
    public JavaResolveResult advancedResolve(boolean incompleteCode) {
        final PsiElement resolved = resolve(incompleteCode);
        // TODO correct?
        return null != resolved ? new CandidateInfo(resolved, EmptySubstitutor.getInstance()) : JavaResolveResult.EMPTY;
    }

    @NotNull
    @Override
    public JavaResolveResult[] multiResolve(boolean incompleteCode) {
        boolean skipCaching = true;
        List<? extends PsiElement> cachedNames
                = skipCaching ? (EIScriptResolver.INSTANCE).resolve(this, incompleteCode)
                : ResolveCache.getInstance(getProject()).resolveWithCaching(this, EIScriptResolver.INSTANCE, true, incompleteCode);

        // CandidateInfo does some extra resolution work when checking validity, so
        // the results have to be turned into a CandidateInfoArray, and not just passed
        // around as the list that EIScriptResolver returns.
        JavaResolveResult[] result = toCandidateInfoArray(cachedNames);
        return result;
    }

    @NotNull
    private static JavaResolveResult[] toCandidateInfoArray(List<? extends PsiElement> elements) {
        final JavaResolveResult[] result = new JavaResolveResult[elements.size()];
        for (int i = 0, size = elements.size(); i < size; i++) {
            result[i] = new CandidateInfo(elements.get(i), EmptySubstitutor.getInstance());
        }
        return result;
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
