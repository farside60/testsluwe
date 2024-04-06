package io.reisub.devious.tempoross.tasks;

import io.reisub.devious.tempoross.Tempoross;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.widgets.Dialog;

public class LeaveGame extends Task {
  @Inject private Tempoross plugin;

  @Override
  public String getStatus() {
    return "Leaving game";
  }

  @Override
  public boolean validate() {
    if (!plugin.isInTemporossArea()) {
      return false;
    }

    NPC pudi = NPCs.getNearest(NpcID.CAPTAIN_PUDI_10585, NpcID.CAPTAIN_PUDI_10586);

    if (pudi != null && Players.getLocal().distanceTo(pudi) < 5) {
      return true;
    }

    if (Players.getAll().size() > 1) {
      return true;
    }

    return plugin.isFinished();
  }

  @Override
  public void execute() {
    NPC leaveNpc = NPCs.getNearest(n -> n.hasAction("Forfeit", "Leave"));
    if (leaveNpc == null) {
      return;
    }

    if (leaveNpc.hasAction("Forfeit")) {
      plugin.setGamesLost(plugin.getGamesLost() + 1);
    }

    leaveNpc.interact("Leave", "Forfeit");
    Time.sleepTicksUntil(() -> plugin.isInDesert(), 20);

    Time.sleepTicksUntil(Dialog::canContinueNPC, 10);
  }
}
