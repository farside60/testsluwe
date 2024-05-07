package io.reisub.devious.tutorialisland;

import net.runelite.client.config.Button;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

/** Tutorial Island configuration. */
@ConfigGroup("sluwetutorialisland")
public interface Config extends net.runelite.client.config.Config {
  @ConfigItem(keyName = "gender", name = "Gender", description = "Choose your gender", position = 0)
  default Gender gender() {
    return Gender.FEMALE;
  }

  @ConfigItem(
      keyName = "randomizeAppearance",
      name = "Randomize appearance",
      description = "Randomize character appearance",
      position = 1)
  default boolean randomizeAppearance() {
    return true;
  }

  @ConfigItem(
      keyName = "ironman",
      name = "Ironman",
      description =
          "Select your preferred ironman type. For obvious reasons, group iron men will "
              + "require manual intervention at the end of the run. NOTE: Setting ironman mode"
              + "requires a bank pin. This plugin does not do that because it would risk"
              + " interfering with other bank pin plugins. I recommend using the Unethical"
              + "one to automatically set up your bank pin.",
      position = 2)
  default Ironman ironman() {
    return Ironman.NONE;
  }

  @ConfigItem(
      keyName = "dontLeave",
      name = "Don't leave island",
      description =
          "Don't leave the island at the end of the run. In case you don't trust the"
              + "automatic iron man setup.",
      position = Integer.MAX_VALUE - 1)
  default boolean dontLeave() {
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
