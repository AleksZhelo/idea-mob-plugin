package com.alekseyzhelo.evilislands.mobplugin.script;

import com.alekseyzhelo.evilislands.mobplugin.script.file.ScriptFileType;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIGlobalVar;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIScriptDeclaration;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIVariableAccess;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptFile;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.impl.EIScriptPsiImplUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.FileBasedIndex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class EIScriptUtil {
    public static List<EIGlobalVar> findGlobalVars(ScriptFile scriptFile, String name) {
        List<EIGlobalVar> result = null;
        if (scriptFile != null) {
            Collection<EIGlobalVar> globals = PsiTreeUtil.collectElementsOfType(scriptFile, EIGlobalVar.class);
            if (globals != null) {
                for (EIGlobalVar global : globals) {
                    if (name.equals(global.getName())) {
                        if (result == null) {
                            result = new ArrayList<>();
                        }
                        result.add(global);
                    }
                }
            }
        }
        return result != null ? result : Collections.<EIGlobalVar>emptyList();
    }

    public static List<EIGlobalVar> findGlobalVars(ScriptFile scriptFile) {
        List<EIGlobalVar> result = null;
        if (scriptFile != null) {
            Collection<EIGlobalVar> globals = PsiTreeUtil.collectElementsOfType(scriptFile, EIGlobalVar.class);
            if (globals != null) {
                result = new ArrayList<>(globals);
            }
        }
        return result != null ? result : Collections.<EIGlobalVar>emptyList();
    }

    public static EIScriptDeclaration findScriptDeclaration(ScriptFile scriptFile, String name) {
        List<EIScriptDeclaration> result = null;
        if (scriptFile != null) {
            Collection<EIScriptDeclaration> declarations = PsiTreeUtil.collectElementsOfType(scriptFile, EIScriptDeclaration.class);
            for (EIScriptDeclaration declaration : declarations) {
                if (name.equals(declaration.getName())) {
                    if (result == null) {
                        result = new ArrayList<>();
                    }
                    result.add(declaration);
                }
            }
        }

        return result != null && result.size() == 1 ? result.get(0) : null;
    }

    public static List<EIVariableAccess> findVariableAccesses(ScriptFile scriptFile) {
        List<EIVariableAccess> result = null;
        if (scriptFile != null) {
            //EIVariableAccess[] accesses = PsiTreeUtil.getChildrenOfType(scriptFile, EIVariableAccess.class);
            Collection<EIVariableAccess> accesses = PsiTreeUtil.collectElementsOfType(scriptFile, EIVariableAccess.class);
            if (accesses != null) {
                //Collections.addAll(result, accesses);
                result = new ArrayList<>(accesses);
            }
        }
        return result != null ? result : Collections.<EIVariableAccess>emptyList();
    }

    public static List<EIGlobalVar> findGlobalVars(Project project, String name) {
        List<EIGlobalVar> result = null;
        Collection<VirtualFile> virtualFiles = FileBasedIndex.getInstance().getContainingFiles(
                FileTypeIndex.NAME,
                ScriptFileType.INSTANCE,
                GlobalSearchScope.allScope(project)
        );
        for (VirtualFile virtualFile : virtualFiles) {
            ScriptFile scriptFile = (ScriptFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (result == null) {
                result = new ArrayList<>();
            }
            result.addAll(findGlobalVars(scriptFile, name));
        }
        return result != null ? result : Collections.<EIGlobalVar>emptyList();
    }

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
}