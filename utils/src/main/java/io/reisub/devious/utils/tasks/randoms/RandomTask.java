package io.reisub.devious.utils.tasks.randoms;

import io.reisub.devious.utils.api.Activity;
import io.reisub.devious.utils.tasks.Task;
import java.time.Duration;
import java.time.Instant;
import lombok.Getter;
import net.runelite.api.NPC;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.movement.Reachable;

public abstract class RandomTask extends Task {
  private Instant last = Instant.EPOCH;
  @Getter private NPC randomNpc;

  @Override
  public Activity getActivity() {
    return Activity.RANDOM_EVENT;
  }

  @Override
  public String getStatus() {
    return "Handling " + this.getClass().getSimpleName() + " random";
  }

  public abstract boolean isEnabled();

  public abstract boolean shouldDismiss();

  public void dismiss(NPC npc) {
    if (npc == null || !npc.hasAction("Dismiss")) {
      return;
    }

    final int npcId = npc.getId();

    npc.interact("Dismiss");

    Time.sleepTicksUntil(
        () ->
            NPCs.getNearest(
                    n -> n.getId() == npcId && n.getInteracting().equals(Players.getLocal()))
                == null,
        20);

    setLast();
  }

  protected boolean validate(String npcName) {
    randomNpc = findNpc(npcName);

    return isEnabled()
        && isLastDurationAgo(Duration.ofSeconds(5))
        && randomNpc != null
        && Reachable.isInteractable(randomNpc);
  }

  protected NPC findNpc(String name) {
    return NPCs.getNearest(
        n ->
            n.getName().equals(name)
                && n.getInteracting().equals(Players.getLocal()));
  }

  protected boolean isLastDurationAgo(Duration duration) {
    return Duration.between(last, Instant.now()).compareTo(duration) >= 0;
  }

  protected void setLast() {
    last = Instant.now();
  }
}
