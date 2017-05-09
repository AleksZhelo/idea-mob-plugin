package com.alekseyzhelo.evilislands.mobplugin.script.psi.references;

import com.alekseyzhelo.evilislands.mobplugin.icon.Icons;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFunctionDeclaration;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIScriptDeclaration;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptFile;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptNamingUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptNativeFunctionsUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptRenameUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptResolveUtil;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

// TODO: handle going to declaration not working from the right half of the script implementation identifier
// This is either a function or a script call
public class FunctionCallReference extends PsiReferenceBase<PsiElement> {
    private String name;
    private boolean scriptOnly;

    public FunctionCallReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
        name = element.getText().substring(textRange.getStartOffset(), textRange.getEndOffset());
        this.scriptOnly = false;
    }

    public FunctionCallReference(@NotNull PsiElement element, TextRange textRange, boolean scriptOnly) {
        this(element, textRange);
        this.scriptOnly = scriptOnly;
    }

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        PsiElement element = resolve();
        if (element instanceof EIFunctionDeclaration) {
            throw new IncorrectOperationException();
        }
        return EIScriptRenameUtil.renameElement(myElement, newElementName);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        PsiFile file = myElement.getContainingFile();
        if (scriptOnly) {
            return EIScriptResolveUtil.findScriptDeclaration((ScriptFile) file, name);
        } else {
            EIFunctionDeclaration function = EIScriptNativeFunctionsUtil.getFunctionDeclaration(file.getProject(), name);
            return function != null
                    ? function
                    : EIScriptResolveUtil.findScriptDeclaration((ScriptFile) file, name);
        }
    }

    @NotNull
    @Override
    // TODO: improve these methods
    public Object[] getVariants() {
        PsiFile file = myElement.getContainingFile();
        List<LookupElement> variants = new ArrayList<>();

        List<EIScriptDeclaration> scripts =
                EIScriptResolveUtil.findScriptDeclarations((ScriptFile) file);
        for (final EIScriptDeclaration script : scripts) {
            if (script.getName() != null && script.getName().length() > 0) {
                variants.add(LookupElementBuilder.create(script).
                        withIcon(Icons.FILE).
                        withTypeText(EIScriptNamingUtil.SCRIPT)
                );
            }
        }

        if (!scriptOnly) {
            List<EIFunctionDeclaration> functions =
                    EIScriptNativeFunctionsUtil.getAllFunctions(myElement.getProject());
            for (final EIFunctionDeclaration function : functions) {
                if (function.getName() != null && function.getName().length() > 0) {
                    variants.add(LookupElementBuilder.create(function).
                            withIcon(Icons.FILE).
                            withTypeText(function.getDisplayableType().getTypeString())
                    );
                }
            }
        }
        return variants.toArray();
    }

}
