package com.alekseyzhelo.evilislands.mobplugin.script.psi.references;

import com.alekseyzhelo.eimob.objects.MobMapEntity;
import com.alekseyzhelo.evilislands.mobplugin.EIMessages;
import com.alekseyzhelo.evilislands.mobplugin.mob.psi.PsiMobObjectsBlock;
import com.alekseyzhelo.evilislands.mobplugin.mob.psi.objects.PsiMobMapEntity;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EILiteral;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptPsiFile;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.PropertyKey;

public abstract class MobObjectReferenceBase<ID> extends PsiReferenceBase<EILiteral> {

    private final String errorMessageKey;

    MobObjectReferenceBase(EILiteral element, TextRange rangeInElement,
                           @NotNull @PropertyKey(resourceBundle = EIMessages.BUNDLE) String errorMessageKey) {
        super(element, rangeInElement);
        this.errorMessageKey = errorMessageKey;
    }

    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        // TODO v2: could actually rename a mob entity - implement for easy mob object management?
        throw new IncorrectOperationException("Can't rename a mob object (yet)!");
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
    public Object[] getVariants() {
        PsiMobObjectsBlock objectsBlock = ((ScriptPsiFile) myElement.getContainingFile()).getCompanionMobObjectsBlock();
        return objectsBlock != null ? getObjectLookupElements(objectsBlock) : new Object[0];
    }

    @NotNull
    public String getErrorMessage() {
        return EIMessages.message(errorMessageKey, getElementId());
    }

    @Nullable
    public abstract ID getElementId();

    @NotNull
    protected abstract LookupElement[] getObjectLookupElements(@NotNull PsiMobObjectsBlock objectsBlock);

    @Nullable
    protected abstract PsiMobMapEntity<? extends MobMapEntity> findMobEntity(@NotNull PsiMobObjectsBlock objectsBlock);

    @SuppressWarnings("rawtypes")
    private static class MyResolver implements ResolveCache.AbstractResolver<MobObjectReferenceBase, PsiElement> {
        static final MyResolver INSTANCE = new MyResolver();

        @Override
        public PsiElement resolve(@NotNull MobObjectReferenceBase mobObjectReference, boolean incompleteCode) {
            PsiMobObjectsBlock objectsBlock =
                    ((ScriptPsiFile) mobObjectReference.myElement.getContainingFile()).getCompanionMobObjectsBlock();
            if (objectsBlock != null) {
                // TODO v2: account for possible AddMob calls?
                return mobObjectReference.findMobEntity(objectsBlock);
            }
            return null;
        }
    }
}
