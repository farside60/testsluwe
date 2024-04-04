package io.reisub.devious.blackjack;

import net.runelite.client.config.Button;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("sluweblackjack")
public interface Config extends net.runelite.client.config.Config {
  @ConfigItem(keyName = "target", name = "Target", description = "Select your target", position = 0)
  default Target target() {
    return Target.BEARDED_BANDIT;
  }

  @ConfigItem(
      keyName = "notedItemId",
      name = "Noted item ID",
      description =
          "Enter the ID of the noted item to exchange. Set to 0 to buy wines from the bar.",
      position = 1)
  default int notedItemId() {
    return 0;
  }

  @ConfigItem(
      keyName = "eatThreshold",
      name = "Eat at missing health",
      description =
          "At how much missing health we should start eating. Ideally you should set this to the "
              + "amount of HP you get from your food + 1 so as not to waste food and keep the "
              + "natural regeneration ticking.",
      position = 2)
  default int eatThreshold() {
    return 12;
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
