package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.inspections;

import com.alekseyzhelo.evilislands.mobplugin.EIMessages;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.annotator.EIScriptAnnotator;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.fixes.CallKillScriptFix;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EICallStatement;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIScriptStatement;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIScriptThenBlock;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIVisitor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;

public class KillScriptDetectorVisitor extends EIVisitor {

    private static final Logger LOG = Logger.getInstance(EIScriptAnnotator.class);

    private final ProblemsHolder myHolder;

    KillScriptDetectorVisitor(ProblemsHolder holder) {
        myHolder = holder;
    }

    @Override
    public void visitScriptThenBlock(@NotNull EIScriptThenBlock thenBlock) {
        super.visitScriptThenBlock(thenBlock);

        boolean ksFound = false;
        for (EIScriptStatement statement : thenBlock.getScriptStatementList()) {
            if (statement instanceof EICallStatement
                    && (StringUtil.equalsIgnoreCase(((EICallStatement) statement).getName(), "KillScript"))) {
                ksFound = true;
                break;
            }
        }
        if (!ksFound) {
            myHolder.registerProblem(
                    thenBlock.getFirstChild(),
                    EIMessages.message("inspection.killScript.not.called.warning"),
                    ProblemHighlightType.WARNING,
                    new CallKillScriptFix(thenBlock)
            );
        }
    }
}
