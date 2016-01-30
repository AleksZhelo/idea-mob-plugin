package com.alekseyzhelo.evilislands.mobplugin.script.highlighting;

import com.alekseyzhelo.evilislands.mobplugin.icon.Icons;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
 
import javax.swing.*;
import java.util.Map;
 
public class EIScriptColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Equals sign", EIScriptSyntaxHighlighter.EQUALS),
            new AttributesDescriptor("Comma", EIScriptSyntaxHighlighter.COMMA),
            new AttributesDescriptor("Parentheses", EIScriptSyntaxHighlighter.PARENTHESES),
            new AttributesDescriptor("Keyword", EIScriptSyntaxHighlighter.KEYWORD),
            new AttributesDescriptor("String", EIScriptSyntaxHighlighter.STRING),
            new AttributesDescriptor("Number", EIScriptSyntaxHighlighter.NUMBER),
            new AttributesDescriptor("Identifier", EIScriptSyntaxHighlighter.IDENTIFIER),
            new AttributesDescriptor("Function call", DefaultLanguageHighlighterColors.FUNCTION_CALL),
            new AttributesDescriptor("Variable", EIScriptSyntaxHighlighter.VARIABLE),
            new AttributesDescriptor("Comment", EIScriptSyntaxHighlighter.COMMENT),
    };
 
    @Nullable
    @Override
    public Icon getIcon() {
        return Icons.FILE;
    }
 
    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new EIScriptSyntaxHighlighter();
    }
 
    @NotNull
    @Override
    public String getDemoText() {
        return "// Диалоговая зона.\n" +
                "// Версия дефолтного скрипта: 4.\n" +
                "// ing_gorod - Город Ингос\n" +
                "\n" +
                "////////////////////////////////////////////\n" +
                "// Переменные\n" +
                "////////////////////////////////////////////\n" +
                "\n" +
                "GlobalVars (\n" +
                "  NULL : object,\n" +
                "  VSS#i#val : object,\n" +
                "  i : object,\n" +
                "  Hero : object,\n" +
                "  Yolochka1 : object,\n" +
                "  Yolochka2 : object,\n" +
                "  SomeValue : float\n" +
                ")\n" +
                "\n" +
                "////////////////////////////////////////////\n" +
                "// Объявления скриптов\n" +
                "////////////////////////////////////////////\n" +
                "\n" +
                "DeclareScript #OnBriefingComplete (  nPlayer : float,  szComplete : string )\n" +
                "DeclareScript DebugScript (  this : object )\n" +
                "DeclareScript DoNameObjects (  this : object )\n" +
                "DeclareScript SetDefZoneVars (  this : object )\n" +
                "DeclareScript EnableZone (  this : object )\n" +
                "\n" +
                "\n" +
                "////////////////////////////////////////////\n" +
                "// Скрипт по окончанию брифингов...\n" +
                "////////////////////////////////////////////\n" +
                "\n" +
                "Script #OnBriefingComplete\n" +
                "(\n" +
                "  if\n" +
                "  (\n" +
                "\n" +
                "  )\n" +
                "  then\n" +
                "  (\n" +
                "    KillScript(  )\n" +
                "\n" +
                "  )\n" +
                ")\n" +
                "\n" +
                "////////////////////////////////////////////\n" +
                "// Скрипты глобальные для зоны\n" +
                "////////////////////////////////////////////\n" +
                "\n" +
                "Script DebugScript\n" +
                "//Отладочный скрипт - для проверки того, что скрипт зоны работает.\n" +
                "(\n" +
                "  if\n" +
                "  (\n" +
                "\n" +
                "  )\n" +
                "  then\n" +
                "  (\n" +
                "    KillScript(  )\n" +
                "    ConsoleString(\"DEBUG SCRIPT LAUNCHED!!!\")\n" +
                "  )\n" +
                ")\n" +
                "\n" +
                "Script DoNameObjects\n" +
                "//Этот скрипт привязывает идешники объектов к переменным - объектам.\n" +
                "(\n" +
                "  if\n" +
                "  (\n" +
                "\n" +
                "  )\n" +
                "  then\n" +
                "  (\n" +
                "    KillScript(  )\n" +
                "\t\n" +
                "\tHero = GetLeader()      //Злобный хуманоид по кличке Хома.\n" +
                "    Yolochka1 = GetObjectByID(\"2300\")   //Мигающая йолка.\n" +
                "    Yolochka2 = GetObjectByID(\"2301\")   //Мигающая йолка нумер два.\n" +
                "  )\n" +
                ")\n" +
                "\n" +
                "Script SetDefZoneVars\n" +
                "//Этот скрипт выставляет умолчальные значение переменных при входе именно в эту зону\n" +
                "(\n" +
                "  if\n" +
                "  (\n" +
                "\n" +
                "  )\n" +
                "  then\n" +
                "  (\n" +
                "    KillScript(  )  //Убить скрипт чтоб больше не выполнялся\n" +
                "\n" +
                "    //Включить те брифинги, которые должны быть постоянно...\n" +
                "\n" +
                "  )\n" +
                ")\n" +
                "\n" +
                "Script EnableZone\n" +
                "//Открывает зону если она еще закрыта\n" +
                "(\n" +
                "  if\n" +
                "  (\n" +
                "    IsEqual(GSGetVar(0,\"z.ing_gorod\"),0)\n" +
                "  )\n" +
                "  then\n" +
                "  (\n" +
                "    KillScript( )\n" +
                "    //DebugScript(NULL)\n" +
                "    gssetvar(0,\"z.ing_gorod\",2)\n" +
                "  )\n" +
                ")\n" +
                "\n" +
                "////////////////////////////////////////////\n" +
                "// Другие скрипты\n" +
                "////////////////////////////////////////////\n" +
                "\n" +
                "////////////////////////////////////////////\n" +
                "// WorldScript\n" +
                "////////////////////////////////////////////\n" +
                "\n" +
                "WorldScript\n" +
                "(\n" +
                "  Sleep( 2 )\n" +
                "  SomeValue = 4\n" +
                "  //Глобальные скрипты зоны...\n" +
                "  DebugScript(NULL)  //Если надо проверить, работает ли скрипт - раскомментируйте.\n" +
                "  DoNameObjects(NULL)  //Привязать идешники объектов к именным объектам\n" +
                "  SetDefZoneVars(NULL) //Выставить умолчальные для зоны переменые.\n" +
                "  EnableZone(NULL) //Открыть зону если она еще закрыта.\n" +
                "\n" +
                "  Sleep( 10 ) //Чтобы другие скрипты не начали выполняться\n" +
                ")\n";
    }
 
    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }
 
    @NotNull
    @Override
    public AttributesDescriptor[] getAttributeDescriptors() {
        return DESCRIPTORS;
    }
 
    @NotNull
    @Override
    public ColorDescriptor[] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }
 
    @NotNull
    @Override
    public String getDisplayName() {
        return "EIScript";
    }
}