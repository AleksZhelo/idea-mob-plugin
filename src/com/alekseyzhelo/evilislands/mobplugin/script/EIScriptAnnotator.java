package com.alekseyzhelo.evilislands.mobplugin.script;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFunctionCall;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFunctionDeclaration;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIScriptDeclaration;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptFile;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptNativeFunctionsUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptResolveUtil;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

// TODO finish, proper
public class EIScriptAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof EIFunctionCall) {
            EIFunctionCall functionCall = (EIFunctionCall) element;
            PsiElement nameElement = element.getFirstChild();
            String name = nameElement.getText();
            if (name != null) {
                Project project = element.getProject();
                EIFunctionDeclaration function = EIScriptNativeFunctionsUtil.getFunctionDeclaration(project, name.toLowerCase(Locale.ENGLISH));
                if (function != null) {
                    annotateAsFunctionCall(holder, nameElement);
                } else {
                    EIScriptDeclaration scriptDeclaration = EIScriptResolveUtil.findScriptDeclaration((ScriptFile) element.getContainingFile(), name);
                    if (scriptDeclaration != null) {
                        // annotateAsFunctionCall(holder, nameElement);
                    } else {
                        TextRange range = new TextRange(nameElement.getTextRange().getStartOffset(),
                                nameElement.getTextRange().getEndOffset());
                        holder.createErrorAnnotation(range, "Unresolved function").setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
                    }
                }
            }
        }
    }

    private void annotateAsFunctionCall(@NotNull AnnotationHolder holder, PsiElement nameElement) {
        TextRange range = new TextRange(nameElement.getTextRange().getStartOffset(),
                nameElement.getTextRange().getEndOffset());
        Annotation annotation = holder.createInfoAnnotation(range, null);
        annotation.setTextAttributes(DefaultLanguageHighlighterColors.FUNCTION_CALL);
    }
}