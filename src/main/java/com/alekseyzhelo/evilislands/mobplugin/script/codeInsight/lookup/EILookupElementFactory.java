package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.lookup;

import com.alekseyzhelo.eimob.util.Float3;
import com.alekseyzhelo.evilislands.mobplugin.icon.Icons;
import com.alekseyzhelo.evilislands.mobplugin.mob.psi.objects.PsiMobEntityBase;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptNamingUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EITypeToken;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.template.Expression;
import com.intellij.codeInsight.template.ExpressionContext;
import com.intellij.codeInsight.template.Result;
import com.intellij.codeInsight.template.TextResult;
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
        return LookupElementBuilder.create(param)
                // TODO: icon
                .withIcon(Icons.FILE)
                .withTypeText(param.getType().getText())
                .withCaseSensitivity(false);
    }

    @NotNull
    public static LookupElement create(EIGlobalVar globalVar) {
        return LookupElementBuilder.create(globalVar)
                .withIcon(Icons.GLOBAL_VAR)
                .withTypeText(
                        globalVar.getType() != null
                                ? globalVar.getType().getText()
                                : EIScriptNamingUtil.UNKNOWN
                )
                .withCaseSensitivity(false);
    }

    @NotNull
    public static LookupElement create(EIScriptDeclaration scriptDeclaration) {
        // TODO: remove old-style element code?
//        return LookupElementBuilder.create(scriptDeclaration)
//                .withIcon(Icons.SCRIPT_IMPL)
//                .withTypeText(EIScriptNamingUtil.SCRIPT)
//                .withCaseSensitivity(false);

        return new EICallableLookupElement(
                scriptDeclaration.getName(),
                scriptDeclaration.getFormalParameterList(),
                EITypeToken.VOID,
                true
        );
    }

    @NotNull
    public static EICallableLookupElement create(EIFunctionDeclaration function) {
        // TODO: remove old-style element code?
//        return LookupElementBuilder.create(function)
//                .withCaseSensitivity(false)
//                .withRenderer(new EICallLookupElementRenderer<>())
//                .withInsertHandler(new EIFunctionParenthesesHandler(function));

        EIType type = function.getType();
        EITypeToken typeToken = type != null ? type.getTypeToken() : EITypeToken.VOID;
        return new EICallableLookupElement(
                function.getName(),
                function.getFormalParameterList(),
                typeToken,
                false
        );
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
