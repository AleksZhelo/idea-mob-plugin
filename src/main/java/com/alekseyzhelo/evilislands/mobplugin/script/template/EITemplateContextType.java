package com.alekseyzhelo.evilislands.mobplugin.script.template;

import com.alekseyzhelo.evilislands.mobplugin.script.EIScriptLanguage;
import com.alekseyzhelo.evilislands.mobplugin.script.highlighting.EIScriptSyntaxHighlighter;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIDeclarations;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIScripts;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIWorldScript;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptPsiFile;
import com.intellij.codeInsight.template.EverywhereContextType;
import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class EITemplateContextType extends TemplateContextType {

    protected EITemplateContextType(@NotNull String id,
                                    @NotNull String presentableName,
                                    @Nullable Class<? extends TemplateContextType> baseContextType) {
        super(id, presentableName, baseContextType);
    }

    @Override
    public boolean isInContext(@NotNull PsiFile file, int offset) {
        if (file.getLanguage() instanceof EIScriptLanguage) {
            PsiElement element = file.findElementAt(offset);
            return element != null && isInContext(element);
        }
        return false;
    }

    @Nullable
    @Override
    public SyntaxHighlighter createHighlighter() {
        return new EIScriptSyntaxHighlighter();
    }

    protected abstract boolean isInContext(@NotNull PsiElement element);

    public static class EIGeneric extends EITemplateContextType {

        public EIGeneric() {
            super("EI", "EIScript generic", EverywhereContextType.class);
        }

        @Override
        protected boolean isInContext(@NotNull PsiElement element) {
            return true;
        }
    }

    public static class ScriptAllowed extends EITemplateContextType {

        public ScriptAllowed() {
            super("EI_SCRIPT_ALLOWED", "Script allowed", EIGeneric.class);
        }

        @Override
        protected boolean isInContext(@NotNull PsiElement element) {
            ScriptPsiFile file = (ScriptPsiFile) element.getContainingFile();
            PsiElement parent = element.getParent();
            int elementOffset = element.getTextOffset();

            EIDeclarations declarations = file.findChildByClass(EIDeclarations.class);
            EIWorldScript worldScript = file.findChildByClass(EIWorldScript.class);

            boolean topLevelAfterDeclarations = declarations != null &&
                    (parent instanceof EIScripts || parent instanceof ScriptPsiFile) &&
                    elementOffset > declarations.getTextRange().getEndOffset();
            return topLevelAfterDeclarations && (worldScript == null || elementOffset < worldScript.getTextRange().getStartOffset());
        }
    }
}
