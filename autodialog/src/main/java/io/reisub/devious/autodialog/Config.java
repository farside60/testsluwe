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

package io.reisub.devious.autodialog;

import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

@ConfigGroup("sluweautodialog")
public interface Config extends net.runelite.client.config.Config {
  @ConfigItem(
      keyName = "disableHotkey",
      name = "Temporary disable hotkey",
      description = "Hold this hotkey to temporarily disable auto continue.",
      position = 0)
  default Keybind disableHotkey() {
    return Keybind.NOT_SET;
  }

  @ConfigItem(
      keyName = "questHelper",
      name = "Quest helper",
      description = "Choose the correct chat option highlighted by the Quest helper plugin.",
      position = 1)
  default boolean questHelper() {
    return true;
  }

  @ConfigItem(
      keyName = "phialsExchangeAll",
      name = "Phials exchange all",
      description = "Choose the Exchange all: option of Phial's dialogue.",
      position = 2)
  default boolean phialsExchangeAll() {
    return true;
  }

  @ConfigItem(
      keyName = "silkMerchant",
      name = "Silk merchant",
      description = "Get 60 coins per silk.",
      position = 3)
  default boolean silkMerchant() {
    return true;
  }

  @ConfigItem(
      keyName = "seersStew",
      name = "Seers stew",
      description = "Buy stew from the bartender in Seers.",
      position = 4)
  default boolean seersStew() {
    return true;
  }
}
