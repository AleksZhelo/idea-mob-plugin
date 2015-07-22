package com.alekseyzhelo.evilislands.mobplugin.script;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.alekseyzhelo.evilislands.mobplugin.script.psi.ScriptTypes;
import com.intellij.psi.TokenType;

%%

%class EIScriptLexer
%implements FlexLexer
%unicode
%caseless
//%line
//%column
%function advance
%type IElementType
%eof{
    // return;
%eof}

%{
    // currently unused
    StringBuilder string = new StringBuilder();
%}

LINE_TERMINATOR = \r|\n|\r\n
INPUT_CHARACTER = [^\r\n]
STRING_CHARACTER = [^\r\n\"\\]
WHITE_SPACE=[\ \t\f]
END_OF_LINE_COMMENT="//" {INPUT_CHARACTER}* {LINE_TERMINATOR}?
FLOAT="float"
STRING="string"
OBJECT="object"
GROUP="group"
GLOBALVARS="globalvars"
DECLARESCRIPT="declarescript"
SCRIPT="script"
IF="if"
THEN="then"
FOR="for"
WORLDSCRIPT="worldscript"
IDENTIFIER=[#a-zA-Z]([#_a-zA-Z0-9-])*
FLOATNUMBER=-?[0-9][0-9]*|[0-9]+"."[0-9]+

%state STRING

%%

<YYINITIAL> {
    {FLOAT}                                      { return ScriptTypes.FLOAT; }
    {STRING}                                     { return ScriptTypes.STRING; }
    {OBJECT}                                     { return ScriptTypes.OBJECT; }
    {GROUP}                                      { return ScriptTypes.GROUP; }
    {GLOBALVARS}                                 { return ScriptTypes.GLOBALVARS; }
    {DECLARESCRIPT}                              { return ScriptTypes.DECLARESCRIPT; }
    {SCRIPT}                                     { return ScriptTypes.SCRIPT; }
    {IF}                                         { return ScriptTypes.IF; }
    {THEN}                                       { return ScriptTypes.THEN; }
    {FOR}                                        { return ScriptTypes.FOR; }
    {WORLDSCRIPT}                                { return ScriptTypes.WORLDSCRIPT; }
    {IDENTIFIER}                                 { return ScriptTypes.IDENTIFIER; }
    {FLOATNUMBER}                                { return ScriptTypes.FLOATNUMBER; }
    "="                                          { return ScriptTypes.EQUALS; }
    "("                                          { return ScriptTypes.LPAREN; }
    ")"                                          { return ScriptTypes.RPAREN; }
    //"{"                                          { return ScriptTypes.LBRACE; }
    //"}"                                          { return ScriptTypes.RBRACE; }
    ","                                          { return ScriptTypes.COMA; }
    ":"                                          { return ScriptTypes.COLON; }
    {END_OF_LINE_COMMENT}                        { /* ignore */  }
    {WHITE_SPACE}                                { /* ignore */  }

    \"                                           { yybegin(STRING); string.setLength(0); }
}

<STRING> {
    \"                             { yybegin(YYINITIAL); return ScriptTypes.CHARACTER_STRING; }

    {STRING_CHARACTER}+            { string.append( yytext() ); }

    /* escape sequences */
    "\\b"                          { string.append( '\b' ); }
    "\\t"                          { string.append( '\t' ); }
    "\\n"                          { string.append( '\n' ); }
    "\\f"                          { string.append( '\f' ); }
    "\\r"                          { string.append( '\r' ); }
    "\\\""                         { string.append( '\"' ); }
    "\\'"                          { string.append( '\'' ); }
    "\\\\"                         { string.append( '\\' ); }
    /*  \\[0-3]?{OctDigit}?{OctDigit}  { char val = (char) Integer.parseInt(yytext().substring(1),8);
                                           string.append( val ); }*/ // what to do with this? needed?

    /* error cases */
    \\.                            { throw new RuntimeException("Illegal escape sequence \""+yytext()+"\""); }
    {LINE_TERMINATOR}              { throw new RuntimeException("Unterminated string at end of line"); }
}

.                                            { return TokenType.BAD_CHARACTER; }