package com.alekseyzhelo.evilislands.mobplugin.script.template;

import com.alekseyzhelo.evilislands.mobplugin.EIMessages;
import com.alekseyzhelo.evilislands.mobplugin.script.EIScriptLanguage;
import com.alekseyzhelo.evilislands.mobplugin.script.highlighting.EIScriptSyntaxHighlighter;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.base.EICallableDeclaration;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EITypeToken;
import com.alekseyzhelo.evilislands.mobplugin.script.util.UsefulPsiTreeUtil;
import com.intellij.codeInsight.template.EverywhereContextType;
import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class EITemplateContextType extends TemplateContextType {

    EITemplateContextType(@NotNull String id,
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

    private static PsiElement getParentSkipError(@NotNull PsiElement element) {
        PsiElement parent = element.getParent();
        if (parent instanceof PsiErrorElement) {  // skip IntellijIdeaRulezzz error element
            parent = parent.getParent();
        }
        return parent;
    }

    private static boolean isAfterNodeClosingParenthesis(int elementOffset, ASTNode node) {
        ASTNode rParen = node.findChildByType(ScriptTypes.RPAREN);
        return rParen != null && elementOffset > rParen.getTextRange().getEndOffset();
    }

    public static class EIGeneric extends EITemplateContextType {

        public EIGeneric() {
            super("EI", EIMessages.message("lang.display.name"), EverywhereContextType.class);
        }

        @Override
        protected boolean isInContext(@NotNull PsiElement element) {
            return true;
        }
    }

    public static class ScriptDeclarationAllowed extends EITemplateContextType {

        public ScriptDeclarationAllowed() {
            super("EI_SCRIPT_DECLARATION_ALLOWED",
                    EIMessages.message("templates.context.script.declaration.allowed"), EIGeneric.class);
        }

        @Override
        protected boolean isInContext(@NotNull PsiElement element) {
            ScriptPsiFile file = (ScriptPsiFile) element.getContainingFile();
            PsiElement parent = getParentSkipError(element);
            int elementOffset = element.getTextOffset();

            EIScripts scripts = file.findChildByClass(EIScripts.class);
            EIWorldScript worldScript = file.findChildByClass(EIWorldScript.class);
            final boolean topLevel = parent instanceof EIDeclarations || parent instanceof ScriptPsiFile;
            final boolean beforeScriptsAndWorldScript =
                    (scripts == null && (worldScript == null || elementOffset < worldScript.getTextOffset()))
                            || (scripts != null && elementOffset < scripts.getTextOffset());

            if (!beforeScriptsAndWorldScript) {
                return false;
            }
            if (topLevel) {
                EIGlobalVars globalVars = file.findChildByClass(EIGlobalVars.class);
                return globalVars == null || elementOffset > globalVars.getTextRange().getEndOffset();
            } else if (parent instanceof EIScriptDeclaration) {
                return isAfterNodeClosingParenthesis(elementOffset, parent.getNode());
            }
            return false;
        }
    }

    public static class ScriptAllowed extends EITemplateContextType {

        public ScriptAllowed() {
            super("EI_SCRIPT_ALLOWED",
                    EIMessages.message("templates.context.script.allowed"), EIGeneric.class);
        }

        @Override
        protected boolean isInContext(@NotNull PsiElement element) {
            ScriptPsiFile file = (ScriptPsiFile) element.getContainingFile();
            PsiElement parent = getParentSkipError(element);
            int elementOffset = element.getTextOffset();

            EIDeclarations declarations = file.findChildByClass(EIDeclarations.class);
            EIWorldScript worldScript = file.findChildByClass(EIWorldScript.class);
            final boolean topLevel = parent instanceof EIScripts || parent instanceof ScriptPsiFile
                    || afterScriptImplClosingParenth(parent, elementOffset);
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
                    return isAfterNodeClosingParenthesis(elementOffset, parent.getNode());
                }
            }
            return false;
        }

        private boolean afterScriptImplClosingParenth(PsiElement correctedParent, int elementOffset) {
            if (correctedParent instanceof EIScriptImplementation) {
                ASTNode node = correctedParent.getNode();
                return isAfterNodeClosingParenthesis(elementOffset, node);
            }
            return false;
        }
    }

    public static class ScriptBlockAllowed extends EITemplateContextType {

        public ScriptBlockAllowed() {
            super("EI_SCRIPT_BLOCK_ALLOWED",
                    EIMessages.message("templates.context.script.block.allowed"), EIGeneric.class);
        }

        @Override
        protected boolean isInContext(@NotNull PsiElement element) {
            PsiElement parent = getParentSkipError(element);

            if (parent instanceof EIScriptImplementation) {
                ASTNode node = parent.getNode();
                ASTNode lParen = node.findChildByType(ScriptTypes.LPAREN);
                ASTNode rParen = node.findChildByType(ScriptTypes.RPAREN);

                return lParen != null
                        && element.getTextOffset() > lParen.getStartOffset()
                        && (rParen == null || element.getTextOffset() < rParen.getStartOffset());
            } else if (parent instanceof EIScriptIfBlock) { // incomplete ifBlock at script end
                ASTNode node = parent.getNode();
                ASTNode lParen = node.findChildByType(ScriptTypes.LPAREN);
                ASTNode rParen = node.findChildByType(ScriptTypes.RPAREN);

                return lParen == null && rParen != null && element.getTextOffset() < rParen.getStartOffset();
            } else {
                return parent instanceof EIScriptBlock;
            }
        }
    }

    public static class ScriptExpressionAllowed extends EITemplateContextType {

        public ScriptExpressionAllowed() {
            super("EI_SCRIPT_EXPRESSION_ALLOWED",
                    EIMessages.message("templates.context.script.expression.allowed"), EIGeneric.class);
        }

        @Override
        protected boolean isInContext(@NotNull PsiElement element) {
            PsiElement parent = getParentSkipError(element);
            if (parent instanceof EIVariableAccess) {
                parent = parent.getParent().getParent(); // skip access and assignment
            }

            return parent instanceof EIScriptThenBlock || parent instanceof EIWorldScript;
        }
    }

    public static class FunctionArgumentAllowed extends EITemplateContextType {

        @SuppressWarnings("unused")
        public FunctionArgumentAllowed() {
            super("EI_FUNCTION_ARGUMENT_ALLOWED",
                    EIMessages.message("templates.context.function.argument.allowed"), EIGeneric.class);
        }

        FunctionArgumentAllowed(@NotNull String id,
                                @NotNull String presentableName,
                                @Nullable Class<? extends TemplateContextType> baseContextType) {
            super(id, presentableName, baseContextType);
        }

        @Override
        protected boolean isInContext(@NotNull PsiElement element) {
            PsiElement parent = getParentSkipError(element);
            if (parent instanceof EIVariableAccess) {
                parent = parent.getParent();
            }

            return parent instanceof EIParams;
        }
    }

    public static class CoordinateArgumentsAllowed extends FunctionArgumentAllowed {

        public CoordinateArgumentsAllowed() {
            super("EI_COORDINATE_ARGUMENTS_ALLOWED",
                    EIMessages.message("templates.context.coordinate.arguments.allowed"), FunctionArgumentAllowed.class);
        }

        @Override
        protected boolean isInContext(@NotNull PsiElement element) {
            if (super.isInContext(element)) {
                EIFunctionCall call = UsefulPsiTreeUtil.getParentFunctionCall(element);
                EICallableDeclaration resolved = call != null
                        ? (EICallableDeclaration) call.getReference().resolve()
                        : null;
                return resolved != null && hasXParam(resolved);
            }
            return false;
        }

        // TODO: two contexts, separate for xy and xyz, and detect exact matches?
        private boolean hasXParam(@NotNull EICallableDeclaration declaration) {
            for (EIFormalParameter parameter : declaration.getCallableParams()) {
                if (StringUtil.equalsIgnoreCase(parameter.getName(), "x")
                        && EITypeToken.FLOAT.equals(Objects.requireNonNull(parameter.getType()).getTypeToken())) {
                    return true;
                }
            }
            return false;
        }
    }
}
