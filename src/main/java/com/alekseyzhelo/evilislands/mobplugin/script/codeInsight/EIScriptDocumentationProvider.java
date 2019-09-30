package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFunctionCall;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFunctionDeclaration;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIGlobalVar;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptNativeFunctionsUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.util.UsefulPsiTreeUtil;
import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EIScriptDocumentationProvider extends AbstractDocumentationProvider {
    @Nullable
    @Override
    public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        if (element.getParent() instanceof EIFunctionCall) {
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
        if (element instanceof EIGlobalVar) {
            return getGlobalVarDoc((EIGlobalVar) element);
        }
        if (element.getParent() instanceof EIFunctionCall) {
            return getFunctionCallDoc(element.getParent());
        }
        if (element instanceof EIFunctionCall) {
            return getFunctionCallDoc(element);
        }
        if (element instanceof EIFunctionDeclaration) {
            return getFunctionDoc((EIFunctionDeclaration) element);
        }
        return null;
    }

    private String getGlobalVarDoc(EIGlobalVar element) {
        PsiElement next = UsefulPsiTreeUtil.getNextSiblingSkipWhiteSpacesAndCommas(element, true);
        return next instanceof PsiComment ? next.getText().substring(2) : null;
    }

    @NotNull
    private String getFunctionCallDoc(PsiElement element) {
        String functionName = getFunctionName((EIFunctionCall) element);
        EIFunctionDeclaration declaration = EIScriptNativeFunctionsUtil.getFunctionDeclaration(element.getProject(), functionName);
        return getFunctionDoc(declaration);
    }

    @NotNull
    private String getFunctionDoc(EIFunctionDeclaration declaration) {
        String documentationText =
                EIScriptNativeFunctionsUtil.getFunctionDoc(declaration.getProject(), declaration.getName());
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
        return call.getScriptIdentifier().getText();
    }
}