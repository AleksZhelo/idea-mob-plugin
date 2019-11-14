package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.fixes;

import com.alekseyzhelo.evilislands.mobplugin.EIMessages;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.util.EICaretPlacementUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptElementFactory;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptTypingUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EITypeToken;
import com.intellij.codeInsight.FileModificationService;
import com.intellij.codeInspection.LocalQuickFixOnPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DeclareGlobalVarFix extends LocalQuickFixOnPsiElement {

    public DeclareGlobalVarFix(@NotNull EIVariableAccess variableAccess) {
        super(variableAccess);
    }

    @NotNull
    @Override
    public String getText() {
        return EIMessages.message("fix.declare.global.var");
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return getText();
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiFile file, @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
        ScriptPsiFile psiFile = (ScriptPsiFile) startElement.getContainingFile();
        EIVariableAccess myElement = (EIVariableAccess) startElement;
        EITypeToken expectedType = EIScriptTypingUtil.getCreatableVariableExpectedType(myElement);

        if (!FileModificationService.getInstance().preparePsiElementForWrite(psiFile)) return;

        EIGlobalVars globalVars = psiFile.findChildByClass(EIGlobalVars.class);
        EIGlobalVar newVar;
        if (globalVars != null) {
            newVar = addVariable(
                    globalVars,
                    EIScriptElementFactory.createGlobalVar(project, myElement.getName(), expectedType)
            );
        } else {
            globalVars = (EIGlobalVars) psiFile.addBefore(
                    EIScriptElementFactory.createGlobalVars(project, myElement.getName(), expectedType),
                    psiFile.getFirstChild()
            );
            newVar = globalVars.getGlobalVarList().get(0);
        }

        if (newVar != null && newVar.getType() == null) {
            FileEditor selectedEditor = FileEditorManager.getInstance(project).getSelectedEditor(file.getVirtualFile());
            if (selectedEditor instanceof TextEditor) {
                EICaretPlacementUtil.placeCaretAtElementEnd(newVar, ((TextEditor) selectedEditor).getEditor());
            }
        }
    }

    /**
     * TODO: exact replicate of
     * {@link com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.fixes.AddScriptParamFix#addParam},
     * generalize?
     */
    private EIGlobalVar addVariable(EIGlobalVars globalVars, EIGlobalVar variable) {
        EIGlobalVar result = null;
        List<EIGlobalVar> vars = globalVars.getGlobalVarList();
        if (vars.size() > 0) {
            result = (EIGlobalVar) globalVars.addAfter(variable, vars.get(vars.size() - 1));
            globalVars.getNode().addLeaf(ScriptTypes.COMMA, ",", result.getNode());
        } else {
            ASTNode lParen = globalVars.getNode().findChildByType(ScriptTypes.LPAREN);
            if (lParen != null) {
                result = (EIGlobalVar) globalVars.addAfter(variable, lParen.getPsi());
            }
        }
        return result;
    }
}
