package com.alekseyzhelo.evilislands.mobplugin.script.highlighting;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFunctionCall;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIVariableAccess;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

// TODO finish, proper
public class EIScriptHighlightingAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof EIVariableAccess) {
            EIVariableAccess variableAccess = (EIVariableAccess) element;
            // there is something wrong with the color definition for variables
            setHighlighting(variableAccess, holder, EIScriptSyntaxHighlightingColors.VARIABLE_ACCESS);
        } else if (element instanceof EIFunctionCall) {
            EIFunctionCall functionCall = (EIFunctionCall) element;
            PsiElement nameElement = element.getFirstChild();
            String name = nameElement.getText();
            if (name != null) {
                annotateAsFunctionCall(holder, functionCall);
            }
        }
    }

    private void annotateAsFunctionCall(@NotNull AnnotationHolder holder, @NotNull EIFunctionCall functionCall) {
        setHighlighting(functionCall.getScriptIdentifier(), holder, DefaultLanguageHighlighterColors.FUNCTION_CALL);
//        TextRange range = new TextRange(nameElement.getTextRange().getStartOffset(),
//                nameElement.getTextRange().getEndOffset());
//        Annotation annotation = holder.createInfoAnnotation(range, null);
//        annotation.setTextAttributes(DefaultLanguageHighlighterColors.FUNCTION_CALL);
    }

    private static void setHighlighting(@NotNull PsiElement element, @NotNull AnnotationHolder holder, @NotNull TextAttributesKey key) {
        holder.createInfoAnnotation(element, null).setEnforcedTextAttributes(TextAttributes.ERASE_MARKER);
        String description = ApplicationManager.getApplication().isUnitTestMode() ? key.getExternalName() : null;
        holder.createInfoAnnotation(element, description).setTextAttributes(key);
    }
}