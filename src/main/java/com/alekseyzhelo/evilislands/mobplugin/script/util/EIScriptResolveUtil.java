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

import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author Aleksey Zhelo
 */
public class EIScriptResolveUtil {

    @Nullable
    public static List<EIFormalParameter> findEnclosingScriptParams(PsiElement myElement) {
        EIScriptImplementation script = UsefulPsiTreeUtil.getParentOfType(myElement, EIScriptImplementation.class);
        if (script == null || script.getName() == null) {
            return null;
        } else {
            EIScriptDeclaration declaration =
                    ((ScriptPsiFile) script.getContainingFile()).findScriptDeclaration(script.getName());
            return declaration != null ? declaration.getFormalParameterList() : null;
        }
    }

    // TODO use
    @Nullable
    public static PsiComment findDocumentation(ScriptNamedElementMixin element) {
        final PsiElement candidate = UsefulPsiTreeUtil.getPrevSiblingSkipWhiteSpaces(element, true);
        if (candidate instanceof PsiComment) {
            return (PsiComment) candidate;
        }
        return null;
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
