package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.references.FunctionCallReference;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EITypeToken;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;

// TODO finish, proper
public class EIScriptAnnotator implements Annotator {

    public static final String UNRESOLVED_FUNCTION_ERROR = "Unresolved function";
    public static final String UNRESOLVED_FUNCTION_OR_SCRIPT_ERROR = "Unresolved function or script";
    public static final String SCRIPT_CALL_NOT_ALLOWED_HERE_ERROR = "Script call not allowed here";
    public static final String NOT_ALLOWED_IN_SCRIPT_IF_ERROR = "Only float-valued functions allowed in this block";
    public static final String SCRIPT_NOT_DECLARED_ERROR = "Script not declared";

    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof EIScriptImplementation) {
            EIScriptImplementation scriptImplementation = (EIScriptImplementation) element;
            PsiReference reference = scriptImplementation.getReference();
            if (reference == null || reference.resolve() == null) {
                TextRange range = scriptImplementation.getScriptIdentifier().getTextRange();
                holder.createErrorAnnotation(range, SCRIPT_NOT_DECLARED_ERROR).setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
            }
        }

        if (element instanceof EIFunctionCall) {
            EIFunctionCall functionCall = (EIFunctionCall) element;
            FunctionCallReference reference = (FunctionCallReference) functionCall.getReference();
            PsiElement nameElement = functionCall.getScriptIdentifier();
            PsiElement resolved = reference != null ? reference.resolve() : null;

            if (element.getParent() instanceof EIScriptIfBlock) {
                if (resolved instanceof EIScriptDeclaration) {
                    markAsError(holder, nameElement, SCRIPT_CALL_NOT_ALLOWED_HERE_ERROR);
                } else {
                    handleFunctionCallInIfBlock(holder, nameElement, (EIFunctionDeclaration) resolved);
                }
            } else {
                handleFunctionOrScriptCall(element, holder, nameElement, resolved);
            }
        }
    }

    private void handleFunctionCallInIfBlock(@NotNull AnnotationHolder holder, PsiElement nameElement, EIFunctionDeclaration function) {
        if (function == null) {
            markAsError(holder, nameElement, UNRESOLVED_FUNCTION_ERROR);
        } else {
            if (function.getType() == null || function.getType().getTypeToken() != EITypeToken.FLOAT) {
                markAsError(holder, nameElement, NOT_ALLOWED_IN_SCRIPT_IF_ERROR);
            }
        }
    }

    private void handleFunctionOrScriptCall(@NotNull PsiElement element, @NotNull AnnotationHolder holder, PsiElement nameElement, PsiElement call) {
        if (call == null) {
            markAsError(holder, nameElement, UNRESOLVED_FUNCTION_OR_SCRIPT_ERROR);
        }
    }

    private void markAsError(@NotNull AnnotationHolder holder, @NotNull PsiElement nameElement, @NotNull String errorString) {
        TextRange range = new TextRange(nameElement.getTextRange().getStartOffset(),
                nameElement.getTextRange().getEndOffset());
        holder.createErrorAnnotation(range, errorString).setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
    }

}