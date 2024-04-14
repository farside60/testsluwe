package io.reisub.devious.motherlodemine;

import net.runelite.client.config.Button;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("sluwemotherlodemine")
public interface Config extends net.runelite.client.config.Config {
  @ConfigItem(
      keyName = "upstairs",
      name = "Upstairs",
      description = "Enable to mine upstairs",
      position = 0)
  default boolean upstairs() {
    return false;
  }

  @ConfigItem(
      keyName = "shortcut",
      name = "Use shortcut",
      description = "Enable to use agility shortcut",
      position = 1)
  default boolean shortcut() {
    return false;
  }

  @ConfigItem(
      keyName = "depositNuggets",
      name = "Deposit nuggets",
      description = "Enable to deposit nuggets",
      position = 2)
  default boolean depositNuggets() {
    return true;
  }

  @ConfigItem(
      keyName = "enableOverlay",
      name = "Enable overlay",
      description = "Toggle the plugin's overlay",
      position = Integer.MAX_VALUE - 1)
  default boolean enableOverlay() {
    return true;
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
