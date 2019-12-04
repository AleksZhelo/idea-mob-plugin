package com.alekseyzhelo.evilislands.mobplugin.script.psi.references;

import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.intellij.EIFunctionsService;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIIncompleteCall;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EITypeToken;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class IncompleteCallReference extends PsiReferenceBase<EIIncompleteCall> {

    public IncompleteCallReference(@NotNull EIIncompleteCall element, TextRange textRange) {
        super(element, textRange);
    }

    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        throw new IncorrectOperationException("Please complete the call first!");
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        return null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        EIFunctionsService service = EIFunctionsService.getInstance(myElement.getProject());
        // just so happens that places that only accept function calls (and not var references),
        // which are the if block and the last argument of ForIf,
        // also only accept float-valued functions
        List<LookupElement> variants = new ArrayList<>(service.getFunctionLookupElements(EITypeToken.FLOAT));
        return variants.toArray();
    }
}
