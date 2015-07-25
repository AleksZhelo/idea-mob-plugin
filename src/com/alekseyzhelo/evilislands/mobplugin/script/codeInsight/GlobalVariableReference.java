package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight;

import com.alekseyzhelo.evilislands.mobplugin.icon.Icons;
import com.alekseyzhelo.evilislands.mobplugin.script.EIScriptUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIGlobalVar;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptFile;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
 
import java.util.ArrayList;
import java.util.List;
 
public class GlobalVariableReference extends PsiReferenceBase<PsiElement>  {
    private String name;
 
    public GlobalVariableReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
        name = element.getText().substring(textRange.getStartOffset(), textRange.getEndOffset());
    }
 
    @Nullable
    @Override
    public PsiElement resolve() {
        PsiFile file = myElement.getContainingFile();
        final List<EIGlobalVar> globalVars = EIScriptUtil.findGlobalVars((ScriptFile) file, name);
        List<ResolveResult> results = new ArrayList<>();
        for (EIGlobalVar global : globalVars) {
            results.add(new PsiElementResolveResult(global));
        }
        return results.size() == 1 ? results.get(0).getElement() : null;
    }
 
    @NotNull
    @Override
    public Object[] getVariants() {
        PsiFile file = myElement.getContainingFile();
        List<EIGlobalVar> globalVars = EIScriptUtil.findGlobalVars((ScriptFile) file);
        List<LookupElement> variants = new ArrayList<>();
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
