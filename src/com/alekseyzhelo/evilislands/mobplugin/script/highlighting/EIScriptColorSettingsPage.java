package com.alekseyzhelo.evilislands.mobplugin.script.highlighting;

import com.alekseyzhelo.evilislands.mobplugin.icon.Icons;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

import static com.alekseyzhelo.evilislands.mobplugin.script.highlighting.EIScriptSyntaxHighlightingColors.*;

public class EIScriptColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Equals sign", EQUALS),
            new AttributesDescriptor("Comma", COMMA),
            new AttributesDescriptor("Parentheses", PARENTHESES),
            new AttributesDescriptor("Keyword", KEYWORD),
            new AttributesDescriptor("Type", TYPE),
            new AttributesDescriptor("String", STRING),
            new AttributesDescriptor("Number", NUMBER),
            new AttributesDescriptor("Identifier", IDENTIFIER),
            new AttributesDescriptor("Function call", FUNCTION_CALL),
            new AttributesDescriptor("Variable access", VARIABLE_ACCESS),
            new AttributesDescriptor("Comment", COMMENT),
    };

    private static final Map<String, TextAttributesKey> ATTRIBUTES_KEY_MAP = ContainerUtil.newHashMap();
    static {
        ATTRIBUTES_KEY_MAP.put("fc", FUNCTION_CALL);
        ATTRIBUTES_KEY_MAP.put("va", VARIABLE_ACCESS);
    }

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
                "    <fc>KillScript</fc>(  )\n" +
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
                "    <fc>KillScript</fc>(  )\n" +
                "    <fc>ConsoleString</fc>(\"DEBUG SCRIPT LAUNCHED!!!\")\n" +
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
                "    <fc>KillScript</fc>(  )\n" +
                "\t\n" +
                "    <va>Hero</va> = <fc>GetLeader</fc>()      //Злобный хуманоид по кличке Хома.\n" +
                "    <va>Yolochka1</va> = <fc>GetObjectByID</fc>(\"2300\")   //Мигающая йолка.\n" +
                "    <va>Yolochka2</va> = <fc>GetObjectByID</fc>(\"2301\")   //Мигающая йолка нумер два.\n" +
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
                "    <fc>KillScript</fc>(  )  //Убить скрипт чтоб больше не выполнялся\n" +
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
                "    <fc>IsEqual</fc>(<fc>GSGetVar</fc>(0,\"z.ing_gorod\"),0)\n" +
                "  )\n" +
                "  then\n" +
                "  (\n" +
                "    <fc>KillScript</fc>( )\n" +
                "    //DebugScript(NULL)\n" +
                "    <fc>gssetvar</fc>(0,\"z.ing_gorod\",2)\n" +
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
                "  <fc>Sleep</fc>( 2 )\n" +
                "  <va>SomeValue</va> = 4\n" +
                "  //Глобальные скрипты зоны...\n" +
                "  <fc>DebugScript</fc>(<va>NULL</va>)  //Если надо проверить, работает ли скрипт - раскомментируйте.\n" +
                "  <fc>DoNameObjects</fc>(<va>NULL</va>)  //Привязать идешники объектов к именным объектам\n" +
                "  <fc>SetDefZoneVars</fc>(<va>NULL</va>) //Выставить умолчальные для зоны переменые.\n" +
                "  <fc>EnableZone</fc>(<va>NULL</va>) //Открыть зону если она еще закрыта.\n" +
                "\n" +
                "  <fc>Sleep</fc>( 10 ) //Чтобы другие скрипты не начали выполняться\n" +
                ")\n";
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return ATTRIBUTES_KEY_MAP;
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