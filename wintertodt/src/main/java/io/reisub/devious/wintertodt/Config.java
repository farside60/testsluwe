/*
 * Copyright (c) 2018, Andrew EP | ElPinche256 <https://github.com/ElPinche256>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package io.reisub.devious.wintertodt;

import net.runelite.client.config.Button;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("sluwewintertodt")
public interface Config extends net.runelite.client.config.Config {
  @Range(min = 1, max = 99)
  @ConfigItem(
      keyName = "eatThreshold",
      name = "Eat threshold",
      description = "The threshold at which to start eating.",
      position = 0)
  default int eatThreshold() {
    return 10;
  }

  @ConfigItem(
      keyName = "checkMissing",
      name = "Check missing HP",
      description = "Check missing HP rather than the absolute health value.",
      position = 1)
  default boolean checkMissing() {
    return true;
  }

  @ConfigItem(
      keyName = "food",
      name = "Food",
      description = "Name of the food item to withdraw from the bank.",
      position = 2)
  default String food() {
    return "Cake";
  }

  @ConfigItem(
      keyName = "foodQuantity",
      name = "Food quantity",
      description = "How much food to withdraw from the bank.",
      position = 3)
  default int foodQuantity() {
    return 8;
  }

  @ConfigItem(
      position = 10,
      keyName = "sideSelection",
      name = "Select side",
      description = "Choose which side you want to play the game at.")
  default Side sideSelection() {
    return Side.EAST;
  }

  @Range(min = 1, max = 100)
  @ConfigItem(
      position = 11,
      keyName = "sideTimeout",
      name = "Side timeout",
      description = "Seconds to wait before switching side because of an incapacitated pyromancer.")
  default int sideTimeout() {
    return 15;
  }

  @ConfigItem(
      keyName = "fletchNearBrazier",
      name = "Fletch near brazier",
      description =
          "Fletch near brazier so we can quickly fix/light the brazier for more experience "
              + "and points at the cost of taking more damage.",
      position = 20)
  default boolean fletchNearBrazier() {
    return true;
  }

  @ConfigItem(
      keyName = "dodgeProjectiles",
      name = "Dodge projectiles",
      description = "Attempts to dodge snow fall and exploding braziers.",
      position = 21)
  default boolean dodgeProjectiles() {
    return true;
  }

  @ConfigItem(
      keyName = "openCrates",
      name = "Open crates",
      description = "Enable to open crates.",
      position = 22)
  default boolean openCrates() {
    return true;
  }

  @ConfigItem(
      keyName = "hop",
      name = "Hop",
      description = "Hop to different worlds after finishing game.",
      position = 30)
  default boolean hop() {
    return true;
  }

  @Range(min = 40, max = 100)
  @ConfigItem(
      position = 31,
      keyName = "hopPercentage",
      name = "Hop percentage",
      description =
          "Minimum percentage the boss' health should be at before hopping. Low values risk not "
              + "getting enough points. 70 is safe, 60 should work, anything lower is very risky.")
  default int hopPercentage() {
    return 70;
  }

  @ConfigItem(
      keyName = "startButton",
      name = "Start/Stop",
      description = "Start the script",
      position = 100)
  default Button startButton() {
    return new Button();
  }
}
