package com.alekseyzhelo.evilislands.mobplugin.script.template;

import com.alekseyzhelo.evilislands.mobplugin.script.EIScriptLanguage;
import com.alekseyzhelo.evilislands.mobplugin.script.highlighting.EIScriptSyntaxHighlighter;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
import com.intellij.codeInsight.template.EverywhereContextType;
import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiFile;
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
            super("EI", "EIScript", EverywhereContextType.class);
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
            if (parent instanceof PsiErrorElement) {  // skip IntellijIdeaRulezzz error element
                parent = parent.getParent();
            }
            int elementOffset = element.getTextOffset();

            EIDeclarations declarations = file.findChildByClass(EIDeclarations.class);
            EIWorldScript worldScript = file.findChildByClass(EIWorldScript.class);
            final boolean topLevel = parent instanceof EIScripts || parent instanceof ScriptPsiFile;
            final boolean beforeWorldScript = (worldScript == null || elementOffset < worldScript.getTextRange().getStartOffset());

            if (!beforeWorldScript) {
                return false;
            }
            if (topLevel) {
                if (declarations == null) {
                    EIGlobalVars globalVars = file.findChildByClass(EIGlobalVars.class);
                    return globalVars == null || elementOffset > globalVars.getTextRange().getEndOffset();
                } else {
                    return elementOffset > declarations.getTextRange().getEndOffset();
                }
            } else if (parent instanceof EIScriptDeclaration) {
                assert declarations != null;
                if (declarations.getLastChild() == parent) {
                    ASTNode rParen = parent.getNode().findChildByType(ScriptTypes.RPAREN);
                    return rParen != null && elementOffset > rParen.getTextRange().getEndOffset();
                }
            }
            return false;
        }
    }

    public static class ScriptExpressionAllowed extends EITemplateContextType {
        
        public ScriptExpressionAllowed() {
            super("EI_SCRIPT_EXPRESSION_ALLOWED", "Script expression allowed", EIGeneric.class);
        }

        @Override
        protected boolean isInContext(@NotNull PsiElement element) {
            PsiElement parent = element.getParent();
            if (parent instanceof PsiErrorElement) {  // skip IntellijIdeaRulezzz error element
                parent = parent.getParent();
            }

            return parent instanceof EIScriptThenBlock || parent instanceof EIWorldScript;
        }
    }
}
