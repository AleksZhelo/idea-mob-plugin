package com.alekseyzhelo.evilislands.mobplugin.script.highlighting;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFunctionCall;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIVariableAccess;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIVisitor;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.base.EIScriptPsiElement;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class EIScriptHighlightingAnnotator extends EIVisitor implements Annotator {

    private AnnotationHolder myHolder = null;

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof EIScriptPsiElement) {
            myHolder = holder;
            element.accept(this);
            myHolder = null;
        }
    }

    @Override
    public void visitVariableAccess(@NotNull EIVariableAccess variableAccess) {
        super.visitVariableAccess(variableAccess);

        setHighlighting(variableAccess, myHolder, EIScriptSyntaxHighlightingColors.VARIABLE_ACCESS);
    }

    @Override
    public void visitFunctionCall(@NotNull EIFunctionCall functionCall) {
        super.visitFunctionCall(functionCall);

        PsiElement nameElement = functionCall.getNameIdentifier();
        if (nameElement != null) {
            setHighlighting(nameElement, myHolder, EIScriptSyntaxHighlightingColors.FUNCTION_CALL);
        }
    }

    private static void setHighlighting(@NotNull PsiElement element, @NotNull AnnotationHolder holder, @NotNull TextAttributesKey key) {
        String description = ApplicationManager.getApplication().isUnitTestMode() ? key.getExternalName() : null;
        holder.createInfoAnnotation(element, description).setTextAttributes(key);
    }
}