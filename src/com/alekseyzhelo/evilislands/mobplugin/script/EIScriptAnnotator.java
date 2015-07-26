package com.alekseyzhelo.evilislands.mobplugin.script;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFunctionCall;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFunctionDeclaration;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptNativeFunctionsUtil;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SyntaxHighlighterColors;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.util.IncorrectOperationException;
import org.intellij.lang.regexp.intention.CheckRegExpIntentionAction;
import org.jetbrains.annotations.NotNull;
 
import java.util.List;
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
                    TextRange range = new TextRange(nameElement.getTextRange().getStartOffset(),
                            nameElement.getTextRange().getEndOffset());
                    Annotation annotation = holder.createInfoAnnotation(range, null);
                    annotation.setTextAttributes(DefaultLanguageHighlighterColors.FUNCTION_CALL);
                } else {
                    TextRange range = new TextRange(nameElement.getTextRange().getStartOffset(),
                            nameElement.getTextRange().getEndOffset());
                    holder.createErrorAnnotation(range, "Unresolved function");
                }
            }
        }
    }
}