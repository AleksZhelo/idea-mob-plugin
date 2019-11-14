/*
 * Copyright 2000-2013 JetBrains s.r.o.
 * Copyright 2014-2014 AS3Boyan
 * Copyright 2014-2014 Elias Ku
 * Copyright 2015-2015 Aleksey Zhelo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alekseyzhelo.evilislands.mobplugin.script.util;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFunctionCall;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIScriptBlock;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptTypes;
import com.intellij.openapi.util.Condition;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.SmartList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Fedor.Korotkov, Aleksey Zhelo
 */
// TODO: remove?
public class UsefulPsiTreeUtil {

    public static final PsiElementPattern.Capture<PsiElement> HAS_ERROR_CHILD = PlatformPatterns.psiElement()
            .withChild(PlatformPatterns.psiElement(PsiErrorElement.class));

    @Nullable
    public static PsiElement getFirstChildSkipWhiteSpacesAndComments(@Nullable PsiElement root) {
        if (root == null) return null;
        for (PsiElement child : root.getChildren()) {
            if (!isWhitespaceOrComment(child)) {
                return child;
            }
        }
        return null;
    }

    @Nullable
    public static PsiElement getPrevSiblingSkippingCondition(@Nullable PsiElement sibling, Condition<PsiElement> condition, boolean strictly) {
        if (sibling == null) return null;
        PsiElement result = strictly ? sibling.getPrevSibling() : sibling;
        while (result != null && condition.value(result)) {
            result = result.getPrevSibling();
        }
        return result;
    }

    @Nullable
    public static PsiElement getNextSiblingSkipWhiteSpacesAndCommas(@Nullable PsiElement sibling, boolean strictly) {
        return getNextSiblingSkippingCondition(
                sibling,
                element -> element instanceof PsiWhiteSpace || element.getNode().getElementType() == ScriptTypes.COMMA,
                strictly
        );
    }

    @Nullable
    public static PsiElement getNextSiblingSkippingCondition(@Nullable PsiElement sibling, Condition<PsiElement> condition, boolean strictly) {
        if (sibling == null) return null;
        PsiElement result = strictly ? sibling.getNextSibling() : sibling;
        while (result != null && condition.value(result)) {
            result = result.getNextSibling();
        }
        return result;
    }

    public static boolean isWhitespaceOrComment(PsiElement element) {
        return element instanceof PsiWhiteSpace || element instanceof PsiComment;
    }

    @Nullable
    public static PsiElement getParentByPattern(@Nullable PsiElement element,
                                                @NotNull PsiElementPattern.Capture<PsiElement> pattern) {
        if (element == null) return null;
        while (element != null) {
            if (pattern.accepts(element)) {
                return element;
            }
            if (element instanceof PsiFile) return null;
            element = element.getParent();
        }

        return null;
    }

    @Nullable
    public static <T extends PsiElement> T[] getChildrenOfType(@Nullable PsiElement element,
                                                               @NotNull Class<T> aClass,
                                                               @Nullable PsiElement lastParent) {
        if (element == null) return null;

        List<T> result = null;
        for (PsiElement child = element.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (lastParent == child) {
                break;
            }
            if (aClass.isInstance(child)) {
                if (result == null) result = new SmartList<>();
                //noinspection unchecked
                result.add((T) child);
            }
        }
        return result == null ? null : ArrayUtil.toObjectArray(result, aClass);
    }

    public static EIFunctionCall getParentFunctionCall(PsiElement element) {
        return PsiTreeUtil.getParentOfType(
                element,
                EIFunctionCall.class,
                true,
                EIScriptBlock.class
        );
    }
}
