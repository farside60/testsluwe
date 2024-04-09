package io.reisub.devious.tithefarm;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import io.reisub.devious.tithefarm.tasks.BuyRewards;
import io.reisub.devious.tithefarm.tasks.Deposit;
import io.reisub.devious.tithefarm.tasks.EnterFarm;
import io.reisub.devious.tithefarm.tasks.FillCans;
import io.reisub.devious.tithefarm.tasks.GetTools;
import io.reisub.devious.tithefarm.tasks.Harvest;
import io.reisub.devious.tithefarm.tasks.LeaveFarm;
import io.reisub.devious.tithefarm.tasks.Plant;
import io.reisub.devious.tithefarm.tasks.StartRun;
import io.reisub.devious.tithefarm.tasks.TakeSeeds;
import io.reisub.devious.tithefarm.tasks.Water;
import io.reisub.devious.utils.TickScript;
import io.reisub.devious.utils.Utils;
import java.util.Set;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.unethicalite.client.Static;
import org.pf4j.Extension;
import org.slf4j.Logger;

@Extension
@PluginDependency(Utils.class)
@PluginDescriptor(
    name = "Sluwe Tithe Farm",
    description = "It ain't much, but it's honest work",
    enabledByDefault = false)
@Slf4j
public class TitheFarm extends TickScript {
  public static final int TITHE_FARM_REGION = 7222;
  public static final Set<Integer> SEED_IDS =
      ImmutableSet.of(ItemID.GOLOVANOVA_SEED, ItemID.BOLOGANO_SEED, ItemID.LOGAVANO_SEED);
  @Inject private Config config;
  @Getter @Setter private boolean startedRun;
  @Getter @Setter private boolean finishedPlanting;

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

    addTask(BuyRewards.class);
    addTask(TakeSeeds.class);
    addTask(EnterFarm.class);
    addTask(GetTools.class);
    addTask(FillCans.class);
    addTask(Deposit.class);
    addTask(LeaveFarm.class);
    addTask(StartRun.class);
    addTask(Plant.class);
    addTask(Water.class);
    addTask(Harvest.class);

    startedRun = false;
    finishedPlanting = false;
  }

  public static boolean isInTitheFarm() {
    return Static.getClient().isInInstancedRegion() && Utils.isInMapRegion(TITHE_FARM_REGION);
  }
}
