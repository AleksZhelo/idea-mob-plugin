package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.lookup;

import com.alekseyzhelo.evilislands.mobplugin.icon.Icons;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.EIFunctionParameterInfoHandler;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.util.EICompletionUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFormalParameter;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFunctionCall;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIParams;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.base.EICallableDeclaration;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EICommonUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EITypeToken;
import com.intellij.codeInsight.CodeInsightSettings;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.completion.OffsetKey;
import com.intellij.codeInsight.hint.ParameterInfoController;
import com.intellij.codeInsight.hint.ShowParameterInfoContext;
import com.intellij.codeInsight.hints.ParameterHintsPass;
import com.intellij.codeInsight.lookup.Lookup;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.codeInsight.lookup.LookupItem;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.registry.Registry;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

// see JavaMethodCallElement
// TODO v2: param name boxes? (probably JavaInlayHintsProvider, uses setCompletionMode)
// TODO: InlayParameterHintsProvider?
public class EICallableLookupElement extends LookupItem<EICallableDeclaration> implements EITypedLookupItem {

    private final boolean forScript;

    EICallableLookupElement(EICallableDeclaration declaration, boolean forScript) {
        super(declaration, declaration.getName());
        this.forScript = forScript;
    }

    private static void showParameterHints(InsertionContext context,
                                           EICallableDeclaration callable,
                                           EIFunctionCall call) {
        if (call == null) {
            return;
        }
        List<EIFormalParameter> parameterList = callable.getCallableParams();
        int parametersCount = parameterList.size();
        EIParams parameterOwner = call.getParams();
        if (!"()".equals(parameterOwner.getText()) ||
                parametersCount == 0 ||
                context.getCompletionChar() == Lookup.COMPLETE_STATEMENT_SELECT_CHAR || context.getCompletionChar() == Lookup.REPLACE_SELECT_CHAR ||
                !CodeInsightSettings.getInstance().SHOW_PARAMETER_NAME_HINTS_ON_COMPLETION) {
            return;
        }

        Editor editor = context.getEditor();

        Project project = context.getProject();
        Document document = editor.getDocument();
        PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(document);

        int limit = 256;

        CaretModel caretModel = editor.getCaretModel();
        int offset = caretModel.getOffset();

        int afterParenOffset = offset + 1;
        if (afterParenOffset < document.getTextLength() &&
                Character.isJavaIdentifierPart(document.getImmutableCharSequence().charAt(afterParenOffset))) {
            return;
        }

        int braceOffset = offset - 1;
        int numberOfCommas = Math.min(parametersCount, limit) - 1;
        String commas = Registry.is("editor.completion.hints.virtual.comma") ? "" : StringUtil.repeat(", ", numberOfCommas);
        document.insertString(offset, commas);

        PsiDocumentManager.getInstance(project).commitDocument(document);
        EIFunctionParameterInfoHandler handler = new EIFunctionParameterInfoHandler();
        ShowParameterInfoContext infoContext = new ShowParameterInfoContext(editor, project, context.getFile(), offset, braceOffset);
        if (!call.isValid() || handler.findElementForParameterInfo(infoContext) == null) {
            document.deleteString(offset, offset + commas.length());
            return;
        }

        ParameterInfoController controller = new ParameterInfoController(project, editor, braceOffset, infoContext.getItemsToShow(), null,
                call.getParams(), handler, false, false);
        if (Disposer.isDisposed(controller)) {
            document.deleteString(offset, offset + commas.length());
        } else {
            ParameterHintsPass.syncUpdate(call, editor);
        }
    }

    private static EIFunctionCall findCallAtOffset(InsertionContext context, int offset) {
        context.commitDocument();
        return PsiTreeUtil.findElementOfClassAtOffset(context.getFile(), offset, EIFunctionCall.class, false);
    }

    @Override
    public void handleInsert(@NotNull InsertionContext context) {
        final EICallableDeclaration callable = getObject();
        final boolean hasParams = callable.getCallableParams().size() > 0;

        // fix suggestion case
        context.getDocument().replaceString(context.getStartOffset(), context.getTailOffset(), callable.getName());

        EICompletionUtil.insertParentheses(context, this, hasParams, true);

        final int startOffset = context.getStartOffset();
        final OffsetKey refStart = context.trackOffset(startOffset, true);

        EIFunctionCall call = findCallAtOffset(context, context.getOffset(refStart));
        showParameterHints(context, callable, call);
    }

    @Override
    public void renderElement(LookupElementPresentation presentation) {
        super.renderElement(presentation);
        List<EIFormalParameter> params = getObject().getCallableParams();
        if (params.size() > 0) {
            presentation.setTailText(" " + EICommonUtil.getParenthesisedParamsString(params), true);
        }
        presentation.setTypeText(getType().getTypeString());
        presentation.setIcon(forScript ? Icons.SCRIPT_IMPL : Icons.FUNCTION);
    }

    @Override
    public boolean isCaseSensitive() {
        return false;
    }

    @NotNull
    public EITypeToken getType() {
        return getObject().getCallableType();
    }
}
