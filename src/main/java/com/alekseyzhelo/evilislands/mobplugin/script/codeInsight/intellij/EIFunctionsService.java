package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.intellij;

import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.lookup.EICallableLookupElement;
import com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.lookup.EILookupElementFactory;
import com.alekseyzhelo.evilislands.mobplugin.script.fileType.ScriptFileType;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFunctionDeclaration;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIType;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptPsiFile;
import com.alekseyzhelo.evilislands.mobplugin.script.util.EITypeToken;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.alekseyzhelo.evilislands.mobplugin.IOUtil.readUTF8String;

public class EIFunctionsService {

    private final EIFunctionDeclaration[] allFunctions;
    private final Map<String, EIFunctionDeclaration> functionNameToPsi;
    private final Map<String, String> functionNameToDoc;
    private final Map<EITypeToken, List<EIFunctionDeclaration>> typeToFunctions;

    private final List<LookupElement> functionLookupElements;
    private final Map<EITypeToken, List<LookupElement>> typeToLookupElements;

    private List<EICallableLookupElement> initFunctionLookup() {
        List<EICallableLookupElement> lookupElements = new ArrayList<>(allFunctions.length);
        for (EIFunctionDeclaration function : allFunctions) {
            lookupElements.add(EILookupElementFactory.create(function));
        }
        return lookupElements;
    }

    public EIFunctionsService(Project project) throws IOException {
        String declarations = readUTF8String(EIFunctionsService.class.getResourceAsStream("/function_declarations.eiscript"));
        String[] documentation = readUTF8String(EIFunctionsService.class.getResourceAsStream("/documentation.txt")).split("\\r?\\n");
        final ScriptPsiFile file = (ScriptPsiFile) PsiFileFactory.getInstance(project).
                createFileFromText("function_declarations.eiscript", ScriptFileType.INSTANCE, declarations);

        Map<String, EIFunctionDeclaration> functionNameToPsiInit = new HashMap<>();
        Map<String, String> functionNameToDocInit = new HashMap<>();
        Map<EITypeToken, List<EIFunctionDeclaration>> typeToFunctionsInit = new EnumMap<>(EITypeToken.class);
        List<EIFunctionDeclaration> voidFunctions = new ArrayList<>();
        List<EIFunctionDeclaration> floatFunctions = new ArrayList<>();
        List<EIFunctionDeclaration> stringFunctions = new ArrayList<>();
        List<EIFunctionDeclaration> objectFunctions = new ArrayList<>();
        List<EIFunctionDeclaration> groupFunctions = new ArrayList<>();

        allFunctions = PsiTreeUtil.getChildrenOfType(file, EIFunctionDeclaration.class);
        assert allFunctions != null;

        int functionNumber = 0;
        for (EIFunctionDeclaration declaration : allFunctions) {
            functionNameToPsiInit.put(declaration.getName().toLowerCase(Locale.ENGLISH), declaration);
            functionNameToDocInit.put(declaration.getName().toLowerCase(Locale.ENGLISH), documentation[functionNumber++]);

            EIType type = declaration.getType();
            if (type != null) {
                switch (type.getTypeToken()) {
                    case VOID:
                        voidFunctions.add(declaration);
                        break;
                    case FLOAT:
                        floatFunctions.add(declaration);
                        break;
                    case STRING:
                        stringFunctions.add(declaration);
                        break;
                    case OBJECT:
                        objectFunctions.add(declaration);
                        break;
                    case GROUP:
                        groupFunctions.add(declaration);
                        break;
                }
            } else {
                voidFunctions.add(declaration);
            }
        }
        typeToFunctionsInit.put(EITypeToken.VOID, voidFunctions);
        typeToFunctionsInit.put(EITypeToken.FLOAT, floatFunctions);
        typeToFunctionsInit.put(EITypeToken.STRING, stringFunctions);
        typeToFunctionsInit.put(EITypeToken.OBJECT, objectFunctions);
        typeToFunctionsInit.put(EITypeToken.GROUP, groupFunctions);
        functionNameToPsi = Collections.unmodifiableMap(functionNameToPsiInit);
        functionNameToDoc = Collections.unmodifiableMap(functionNameToDocInit);
        typeToFunctions = Collections.unmodifiableMap(typeToFunctionsInit);

        List<EICallableLookupElement> functionLookupElementsInit = initFunctionLookup();
        Map<EITypeToken, List<LookupElement>> typeToLookupElementsInit = new EnumMap<>(EITypeToken.class);
        for (EITypeToken type : EITypeToken.values()) {
            typeToLookupElementsInit.put(type,
                    Collections.unmodifiableList(
                            functionLookupElementsInit.stream()
                                    .filter((x) -> x.getType() == type)
                                    .collect(Collectors.toList()))
            );
        }
        functionLookupElements = Collections.unmodifiableList(functionLookupElementsInit);
        typeToLookupElements = Collections.unmodifiableMap(typeToLookupElementsInit);
    }

    public static EIFunctionsService getInstance(Project project) {
        return ServiceManager.getService(project, EIFunctionsService.class);
    }

    @Nullable
    public EIFunctionDeclaration getFunctionDeclaration(String functionName) {
        return functionName != null ? functionNameToPsi.get(functionName.toLowerCase(Locale.ENGLISH)) : null;
    }

    @NotNull
    public EIFunctionDeclaration[] getAllFunctions() {
        return allFunctions;
    }

    @NotNull
    public List<EIFunctionDeclaration> getAllFunctions(EITypeToken type) {
        return typeToFunctions.get(type);
    }

    @Nullable
    public String getFunctionDoc(String functionName) {
        return functionName != null ? functionNameToDoc.get(functionName.toLowerCase(Locale.ENGLISH)) : null;
    }

    @NotNull
    public List<LookupElement> getFunctionLookupElements() {
        return functionLookupElements;
    }

    @Contract("null -> null")
    public List<LookupElement> getFunctionLookupElements(@Nullable EITypeToken type) {
        return typeToLookupElements.get(type);
    }
}
