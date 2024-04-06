package io.reisub.devious.mining;

import com.google.inject.Provides;
import io.reisub.devious.mining.tasks.CastHumidify;
import io.reisub.devious.mining.tasks.CastSuperheat;
import io.reisub.devious.mining.tasks.ChiselAmethyst;
import io.reisub.devious.mining.tasks.CrushBarronite;
import io.reisub.devious.mining.tasks.Deposit;
import io.reisub.devious.mining.tasks.GoToBank;
import io.reisub.devious.mining.tasks.GoToMiningArea;
import io.reisub.devious.mining.tasks.HandleBank;
import io.reisub.devious.mining.tasks.Mine;
import io.reisub.devious.mining.tasks.MoveToRespawning;
import io.reisub.devious.utils.TickScript;
import io.reisub.devious.utils.Utils;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Varbits;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.unethicalite.api.game.Vars;
import org.pf4j.Extension;
import org.slf4j.Logger;

@PluginDescriptor(
    name = "Sluwe Mining",
    description = "Diggy, diggy hole",
    enabledByDefault = false)
@PluginDependency(Utils.class)
@Slf4j
@Extension
public class Mining extends TickScript {
  @Inject private Config config;
  @Getter @Setter private boolean arrived;

  @Provides
  public Config getConfig(ConfigManager configManager) {
    return configManager.getConfig(Config.class);
  }

  @Override
  public Logger getLogger() {
    return log;
  }

  @Override
  protected void onStart() {
    super.onStart();

    final Mine mine = injector.getInstance(Mine.class);
    mine.setCurrentRockPosition(null);

    addTask(CastSuperheat.class);
    addTask(HandleBank.class);
    addTask(GoToBank.class);
    addTask(CastHumidify.class);
    addTask(Deposit.class);
    addTask(GoToMiningArea.class);
    addTask(ChiselAmethyst.class);
    addTask(CrushBarronite.class);
    addTask(mine);
    addTask(MoveToRespawning.class);
  }

  @Subscribe
  public void onVarbitChanged(VarbitChanged varbitChanged) {
    if (!isRunning()
        || config.location() != Location.MISCELLANIA_COAL
        || varbitChanged.getVarbitId() != Varbits.KINGDOM_APPROVAL) {
      return;
    }

    if (Vars.getBit(Varbits.KINGDOM_APPROVAL) == 127) {
      stop("Reached max approval, stopping plugin");
    }
  }
}
