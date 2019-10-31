package com.alekseyzhelo.evilislands.mobplugin.script.psi.references;

import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.intellij.EIFunctionsService;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFunctionCall;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFunctionDeclaration;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptPsiFile;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptRenameUtil;
import com.intellij.codeInsight.lookup.LookupElement;
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
public class FunctionCallReference extends PsiReferenceBase<EIFunctionCall> {

    private final String name;
    private final ScriptPsiFile file;

    public FunctionCallReference(@NotNull EIFunctionCall element, TextRange textRange) {
        super(element, textRange);
        name = element.getName();
        file = (ScriptPsiFile) element.getContainingFile();
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
        return ResolveCache.getInstance(file.getProject()).resolveWithCaching(
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
        List<LookupElement> variants = new ArrayList<>(file.getScriptLookupElements());

        EIFunctionsService service = EIFunctionsService.getInstance(file.getProject());
        variants.addAll(service.getFunctionLookupElements());
        return variants.toArray();
    }

    private static class MyResolver implements ResolveCache.AbstractResolver<FunctionCallReference, PsiElement> {
        static final MyResolver INSTANCE = new MyResolver();

        @Override
        // TODO: what is the use of incompleteCode?
        public PsiElement resolve(@NotNull FunctionCallReference ref, boolean incompleteCode) {
            EIFunctionsService service = EIFunctionsService.getInstance(ref.file.getProject());
            EIFunctionDeclaration function = service.getFunctionDeclaration(ref.name);
            return function != null ? function : ref.file.findScriptDeclaration(ref.name);
        }
    }
}
