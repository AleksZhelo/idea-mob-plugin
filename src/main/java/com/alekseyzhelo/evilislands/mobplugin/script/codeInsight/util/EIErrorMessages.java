package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.util;

import com.intellij.AbstractBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

public class EIErrorMessages extends AbstractBundle {

  public static String message(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, @NotNull Object... params) {
    return INSTANCE.getMessage(key, params);
  }

  public static final EIErrorMessages INSTANCE = new EIErrorMessages();
  @NonNls
  public static final String BUNDLE = "messages.EIErrorMessages";

  private EIErrorMessages() {
    super(BUNDLE);
  }
}
