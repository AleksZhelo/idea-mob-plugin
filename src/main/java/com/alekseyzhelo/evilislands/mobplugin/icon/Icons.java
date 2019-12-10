package com.alekseyzhelo.evilislands.mobplugin.icon;

import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
 
public class Icons {
    
    private static Icon load(@NonNls @NotNull final String path) {
        return IconLoader.findIcon(path, Icons.class);
    }
    
    public static final Icon MODULE = load("/icons/eiscriptModule.svg");
    public static final Icon MOB_FILE = load("/icons/mobFile.svg");
    public static final Icon SCRIPT_FILE = load("/icons/scriptFile.svg");
    public static final Icon FUNCTION = load("/icons/function.svg");
    public static final Icon SCRIPT_IMPL = load("/icons/scriptImpl.svg");
    public static final Icon GLOBAL_VAR = load("/icons/globalVar.svg");
    public static final Icon GS_VAR = load("/icons/gsVar.svg");
    public static final Icon AREA = load("/icons/area.svg");

    public static class Objects {
        public static final Icon FLAME = load("/icons/objects/mobFlame.svg");
        public static final Icon LEVER = load("/icons/objects/mobLever.svg");
        public static final Icon LIGHT = load("/icons/objects/mobLight.svg");
        public static final Icon OBJECT = load("/icons/objects/mobObject.svg");
        public static final Icon PARTICLE = load("/icons/objects/mobParticle.svg");
        public static final Icon SOUND = load("/icons/objects/mobSound.svg");
        public static final Icon TRAP = load("/icons/objects/mobTrap.svg");
        public static final Icon UNIT = load("/icons/objects/mobUnit.svg");
    }
}