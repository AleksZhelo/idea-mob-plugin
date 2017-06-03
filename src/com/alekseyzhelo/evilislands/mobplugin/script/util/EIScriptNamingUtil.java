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
