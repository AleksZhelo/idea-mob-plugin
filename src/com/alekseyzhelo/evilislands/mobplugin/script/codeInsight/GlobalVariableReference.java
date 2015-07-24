package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight;

import com.alekseyzhelo.evilislands.mobplugin.icon.Icons;
import com.alekseyzhelo.evilislands.mobplugin.script.EIScriptUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIGlobalVar;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
 
import java.util.ArrayList;
import java.util.List;
 
public class GlobalVariableReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {
    private String key;
 
    public GlobalVariableReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
        key = element.getText().substring(textRange.getStartOffset(), textRange.getEndOffset());
    }
 
    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        Project project = myElement.getProject();
        final List<EIGlobalVar> globalVars = EIScriptUtil.findGlobalVars(project, key);
        List<ResolveResult> results = new ArrayList<ResolveResult>();
        for (EIGlobalVar global : globalVars) {
            results.add(new PsiElementResolveResult(global));
        }
        return results.toArray(new ResolveResult[results.size()]);
    }
 
    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }
 
    @NotNull
    @Override
    public Object[] getVariants() {
        Project project = myElement.getProject();
        List<EIGlobalVar> globalVars = EIScriptUtil.findGlobalVars(project);
        List<LookupElement> variants = new ArrayList<LookupElement>();
        for (final EIGlobalVar global : globalVars) {
            if (global.getName() != null && global.getName().length() > 0) {
                variants.add(LookupElementBuilder.create(global).
                        withIcon(Icons.FILE).
                        withTypeText(global.getContainingFile().getName())
                );
            }
        }
        return variants.toArray();
    }
}