package io.reisub.devious.stallstealer;

import net.runelite.client.config.Button;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("sluwestallstealer")
public interface Config extends net.runelite.client.config.Config {
  @ConfigItem(
      keyName = "stall",
      name = "Stall",
      description = "What stall to steal from",
      position = 0)
  default Stall stall() {
    return Stall.ARDOUGNE_CAKE;
  }

  @ConfigItem(
      keyName = "dropItems",
      name = "Drop items",
      description =
          "Specify any items you'd like to drop, separated by newline, comma or semicolon",
      position = 1)
  default String dropItems() {
    return "Bread\nChocolate slice";
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
