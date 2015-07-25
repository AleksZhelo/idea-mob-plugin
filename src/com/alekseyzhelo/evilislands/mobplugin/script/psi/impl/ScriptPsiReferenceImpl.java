package com.alekseyzhelo.evilislands.mobplugin.script.psi.impl;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIScriptIdentifier;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIScriptResolver;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptNamedElementMixin;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptPsiReference;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptElementFactory;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.infos.CandidateInfo;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by Aleks on 25-07-2015.
 */
public class ScriptPsiReferenceImpl extends ScriptPsiElementImpl
        implements ScriptPsiReference {

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
        if(element instanceof ScriptNamedElementMixin){
            handleElementRename(((ScriptNamedElementMixin) element).getName());
        }
        return this;
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        // TODO
        if(element instanceof ScriptPsiReference){
            return false;
        }
        if(element instanceof ScriptNamedElementMixin) {
            String name = ((ScriptNamedElementMixin) element).getName();
            if(name != null && name.equals(getText())) {
                return true;
            }
        }
        return false;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        // TODO
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
        JavaResolveResult [] result = toCandidateInfoArray(cachedNames);
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

}
