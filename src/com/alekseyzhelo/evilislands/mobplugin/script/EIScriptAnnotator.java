package com.alekseyzhelo.evilislands.mobplugin.script;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
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
 
//public class EIScriptAnnotator implements Annotator {
//    @Override
//    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
//        if (element instanceof EIVariable) {
//            EIVariable variable = (EIVariable) element;
//            String value = (String) variable.getValue();
//            if (value != null && value.startsWith("simple:")) {
//                Project project = element.getProject();
//                List<SimpleProperty> properties = EIScriptUtil.findProperties(project, value.substring(7));
//                if (properties.size() == 1) {
//                    TextRange range = new TextRange(element.getTextRange().getStartOffset() + 7,
//                            element.getTextRange().getStartOffset() + 7);
//                    Annotation annotation = holder.createInfoAnnotation(range, null);
//                    annotation.setTextAttributes(SyntaxHighlighterColors.LINE_COMMENT);
//                } else if (properties.size() == 0) {
//                    TextRange range = new TextRange(element.getTextRange().getStartOffset() + 8,
//                            element.getTextRange().getEndOffset());
//                    holder.createErrorAnnotation(range, "Unresolved property");
//                }
//            }
//        }
//    }
//}