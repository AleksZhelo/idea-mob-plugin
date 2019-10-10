package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.impl.EIGSVar;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.references.FunctionCallReference;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.references.MobObjectReference;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EITypeToken;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

// TODO finish, proper
public class EIScriptAnnotator extends EIVisitor implements Annotator {

    private static final String UNRESOLVED_FUNCTION_ERROR = "Unresolved function";
    private static final String UNRESOLVED_FUNCTION_OR_SCRIPT_ERROR = "Unresolved function or script";
    private static final String UNDEFINED_VARIABLE = "Undefined variable";
    private static final String WRONG_OBJECT_ID = "Object with given ID does not exist";
    private static final String SCRIPT_CALL_NOT_ALLOWED_HERE_ERROR = "Script call not allowed here";
    private static final String NOT_ALLOWED_IN_SCRIPT_IF_ERROR = "Only float-valued functions allowed in this block";
    private static final String SCRIPT_NOT_DECLARED_ERROR = "Script not declared";
    private static final String GS_VAR_ONLY_READ = "Variable only read, never written to";
    private static final String GS_VAR_ONLY_WRITTEN = "Variable only written to, never read";
    private static final String GS_VAR_ONLY_READ_AND_ONCE = "Variable is read only once and never written to";
    private static final String GS_VAR_ONLY_WRITTEN_AND_ONCE = "Variable is written to only once and never read";

    private AnnotationHolder myHolder = null;

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof ScriptPsiElement) {
            myHolder = holder;
            element.accept(this);
            myHolder = null;
        }
    }

    @Override
    public void visitScriptImplementation(@NotNull EIScriptImplementation scriptImplementation) {
        super.visitScriptImplementation(scriptImplementation);

        PsiReference reference = scriptImplementation.getReference();
        if (reference.resolve() == null) {
            PsiElement ident = scriptImplementation.getScriptIdentifier();
            if (ident != null) {
                markAsError(myHolder, ident, SCRIPT_NOT_DECLARED_ERROR);
            }
        }
    }

    @Override
    public void visitFunctionCall(@NotNull EIFunctionCall functionCall) {
        super.visitFunctionCall(functionCall);

        FunctionCallReference reference = (FunctionCallReference) functionCall.getReference();
        PsiElement nameElement = functionCall.getScriptIdentifier();
        PsiElement resolved = reference.resolve();

        if (EIGSVar.isGSVarRead(functionCall) || EIGSVar.isGSVarWrite(functionCall)) {
            PsiElement varNameElement = EIGSVar.getVarNameElement(functionCall);
            String varName = EIGSVar.getVarName(varNameElement.getText());
            Map<String, EIGSVar> vars = ((ScriptPsiFile) functionCall.getContainingFile()).findGSVars();
            EIGSVar myVar = vars.get(varName);
            if (myVar != null) {
                if (myVar.getReads() == 0 && !varName.startsWith("z.")) {
                    markAsWeakWarning(myHolder, varNameElement,
                            myVar.getWrites() == 1 ? GS_VAR_ONLY_WRITTEN_AND_ONCE : GS_VAR_ONLY_WRITTEN);
                }
                if (myVar.getWrites() == 0) {
                    markAsWeakWarning(myHolder, varNameElement,
                            myVar.getReads() == 1 ? GS_VAR_ONLY_READ_AND_ONCE : GS_VAR_ONLY_READ);
                }
            }
        }

        if (functionCall.getParent() instanceof EIScriptIfBlock) {
            if (resolved instanceof EIScriptDeclaration) {
                markAsError(myHolder, nameElement, SCRIPT_CALL_NOT_ALLOWED_HERE_ERROR);
            } else {
                handleFunctionCallInIfBlock(myHolder, nameElement, (EIFunctionDeclaration) resolved);
            }
        } else {
            handleFunctionOrScriptCall(functionCall, myHolder, nameElement, resolved);
        }
    }

    @Override
    public void visitAssignment(@NotNull EIAssignment assignment) {
        super.visitAssignment(assignment);

        if (assignment.getReference().resolve() == null) {
            markAsError(myHolder, assignment.getScriptIdentifier(), UNDEFINED_VARIABLE);
        }
    }

    @Override
    public void visitExpression(@NotNull EIExpression expression) {
        super.visitExpression(expression);

        PsiReference reference = expression.getReference();
        if (reference instanceof MobObjectReference && reference.resolve() == null) {
            markAsError(myHolder, expression, WRONG_OBJECT_ID);
        }
    }

    private void handleFunctionCallInIfBlock(@NotNull AnnotationHolder holder, PsiElement nameElement, EIFunctionDeclaration function) {
        if (function == null) {
            markAsError(holder, nameElement, UNRESOLVED_FUNCTION_ERROR);
        } else {
            if (function.getType() == null || function.getType().getTypeToken() != EITypeToken.FLOAT) {
                markAsError(holder, nameElement, NOT_ALLOWED_IN_SCRIPT_IF_ERROR);
            }
        }
    }

    private void handleFunctionOrScriptCall(@NotNull PsiElement element, @NotNull AnnotationHolder holder, PsiElement nameElement, PsiElement call) {
        if (call == null) {
            markAsError(holder, nameElement, UNRESOLVED_FUNCTION_OR_SCRIPT_ERROR);
        }
    }

    private void markAsError(@NotNull AnnotationHolder holder, @NotNull PsiElement nameElement, @NotNull String errorString) {
        holder.createErrorAnnotation(nameElement.getTextRange(), errorString).setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
    }

    private void markAsWarning(@NotNull AnnotationHolder holder, @NotNull PsiElement nameElement, @NotNull String warningString) {
        holder.createWarningAnnotation(nameElement.getTextRange(), warningString).setHighlightType(ProblemHighlightType.WARNING);
    }

    private void markAsWeakWarning(@NotNull AnnotationHolder holder, @NotNull PsiElement nameElement, @NotNull String warningString) {
        holder.createWeakWarningAnnotation(nameElement.getTextRange(), warningString).setHighlightType(ProblemHighlightType.WEAK_WARNING);
    }

}