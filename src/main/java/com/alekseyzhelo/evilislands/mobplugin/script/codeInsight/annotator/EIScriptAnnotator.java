package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.annotator;

import com.alekseyzhelo.evilislands.mobplugin.EIMessages;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.fixes.*;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.util.EICallArgumentErrorDetector;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.util.EICodeInsightUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.base.EICallableDeclaration;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.base.EIScriptPsiElement;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.references.FunctionCallReference;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.references.MobObjectReferenceBase;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EITypeToken;
import com.intellij.codeInsight.intention.IntentionAction;
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
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

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
    public void visitGlobalVars(@NotNull EIGlobalVars globalVars) {
        super.visitGlobalVars(globalVars);

        EIRepeatDeclarationHandler<EIGlobalVar> repeatChecker =
                new EIRepeatDeclarationHandler<EIGlobalVar>(globalVars.getGlobalVarList(), true) {
                    @Override
                    protected IntentionAction createNavigateToAlreadyDeclaredElementFix(EIGlobalVar element) {
                        return new NavigateToAlreadyDeclaredGlobalVarFix(element);
                    }
                };
        repeatChecker.registerErrors(myHolder, "error.global.var.already.defined");
    }

    @Override
    public void visitDeclarations(@NotNull EIDeclarations declarations) {
        super.visitDeclarations(declarations);

        EIRepeatDeclarationHandler<EIScriptDeclaration> repeatChecker =
                new EIRepeatDeclarationHandler<EIScriptDeclaration>(declarations.getScriptDeclarationList(), false) {
                    @Override
                    protected IntentionAction createNavigateToAlreadyDeclaredElementFix(EIScriptDeclaration element) {
                        return new NavigateToAlreadyDeclaredScriptFix(element);
                    }
                };
        repeatChecker.registerErrors(myHolder, "error.script.already.declared");
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
            annotation.registerFix(EICodeInsightUtil.createDeleteElementFix(scriptDeclaration, false), range);
        }

        EIRepeatDeclarationHandler<EIFormalParameter> repeatChecker =
                new EIRepeatDeclarationHandler<EIFormalParameter>(scriptDeclaration.getFormalParameterList(), true) {
                    @Override
                    protected IntentionAction createNavigateToAlreadyDeclaredElementFix(EIFormalParameter element) {
                        return new NavigateToAlreadyDeclaredParameterFix(element);
                    }
                };
        repeatChecker.registerErrors(myHolder, "error.parameter.already.defined");
    }

    @Override
    public void visitScripts(@NotNull EIScripts scripts) {
        super.visitScripts(scripts);

        EIRepeatDeclarationHandler<EIScriptImplementation> repeatChecker =
                new EIRepeatDeclarationHandler<EIScriptImplementation>(scripts.getScriptImplementationList(), false) {
                    @Override
                    protected IntentionAction createNavigateToAlreadyDeclaredElementFix(EIScriptImplementation element) {
                        return new NavigateToAlreadyImplementedScriptFix(element);
                    }
                };
        repeatChecker.registerErrors(myHolder, "error.script.already.implemented");
    }

    @Override
    public void visitScriptImplementation(@NotNull EIScriptImplementation scriptImplementation) {
        super.visitScriptImplementation(scriptImplementation);

        PsiReference reference = scriptImplementation.getReference();
        PsiElement ident = scriptImplementation.getNameIdentifier();
        if (ident != null && reference.resolve() == null) {
            Annotation annotation = AnnotatorUtil.markAsError(myHolder, ident,
                    EIMessages.message("warn.script.not.declared", scriptImplementation.getName()), true);
            AnnotatorUtil.registerReferenceQuickFixes(annotation, reference);
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
            AnnotatorUtil.tryRegisterChangeVariableTypeFix(left.getReference().resolve(), rType,
                    assignment, annotation);
        }
    }

    @Override
    // TODO: generalize with forIf
    public void visitForBlock(@NotNull EIForBlock forBlock) {
        super.visitForBlock(forBlock);

        List<EIExpression> expressions = forBlock.getArguments();
        if (expressions.size() == 2) {
            EIExpression firstArg = expressions.get(0);
            EIExpression secondArg = expressions.get(1);
            if (firstArg != null && secondArg != null) {
                EITypeToken typeFirst = firstArg.getType();
                EITypeToken typeSecond = secondArg.getType();
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
    public void visitForIfBlock(@NotNull EIForIfBlock forIfBlock) {
        super.visitForIfBlock(forIfBlock);

        List<EIExpression> expressions = forIfBlock.getArguments();
        if (expressions.size() == 3) {
            EIExpression firstArg = expressions.get(0);
            EIExpression secondArg = expressions.get(1);
            EIExpression thirdArg = expressions.get(2);
            if (firstArg != null && secondArg != null && thirdArg != null) {
                EITypeToken typeFirst = firstArg.getType();
                EITypeToken typeSecond = secondArg.getType();
                EITypeToken typeThird = thirdArg.getType();
                if (typeFirst != EITypeToken.OBJECT || typeSecond != EITypeToken.GROUP
                        || typeThird != EITypeToken.FLOAT) {
                    AnnotatorUtil.createBadForIfArgumentsAnnotation(
                            myHolder,
                            TextRange.create(firstArg.getTextOffset(),
                                    thirdArg.getTextOffset() + thirdArg.getTextLength()),
                            typeFirst,
                            typeSecond,
                            typeThird
                    );
                }
            }
        }
    }

    @Override
    public void visitIncompleteCall(@NotNull EIIncompleteCall incompleteCall) {
        super.visitIncompleteCall(incompleteCall);

        AnnotatorUtil.markAsError(myHolder, incompleteCall,
                EIMessages.message("error.incomplete.call", incompleteCall.getText()), true);
    }

    @Override
    public void visitFunctionCall(@NotNull EIFunctionCall call) {
        super.visitFunctionCall(call);

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
                // TODO v2: could prompt to create a script here
                Annotation annotation = AnnotatorUtil.markAsError(myHolder, callNameElement,
                        errorMessage, resolvedTo == null);
                annotation.registerFix(
                        EICodeInsightUtil.createDeleteElementFix(call, false),
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

                if (resolvedTo instanceof EIScriptDeclaration) {
                    EIFormalParameter parameter = errorDetector.getFirstWrongParameter();
                    EIExpression expression = errorDetector.getFirstWrongArgument();
                    if (expression != null) {
                        AnnotatorUtil.tryRegisterChangeVariableTypeFix(parameter, expression.getType(),
                                expression, annotation);
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
                    EIMessages.message("error.undefined.variable", access.getName()), true);
            AnnotatorUtil.registerReferenceQuickFixes(annotation, reference);
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void visitLiteral(@NotNull EILiteral literal) {
        super.visitLiteral(literal);

        PsiReference reference = literal.getReference();
        if (reference instanceof MobObjectReferenceBase) {
            if (reference.resolve() == null) {
                AnnotatorUtil.markAsWarning(myHolder, literal,
                        ((MobObjectReferenceBase) reference).getErrorMessage(), true);
            } else if (EITypeToken.FLOAT.equals(literal.getType()) && literal.getTextLength() >= 10) {
                AnnotatorUtil.markAsWarning(myHolder, literal,
                        EIMessages.message("warn.getObject.does.not.support.long.ids"));
            }
        }
    }
}