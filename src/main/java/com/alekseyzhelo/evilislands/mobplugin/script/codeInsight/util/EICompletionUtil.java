package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.util;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFunctionCall;
import com.intellij.application.options.CodeStyle;
import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.CodeInsightSettings;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.completion.util.ParenthesesInsertHandler;
import com.intellij.codeInsight.lookup.Lookup;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.lang.Language;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.util.PsiUtilCore;
import org.jetbrains.annotations.NotNull;

public final class EICompletionUtil {

    private EICompletionUtil() {

    }

    public static void insertParentheses(@NotNull InsertionContext context,
                                         @NotNull LookupElement item,
                                         boolean hasParams,
                                         final boolean forceClosingParenthesis) {
        final Editor editor = context.getEditor();
        final char completionChar = context.getCompletionChar();
        final PsiFile file = context.getFile();

        final boolean smart = completionChar == Lookup.COMPLETE_STATEMENT_SELECT_CHAR;

        if (completionChar == '(' || completionChar == '.' || completionChar == ',' || completionChar == ';' || completionChar == ':' || completionChar == ' ') {
            context.setAddCompletionChar(false);
        }

        final boolean needRightParenth = forceClosingParenthesis ||
                !smart && (CodeInsightSettings.getInstance().AUTOINSERT_PAIR_BRACKET ||
                        !hasParams && completionChar != '(');

        context.commitDocument();

        Language lang = PsiUtilCore.getLanguageAtOffset(context.getFile(), context.getTailOffset());
        final CommonCodeStyleSettings styleSettings = CodeStyle.getLanguageSettings(context.getFile(), lang);
        final PsiElement elementAt = file.findElementAt(context.getStartOffset());
        if (elementAt == null || !(elementAt.getParent() instanceof EIFunctionCall)) {
            final boolean hasParameters = hasParams;
            final boolean spaceBetweenParentheses = styleSettings.SPACE_WITHIN_METHOD_CALL_PARENTHESES && hasParams;
            new ParenthesesInsertHandler<LookupElement>(styleSettings.SPACE_BEFORE_METHOD_CALL_PARENTHESES, spaceBetweenParentheses,
                    needRightParenth, styleSettings.METHOD_PARAMETERS_LPAREN_ON_NEXT_LINE) {
                @Override
                protected boolean placeCaretInsideParentheses(InsertionContext context1, LookupElement item1) {
                    return hasParameters;
                }
            }.handleInsert(context, item);
        }

        if (hasParams) {
            // Invoke parameters popup
            AutoPopupController.getInstance(file.getProject()).autoPopupParameterInfo(editor, (PsiElement) item.getObject());
        }

        if (smart || !needRightParenth) {
            return;
        }

        if (completionChar == ',') {
            AutoPopupController.getInstance(file.getProject()).autoPopupParameterInfo(context.getEditor(), null);
        }
    }
}
