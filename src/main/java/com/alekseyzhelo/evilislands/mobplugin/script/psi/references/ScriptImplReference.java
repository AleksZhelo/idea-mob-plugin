package com.alekseyzhelo.evilislands.mobplugin.script.psi.references;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIScriptImplementation;
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

public class ScriptImplReference extends PsiReferenceBase<EIScriptImplementation> {

    private final String name;
    private final ScriptPsiFile file;

    public ScriptImplReference(@NotNull EIScriptImplementation element, TextRange textRange) {
        super(element, textRange);
        name = element.getName();
        file = (ScriptPsiFile) element.getContainingFile();
    }

    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
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
    // TODO: cache as well?
    public Object[] getVariants() {
        List<LookupElement> variants = new ArrayList<>(file.getScriptLookupElements());
        return variants.toArray();
    }

    private static class MyResolver implements ResolveCache.AbstractResolver<ScriptImplReference, PsiElement> {
        static final MyResolver INSTANCE = new MyResolver();

        @Override
        // TODO: what is the use of incompleteCode?
        public PsiElement resolve(@NotNull ScriptImplReference reference, boolean incompleteCode) {
            return reference.file.findScriptDeclaration(reference.name);
        }
    }
}
