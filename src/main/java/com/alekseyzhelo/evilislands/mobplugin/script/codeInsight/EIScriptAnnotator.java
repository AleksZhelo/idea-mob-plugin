package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.base.EIScriptPsiElement;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.references.FunctionCallReference;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.references.MobObjectReference;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EITypeToken;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;

// TODO finish, proper
public class EIScriptAnnotator extends EIVisitor implements Annotator {

    private static final String UNRESOLVED_FUNCTION_ERROR = "Unresolved function";
    private static final String UNRESOLVED_FUNCTION_OR_SCRIPT_ERROR = "Unresolved function or script";
    private static final String UNDEFINED_VARIABLE_ERROR = "Undefined variable";
    private static final String WRONG_OBJECT_ID_ERROR = "Object with given ID does not exist";
    private static final String SCRIPT_CALL_NOT_ALLOWED_HERE_ERROR = "Script call not allowed here";
    private static final String NOT_ALLOWED_IN_SCRIPT_IF_ERROR = "Only float-valued functions allowed in this block";
    private static final String SCRIPT_NOT_DECLARED_ERROR = "Script not declared";
    private static final String SCRIPT_NOT_IMPLEMENTED_WARNING = "Declared script not implemented";
    private static final String GS_VAR_ONLY_READ_WARNING = "Variable never written to";
    private static final String GS_VAR_ONLY_WRITTEN_WARNING = "Variable only written to, never read";
    private static final String GS_VAR_ONLY_READ_AND_ONCE_WARNING = GS_VAR_ONLY_READ_WARNING + ", read only once";
    private static final String GS_VAR_ONLY_WRITTEN_AND_ONCE_WARNING = "Variable is written to only once and never read";
    // TODO: need "once" stuff for areas?
    private static final String AREA_ONLY_READ_WARNING = "Area not defined";
    private static final String AREA_ONLY_WRITTEN_WARNING = "Area defined, but never used";

    private static final Logger LOG = Logger.getInstance(EIScriptAnnotator.class);

    private AnnotationHolder myHolder = null;

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof EIScriptPsiElement) {
            myHolder = holder;
            element.accept(this);
            myHolder = null;
        }
    }

    @Override
    public void visitScriptDeclaration(@NotNull EIScriptDeclaration scriptDeclaration) {
        super.visitScriptDeclaration(scriptDeclaration);

        ScriptPsiFile psiFile = (ScriptPsiFile) scriptDeclaration.getContainingFile();
        if (psiFile.findScriptImplementation(scriptDeclaration.getName()) == null) {
            PsiElement ident = scriptDeclaration.getNameIdentifier();
            if (ident != null) {
                markAsWarning(myHolder, ident, SCRIPT_NOT_IMPLEMENTED_WARNING);
            }
        }
    }

    @Override
    public void visitScriptImplementation(@NotNull EIScriptImplementation scriptImplementation) {
        super.visitScriptImplementation(scriptImplementation);

        PsiReference reference = scriptImplementation.getReference();
        if (reference.resolve() == null) {
            PsiElement ident = scriptImplementation.getNameIdentifier();
            if (ident != null) {
                markAsError(myHolder, ident, SCRIPT_NOT_DECLARED_ERROR);
            }
        }
    }

    @Override
    public void visitFunctionCall(@NotNull EIFunctionCall call) {
        super.visitFunctionCall(call);

        // TODO: uncomment!
//        if (EIGSVar.isReadOrWrite(call)) {
//            PsiElement varNameElement = EIGSVar.getVarNameElement(call);
//            if (varNameElement != null &&
//                    varNameElement.getNode().getElementType() == ScriptTypes.CHARACTER_STRING) {
//                String varName = EIGSVar.getVarName(varNameElement.getText());
//                Map<String, EIGSVar> vars = ((ScriptPsiFile) call.getContainingFile()).findGSVars();
//                EIGSVar gsVar = vars.get(varName);
//                if (gsVar != null) {
//                    if (gsVar.getReads() == 0 && !varName.startsWith("z.")) {
//                        markAsWeakWarning(myHolder, varNameElement,
//                                gsVar.getWrites() == 1 ? GS_VAR_ONLY_WRITTEN_AND_ONCE_WARNING : GS_VAR_ONLY_WRITTEN_WARNING);
//                    }
//                    if (gsVar.getWrites() == 0) {
//                        markAsWeakWarning(myHolder, varNameElement,
//                                gsVar.getReads() == 1 ? GS_VAR_ONLY_READ_AND_ONCE_WARNING : GS_VAR_ONLY_READ_WARNING);
//                    }
//                } else {
//                    LOG.error("GSVar is null for " + call);
//                }
//            }
//        } else if (EIArea.isReadOrWrite(call)) {
//            PsiElement areaIdElement = EIArea.getAreaIdElement(call);
//            if (areaIdElement != null &&
//                    areaIdElement.getNode().getElementType() == ScriptTypes.FLOATNUMBER) {
//                try {
//                    int areaId = Integer.parseInt(areaIdElement.getText());
//                    Map<Integer, EIArea> vars = ((ScriptPsiFile) call.getContainingFile()).findAreas();
//                    EIArea area = vars.get(areaId);
//                    if (area != null) {
//                        if (area.getReads() == 0) {
//                            markAsWarning(myHolder, areaIdElement, AREA_ONLY_WRITTEN_WARNING);
//                        }
//                        if (area.getWrites() == 0) {
//                            markAsWarning(myHolder, areaIdElement, AREA_ONLY_READ_WARNING);
//                        }
//                    } else {
//                        LOG.error("Area  is null for " + call);
//                    }
//                } catch (NumberFormatException ignored) {
//                    //don't really care
//                }
//            }
//        }

        // TODO: optimize?
        FunctionCallReference reference = (FunctionCallReference) call.getReference();
        PsiElement nameElement = call.getNameIdentifier();
        PsiElement resolved = reference.resolve();
        if (call.getParent() instanceof EIScriptIfBlock) {
            if (resolved instanceof EIScriptDeclaration) {
                markAsError(myHolder, nameElement, SCRIPT_CALL_NOT_ALLOWED_HERE_ERROR);
            } else {
                handleFunctionCallInIfBlock(myHolder, nameElement, (EIFunctionDeclaration) resolved);
            }
        } else {
            handleFunctionOrScriptCall(call, myHolder, nameElement, resolved);
        }
    }

//    @Override
//    public void visitAssignment(@NotNull EIAssignment assignment) {
//        super.visitAssignment(assignment);
//
//        if (assignment.getReference().resolve() == null) {
//            markAsError(myHolder, assignment.getScriptIdentifier(), UNDEFINED_VARIABLE_ERROR);
//        }
//    }

    @Override
    public void visitVariableAccess(@NotNull EIVariableAccess access) {
        super.visitVariableAccess(access);

        if (access.getReference().resolve() == null) {
            markAsError(myHolder, access.getNameIdentifier(), UNDEFINED_VARIABLE_ERROR);
        }
    }

    @Override
    public void visitExpression(@NotNull EIExpression expression) {
        super.visitExpression(expression);

        PsiReference reference = expression.getReference();
        if (reference instanceof MobObjectReference && reference.resolve() == null) {
            markAsError(myHolder, expression, WRONG_OBJECT_ID_ERROR);
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