package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.util;

import com.alekseyzhelo.eimob.util.Float3;
import com.alekseyzhelo.evilislands.mobplugin.icon.Icons;
import com.alekseyzhelo.evilislands.mobplugin.mob.psi.objects.PsiMobEntityBase;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFormalParameter;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFunctionDeclaration;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIGlobalVar;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptNamingUtil;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.template.Expression;
import com.intellij.codeInsight.template.ExpressionContext;
import com.intellij.codeInsight.template.Result;
import com.intellij.codeInsight.template.TextResult;
import com.intellij.codeInsight.template.impl.LiveTemplateLookupElementImpl;
import com.intellij.codeInsight.template.impl.TemplateImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public final class EILookupElementFactory {

    private EILookupElementFactory() {

    }

    @NotNull
    public static LookupElement create(EIFormalParameter param) {
        return LookupElementBuilder.create(param).
                withIcon(Icons.FILE).
                withTypeText(param.getType().getText());
    }

    @NotNull
    public static LookupElement create(EIGlobalVar globalVar) {
        return LookupElementBuilder.create(globalVar).
                withIcon(Icons.FILE).
                withTypeText(
                        globalVar.getType() != null
                                ? globalVar.getType().getText()
                                : EIScriptNamingUtil.UNKNOWN
                );
    }

//    @NotNull
//    public static LookupElement create(EIFunctionDeclaration function) {
//        return LookupElementBuilder.create(function)
//                .withCaseSensitivity(false)
//                .withRenderer(new EICallLookupElementRenderer<>())
//                .withInsertHandler(new EIFunctionParenthesesHandler(function));
//    }

    @NotNull
    // TODO: replace with inline template as in Mathematica's BuiltinSymbolLookupElement.java?
    public static LookupElement create(EIFunctionDeclaration function) {
        List<EIFormalParameter> params = function.getFormalParameterList();
        String argumentStr = params.size() > 0
                ? params.stream()
                .reduce("", (u, x) -> u + "$" + x.getName().toUpperCase(Locale.ENGLISH) + "$" + ", ", String::concat)
                : "";
//        String argumentStr = params.size() > 0
//                ? params.stream()
//                .reduce("", (u, x) -> u + String.format("$__Variable%d$", params.indexOf(x)) + ", ", String::concat)
//                : "";
        if (argumentStr.length() > 0) {
            argumentStr = argumentStr.substring(0, argumentStr.length() - 2);
        }

        String templateText = function.getName() + "(" + argumentStr + ")";

        TemplateImpl template = new TemplateImpl(function.getName(), "");
        template.setString(templateText);
        template.setDescription(function.getName());
        for (EIFormalParameter parameter : params) {
            template.addVariable(parameter.getName().toUpperCase(Locale.ENGLISH), new Expression() {
                @Nullable
                @Override
                public Result calculateResult(ExpressionContext context) {
                    return new TextResult(parameter.getName());
                }

                @Nullable
                @Override
                public Result calculateQuickResult(ExpressionContext context) {
                    return new TextResult(parameter.getName());
                }

                @Nullable
                @Override
                public LookupElement[] calculateLookupItems(ExpressionContext context) {
                    return null;
                    // TODO: the below is likely not necessary, standard lookup rules should apply
//                    ScriptPsiFile file = (ScriptPsiFile) context.getPsiElementAtStartOffset().getContainingFile();
//                    return file.findGlobalVars().stream()
//                            .filter((x) -> x.getType().getTypeToken() == parameter.getType().getTypeToken())
//                            .map(EILookupElementFactory::create)
//                            .toArray(LookupElement[]::new);
                }
            }, true);
        }
//        AutoPopupController.getInstance(function.getProject()).autoPopupParameterInfo();
        LiveTemplateLookupElementImpl element = new LiveTemplateLookupElementWithAutoPopup(template, false);
//        return LookupElementDecorator.withRenderer(element, new EICallLookupElementRenderer());
        return element;
    }

    @NotNull
    public static LookupElement create(PsiMobEntityBase entity) {
        return LookupElementBuilder
                .create(entity.getText())
                .withTypeText(entity.getObjectKind())
                .withPresentableText(lookupText(entity));
    }

    private static String lookupText(PsiMobEntityBase entity) {
        Float3 location = entity.getLocation();
        return String.format("%-10d %s at (%.2f, %.2f, %.2f)", entity.getId(), entity.getName(),
                location.getX(), location.getY(), location.getZ());
    }
}
