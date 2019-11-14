package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.annotator;

import com.alekseyzhelo.evilislands.mobplugin.EIMessages;
import com.alekseyzhelo.evilislands.mobplugin.script.EIScriptLanguage;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.fixes.ChangeLvalueTypeFix;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.fixes.ImplementScriptFix;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.util.EICallArgumentErrorDetector;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.base.EICallableDeclaration;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.base.EIScriptPsiElement;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.impl.EIArea;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.impl.EIGSVar;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.references.FunctionCallReference;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.references.MobObjectReference;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EITypeToken;
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
import java.util.Map;
import java.util.Objects;

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
        PsiElement ident = scriptDeclaration.getNameIdentifier();
        if (ident != null && psiFile.findScriptImplementation(scriptDeclaration.getName()) == null) {
            Annotation annotation = AnnotatorUtil.markAsWarning(myHolder, ident,
                    EIMessages.message("warn.script.not.implemented", scriptDeclaration.getName()));

            LocalQuickFix fix = new ImplementScriptFix();
            InspectionManager inspectionManager = InspectionManager.getInstance(psiFile.getProject());
            ProblemDescriptor descriptor = inspectionManager.createProblemDescriptor(
                    ident,
                    annotation.getMessage(),
                    fix,
                    ProblemHighlightType.WARNING,
                    true
            );
            TextRange range = scriptDeclaration.getTextRange();
            annotation.registerFix(fix, range, null, descriptor);
            annotation.registerFix(
                    new DeleteElementFix(scriptDeclaration, EIMessages.message("fix.remove.element")),
                    range
            );
        }
    }

    @Override
    public void visitScriptImplementation(@NotNull EIScriptImplementation scriptImplementation) {
        super.visitScriptImplementation(scriptImplementation);

        PsiReference reference = scriptImplementation.getReference();
        if (reference.resolve() == null) {
            PsiElement ident = scriptImplementation.getNameIdentifier();
            if (ident != null) {
                Annotation annotation = AnnotatorUtil.markAsError(myHolder, ident,
                        EIMessages.message("warn.script.not.declared", scriptImplementation.getName()));
                AnnotatorUtil.registerReferenceQuickFixes(annotation, reference);
            }
        }
    }

    @Override
    public void visitCallStatement(@NotNull EICallStatement callStatement) {
        super.visitCallStatement(callStatement);

        if (callStatement.getType() != EITypeToken.VOID) {
            AnnotatorUtil.markAsWarning(myHolder, callStatement,
                    EIMessages.message("warn.script.call.statement.result.ignored", callStatement.getText()));
        }
    }

    @Override
    public void visitAssignment(@NotNull EIAssignment assignment) {
        super.visitAssignment(assignment);

        EIVariableAccess left = assignment.getLeftSide();
        EITypeToken lType = left.getType();
        EITypeToken rType = assignment.isComplete()
                ? Objects.requireNonNull(assignment.getRightSide()).getType()
                : null;
        if (lType == null || !lType.equals(rType)) {
            Annotation annotation =
                    AnnotatorUtil.createIncompatibleTypesAnnotation(myHolder, assignment.getTextRange(), lType, rType);

            PsiElement target = left.getReference().resolve();
            // TODO: don't like the numerous VOID checks
            if (lType != null && rType != null && target != null && rType != EITypeToken.VOID) {
                // TODO: move to local quick fix provider in reference?  | seems like I can't, really
                InspectionManager inspectionManager = InspectionManager.getInstance(assignment.getProject());
                LocalQuickFix fix = new ChangeLvalueTypeFix((PsiNamedElement) target, rType);
                ProblemDescriptor descriptor = inspectionManager.createProblemDescriptor(
                        left,
                        annotation.getMessage(),
                        fix,
                        ProblemHighlightType.ERROR,
                        true
                );
                annotation.registerFix(fix, assignment.getTextRange(), null, descriptor);
            }
        }
    }

    @Override
    public void visitForBlock(@NotNull EIForBlock forBlock) {
        super.visitForBlock(forBlock);

        List<EIExpression> expressions = forBlock.getExpressionList();
        if (expressions.size() == 2) {
            EIExpression firstArg = expressions.get(0);
            PsiElement secondArg = expressions.get(1);
            if (firstArg != null && secondArg != null) {
                EITypeToken typeFirst = firstArg.getType();
                EITypeToken typeSecond = secondArg instanceof EIFunctionCall
                        ? ((EIFunctionCall) secondArg).getType()
                        : ((EIVariableAccess) secondArg).getType();
                if (typeFirst != EITypeToken.OBJECT || typeSecond != EITypeToken.GROUP) {
                    AnnotatorUtil.createBadForArgumentsAnnotation(
                            myHolder,
                            TextRange.create(firstArg.getTextOffset(),
                                    secondArg.getTextOffset() + secondArg.getTextLength()),
                            typeFirst,
                            typeSecond
                    );
                }
            }
        }
    }

    @Override
    public void visitFunctionCall(@NotNull EIFunctionCall call) {
        super.visitFunctionCall(call);

        tryAnnotateGSVarUsage(myHolder, call);
        tryAnnotateAreaUsage(myHolder, call);

        final FunctionCallReference reference = (FunctionCallReference) call.getReference();
        final PsiElement callNameElement = call.getNameIdentifier();
        final PsiElement resolvedTo = reference.resolve();

        if (callNameElement != null) {  // should never be null, actually
            String errorMessage = AnnotatorUtil.detectFunctionCallError(
                    callNameElement,
                    resolvedTo,
                    call.getParent() instanceof EIScriptIfBlock
            );
            if (errorMessage != null) {
                // TODO: could prompt to create a script here?
                Annotation annotation = AnnotatorUtil.markAsError(myHolder, callNameElement, errorMessage);
                annotation.registerFix(
                        // TODO: add factory? maybe just for delete element, or for all my fixes?
                        new DeleteElementFix(call, EIMessages.message("fix.remove.element")),
                        call.getTextRange()
                );
            }
        }

        if (resolvedTo instanceof EICallableDeclaration) {
            EICallableDeclaration callable = (EICallableDeclaration) resolvedTo;
            EIParams argumentHolder = call.getParams();

            EICallArgumentErrorDetector errorDetector = new EICallArgumentErrorDetector(
                    callable.getCallableParams(),
                    argumentHolder.getExpressionList()
            ).invoke();

            if (errorDetector.errorsDetected()) {
                Annotation annotation;
                if (errorDetector.getNumErrors() > 1 || errorDetector.wrongArgumentCount()) {
                    annotation = AnnotatorUtil.createBadCallArgumentsAnnotation(
                            myHolder,
                            callable,
                            argumentHolder
                    );
                } else {
                    annotation = AnnotatorUtil.createIncompatibleCallTypesAnnotation(
                            myHolder,
                            errorDetector
                    );
                }

                // TODO: ugly code in this kind of repetitious LocalQuickFix creation?
                if (resolvedTo instanceof EIScriptDeclaration) {
                    EIFormalParameter parameter = errorDetector.getFirstWrongParameter();
                    EIExpression expression = errorDetector.getFirstWrongArgument();
                    // TODO: don't like the numerous VOID checks
                    if (parameter != null && expression != null && expression.getType() != EITypeToken.VOID) {
                        InspectionManager inspectionManager = InspectionManager.getInstance(resolvedTo.getProject());
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

        final PsiElement ident = access.getNameIdentifier();
        final PsiReference reference = access.getReference();
        if (ident != null && reference.resolve() == null) {
            Annotation annotation = AnnotatorUtil.markAsError(myHolder, ident,
                    EIMessages.message("error.undefined.variable", access.getName()));
            AnnotatorUtil.registerReferenceQuickFixes(annotation, reference);
        }
    }

    @Override
    public void visitLiteral(@NotNull EILiteral literal) {
        super.visitLiteral(literal);

        PsiReference reference = literal.getReference();
        if (reference instanceof MobObjectReference && reference.resolve() == null) {
            AnnotatorUtil.markAsError(myHolder, literal, EIMessages.message("error.wrong.object.id", reference.getCanonicalText()));
        }
    }

    private static void tryAnnotateGSVarUsage(@NotNull AnnotationHolder holder, @NotNull EIFunctionCall call) {
        if (EIScriptLanguage.GS_VARS_ENABLED && EIGSVar.isReadOrWrite(call)) {
            PsiElement varNameElement = EIGSVar.getVarNameElement(call);
            if (varNameElement != null &&
                    varNameElement.getNode().getElementType() == ScriptTypes.CHARACTER_STRING) {
                String varName = EIGSVar.getVarName(varNameElement.getText());
                Map<String, EIGSVar> vars = ((ScriptPsiFile) call.getContainingFile()).findGSVars();
                EIGSVar gsVar = vars.get(varName);
                if (gsVar != null) {
                    if (!gsVar.isZoneOrQuestVar()) { // TODO: should still warn about only reading a special var?
                        if (gsVar.getReads() == 0) {
                            AnnotatorUtil.markAsWeakWarning(holder, varNameElement,
                                    EIMessages.message(gsVar.getWrites() == 1 ? "warn.gs.var.used.once" : "warn.gs.var.only.written", gsVar));
                        }
                        if (gsVar.getWrites() == 0) {
                            AnnotatorUtil.markAsWeakWarning(holder, varNameElement,
                                    EIMessages.message(gsVar.getReads() == 1 ? "warn.gs.var.used.once" : "warn.gs.var.only.read", gsVar));
                        }
                    }
                } else {
                    LOG.error("GSVar is null for " + call);
                }
            }
        }
    }

    private static void tryAnnotateAreaUsage(@NotNull AnnotationHolder holder, @NotNull EIFunctionCall call) {
        if (EIScriptLanguage.AREAS_ENABLED && EIArea.isReadOrWrite(call)) {
            PsiElement areaIdElement = EIArea.getAreaIdElement(call);
            if (areaIdElement != null &&
                    areaIdElement.getNode().getElementType() == ScriptTypes.FLOATNUMBER) {
                try {
                    int areaId = Integer.parseInt(areaIdElement.getText());
                    Map<Integer, EIArea> vars = ((ScriptPsiFile) call.getContainingFile()).findAreas();
                    EIArea area = vars.get(areaId);
                    if (area != null) {
                        if (area.getReads() == 0) {
                            AnnotatorUtil.markAsWarning(holder, areaIdElement, EIMessages.message("warn.area.only.written", area));
                        }
                        if (area.getWrites() == 0) {
                            AnnotatorUtil.markAsWarning(holder, areaIdElement, EIMessages.message("warn.area.only.read", area));
                        }
                    } else {
                        LOG.error("Area  is null for " + call);
                    }
                } catch (NumberFormatException ignored) {
                    //don't really care
                }
            }
        }
    }

}