package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight;

import com.alekseyzhelo.evilislands.mobplugin.script.EIScriptSyntaxHighlighter;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFunctionCall;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFunctionDeclaration;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIScriptReference;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptNativeFunctionsUtil;
import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.ui.GuiUtils;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Locale;

public class EIScriptDocumentationProvider extends AbstractDocumentationProvider {
    @Nullable
    @Override
    public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        if(element.getParent() instanceof EIFunctionCall) {
            return getFunctionCallQuickNavigateInfo(element.getParent());
        }
        if (element instanceof EIFunctionCall) {
            return getFunctionCallQuickNavigateInfo(element);
        }
        return null;
    }

    private String getFunctionCallQuickNavigateInfo(PsiElement element) {
        EIFunctionDeclaration declaration = EIScriptNativeFunctionsUtil.getFunctionDeclaration(element.getProject(), getFunctionName((EIFunctionCall) element));
        return declaration != null ? declaration.getText() : "Unknown function";
    }

    @Override
    public String generateDoc(final PsiElement element, @Nullable final PsiElement originalElement) {
        if(element.getParent() instanceof EIFunctionCall) {
            return getFunctionCallDoc(element.getParent());
        }
        if (element instanceof EIFunctionCall) {
            return getFunctionCallDoc(element);
        }
        return null;
    }

    @NotNull
    private String getFunctionCallDoc(PsiElement element) {
        String functionName = getFunctionName((EIFunctionCall) element);
        EIFunctionDeclaration declaration = EIScriptNativeFunctionsUtil.getFunctionDeclaration(element.getProject(), functionName);
        String documentationText = EIScriptNativeFunctionsUtil.getFunctionDoc(element.getProject(), functionName);
        @NonNls String info = "";
        if (documentationText != null) {
            info += "<b>" + declaration.getText() + "</b>";
            info += "<br/><div style=\"margin-top: 1.5mm;\">" + documentationText + "</div>";
//            TextAttributes attributes = EditorColorsManager.getInstance().getGlobalScheme().getAttributes(EIScriptSyntaxHighlighter.COMMENT).clone();
//            Color background = attributes.getBackgroundColor();
//            if (background != null) {
//                info += "<div bgcolor=#" + GuiUtils.colorToHex(background) + ">";
//            }
//            String doc = StringUtil.join(ContainerUtil.map(StringUtil.split(documentationText, "\n"), new Function<String, String>() {
//                @Override
//                public String fun(String s) {
//                    return StringUtil.trimStart(StringUtil.trimStart(s, "#"), "!").trim();
//                }
//            }), "<br>");
//            info += "<font color=#" + GuiUtils.colorToHex(attributes.getForegroundColor()) + ">" + doc + "</font>\n<br>";
//            if (background != null) {
//                info += "</div>";
//            }
        }
        return info;
    }

    private String getFunctionName(EIFunctionCall call) {
        return call.getFirstChild().getText();
    }
}