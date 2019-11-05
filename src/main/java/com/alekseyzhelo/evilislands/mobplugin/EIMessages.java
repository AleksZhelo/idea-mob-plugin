package com.alekseyzhelo.evilislands.mobplugin;

import com.intellij.AbstractBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

public class EIMessages extends AbstractBundle {

  public static String message(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, @NotNull Object... params) {
    return INSTANCE.getMessage(key, params);
  }

  public static final EIMessages INSTANCE = new EIMessages();
  @NonNls
  public static final String BUNDLE = "messages.EIMessages";

  private EIMessages() {
    super(BUNDLE);
  }
}
