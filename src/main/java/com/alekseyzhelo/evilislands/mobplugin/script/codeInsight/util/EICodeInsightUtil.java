package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.util;

import com.alekseyzhelo.evilislands.mobplugin.EIMessages;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.fixes.DeleteElementFix;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.fixes.DeleteListElementFix;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.psi.PsiElement;

public final class EICodeInsightUtil {

    private EICodeInsightUtil() {

    }

    // TODO v2: QuickFixFactory?
    public static LocalQuickFixAndIntentionActionOnPsiElement createDeleteElementFix(
            PsiElement toDelete, boolean isInList) {
        return isInList
                ? new DeleteListElementFix(toDelete, EIMessages.message("fix.remove.element"))
                : new DeleteElementFix(toDelete, EIMessages.message("fix.remove.element"));
    }
}
