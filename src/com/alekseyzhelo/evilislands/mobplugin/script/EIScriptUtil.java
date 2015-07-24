package com.alekseyzhelo.evilislands.mobplugin.script;

import com.alekseyzhelo.evilislands.mobplugin.script.file.ScriptFileType;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIGlobalVar;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptFile;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.impl.EIScriptPsiImplUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
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
    public static List<EIGlobalVar> findGlobalVars(Project project, String name) {
        List<EIGlobalVar> result = null;
        Collection<VirtualFile> virtualFiles = FileBasedIndex.getInstance().getContainingFiles(
                FileTypeIndex.NAME,
                ScriptFileType.INSTANCE,
                GlobalSearchScope.allScope(project)
        );
        for (VirtualFile virtualFile : virtualFiles) {
            ScriptFile scriptFile = (ScriptFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (scriptFile != null) {
                EIGlobalVar[] globals = PsiTreeUtil.getChildrenOfType(scriptFile, EIGlobalVar.class);
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
            if (scriptFile != null) {
                EIGlobalVar[] globals = PsiTreeUtil.getChildrenOfType(scriptFile, EIGlobalVar.class);
                if (globals != null) {
                    Collections.addAll(result, globals);
                }
            }
        }
        return result;
    }
}