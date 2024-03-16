package io.reisub.devious.combathelper;

import com.google.inject.Provides;
import io.reisub.devious.alchemicalhydra.SluweAlchemicalHydra;
import io.reisub.devious.cerberus.SluweCerberus;
import io.reisub.devious.combathelper.alch.AlchHelper;
import io.reisub.devious.combathelper.bones.BonesHelper;
import io.reisub.devious.combathelper.boss.BossHelper;
import io.reisub.devious.combathelper.consume.ConsumeHelper;
import io.reisub.devious.combathelper.misc.MiscHelper;
import io.reisub.devious.combathelper.prayer.PrayerHelper;
import io.reisub.devious.combathelper.special.SpecialHelper;
import io.reisub.devious.combathelper.swap.SwapHelper;
import io.reisub.devious.gauntletextended.SluweGauntletExtended;
import io.reisub.devious.grotesqueguardians.SluweGrotesqueGuardians;
import io.reisub.devious.zulrah.SluweZulrah;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.GameState;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.InteractingChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.itemstats.ItemStatPlugin;
import net.runelite.client.plugins.unethicalite.UnethicalitePlugin;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.game.Game;
import net.unethicalite.client.Static;
import org.pf4j.Extension;

@Extension
@PluginDependency(ItemStatPlugin.class)
@PluginDependency(UnethicalitePlugin.class)
@PluginDependency(SluweAlchemicalHydra.class)
@PluginDependency(SluweCerberus.class)
@PluginDependency(SluweGauntletExtended.class)
@PluginDependency(SluweGrotesqueGuardians.class)
@PluginDependency(SluweZulrah.class)
@PluginDescriptor(
    name = "Sluwe Combat Helper",
    description = "Various utilities to make combat easier")
@Singleton
@Slf4j
public class CombatHelper extends Plugin {

  @Inject private Config config;
  @Getter private Actor lastTarget;
  private ScheduledExecutorService executor;
  private List<Helper> helpers;
  @Inject @Getter private PrayerHelper prayerHelper;
  @Inject @Getter private SwapHelper swapHelper;
  @Inject @Getter private MiscHelper miscHelper;

  @Provides
  Config provideConfig(ConfigManager configManager) {
    return configManager.getConfig(Config.class);
  }

  @Override
  protected void startUp() {
    log.info("Starting Sluwe Combat Helper");

    executor = Executors.newSingleThreadScheduledExecutor();
    helpers = new ArrayList<>();

    helpers.add(prayerHelper);
    helpers.add(injector.getInstance(ConsumeHelper.class));
    helpers.add(injector.getInstance(SpecialHelper.class));
    helpers.add(injector.getInstance(BonesHelper.class));
    helpers.add(injector.getInstance(AlchHelper.class));
    helpers.add(swapHelper);
    helpers.add(miscHelper);
    helpers.add(injector.getInstance(BossHelper.class));

    for (Helper helper : helpers) {
      helper.startUp();

      Static.getKeyManager().registerKeyListener(helper);
      Static.getEventBus().register(helper);
    }
  }

  @Override
  protected void shutDown() {
    log.info("Stopping Sluwe Combat Helper");

    for (Helper helper : helpers) {
      Static.getKeyManager().unregisterKeyListener(helper);
      Static.getEventBus().unregister(helper);

      helper.shutDown();
    }

    helpers.clear();
    executor.shutdownNow();
  }

  public void schedule(Runnable runnable, int delay) {
    executor.schedule(runnable, delay, TimeUnit.MILLISECONDS);
  }

  @Subscribe
  private void onInteractingChanged(InteractingChanged event) {
    if (!isLoggedIn()) {
      return;
    }

    if (event.getSource() == Players.getLocal() && event.getTarget() != null) {
      lastTarget = Players.getLocal().getInteracting();
    }
  }

  @Subscribe
  private void onActorDeath(ActorDeath event) {
    if (!isLoggedIn()) {
      return;
    }

    if (lastTarget == null || lastTarget.isDead()) {
      lastTarget = null;
    }
  }

  public final boolean isLoggedIn() {
    return Game.getState() == GameState.LOGGED_IN;
  }
}
