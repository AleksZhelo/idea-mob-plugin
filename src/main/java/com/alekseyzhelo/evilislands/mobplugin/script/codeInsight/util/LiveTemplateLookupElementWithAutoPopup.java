package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.util;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateEditingAdapter;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.LiveTemplateLookupElementImpl;
import com.intellij.codeInsight.template.impl.TemplateImpl;
import com.intellij.codeInsight.template.impl.TemplateState;
import org.jetbrains.annotations.NotNull;

public class LiveTemplateLookupElementWithAutoPopup extends LiveTemplateLookupElementImpl {
    public LiveTemplateLookupElementWithAutoPopup(@NotNull TemplateImpl template, boolean sudden) {
        super(template, sudden);
    }

    @Override
    public void handleInsert(@NotNull InsertionContext context) {
        context.getDocument().deleteString(context.getStartOffset(), context.getTailOffset());
        context.setAddCompletionChar(false);
        TemplateManager.getInstance(context.getProject()).startTemplate(
                context.getEditor(),
                getTemplate(),
                new TemplateEditingAdapter() {
                    @Override
                    public void currentVariableChanged(@NotNull TemplateState templateState, Template template, int oldIndex, int newIndex) {
                        if (newIndex != -1) {
                            AutoPopupController.getInstance(context.getProject()).autoPopupParameterInfo(context.getEditor(), null);
                        }
                    }
                }
        );
    }
}
