package com.alekseyzhelo.evilislands.mobplugin.script.util;


import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIType;
import org.jetbrains.annotations.Nullable;

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
    public static final String IF = "if";
    public static final String THEN = "then";
    public static final String FOR = "For";
    public static final String WORLDSCRIPT = "WorldScript";

    public static final String EQUALS = "=";
    public static final String LPAREN = "(";
    public static final String RPAREN = ")";
    public static final String COMMA = ",";
    public static final String COLON = ":";

//    public static final String NAME_IDENTIFIER = "Identifier:";
    public static final String NAME_TYPE = "Type:";
    public static final String NAME_SCRIPT_IMPL = "ScriptImpl:";
    public static final String NAME_SCRIPT_DECL = "ScriptDecl:";
    public static final String NAME_GLOBAL_VAR = "GlobalVar:";
    public static final String NAME_LITERAL = "Literal:";
    public static final String NAME_FUNCTION_CALL= "Call:";
    public static final String NAME_VAR_ACCESS = "VariableAccess:";
    public static final String NAME_ASSIGNMENT = "Assignment:";
    public static final String NAME_CALL_STATEMENT = "CallStatement:";
    public static final String NAME_FOR_BLOCK = "ForBlock";
    public static final String NAME_SCRIPT_BLOCK = "ScriptBlock";
    public static final String NAME_SCRIPT_IF_BLOCK = "IfBlock";
    public static final String NAME_SCRIPT_THEN_BLOCK = "ThenBlock";

    public static final String NAME_GLOBALVARS = "GlobalVars";
    public static final String NAME_DECLARATIONS = "Declarations";
    public static final String NAME_SCRIPTS = "Scripts";
    public static final String NAME_WORLDSCRIPT = "WorldScript";

    private EIScriptNamingUtil() {

    }

    public static String getName(@Nullable EITypeToken type) {
        return type == null ? "" : type.getTypeString();
    }

    public static String getName(@Nullable EIType type) {
        return type == null ? "" : getName(type.getTypeToken());
    }
}
