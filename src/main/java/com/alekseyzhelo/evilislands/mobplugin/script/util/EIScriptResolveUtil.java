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

import com.alekseyzhelo.evilislands.mobplugin.script.file.ScriptFileType;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.FileBasedIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author Aleksey Zhelo
 */
public class EIScriptResolveUtil {

    public static List<EIGlobalVar> findGlobalVars(ScriptFile scriptFile) {
        if (scriptFile == null) {
            return Collections.emptyList();
        }
        final EIGlobalVars globalVars = PsiTreeUtil.getChildOfType(scriptFile, EIGlobalVars.class);
        if (globalVars == null) {
            return Collections.emptyList();
        }

        final EIGlobalVar[] globalVarsActual = PsiTreeUtil.getChildrenOfType(globalVars, EIGlobalVar.class);
        if (globalVarsActual == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(globalVarsActual);
    }

    public static EIGlobalVar findGlobalVar(ScriptFile scriptFile, String varName) {
        return matchByName(varName, findGlobalVars(scriptFile));
    }

    // useless, but do not want to delete for now
    public static List<EIGlobalVar> findGlobalVars(Project project) {
        List<EIGlobalVar> result = new ArrayList<>();
        Collection<VirtualFile> virtualFiles = FileBasedIndex.getInstance().getContainingFiles(
                FileTypeIndex.NAME,
                ScriptFileType.INSTANCE,
                GlobalSearchScope.allScope(project)
        );
        for (VirtualFile virtualFile : virtualFiles) {
            ScriptFile scriptFile = (ScriptFile) PsiManager.getInstance(project).findFile(virtualFile);
            result.addAll(findGlobalVars(scriptFile));
        }
        return result;
    }

    @NotNull
    public static List<EIScriptDeclaration> findScriptDeclarations(@Nullable ScriptFile file) {
        if (file == null) {
            return Collections.emptyList();
        }
        final EIDeclarations declarations = PsiTreeUtil.getChildOfType(file, EIDeclarations.class);
        if (declarations == null) {
            return Collections.emptyList();
        }
        final EIScriptDeclaration[] scriptDeclarations = PsiTreeUtil.getChildrenOfType(declarations, EIScriptDeclaration.class);
        if (scriptDeclarations == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(scriptDeclarations);
    }

    @Nullable
    public static EIScriptDeclaration findScriptDeclaration(@Nullable ScriptFile file, @NotNull String scriptName) {
        return matchByName(scriptName, findScriptDeclarations(file));
    }

    @Nullable
    public static List<EIFormalParameter> findEnclosingScriptParams(PsiElement myElement) {
        EIScriptImplementation script = UsefulPsiTreeUtil.getParentOfType(myElement, EIScriptImplementation.class);
        if (script == null || script.getName() == null) {
            return null;
        } else {
            EIScriptDeclaration declaration = EIScriptResolveUtil.findScriptDeclaration(
                    (ScriptFile) script.getContainingFile(),
                    script.getName()
            );
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
