package io.reisub.devious.tempoross;

import net.runelite.client.config.Button;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("sluwetempoross")
public interface Config extends net.runelite.client.config.Config {
  @ConfigItem(
      keyName = "enableRelog",
      name = "Enable relog",
      description =
          "Log out and back in after getting the 'you've been playing for a while' "
              + "message after finishing a game. Only supports Jagex accounts.",
      position = 0)
  default boolean enableRelog() {
    return false;
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
