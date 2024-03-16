package io.reisub.devious.fishingtrawler;

import net.runelite.client.config.Button;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("sluwefishingtrawler")
public interface Config extends net.runelite.client.config.Config {
  @ConfigItem(
      keyName = "stopAtFifty",
      name = "Stop at 50 points",
      description = "Don't do anything once you have 50 points.",
      position = 0
  )
  default boolean stopAtFifty() {
    return false;
  }

  @ConfigItem(
      keyName = "startButton",
      name = "Start/Stop",
      description = "Start the script",
      position = Integer.MAX_VALUE
  )
  default Button startButton() {
    return new Button();
  }
}
