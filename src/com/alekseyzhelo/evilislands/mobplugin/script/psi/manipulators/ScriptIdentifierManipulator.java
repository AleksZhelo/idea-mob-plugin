package com.alekseyzhelo.evilislands.mobplugin.script.psi.manipulators;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIScriptIdentifier;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptElementFactory;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptGenerationUtil;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;

// I have no idea if this is the right approach, but it works, and I couldn't find a different way to make it work
public class ScriptIdentifierManipulator extends AbstractElementManipulator<EIScriptIdentifier> {
    @Override
    public EIScriptIdentifier handleContentChange(EIScriptIdentifier element, TextRange range, String newContent)
            throws IncorrectOperationException {
        String oldText = element.getText();
        PsiFile file = element.getContainingFile();
        newContent = StringUtil.escapeSlashes(newContent);
        String newText = oldText.substring(0, range.getStartOffset()) + newContent + oldText.substring(range.getEndOffset());
        PsiElement fromText = EIScriptElementFactory.createGlobalVar(file.getProject(), newText);
        if (fromText.getFirstChild() instanceof EIScriptIdentifier) {
            return (EIScriptIdentifier) element.replace(fromText.getFirstChild());
        }
        return element;
    }
}
