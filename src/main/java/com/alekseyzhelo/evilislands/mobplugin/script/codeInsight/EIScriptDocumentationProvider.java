package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight;

import com.alekseyzhelo.evilislands.mobplugin.mob.psi.objects.PsiMobEntityBase;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.intellij.EIFunctionsService;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.util.DocumentationFormatter;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EITypeToken;
import com.alekseyzhelo.evilislands.mobplugin.script.util.UsefulPsiTreeUtil;
import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EIScriptDocumentationProvider extends AbstractDocumentationProvider {
    @Nullable
    @Override
    public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        if (element.getParent() instanceof EIFunctionCall) {
            return getFunctionCallQuickNavigateInfo((EIFunctionCall) element.getParent());
        }
        if (element instanceof EIFunctionCall) {
            return getFunctionCallQuickNavigateInfo((EIFunctionCall) element);
        }
        return null;
    }

    private String getFunctionCallQuickNavigateInfo(EIFunctionCall element) {
        EIFunctionsService service = EIFunctionsService.getInstance(element.getProject());
        EIFunctionDeclaration declaration = service.getFunctionDeclaration(element.getName());
        return declaration != null ? declaration.getText() : "Unknown function";
    }

    @Override
    public String generateDoc(final PsiElement element, @Nullable final PsiElement originalElement) {
        if (element instanceof EIGlobalVar) {
            return getGlobalVarDoc((EIGlobalVar) element);
        }
        if (element.getParent() instanceof EIFunctionCall) {
            return getFunctionCallDoc((EIFunctionCall) element.getParent());
        }
        if (element instanceof EIFunctionCall) {
            return getFunctionCallDoc((EIFunctionCall) element);
        }
        if (element instanceof EIFunctionDeclaration) {
            return getFunctionDoc(EIFunctionsService.getInstance(element.getProject()),
                    (EIFunctionDeclaration) element);
        }
        if (element instanceof PsiMobEntityBase) {
            return ((PsiMobEntityBase) element).getDoc();
        }
        if (element instanceof EIScriptDeclaration) {
            return getScriptDeclarationDoc((EIScriptDeclaration) element);
        }
        return null;
    }

    private String getGlobalVarDoc(EIGlobalVar element) {
        String doc = DocumentationFormatter.wrapDefinition(DocumentationFormatter.wrapKeyword("GlobalVar") + " " + DocumentationFormatter.bold(element.getName()));
        PsiElement next = UsefulPsiTreeUtil.getNextSiblingSkipWhiteSpacesAndCommas(element, true);
        if (next instanceof PsiComment) {
            doc += DocumentationFormatter.wrapContent(next.getText().substring(2, next.getTextLength()));
        }
        return doc;
    }

    @NotNull
    private String getFunctionCallDoc(EIFunctionCall element) {
        String functionName = element.getName();
        EIFunctionsService service = EIFunctionsService.getInstance(element.getProject());
        EIFunctionDeclaration declaration = service.getFunctionDeclaration(functionName);
        return getFunctionDoc(service, declaration);
    }

    @NotNull
    private String getFunctionDoc(EIFunctionsService service, EIFunctionDeclaration declaration) {
        @NonNls String info = "";
        if (declaration != null) {
            String documentationText = service.getFunctionDoc(declaration.getName());
            if (documentationText != null) {
                info += DocumentationFormatter.wrapDefinition(
                        getCallableDefinitionDoc(
                                true,
                                declaration.getNameIdentifier(),
                                declaration.getFormalParameterList(),
                                declaration.getDisplayableType()
                        )
                );
                info += DocumentationFormatter.wrapContent(documentationText);
            }
        }
        return info;
    }

    private String getCallableDefinitionDoc(
            boolean isFunction,
            PsiElement nameElement,
            List<EIFormalParameter> parameterList,
            EITypeToken returnType
    ) {
        String prefix = DocumentationFormatter.wrapKeyword(isFunction ? "Function" : "Script");
        String name = DocumentationFormatter.bold(nameElement.getText().trim());
        String arguments = parameterList.size() > 0
                ? parameterList.stream().reduce("", (u, x) -> u + x.getText() + ", ", String::concat)
                : "  ";
        return prefix + " " + name + "(" + arguments.substring(0, arguments.length() - 2) + ")" + " : " + returnType.getTypeString();
    }

    @NotNull
    private String getScriptDeclarationDoc(EIScriptDeclaration element) {
        @NonNls String doc = DocumentationFormatter.wrapDefinition(
                getCallableDefinitionDoc(
                        false,
                        element.getNameIdentifier(),
                        element.getFormalParameterList(),
                        EITypeToken.VOID
                )
        );
        PsiElement next = PsiTreeUtil.skipWhitespacesForward(element);
        if (next == null) {
            next = PsiTreeUtil.skipWhitespacesForward(element.getParent());
        }
        if (next instanceof PsiComment) {
            doc += DocumentationFormatter.wrapContent(next.getText().substring(2, next.getTextLength()).trim());
        }
        return doc;
    }

}