package io.reisub.devious.roguesden;

import net.runelite.client.config.Button;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("sluweroguesden")
public interface Config extends net.runelite.client.config.Config {
  @ConfigItem(
      keyName = "stopAtFiveCrates",
      name = "Stop at 5 crates",
      description = "Stop the script once we have 5 Rogue's equipment crates",
      position = 0)
  default boolean stopAtFiveCrates() {
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
