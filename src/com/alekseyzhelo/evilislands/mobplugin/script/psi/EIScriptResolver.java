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
package com.alekseyzhelo.evilislands.mobplugin.script.psi;

import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.PackageReferenceSet;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.PsiPackageReference;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author: Fedor.Korotkov, Aleksey Zhelo
 */
public class EIScriptResolver implements ResolveCache.AbstractResolver<ScriptPsiReference, List<? extends PsiElement>> {
    public static final EIScriptResolver INSTANCE = new EIScriptResolver();
    public static final String IMPORT_EXTENSION = ".eiscript";

    @Override
    public List<? extends PsiElement> resolve(@NotNull ScriptPsiReference reference, boolean incompleteCode) {
        final List<PsiElement> result = new ArrayList<PsiElement>();
        PsiTreeUtil.treeWalkUp(new ResolveScopeProcessor(result, reference.getCanonicalText()), reference, null, new ResolveState());
        if (!result.isEmpty()) {
            return result;
        }

        return ContainerUtil.emptyList();
    }

    private static List<? extends PsiElement> toCandidateInfoArray(@Nullable PsiElement element) {
        return element == null ? Collections.<PsiElement>emptyList() : Arrays.asList(element);
    }

    private class ResolveScopeProcessor implements PsiScopeProcessor {
        private final List<PsiElement> result;
        final String name;

        private ResolveScopeProcessor(List<PsiElement> result, String name) {
            this.result = result;
            this.name = name;
        }

        @Override
        public boolean execute(@NotNull PsiElement element, ResolveState state) {
            if (element instanceof ScriptNamedElementMixin) {
                final PsiElement elementName = ((ScriptNamedElementMixin) element).getNameIdentifier();
                if (elementName != null && name.equals(elementName.getText())) {
                    result.add(elementName);
                    return false;
                }
            }
            return true;
        }

        @Override
        public <T> T getHint(@NotNull Key<T> hintKey) {
            return null;
        }

        @Override
        public void handleEvent(Event event, @Nullable Object associated) {
        }
    }
}
