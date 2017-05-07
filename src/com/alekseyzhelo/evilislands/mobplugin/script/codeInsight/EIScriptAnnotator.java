package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptNativeFunctionsUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptResolveUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIType;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

// TODO finish, proper
public class EIScriptAnnotator implements Annotator {

    public static final String UNRESOLVED_FUNCTION_ERROR = "Unresolved function";
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
            PsiElement nameElement = functionCall.getScriptIdentifier();
            String name = nameElement.getText();
            if (name != null) {
                Project project = element.getProject();
                EIFunctionDeclaration function = EIScriptNativeFunctionsUtil.getFunctionDeclaration(project, name.toLowerCase(Locale.ENGLISH));
                if (element.getParent() instanceof EIScriptIfBlock) {
                    handleFunctionCallInIfBlock(holder, nameElement, function);
                } else {
                    handleFunctionOrScriptCall(element, holder, nameElement, name, function);
                }
            }
        }
    }

    private void handleFunctionCallInIfBlock(@NotNull AnnotationHolder holder, PsiElement nameElement, EIFunctionDeclaration function) {
        if (function == null) {
            markAsError(holder, nameElement, UNRESOLVED_FUNCTION_ERROR);
        } else {
            if (function.getType() == null || function.getType().getTypeToken() != EIType.FLOAT) {
                markAsError(holder, nameElement, NOT_ALLOWED_IN_SCRIPT_IF_ERROR);
            }
        }
    }

    private void handleFunctionOrScriptCall(@NotNull PsiElement element, @NotNull AnnotationHolder holder, PsiElement nameElement, String name, EIFunctionDeclaration function) {
        if (function == null) {
            EIScriptDeclaration scriptDeclaration = EIScriptResolveUtil.findScriptDeclaration((ScriptFile) element.getContainingFile(), name);
            if (scriptDeclaration == null) {
                markAsError(holder, nameElement, UNRESOLVED_FUNCTION_ERROR);
            }
        }
    }

    private void markAsError(@NotNull AnnotationHolder holder, @NotNull PsiElement nameElement, @NotNull String errorString) {
        TextRange range = new TextRange(nameElement.getTextRange().getStartOffset(),
                nameElement.getTextRange().getEndOffset());
        holder.createErrorAnnotation(range, errorString).setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
    }

}