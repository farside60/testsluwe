package io.reisub.devious.autopickup.tasks;

import io.reisub.devious.autopickup.AutoPickup;
import io.reisub.devious.autopickup.Config;
import io.reisub.devious.utils.tasks.Task;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.GameState;
import net.runelite.api.World;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.game.Game;
import net.unethicalite.api.game.Worlds;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.client.Static;

public class Hop extends Task {
  @Inject private AutoPickup plugin;
  @Inject private Config config;

  private Deque<World> worldQueue;
  private int last;

  @Override
  public String getStatus() {
    return "Hopping to next world";
  }

  @Override
  public boolean validate() {
    if (last + 5 > Static.getClient().getTickCount() || Inventory.isFull()) {
      return false;
    }

    return needMoreItemsAndNoneAreAvailable() && !isWaitingForAshes();
  }

  private boolean needMoreItemsAndNoneAreAvailable() {
    return plugin.getNearestItem() == null
        && (config.amount() == 0 || plugin.getItemsPickedUp() < plugin.getNumberOfItemsRequired());
  }

  private boolean isWaitingForAshes() {
    return config.pickUpAshes()
        && (config.ashAmount() == 0 || plugin.getAshesPickedUp() < config.ashAmount())
        && plugin.hasFire();
  }

  @Override
  public void execute() {
    final World world = getWorld();

    Worlds.hopTo(world);
    Time.sleep(1000);
    Time.sleepTicksUntil(() -> Game.getState() == GameState.LOGGED_IN, 20);

    last = Static.getClient().getTickCount();
  }

  private World getWorld() {
    if (worldQueue == null || worldQueue.isEmpty()) {
      initializeWorldQueue(Worlds.getCurrentId());
    }

    final World world = worldQueue.poll();

    worldQueue.add(world);

    return world;
  }

  private void initializeWorldQueue(int startWorldId) {
    List<World> worlds =
        Worlds.getAll(
            w -> {
              if (!w.isMembers() && !config.enableFreeWorlds()) {
                return false;
              }

              if (w.isAllPkWorld()
                  || w.isTournament()
                  || w.isLeague()
                  || w.isPvpArena()
                  || w.isQuestSpeedRunning()) {
                return false;
              }

              if (w.isSkillTotal()) {
                try {
                  int totalRequirement =
                      Integer.parseInt(w.getActivity().substring(0, w.getActivity().indexOf(" ")));

                  return Static.getClient().getTotalLevel() >= totalRequirement;
                } catch (NumberFormatException e) {
                  return false;
                }
              }

              return true;
            });

    worldQueue = new ArrayDeque<>();
    worldQueue.addAll(worlds);

    while (true) {
      World world = worldQueue.poll();
      worldQueue.add(world);

      if (world == null || world.getId() == startWorldId) {
        break;
      }
    }
  }
}
