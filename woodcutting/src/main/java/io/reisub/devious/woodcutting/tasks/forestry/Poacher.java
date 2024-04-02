package io.reisub.devious.woodcutting.tasks.forestry;

import io.reisub.devious.utils.tasks.Task;
import io.reisub.devious.woodcutting.Config;
import io.reisub.devious.woodcutting.Woodcutting;
import javax.inject.Inject;
import net.runelite.api.NpcID;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.entities.Players;

public class Poacher extends Task {
  @Inject private Woodcutting plugin;
  @Inject private Config config;

  @Override
  public String getStatus() {
    return "Getting rid of poacher traps";
  }

  @Override
  public boolean validate() {
    return config.forestryPoacher()
        && NPCs.getNearest(NpcID.FOX_TRAP) != null
        && !Players.getLocal().isInteracting();
  }

  @Override
  public void execute() {
    plugin.setActivity(Woodcutting.FORESTRY);

    NPCs.getNearest(NpcID.FOX_TRAP).interact(0);
    Time.sleepTicksUntil(() -> Players.getLocal().isInteracting(), 5);
  }
}
