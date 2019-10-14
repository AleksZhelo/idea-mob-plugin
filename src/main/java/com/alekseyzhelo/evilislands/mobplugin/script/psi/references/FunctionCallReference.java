package com.alekseyzhelo.evilislands.mobplugin.script.psi.references;

import com.alekseyzhelo.evilislands.mobplugin.icon.Icons;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFunctionDeclaration;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIScriptDeclaration;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIScriptImplementation;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptPsiFile;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptNamingUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptNativeFunctionsUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptRenameUtil;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
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
        ScriptPsiFile file = (ScriptPsiFile) myElement.getContainingFile();
        if (scriptOnly) {
            return file.findScriptDeclaration(name);
        } else {
            EIFunctionDeclaration function = EIScriptNativeFunctionsUtil.getFunctionDeclaration(file.getProject(), name);
            return function != null ? function : file.findScriptDeclaration(name);
        }
    }

    @NotNull
    @Override
    // TODO: improve these methods
    public Object[] getVariants() {
        ScriptPsiFile file = (ScriptPsiFile) myElement.getContainingFile();
        List<LookupElement> variants = new ArrayList<>();

        List<EIScriptDeclaration> scripts = file.findScriptDeclarations();
        for (final EIScriptDeclaration script : scripts) {
            if (script.getName().length() > 0) {
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
                if (function.getName().length() > 0) {
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
