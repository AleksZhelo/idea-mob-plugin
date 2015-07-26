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
//%debug
//%line //why doesn't this work?
//%column //why doesn't this work?
%function advance
%type IElementType
%eof{  return;
%eof}

%{
%}

LINE_TERMINATOR = \r|\n|\r\n
INPUT_CHARACTER = [^\r\n]
STRING_CHARACTER = [^\r\n\"]
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
// apparently it is very important to not ignore any characters from the input
<YYINITIAL> {
    {FLOAT}                                      { return ScriptTypes.FLOAT; }
    {STRING}                                     { return ScriptTypes.STRING; }
    {OBJECT}                                     { return ScriptTypes.OBJECT; }
    {GROUP}                                      { return ScriptTypes.GROUP; }
    {GLOBALVARS}                                 { return ScriptTypes.GLOBALVARS; }
    {DECLARESCRIPT}                              { return ScriptTypes.DECLARESCRIPT; }
    {WORLDSCRIPT}                                { return ScriptTypes.WORLDSCRIPT; }
    {SCRIPT}                                     { return ScriptTypes.SCRIPT; }
    {IF}                                         { return ScriptTypes.IF; }
    {THEN}                                       { return ScriptTypes.THEN; }
    {FOR}                                        { return ScriptTypes.FOR; }
    {IDENTIFIER}                                 { return ScriptTypes.IDENTIFIER; }
    {FLOATNUMBER}                                { return ScriptTypes.FLOATNUMBER; }
    "="                                          { return ScriptTypes.EQUALS; }
    "("                                          { return ScriptTypes.LPAREN; }
    ")"                                          { return ScriptTypes.RPAREN; }
    ","                                          { return ScriptTypes.COMMA; }
    ":"                                          { return ScriptTypes.COLON; }
    {END_OF_LINE_COMMENT}                        { return ScriptTypes.COMMENT;  }
    {WHITE_SPACE}                                { return TokenType.WHITE_SPACE;  }
    {LINE_TERMINATOR}                            { return TokenType.WHITE_SPACE;  }

    \"{STRING_CHARACTER}+\"                      { return ScriptTypes.CHARACTER_STRING; }
}


.                                    { return TokenType.BAD_CHARACTER; } // why in God's name do I need this? Why the fucking fuck is there a fucking bad character on file start? // apparently it might have been the byte order mark