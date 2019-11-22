package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.lookup;

import com.alekseyzhelo.evilislands.mobplugin.icon.Icons;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFormalParameter;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.base.EICallableDeclaration;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EICommonUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EITypeToken;
import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateEditingAdapter;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.ConstantNode;
import com.intellij.codeInsight.template.impl.LiveTemplateLookupElementImpl;
import com.intellij.codeInsight.template.impl.TemplateImpl;
import com.intellij.codeInsight.template.impl.TemplateState;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

// TODO v2: use?
public class EICallableTemplateLookupElement extends LiveTemplateLookupElementImpl implements EITypedLookupItem {

    private final EITypeToken type;
    private final boolean forScript;

    EICallableTemplateLookupElement(EICallableDeclaration declaration, boolean forScript) {
        super(constructTemplate(declaration.getName(), declaration.getCallableParams()), true);
        type = declaration.getCallableType();
        this.forScript = forScript;
    }

    // v2: replace with inline template as in Mathematica's BuiltinSymbolLookupElement.java?
    private static TemplateImpl constructTemplate(@NotNull String name, @NotNull List<EIFormalParameter> params) {
        String argumentStr = params.size() > 0
                ? params.stream()
                .reduce("", (u, x) -> u + "$" + x.getName().toUpperCase(Locale.ENGLISH) + "$" + ", ", String::concat)
                : "";
        if (argumentStr.length() > 0) {
            argumentStr = argumentStr.substring(0, argumentStr.length() - 2);
        }

        String templateText = name + "(" + argumentStr + ")";

        TemplateImpl template = new TemplateImpl(name, "");
        template.setString(templateText);
        template.setDescription(EICommonUtil.getParamsString(params));

        for (EIFormalParameter parameter : params) {
            template.addVariable(
                    parameter.getName().toUpperCase(Locale.ENGLISH),
                    new ConstantNode(parameter.getName()),
                    true
            );
        }
        template.parseSegments();
        template.addEndVariable();

        return template;
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
        super.renderElement(presentation);
        presentation.setTypeText(type.getTypeString());
        presentation.setIcon(forScript ? Icons.SCRIPT_IMPL : Icons.FUNCTION);
    }

    @Override
    public boolean isCaseSensitive() {
        return false;
    }

    @NotNull
    public EITypeToken getType() {
        return type;
    }
}
