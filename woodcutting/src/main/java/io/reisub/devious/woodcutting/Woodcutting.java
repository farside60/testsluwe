package io.reisub.devious.woodcutting;

import com.google.inject.Provides;
import io.reisub.devious.utils.TickScript;
import io.reisub.devious.utils.Utils;
import io.reisub.devious.utils.api.Activity;
import io.reisub.devious.woodcutting.tasks.Burn;
import io.reisub.devious.woodcutting.tasks.Chop;
import io.reisub.devious.woodcutting.tasks.Drop;
import io.reisub.devious.woodcutting.tasks.GoToBank;
import io.reisub.devious.woodcutting.tasks.GoToChoppingArea;
import io.reisub.devious.woodcutting.tasks.HandleBank;
import io.reisub.devious.woodcutting.tasks.MoveToBurnLine;
import io.reisub.devious.woodcutting.tasks.MoveToRespawning;
import io.reisub.devious.woodcutting.tasks.PickupNest;
import io.reisub.devious.woodcutting.tasks.UseSpecial;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import org.pf4j.Extension;
import org.slf4j.Logger;

@Extension
@PluginDependency(Utils.class)
@PluginDescriptor(
    name = "Sluwe Woodcutting",
    description = "I hear digging but I don't hear chopping",
    enabledByDefault = false)
@Slf4j
public class Woodcutting extends TickScript {
  public static final Activity CHOPPING = new Activity("Chopping");
  public static final Activity BURNING = new Activity("Burning");
  @Inject private Config config;
  @Getter @Setter private int lastBankTick;

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

    final Chop chop = injector.getInstance(Chop.class);
    chop.setCurrentTreePosition(null);

    addTask(MoveToBurnLine.class);
    addTask(Burn.class);
    addTask(Drop.class);
    addTask(HandleBank.class);
    addTask(GoToBank.class);
    addTask(GoToChoppingArea.class);
    addTask(PickupNest.class);
    addTask(chop);
    addTask(UseSpecial.class);
    addTask(MoveToRespawning.class);
  }
}
