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

import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.lookup.EILookupElementFactory;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Aleksey Zhelo
 */
public class EIScriptResolveUtil {

    @Nullable
    public static List<EIFormalParameter> findEnclosingScriptParams(ScriptPsiFile scriptFile, PsiElement myElement) {
        EIScriptImplementation script = PsiTreeUtil.getParentOfType(myElement, EIScriptImplementation.class);
        if (script == null || script.getName().isEmpty()) {
            return null;
        } else {
            EIScriptDeclaration declaration = scriptFile.findScriptDeclaration(script.getName());
            return declaration != null ? declaration.getFormalParameterList() : null;
        }
    }

    public static void fillParamVariantsOfType(@NotNull List<LookupElement> variants,
                                               @NotNull List<EIFormalParameter> params,
                                               @NotNull EITypeToken expectedType) {
        for (final EIFormalParameter param : params) {
            EIType paramType = param.getType();
            if (param.getName().length() > 0 && paramType != null) {
                if (expectedType != EITypeToken.ANY && paramType.getTypeToken() != expectedType) {
                    continue;
                }
                variants.add(EILookupElementFactory.create(param));
            }
        }
    }

    @Nullable
    public static <T extends PsiNamedElement> T matchByName(String name, Collection<T> elements) {
        if (elements == null) {
            return null;
        }
        for (T element : elements) {
            final String elementName = element.getName();
            if (name.equals(elementName)) {
                return element;
            }
        }
        return null;
    }

    @Nullable
    public static <T extends PsiNamedElement> T matchByName(String name, T[] elements) {
        return matchByName(name, Arrays.asList(elements));
    }
}
