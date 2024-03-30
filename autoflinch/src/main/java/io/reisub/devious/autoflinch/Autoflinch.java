package io.reisub.devious.autoflinch;

import com.google.inject.Provides;
import io.reisub.devious.autoflinch.tasks.Attack;
import io.reisub.devious.utils.TickScript;
import io.reisub.devious.utils.Utils;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.InteractingChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.entities.Players;
import org.pf4j.Extension;
import org.slf4j.Logger;

@Extension
@PluginDependency(Utils.class)
@PluginDescriptor(
    name = "Sluwe Autoflinch",
    description = "Being a little bitch with zero effort",
    enabledByDefault = false)
@Slf4j
public class Autoflinch extends TickScript {
  @Inject private Config config;

  @Getter private WorldPoint safeLocation;
  @Getter private NPC target;

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
    this.safeLocation = Players.getLocal().getWorldLocation();

    addTask(Attack.class);
  }

  @Override
  protected void onStop() {
    this.safeLocation = null;
    this.target = null;
  }

  @Subscribe
  private void onInteractingChanged(InteractingChanged event) {
    if (!event.getSource().equals(Players.getLocal()) || target != null) {
      return;
    }

    final Actor targetActor = event.getTarget();

    if (targetActor == null) {
      return;
    }

    target =
        NPCs.getNearest(
            n ->
                n.getId() == targetActor.getId()
                    && n.getWorldLocation().equals(targetActor.getWorldLocation()));
  }
}
