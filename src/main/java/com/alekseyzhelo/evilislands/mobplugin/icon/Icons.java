package com.alekseyzhelo.evilislands.mobplugin.icon;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;
 
public class Icons {
    public static final Icon MOB_FILE = IconLoader.getIcon("/icons/mobFile.svg");
    public static final Icon SCRIPT_FILE = IconLoader.getIcon("/icons/scriptFile.svg");
    public static final Icon FUNCTION = IconLoader.getIcon("/icons/function.svg");
    public static final Icon SCRIPT_IMPL = IconLoader.getIcon("/icons/scriptImpl.svg");
    public static final Icon GLOBAL_VAR = IconLoader.getIcon("/icons/globalVar.svg");
    public static final Icon GS_VAR = IconLoader.getIcon("/icons/gsVar.svg");
    public static final Icon AREA = IconLoader.getIcon("/icons/area.svg");

    public static class Objects {
        public static final Icon FLAME = IconLoader.getIcon("/icons/objects/mobFlame.svg");
        public static final Icon LEVER = IconLoader.getIcon("/icons/objects/mobLever.svg");
        public static final Icon LIGHT = IconLoader.getIcon("/icons/objects/mobLight.svg");
        public static final Icon OBJECT = IconLoader.getIcon("/icons/objects/mobObject.svg");
        public static final Icon PARTICLE = IconLoader.getIcon("/icons/objects/mobParticle.svg");
        public static final Icon SOUND = IconLoader.getIcon("/icons/objects/mobSound.svg");
        public static final Icon TRAP = IconLoader.getIcon("/icons/objects/mobTrap.svg");
        public static final Icon UNIT = IconLoader.getIcon("/icons/objects/mobUnit.svg");
    }
}