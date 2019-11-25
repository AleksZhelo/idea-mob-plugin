package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.inspections;

import com.alekseyzhelo.evilislands.mobplugin.EIMessages;
import com.alekseyzhelo.evilislands.mobplugin.script.EIScriptLanguage;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.annotator.EIScriptAnnotator;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFunctionCall;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIVisitor;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptPsiFile;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptTypes;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.impl.EIArea;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.impl.EIGSVar;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class EngineVarsUsageVisitor extends EIVisitor {

    private static final Logger LOG = Logger.getInstance(EIScriptAnnotator.class);

    private final ProblemsHolder myHolder;

    EngineVarsUsageVisitor(ProblemsHolder holder) {
        myHolder = holder;
    }

    @Override
    public void visitFunctionCall(@NotNull EIFunctionCall call) {
        super.visitFunctionCall(call);

        tryAnnotateGSVarUsage(call);
        tryAnnotateAreaUsage(call);
    }

    private void tryAnnotateGSVarUsage(@NotNull EIFunctionCall call) {
        if (EIScriptLanguage.GS_VARS_ENABLED && EIGSVar.isReadOrWrite(call)) {
            PsiElement varNameElement = EIGSVar.getGSVarArgument(call);
            if (varNameElement != null &&
                    varNameElement.getNode().getElementType() == ScriptTypes.CHARACTER_STRING) {
                Map<String, EIGSVar> vars = ((ScriptPsiFile) call.getContainingFile()).findGSVars();
                EIGSVar gsVar = vars.get(EIGSVar.getVarName(varNameElement.getText()));
                if (gsVar != null) {
                    String problem = null;
                    if (!gsVar.isZoneOrQuestVar()) {  // TODO v2: should still warn about only reading a special var?
                        if (gsVar.getReads() == 0) {
                            problem = EIMessages.message(gsVar.getWrites() == 1 ? "warn.gs.var.used.once" : "warn.gs.var.only.written", gsVar);
                        } else if (gsVar.getWrites() == 0) {
                            problem = EIMessages.message(gsVar.getReads() == 1 ? "warn.gs.var.used.once" : "warn.gs.var.only.read", gsVar);
                        }
                    }
                    if (problem != null) {
                        myHolder.registerProblem(varNameElement, problem, ProblemHighlightType.WEAK_WARNING);
                    }
                } else {
                    LOG.error("GSVar is null for " + call);
                }
            }
        }
    }

    // TODO: separate inspection?
    private void tryAnnotateAreaUsage(@NotNull EIFunctionCall call) {
        if (EIScriptLanguage.AREAS_ENABLED && EIArea.isReadOrWrite(call)) {
            PsiElement areaIdElement = EIArea.getAreaIdElement(call);
            if (areaIdElement != null &&
                    areaIdElement.getNode().getElementType() == ScriptTypes.FLOATNUMBER) {
                try {
                    Map<Integer, EIArea> vars = ((ScriptPsiFile) call.getContainingFile()).findAreas();
                    EIArea area = vars.get(Integer.parseInt(areaIdElement.getText()));
                    if (area != null) {
                        String problem = null;
                        if (area.getReads() == 0) {
                            problem = EIMessages.message("warn.area.only.written", area);
                        } else if (area.getWrites() == 0) {
                            problem = EIMessages.message("warn.area.only.read", area);
                        }

                        if (problem != null) {
                            myHolder.registerProblem(areaIdElement, problem, ProblemHighlightType.WARNING);
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
