package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight;

import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.fixes.ChangeLvalueTypeFix;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.fixes.DeclareScriptFix;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.fixes.ImplementScriptFix;
import com.alekseyzhelo.evilislands.mobplugin.EIMessages;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.base.EICallableDeclaration;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.base.EIScriptPsiElement;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.references.FunctionCallReference;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.references.MobObjectReference;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EITypeToken;
import com.intellij.codeInsight.daemon.JavaErrorMessages;
import com.intellij.codeInsight.daemon.impl.quickfix.DeleteElementFix;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;

import java.util.List;

// TODO finish, proper
public class EIScriptAnnotator extends EIVisitor implements Annotator {

    private static final Logger LOG = Logger.getInstance(EIScriptAnnotator.class);

    private AnnotationHolder myHolder = null;

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof EIScriptPsiElement) {
            myHolder = holder;
            try {
                element.accept(this);
            } finally {
                myHolder = null;
            }
        }
    }

    @Override
    public void visitScriptDeclaration(@NotNull EIScriptDeclaration scriptDeclaration) {
        super.visitScriptDeclaration(scriptDeclaration);

        ScriptPsiFile psiFile = (ScriptPsiFile) scriptDeclaration.getContainingFile();
        if (psiFile.findScriptImplementation(scriptDeclaration.getName()) == null) {
            PsiElement ident = scriptDeclaration.getNameIdentifier();
            if (ident != null) {
                Annotation annotation = markAsWarning(myHolder, ident,
                        EIMessages.message("warn.script.not.implemented", scriptDeclaration.getName()));

                LocalQuickFix fix = new ImplementScriptFix();
                InspectionManager inspectionManager = InspectionManager.getInstance(psiFile.getProject());
                ProblemDescriptor descriptor = inspectionManager.createProblemDescriptor(ident, annotation.getMessage(), fix,
                        ProblemHighlightType.WARNING, true);
                TextRange range = scriptDeclaration.getTextRange();
                annotation.registerFix(fix, range, null, descriptor);
                annotation.registerFix(
                        new DeleteElementFix(scriptDeclaration, EIMessages.message("fix.remove.element")),
                        range
                );
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
                Annotation annotation = markAsError(myHolder, ident,
                        EIMessages.message("warn.script.not.declared", scriptImplementation.getName()));

                // TODO: move to local quick fix provider in reference?
                InspectionManager inspectionManager = InspectionManager.getInstance(scriptImplementation.getProject());
                LocalQuickFix fix = new DeclareScriptFix(scriptImplementation);
                ProblemDescriptor descriptor = inspectionManager.createProblemDescriptor(ident, annotation.getMessage(), fix,
                        ProblemHighlightType.ERROR, true);
                TextRange range = new TextRange(
                        scriptImplementation.getTextRange().getStartOffset(),
                        ident.getTextRange().getEndOffset()
                );
                annotation.registerFix(fix, range, null, descriptor);
                annotation.registerFix(
                        new DeleteElementFix(scriptImplementation, EIMessages.message("fix.remove.element")),
                        range
                );
            }
        }
    }

    @Override
    public void visitScriptExpression(@NotNull EIScriptExpression scriptExpression) {
        super.visitScriptExpression(scriptExpression);

        if (scriptExpression.getType() != EITypeToken.VOID) {
            markAsWarning(myHolder, scriptExpression,
                    EIMessages.message("warn.script.expression.result.ignored", scriptExpression.getText()));
        }
    }

    @Override
    public void visitForBlock(@NotNull EIForBlock forBlock) {
        super.visitForBlock(forBlock);

        List<EIVariableAccess> variableAccessList = forBlock.getVariableAccessList();
        EIVariableAccess firstArg = variableAccessList.get(0);
        // TODO: baaaad
        PsiElement secondArg = variableAccessList.size() > 1 ? variableAccessList.get(1) : forBlock.getFunctionCall();
        if (firstArg != null && secondArg != null) {
            EITypeToken typeFirst = firstArg.getType();
            EITypeToken typeSecond = secondArg instanceof EIFunctionCall ? ((EIFunctionCall) secondArg).getType() : ((EIVariableAccess) secondArg).getType();
            if (typeFirst != EITypeToken.OBJECT || typeSecond != EITypeToken.GROUP) {
                AnnotatorUtil.createBadForArgumentsAnnotation(
                        myHolder,
                        TextRange.create(firstArg.getTextOffset(), secondArg.getTextOffset() + secondArg.getTextLength()),
                        typeFirst,
                        typeSecond);
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

        FunctionCallReference reference = (FunctionCallReference) call.getReference();
        PsiElement nameElement = call.getNameIdentifier();
        PsiElement resolved = reference.resolve();
        if (call.getParent() instanceof EIScriptIfBlock) {
            if (resolved instanceof EIScriptDeclaration) {
                markAsError(myHolder, nameElement, EIMessages.message("error.not.allowed.in.script.if"));
            } else {
                handleFunctionCallInIfBlock(myHolder, nameElement, (EIFunctionDeclaration) resolved);
            }
        } else {
            handleFunctionOrScriptCall(call, myHolder, nameElement, resolved);
        }

        // TODO: same for For args!
        if (resolved instanceof EICallableDeclaration) {
            EICallableDeclaration callable = (EICallableDeclaration) resolved;
            List<EIFormalParameter> formalParameters = callable.getCallableParams();
            EIParams argumentHolder = call.getParams();
            List<EIExpression> actualArguments = argumentHolder != null ? argumentHolder.getExpressionList() : null;
            assert actualArguments != null;
            int numErrors = 0;
            int firstWrong = Integer.MAX_VALUE;
            for (int i = 0; i < Math.max(formalParameters.size(), actualArguments.size()); i++) {
                EIFormalParameter parameter = i < formalParameters.size() ? formalParameters.get(i) : null;
                EIExpression expression = i < actualArguments.size() ? actualArguments.get(i) : null;
                EIType expectedType = parameter != null ? parameter.getType() : null;
                EITypeToken actualType = expression != null ? expression.getType() : null;
                if (actualType == null || expectedType == null || !expectedType.getTypeToken().equals(actualType)) {
                    numErrors++;
                    firstWrong = Math.min(firstWrong, i);
                }
            }
            if (numErrors > 0) {
                if (numErrors > 1 || formalParameters.size() != actualArguments.size()) {
                    Annotation annotation = AnnotatorUtil.createBadCallArgumentsAnnotation(
                            myHolder,
                            callable,
                            argumentHolder
                    );
                } else {
                    Annotation annotation = AnnotatorUtil.createIncompatibleCallTypesAnnotation(
                            myHolder,
                            formalParameters,
                            actualArguments,
                            firstWrong);
                    if (resolved instanceof EIScriptDeclaration) {
                        EIFormalParameter parameter = formalParameters.get(firstWrong);
                        EIExpression expression = actualArguments.get(firstWrong);
                        InspectionManager inspectionManager = InspectionManager.getInstance(resolved.getProject());
                        LocalQuickFix fix = new ChangeLvalueTypeFix(parameter, expression.getType());
                        ProblemDescriptor descriptor = inspectionManager.createProblemDescriptor(
                                expression,
                                annotation.getMessage(),
                                fix,
                                ProblemHighlightType.ERROR,
                                true
                        );
                        annotation.registerFix(fix, expression.getTextRange(), null, descriptor);
                    }
                }
            }
        }
    }

    @Override
    public void visitVariableAccess(@NotNull EIVariableAccess access) {
        super.visitVariableAccess(access);

        if (access.getReference().resolve() == null) {
            markAsError(myHolder, access.getNameIdentifier(),
                    EIMessages.message("error.undefined.variable", access.getName()));
        }
    }

    @Override
    public void visitExpression(@NotNull EIExpression expression) {
        super.visitExpression(expression);

        PsiReference reference = expression.getReference();
        if (reference instanceof MobObjectReference && reference.resolve() == null) {
            markAsError(myHolder, expression, EIMessages.message("error.wrong.object.id", reference.getCanonicalText()));
        }
    }

    @Override
    public void visitAssignment(@NotNull EIAssignment assignment) {
        super.visitAssignment(assignment);

        EIVariableAccess access = assignment.getVariableAccess();
        EITypeToken lType = access.getType();
        EITypeToken rType = assignment.getExpression() != null ? assignment.getExpression().getType() : null;
        if (lType == null || !lType.equals(rType)) {
            Annotation annotation =
                    AnnotatorUtil.createIncompatibleTypesAnnotation(myHolder, assignment.getTextRange(), lType, rType);

            PsiElement target = access.getReference().resolve();
            if (lType != null && rType != null && target != null) {
                // TODO: move to local quick fix provider in reference?
                InspectionManager inspectionManager = InspectionManager.getInstance(assignment.getProject());
                LocalQuickFix fix = new ChangeLvalueTypeFix((PsiNamedElement) target, rType);
                ProblemDescriptor descriptor = inspectionManager.createProblemDescriptor(
                        access.getNameIdentifier(),
                        annotation.getMessage(),
                        fix,
                        ProblemHighlightType.ERROR,
                        true
                );
                annotation.registerFix(fix, assignment.getTextRange(), null, descriptor);
            }
        }
    }

    // TODO: redo, as well as markAsError
    private void handleFunctionCallInIfBlock(@NotNull AnnotationHolder holder, PsiElement nameElement, EIFunctionDeclaration function) {
        if (function == null) {
            markAsError(holder, nameElement, EIMessages.message("error.unresolved.function", nameElement.getText()));
        } else {
            if (function.getType() == null || function.getType().getTypeToken() != EITypeToken.FLOAT) {
                markAsError(holder, nameElement, EIMessages.message("error.not.allowed.in.script.if"));
            }
        }
    }

    private void handleFunctionOrScriptCall(@NotNull PsiElement element, @NotNull AnnotationHolder holder, PsiElement nameElement, PsiElement call) {
        if (call == null) {
            markAsError(holder, nameElement, EIMessages.message("error.unresolved.function.or.script", nameElement.getText()));
        }
    }

    private Annotation markAsError(@NotNull AnnotationHolder holder, @NotNull PsiElement nameElement, @NotNull String errorString) {
        Annotation annotation = holder.createErrorAnnotation(nameElement.getTextRange(), errorString);
        // TODO: should I keep this?
        annotation.setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
        return annotation;
    }

    private Annotation markAsWarning(@NotNull AnnotationHolder holder, @NotNull PsiElement element, @NotNull String warningString) {
        return holder.createWarningAnnotation(element.getTextRange(), warningString);
    }

    private void markAsWeakWarning(@NotNull AnnotationHolder holder, @NotNull PsiElement nameElement, @NotNull String warningString) {
        holder.createWeakWarningAnnotation(nameElement.getTextRange(), warningString).setHighlightType(ProblemHighlightType.WEAK_WARNING);
    }
}