package com.alekseyzhelo.evilislands.mobplugin.script.util;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class EIScriptNamingUtil {

    public static final String VOID = "void";
    public static final String UNKNOWN = "unknown";

    public static final String FLOAT = "float";
    public static final String STRING = "string";
    public static final String OBJECT = "object";
    public static final String GROUP = "group";
    public static final String ANY = "any";
    public static final String GLOBALVARS = "GlobalVars";
    public static final String DECLARESCRIPT = "DeclareScript";
    public static final String SCRIPT = "Script";
    public static final String IF = "IF";
    public static final String THEN = "THEN";
    public static final String FOR = "FOR";
    public static final String WORLDSCRIPT = "WorldScript";

    public static final String EQUALS = "=";
    public static final String LPAREN = "(";
    public static final String RPAREN = ")";
    public static final String COMMA = ",";
    public static final String COLON = ":";


    public static final Map<String, String> tokenMap;

//    public static final String NAME_IDENTIFIER = "Identifier:";
//    public static final String NAME_TYPE = "Type:";
//    public static final String NAME_SCRIPT_IMPL = "ScriptImpl:";
//    public static final String NAME_SCRIPT_DECL = "ScriptDecl:";
//    public static final String NAME_GLOBAL_VAR = "GlobalVar:";
    public static final String NAME_EXPRESSION = "Expression";
//    public static final String NAME_FUNCTION_CALL= "Call:";
//    public static final String NAME_VAR_ACCESS = "VariableAccess:";
//    public static final String NAME_ASSIGNMENT = "Assignment:";
    public static final String NAME_SCRIPT_EXPRESSION = "ScriptExpression";
    public static final String NAME_SCRIPT_BLOCK = "ScriptBlock";
    public static final String NAME_SCRIPT_IF_BLOCK = "IfBlock";
    public static final String NAME_SCRIPT_THEN_BLOCK = "ThenBlock";

    public static final String NAME_GLOBALVARS = "GlobalVars";
    public static final String NAME_DECLARATIONS = "Declarations";
    public static final String NAME_SCRIPTS = "Scripts";
    public static final String NAME_WORLDSCRIPT = "WorldScript";

    static {
        Map<String, String> tmpMap = new HashMap<>();
        tmpMap.put("FLOAT", FLOAT);
        tmpMap.put("STRING", STRING);
        tmpMap.put("OBJECT", OBJECT);
        tmpMap.put("GROUP", GROUP);
        tmpMap.put("GLOBALVARS", GLOBALVARS);
        tmpMap.put("DECLARESCRIPT", DECLARESCRIPT);
        tmpMap.put("SCRIPT", SCRIPT);
        tmpMap.put("IF", IF);
        tmpMap.put("THEN", THEN);
        tmpMap.put("FOR", FOR);
        tmpMap.put("WORLDSCRIPT", WORLDSCRIPT);

        tmpMap.put("EQUALS", EQUALS);
        tmpMap.put("LPAREN", LPAREN);
        tmpMap.put("RPAREN", RPAREN);
        tmpMap.put("COMMA", COMMA);
        tmpMap.put("COLON", COLON);
        tokenMap = Collections.unmodifiableMap(tmpMap);
    }

    private EIScriptNamingUtil() {

    }

}
