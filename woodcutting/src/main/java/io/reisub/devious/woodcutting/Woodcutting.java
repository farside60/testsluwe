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
import io.reisub.devious.woodcutting.tasks.forestry.Pheasants;
import io.reisub.devious.woodcutting.tasks.forestry.Poacher;
import io.reisub.devious.woodcutting.tasks.forestry.Roots;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.NpcDespawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.entities.TileObjects;
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
  public static final Activity FORESTRY = new Activity("Forestry");
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

    addTask(Roots.class);
    addTask(Poacher.class);
    addTask(Pheasants.class);
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
  
  @Subscribe
  private void onGameObjectDespawned(GameObjectDespawned gameObjectDespawned) {
    if (!isRunning() || !isDoingForestry()) {
      return;
    }

    final int id = gameObjectDespawned.getGameObject().getId();

    if (id == ObjectID.TREE_ROOTS || id == ObjectID.ANIMAINFUSED_TREE_ROOTS) {
      if (TileObjects.getAll(ObjectID.TREE_ROOTS, ObjectID.ANIMAINFUSED_TREE_ROOTS).isEmpty()) {
        setActivity(Activity.IDLE);
      }
    }
  }

  @Subscribe
  private void onNpcDespawned(NpcDespawned npcDespawned) {
    if (!isRunning() || !isDoingForestry()) {
      return;
    }

    final int id = npcDespawned.getNpc().getId();

    if (id == NpcID.FRIGHTENED_FOX || id == NpcID.FRIGHTENED_FOX_12560) {
      if (NPCs.getAll(NpcID.FRIGHTENED_FOX, NpcID.FRIGHTENED_FOX_12560).isEmpty()) {
        setActivity(Activity.IDLE);
      }
    } else if (id == NpcID.FREAKY_FORESTER_12536) {
      setActivity(Activity.IDLE);
    }
  }
  
  public boolean isDoingForestry() {
    return isCurrentActivity(FORESTRY) && (config.forestryRoots() || config.forestryPoacher());
  }
}
