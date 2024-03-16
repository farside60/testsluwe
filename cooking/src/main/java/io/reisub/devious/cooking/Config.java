package io.reisub.devious.cooking;

import net.runelite.client.config.Button;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("sluwecooking")
public interface Config extends net.runelite.client.config.Config {
  @ConfigItem(
      keyName = "food",
      name = "Food",
      description = "Raw food to cook",
      position = 0)
  default String food() {
    return "Raw shark";
  }

  @ConfigItem(
      keyName = "sonicMode",
      name = "Sonic mode",
      description = "Gotta go fast",
      position = 1)
  default boolean sonicMode() {
    return false;
  }

  @ConfigItem(
      keyName = "startButton",
      name = "Start/Stop",
      description = "Start the script",
      position = Integer.MAX_VALUE)
  default Button startButton() {
    return new Button();
  }
}
