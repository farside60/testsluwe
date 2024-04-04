package io.reisub.devious.autoflinch;

import net.runelite.client.config.Button;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("sluweautoflinch")
public interface Config extends net.runelite.client.config.Config {
  @ConfigItem(
      keyName = "waitTicks",
      name = "Wait ticks",
      description = "Ticks to wait between attacking and retreating",
      position = 0
  )
  default int waitTicks() {
    return 1;
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
