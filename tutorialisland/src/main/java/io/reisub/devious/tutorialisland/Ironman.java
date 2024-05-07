package io.reisub.devious.tutorialisland;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Ironman {
  NONE("None", -1),
  STANDARD("Ironman", 9),
  HARDCORE("Hardcore ironman", 10),
  ULTIMATE("Ultimate ironman", 11),
  GROUP("Group ironman", 13),
  GROUP_HARDCORE("Hardcore group ironman", 32);

  private final String name;
  @Getter private final int widgetId;

  @Override
  public String toString() {
    return name;
  }
}
