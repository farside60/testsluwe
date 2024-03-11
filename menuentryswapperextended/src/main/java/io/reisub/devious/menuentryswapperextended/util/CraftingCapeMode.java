package io.reisub.devious.menuentryswapperextended.util;

public enum CraftingCapeMode {
  OFF("None"),
  INVENTORY("Inventory"),
  EQUIPPED("Worn"),
  ALWAYS("Both");

  private final String name;

  CraftingCapeMode(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
