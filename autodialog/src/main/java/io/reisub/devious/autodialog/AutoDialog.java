package io.reisub.devious.autodialog;

import com.google.inject.Provides;
import io.reisub.devious.utils.Utils;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.DialogOption;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.unethicalite.api.widgets.Dialog;
import org.pf4j.Extension;

@Extension
@PluginDependency(Utils.class)
@PluginDescriptor(
    name = "Sluwe Auto Dialog",
    description = "Automatically continues dialogs",
    enabledByDefault = false
)
@Slf4j
public class AutoDialog extends Plugin implements KeyListener {
  @Inject
  private Config config;
  @Inject
  private KeyManager keyManager;
  private boolean disable = false;

  @Provides
  Config provideConfig(ConfigManager configManager) {
    return configManager.getConfig(Config.class);
  }

  @Override
  protected void startUp() {
    log.info("Starting Sluwe Auto Dialog");
    keyManager.registerKeyListener(this);
  }

  @Override
  protected void shutDown() {
    log.info("Stopping Sluwe Auto Dialog");
    keyManager.unregisterKeyListener(this);
  }

  @Subscribe
  private void onGameTick(GameTick event) {
    if (disable) {
      return;
    }

    if (config.silkMerchant()
        && Dialog.canContinue() && Dialog.getText().startsWith("Hello. I have some fine silk")) {
      Dialog.invokeDialog(
          DialogOption.PLAYER_CONTINUE,
          DialogOption.NPC_CONTINUE,
          DialogOption.CHAT_OPTION_THREE,
          DialogOption.PLAYER_CONTINUE,
          DialogOption.NPC_CONTINUE,
          DialogOption.CHAT_OPTION_TWO,
          DialogOption.PLAYER_CONTINUE,
          DialogOption.NPC_CONTINUE
      );

      return;
    }

    if (Dialog.isViewingOptions()) {
      List<Widget> options = Dialog.getOptions();

      for (Widget opt : options) {
        if ((config.questHelper() && opt.getText().startsWith("["))
            || (config.phialsExchangeAll() && opt.getText().startsWith("Exchange All:"))) {
          Dialog.chooseOption(opt.getIndex());
          return;
        }
      }
    }

    if (Dialog.canContinue()) {
      Dialog.continueSpace();
    }
  }

  @Override
  public void keyTyped(KeyEvent e) {

  }

  @Override
  public void keyPressed(KeyEvent e) {
    if (config.disableHotkey().matches(e)) {
      disable = true;
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {
    if (config.disableHotkey().matches(e)) {
      disable = false;
    }
  }
}
