package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight;

import com.alekseyzhelo.evilislands.mobplugin.EIMessages;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIExpression;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFormalParameter;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIParams;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIType;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.base.EICallableDeclaration;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptTypingUtil;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EITypeToken;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.util.TextRange;
import com.intellij.ui.ColorUtil;
import com.intellij.util.ui.UIUtil;
import com.intellij.xml.util.XmlStringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.alekseyzhelo.evilislands.mobplugin.script.util.EIScriptNamingUtil.*;

final class AnnotatorUtil {

    private AnnotatorUtil() {

    }

    @NotNull
    static Annotation createIncompatibleCallTypesAnnotation(@NotNull AnnotationHolder holder,
                                                            @NotNull List<EIFormalParameter> parameters,
                                                            @NotNull List<EIExpression> arguments,
                                                            int idxError) {
        EIFormalParameter parameter = parameters.get(idxError);
        EIExpression expression = arguments.get(idxError);
        return holder.createErrorAnnotation(
                expression.getTextRange(),
                EIMessages.message("error.incompatible.call.types",
                        idxError + 1,
                        getName(parameter.getType()),
                        getName(expression.getType()))
        );
    }

    @NotNull
    static Annotation createBadCallArgumentsAnnotation(@NotNull AnnotationHolder holder,
                                                       @NotNull EICallableDeclaration declaration,
                                                       @NotNull EIParams arguments) {
        // TODO; need a message?
        Annotation annotation = holder.createErrorAnnotation(arguments.getTextRange(), "");
        annotation.setTooltip(badCallArgumentsTooltip(declaration, arguments.getExpressionList()));
        return annotation;
    }

    @NotNull
    private static String badCallArgumentsTooltip(@NotNull EICallableDeclaration callable,
                                                  @NotNull List<EIExpression> expressions) {
        List<EIFormalParameter> parameters = callable.getCallableParams();
        final String spacing = "&nbsp;&nbsp;&nbsp;&nbsp;";

        StringBuilder s = new StringBuilder()
                .append("<html><body><table border=0>")
                .append("<tr><td colspan=3>").append("<nobr><b>").append(callable.getName()).append("()")
                .append(String.format("</b> %s</nobr>", EIMessages.message("error.call.cannot.be.applied.to")))
                .append("</td></tr>")
                .append(String.format("<tr><td colspan=2 align=left>%s</td><td align=left>%s</td></tr>",
                        EIMessages.message("error.call.expected.parameters"), EIMessages.message("error.call.actual.arguments")))
                .append("<tr><td colspan=3><hr></td></tr>");
        for (int i = 0; i < Math.max(parameters.size(), expressions.size()); i++) {
            EIFormalParameter parameter = i < parameters.size() ? parameters.get(i) : null;
            EIExpression expression = i < expressions.size() ? expressions.get(i) : null;
            String mismatchColor = EIScriptTypingUtil.matchingType(parameter, expression) ? null : getFittingRed();

            s.append("<tr");
            if (i % 2 == 0) {
                //noinspection SpellCheckingInspection
                String bg = UIUtil.isUnderDarcula() ? ColorUtil.toHex(ColorUtil.shift(UIUtil.getToolTipBackground(), 1.1)) : "eeeeee";
                s.append(" style='background-color: #").append(bg).append("'");
            }
            s.append(">");

            s.append("<td><b><nobr>");
            if (parameter != null) {
                String name = parameter.getName();
                s.append(escape(name)).append(":").append(spacing);
            }
            s.append("</nobr></b></td>");

            s.append("<td><b><nobr>");
            if (parameter != null) {
                EIType type = parameter.getType();
                s.append("<font ");
                if (mismatchColor != null) s.append("color=").append(mismatchColor);
                s.append(">");
                s.append(escape(getName(type))).append(spacing);
                s.append("</font>");
            }
            s.append("</nobr></b></td>");

            s.append("<td><b><nobr>");
            if (expression != null) {
                EITypeToken type = expression.getType();
                s.append("<font ");
                if (mismatchColor != null) s.append("color='").append(mismatchColor).append("'");
                s.append(">");
                s.append(escape(expression.getText()));
                s.append("&nbsp;&nbsp;");
                if (mismatchColor != null && type != null) {
                    s.append("(").append(escape(getName(type))).append(")");
                }
                s.append("</font>");
            }
            s.append("</nobr></b></td>");
            s.append("</tr>");
        }
        s.append("</table>");
        s.append("</body></html>");

        return s.toString();
    }

    @NotNull
    static Annotation createIncompatibleTypesAnnotation(@NotNull AnnotationHolder holder,
                                                        @NotNull TextRange textRange,
                                                        EITypeToken lType,
                                                        EITypeToken rType) {
        boolean assignable = EIScriptTypingUtil.matchingType(lType, rType);
        String toolTip = EIMessages.message("incompatible.types.html.tooltip",
                redIfNotMatch(lType, assignable),
                redIfNotMatch(rType, assignable)
        );
        String message = EIMessages.message(
                "error.incompatible.types", getName(lType), getName(rType)
        );

        Annotation annotation = holder.createErrorAnnotation(textRange, message);
        annotation.setTooltip(toolTip);
        return annotation;
    }

    @NotNull
    private static String redIfNotMatch(@Nullable EITypeToken type, boolean matches) {
        if (matches) return getName(type);
        return "<font color='" + getFittingRed() + "'><b>" + getName(type) + "</b></font>";
    }

    @NotNull
    private static String getFittingRed() {
        return UIUtil.isUnderDarcula() ? "FF6B68" : "red";
    }

    @NotNull
    private static String escape(@NotNull String s) {
        return XmlStringUtil.escapeString(s);
    }
}
