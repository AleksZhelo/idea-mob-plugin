package com.alekseyzhelo.evilislands.mobplugin.script.util;

import com.alekseyzhelo.evilislands.mobplugin.script.fileType.ScriptFileType;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFunctionDeclaration;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptPsiFile;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.util.PsiTreeUtil;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.alekseyzhelo.evilislands.mobplugin.IOUtil.readUTF8String;

/**
 * Created by Aleks on 26-07-2015.
 */
public class EIScriptNativeFunctionsUtil {

    private static List<String> functionNames;
    private static Map<String, EIFunctionDeclaration> functionNameToPsi;
    private static Map<String, String> functionNameToDoc;

    public static EIFunctionDeclaration getFunctionDeclaration(Project project, String functionName) {
        if (functionNameToPsi == null || functionNameToDoc == null) {
            initData(project);
        }
        return functionName != null ? functionNameToPsi.get(functionName.toLowerCase(Locale.ENGLISH)) : null;
    }

    public static List<String> getAllFunctionNames(Project project) {
        if (functionNames == null) {
            initData(project);
        }

        return Collections.unmodifiableList(functionNames);
    }

    public static List<EIFunctionDeclaration> getAllFunctions(Project project) {
        if (functionNameToPsi == null) {
            initData(project);
        }

        return new ArrayList<>(functionNameToPsi.values());
    }

    public static List<EIFunctionDeclaration> getAllFunctions(Project project, final EITypeToken type) {
        if (functionNameToPsi == null) {
            initData(project);
        }

        return new ArrayList<>(functionNameToPsi.values()).stream()
                .filter(x -> {
                    if (type != EITypeToken.VOID) {
                        return x.getType() != null && x.getType().getTypeToken() == type;
                    } else {
                        return x.getType() == null || x.getType().getTypeToken() == EITypeToken.VOID;
                    }
                })
                .collect(Collectors.toList());
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
        String[] documentation = null;
        try {
            declarations = readUTF8String(EIScriptNativeFunctionsUtil.class.getResourceAsStream("/declarations.eiscript"));
            documentation = readUTF8String(EIScriptNativeFunctionsUtil.class.getResourceAsStream("/documentation.txt")).split("\\r?\\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (declarations != null && documentation != null) {
            final ScriptPsiFile file = (ScriptPsiFile) PsiFileFactory.getInstance(project).
                    createFileFromText("declarations.eiscript", ScriptFileType.INSTANCE, declarations);
            functionNameToPsi = new HashMap<>();
            functionNameToDoc = new HashMap<>();
            functionNames = new ArrayList<>();
            EIFunctionDeclaration[] functionDeclarations = PsiTreeUtil.getChildrenOfType(file, EIFunctionDeclaration.class);
            int functionNumber = 0;
            if (functionDeclarations != null) {
                for (EIFunctionDeclaration declaration : functionDeclarations) {
                    functionNames.add(declaration.getName());
                    functionNameToPsi.put(declaration.getName().toLowerCase(Locale.ENGLISH), declaration);
                    functionNameToDoc.put(declaration.getName().toLowerCase(Locale.ENGLISH), documentation[functionNumber++]);
                }
            }
        }
    }

}
