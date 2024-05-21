package io.reisub.devious.utils;

import io.reisub.devious.utils.api.SluweSkill;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Keybind;
import net.runelite.client.config.Range;

@ConfigGroup("sluweutils")
public interface Config extends net.runelite.client.config.Config {
  @ConfigSection(
      keyName = "kittenConfig",
      name = "Kitten Configuration",
      description = "Configure handling of kittens",
      position = 10)
  String kittenConfig = "kittenConfig";

  @ConfigSection(
      keyName = "randomConfig",
      name = "Random Configuration",
      description = "Configure handling of random events",
      position = 20)
  String randomConfig = "randomConfig";

  @Range(min = 50, max = 500)
  @ConfigItem(
      keyName = "minDelay",
      name = "Minimum delay",
      description = "Minimum delay in ms for actions to run after start of a game tick",
      position = 0)
  default int minDelay() {
    return 250;
  }

  @Range(min = 100, max = 550)
  @ConfigItem(
      keyName = "maxDelay",
      name = "Maximum delay",
      description = "Maximum delay in ms for actions to run after start of a game tick",
      position = 1)
  default int maxDelay() {
    return 300;
  }

  @ConfigItem(
      keyName = "walkingInterruptHotkey",
      name = "Walking interrupt hotkey",
      description = "Hotkey to interrupt walking with",
      position = 2)
  default Keybind walkingInterruptHotkey() {
    return new Keybind(KeyEvent.VK_F12, InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK);
  }

  @ConfigItem(
      keyName = "handleKitten",
      name = "Handle kitten",
      description =
          "If the script supports it, this will feed and entertain your kitten and pick it up "
              + "once it's grown up into a cat",
      section = kittenConfig,
      position = 10)
  default boolean handleKitten() {
    return true;
  }

  @ConfigItem(
      keyName = "kittenFood",
      name = "Kitten food",
      description = "Specify the food to feed your kitten",
      section = kittenConfig,
      position = 11)
  default String kittenFood() {
    return "Raw karambwanji";
  }

  @ConfigItem(
      keyName = "kittenFoodAmount",
      name = "Kitten food amount",
      description = "Specify the amount of food to withdraw from the bank (0 for all)",
      section = kittenConfig,
      position = 12)
  default int kittenFoodAmount() {
    return 0;
  }

  @ConfigItem(
      keyName = "rewardSkill",
      name = "Reward skill",
      description =
          "Specify which skill to level using random event rewards."
              + "If the skill is not unlocked, it will fall back to Prayer.",
      section = randomConfig,
      position = 20)
  default SluweSkill rewardSkill() {
    return SluweSkill.HERBLORE;
  }

  @ConfigItem(
      keyName = "enableGenie",
      name = "Enable Genie",
      description = "Handle Genie random events in scripts that support it",
      section = randomConfig,
      position = 21)
  default boolean enableGenie() {
    return true;
  }

  @ConfigItem(
      keyName = "dismissGenie",
      name = "Dismiss Genie",
      description = "Dismiss the Genie instead of completing the event",
      section = randomConfig,
      hidden = true,
      unhide = "enableGenie",
      position = 22)
  default boolean dismissGenie() {
    return false;
  }

  @ConfigItem(
      keyName = "enableCountCheck",
      name = "Enable Count Check",
      description = "Handle Count Check random events in scripts that support it",
      section = randomConfig,
      position = 23)
  default boolean enableCountCheck() {
    return true;
  }

  @ConfigItem(
      keyName = "dismissCountCheck",
      name = "Dismiss Count Check",
      description = "Dismiss the Count Check instead of completing the event",
      section = randomConfig,
      hidden = true,
      unhide = "enableCountCheck",
      position = 24)
  default boolean dismissCountCheck() {
    return false;
  }

  @ConfigItem(
      keyName = "enableSurpriseExam",
      name = "Enable Surprise Exam",
      description = "Handle Surprise Exam random events in scripts that support it",
      section = randomConfig,
      position = 25)
  default boolean enableSurpriseExam() {
    return true;
  }

  @ConfigItem(
      keyName = "dismissSurpriseExam",
      name = "Dismiss Surprise Exam",
      description = "Dismiss the Surprise Exam instead of completing the event",
      section = randomConfig,
      hidden = true,
      unhide = "enableSurpriseExam",
      position = 26)
  default boolean dismissSurpriseExam() {
    return false;
  }

  @ConfigItem(
      keyName = "enableFrog",
      name = "Enable Frog",
      description = "Handle Frog random events in scripts that support it",
      section = randomConfig,
      position = 27)
  default boolean enableFrog() {
    return true;
  }

  @ConfigItem(
      keyName = "dismissFrog",
      name = "Dismiss Frog",
      description = "Dismiss the Frog instead of completing the event",
      section = randomConfig,
      hidden = true,
      unhide = "enableFrog",
      position = 28)
  default boolean dismissFrog() {
    return true;
  }

  @ConfigItem(
      keyName = "enableRickTurpentine",
      name = "Enable Rick Turpentine",
      description = "Handle Rick Turpentine random events in scripts that support it",
      section = randomConfig,
      position = 29)
  default boolean enableRickTurpentine() {
    return true;
  }

  @ConfigItem(
      keyName = "dismissRickTurpentine",
      name = "Dismiss Rick Turpentine",
      description = "Dismiss Rick Turpentine instead of completing the event",
      section = randomConfig,
      hidden = true,
      unhide = "enableRickTurpentine",
      position = 30)
  default boolean dismissRickTurpentine() {
    return true;
  }

  @ConfigItem(
      keyName = "enableFreakyForester",
      name = "Enable Freaky Forester",
      description = "Handle Freaky Forester random events in scripts that support it",
      section = randomConfig,
      position = 30)
  default boolean enableFreakyForester() {
    return true;
  }

  @ConfigItem(
      keyName = "dismissFreakyForester",
      name = "Dismiss Freaky Forester",
      description = "Dismiss Freaky Forester instead of completing the event",
      section = randomConfig,
      hidden = true,
      unhide = "enableFreakyForester",
      position = 30)
  default boolean dismissFreakyForester() {
    return false;
  }
}
