package com.alekseyzhelo.evilislands.mobplugin.script.util;

import com.alekseyzhelo.evilislands.mobplugin.script.file.ScriptFileType;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFunctionDeclaration;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptFile;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.util.PsiTreeUtil;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Aleks on 26-07-2015.
 */
public class EIScriptNativeFunctionsUtil {

    private static Map<String, EIFunctionDeclaration> functionNameToPsi;
    private static Map<String, String> functionNameToDoc;

    public static EIFunctionDeclaration getFunctionDeclaration(Project project, String functionName) {
        if (functionNameToPsi == null || functionNameToDoc == null) {
            initData(project);
        }
        return functionName != null ? functionNameToPsi.get(functionName.toLowerCase(Locale.ENGLISH)) : null;
    }

    public static String getFunctionDoc(Project project, String functionName) {
        if (functionNameToPsi == null || functionNameToDoc == null) {
            initData(project);
        }
        return functionName != null ? functionNameToDoc.get(functionName.toLowerCase(Locale.ENGLISH)) : null;
    }

    // TODO unfuck this method
    private static void initData(Project project) {
        String declarations = null;
        List<String> documentation = null;
        try {
            declarations = new String(
                    Files.readAllBytes(Paths.get(EIScriptNativeFunctionsUtil.class.getResource("/com/alekseyzhelo/evilislands/mobplugin/res/declarations.eiscript").toURI())),
                    Charset.forName("UTF-8")
            );
            documentation = Files.readAllLines(Paths.get(EIScriptNativeFunctionsUtil.class.getResource("/com/alekseyzhelo/evilislands/mobplugin/res/documentation.txt").toURI()));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        if(declarations != null && documentation != null) {
            final ScriptFile file = (ScriptFile) PsiFileFactory.getInstance(project).
                    createFileFromText("declarations.eiscript", ScriptFileType.INSTANCE, declarations);
            functionNameToPsi = new HashMap<>();
            functionNameToDoc = new HashMap<>();
            EIFunctionDeclaration[] functionDeclarations = PsiTreeUtil.getChildrenOfType(file, EIFunctionDeclaration.class);
            int functionNumber = 0;
            if(functionDeclarations != null) {
                for (EIFunctionDeclaration declaration : functionDeclarations) {
                    functionNameToPsi.put(declaration.getName().toLowerCase(Locale.ENGLISH), declaration);
                    functionNameToDoc.put(declaration.getName().toLowerCase(Locale.ENGLISH), documentation.get(functionNumber++));
                }
            }
            System.out.println();
        }
    }

}
