package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.lookup;

import com.alekseyzhelo.evilislands.mobplugin.icon.Icons;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EITypeToken;
import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateEditingAdapter;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.LiveTemplateLookupElementImpl;
import com.intellij.codeInsight.template.impl.TemplateImpl;
import com.intellij.codeInsight.template.impl.TemplateState;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class EIFunctionLookupElement extends LiveTemplateLookupElementImpl {

    private final EITypeToken type;

    EIFunctionLookupElement(@NotNull EITypeToken returnType, @NotNull TemplateImpl template, boolean sudden) {
        super(template, sudden);
        type = returnType;
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

    @Override
    public void renderElement(LookupElementPresentation presentation) {
        // TODO: investigate
        super.renderElement(presentation);
        presentation.setIcon(Icons.FUNCTION);
    }

    @Override
    public boolean isCaseSensitive() {
        return false;
    }


    public EITypeToken getType() {
        return type;
    }
}
