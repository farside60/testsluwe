package io.reisub.devious.autodialog;

import com.google.common.collect.ImmutableList;
import com.google.inject.Provides;
import io.reisub.devious.autodialog.tasks.DialogTask;
import io.reisub.devious.autodialog.tasks.PhialsExchange;
import io.reisub.devious.autodialog.tasks.QuestHelper;
import io.reisub.devious.autodialog.tasks.SeersStew;
import io.reisub.devious.autodialog.tasks.SilkMerchant;
import io.reisub.devious.utils.Utils;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.unethicalite.api.exception.InteractionException;
import net.unethicalite.api.widgets.Dialog;
import org.pf4j.Extension;

@Extension
@PluginDependency(Utils.class)
@PluginDescriptor(
    name = "Sluwe Auto Dialog",
    description = "Automatically continues dialogs",
    enabledByDefault = false)
@Slf4j
public class AutoDialog extends Plugin implements KeyListener {
  @Inject private Config config;
  @Inject private KeyManager keyManager;
  private boolean disable = false;
  private List<DialogTask> tasks;

  @Provides
  Config provideConfig(ConfigManager configManager) {
    return configManager.getConfig(Config.class);
  }

  @Override
  protected void startUp() {
    log.info("Starting Sluwe Auto Dialog");
    keyManager.registerKeyListener(this);

    tasks =
        ImmutableList.of(
            injector.getInstance(SeersStew.class),
            injector.getInstance(SilkMerchant.class),
            injector.getInstance(PhialsExchange.class),
            injector.getInstance(QuestHelper.class));
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

    for (DialogTask task : tasks) {
      if (task.validate()) {
        try {
          task.execute();
        } catch (InteractionException interactionException) {
          log.info(interactionException.getMessage());
        }
        return;
      }
    }

    if (Dialog.canContinue()) {
      Dialog.continueSpace();
    }
  }

  @Override
  public void keyTyped(KeyEvent e) {}

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
