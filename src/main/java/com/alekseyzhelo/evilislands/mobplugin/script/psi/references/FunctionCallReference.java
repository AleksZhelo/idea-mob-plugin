package com.alekseyzhelo.evilislands.mobplugin.script.psi.references;

import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.intellij.EIFunctionsService;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFunctionDeclaration;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIScriptImplementation;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptPsiFile;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptRenameUtil;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.TypedLookupItem;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

// This is either a function or a script call
// TODO: split into two classes implementing a common interface?
public class FunctionCallReference extends PsiReferenceBase<PsiElement> {
    private final String name;
    private boolean scriptOnly;

    public FunctionCallReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
        if (element instanceof EIScriptImplementation) {
            name = ((EIScriptImplementation) element).getName();
        } else {
            name = element.getText().substring(textRange.getStartOffset(), textRange.getEndOffset());
        }
        this.scriptOnly = false;
    }

    public FunctionCallReference(@NotNull PsiElement element, TextRange textRange, boolean scriptOnly) {
        this(element, textRange);
        this.scriptOnly = scriptOnly;
    }

    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        PsiElement element = resolve();
        if (element instanceof EIFunctionDeclaration) {
            throw new IncorrectOperationException();
        }
        return EIScriptRenameUtil.renameElement(myElement, newElementName);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        return ResolveCache.getInstance(myElement.getProject()).resolveWithCaching(
                this,
                MyResolver.INSTANCE,
                true,
                false
        );
    }

    @NotNull
    @Override
    // TODO: typing here too?
    // TODO: cache as well?
    public Object[] getVariants() {
        ScriptPsiFile file = (ScriptPsiFile) myElement.getContainingFile();
        List<LookupElement> variants = new ArrayList<>(file.getScriptLookupElements());

        if (!scriptOnly) {
            EIFunctionsService service = EIFunctionsService.getInstance(file.getProject());
            variants.addAll(service.getFunctionLookupElements());
        }
        return variants.toArray();
    }

    private static class MyResolver implements ResolveCache.AbstractResolver<FunctionCallReference, PsiElement> {
        static final MyResolver INSTANCE = new MyResolver();

        @Override
        // TODO: what is the use of incompleteCode?
        public PsiElement resolve(@NotNull FunctionCallReference functionCallReference, boolean incompleteCode) {
            ScriptPsiFile file = (ScriptPsiFile) functionCallReference.getElement().getContainingFile();
            if (functionCallReference.scriptOnly) {
                return file.findScriptDeclaration(functionCallReference.name);
            } else {
                EIFunctionsService service = EIFunctionsService.getInstance(file.getProject());
                EIFunctionDeclaration function = service.getFunctionDeclaration(functionCallReference.name);
                return function != null ? function : file.findScriptDeclaration(functionCallReference.name);
            }
        }
    }
}
