package com.alekseyzhelo.evilislands.mobplugin.script.psi.impl;

import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.GlobalVariableReference;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptElementFactory;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptResolveUtil;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiReference;
import com.intellij.psi.filters.ElementFilter;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.scope.processor.FilterScopeProcessor;
import org.picocontainer.defaults.SimpleReference;

import java.util.ArrayList;
import java.util.List;

public class EIScriptPsiImplUtil {


//    public static PsiReference[] getReferences(EIGlobalVar variable) {
//        List<PsiReference> references = new ArrayList<>();
//        ScriptFile file = (ScriptFile) variable.getContainingFile();
//        List<EIVariableAccess> accesses = EIScriptUtil.findVariableAccesses(file);
//        for (EIVariableAccess access : accesses) {
//            String text = (String) access.getNameIdentifier().getText();
//            if (text != null && text.equals(variable.getName())) {
//                references.add(
//                        new GlobalVariableReference(access.getNameIdentifier(), new TextRange(0, text.length()))
//                );
//            }
//        }
//
//        return references.toArray(new PsiReference[references.size()]);
//    }

    public static PsiReference getReference(EIVariableAccess variable) {
        List<PsiReference> references = new ArrayList<>();
        ScriptFile file = (ScriptFile) variable.getContainingFile();
        List<EIGlobalVar> globals = EIScriptResolveUtil.findGlobalVars(file);
        for (EIGlobalVar global : globals) {
            String text = (String) global.getName();
            if (text != null && text.equals(variable.getText())) {
                references.add(
                        new GlobalVariableReference(variable, new TextRange(0, text.length()))
                );
            }
        }

        return references.size() == 1 ? references.get(0) : null;
    }

    // UNUSED
    public static String getType(EIGlobalVar element) {
        ASTNode valueNode = element.getNode().findChildByType(ScriptTypes.TYPE);
        if (valueNode != null) {
            return valueNode.getText();
        } else {
            return null;
        }
    }
}