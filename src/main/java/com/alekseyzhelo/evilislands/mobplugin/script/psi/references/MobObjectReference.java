package com.alekseyzhelo.evilislands.mobplugin.script.psi.references;

import com.alekseyzhelo.evilislands.mobplugin.mob.psi.PsiMobObjectsBlock;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIExpression;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptPsiFile;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class MobObjectReference extends PsiReferenceBase<EIExpression> {

    public MobObjectReference(@NotNull EIExpression element, TextRange rangeInElement) {
        super(element, rangeInElement);
    }

    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        // TODO: correct?
        throw new IncorrectOperationException("Can't rename a literal!");
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        // TODO: cache works?
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
        return objectsBlock != null ? objectsBlock.getObjectLookupElements() : new Object[0];
    }

    private static class MyResolver implements ResolveCache.AbstractResolver<MobObjectReference, PsiElement> {
        static final MyResolver INSTANCE = new MyResolver();

        @Override
        public PsiElement resolve(@NotNull MobObjectReference mobObjectReference, boolean incompleteCode) {
            PsiElement element = mobObjectReference.myElement;
            try {
                int objectId = Integer.parseInt(element.getFirstChild().getText().replace("\"", ""));
                PsiMobObjectsBlock objectsBlock = ((ScriptPsiFile) element.getContainingFile()).getCompanionMobObjectsBlock();
                if (objectsBlock != null) {
                    // TODO: account for possible AddMob calls?
                    return objectsBlock.getChild(objectId);
                }
            } catch (NumberFormatException e) {
                return null;
            }
            return null;
        }
    }
}
