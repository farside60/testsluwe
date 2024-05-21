package io.reisub.devious.utils.tasks;

import io.reisub.devious.utils.Config;
import javax.inject.Inject;
import net.runelite.api.NPC;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Reachable;
import net.unethicalite.api.utils.MessageUtils;

public class PickupCat extends Task {
  @Inject private Config config;
  private NPC cat;
  private NPC kitten;

  @Override
  public String getStatus() {
    return "Picking up cat";
  }

  @Override
  public boolean validate() {
    kitten =
        NPCs.getNearest(
            n -> n.getName().equals("Kitten") && n.getInteracting() == Players.getLocal());

    if (kitten != null
        && KittenTask.handleKitten
        && !Inventory.isFull()
        && !Inventory.contains(config.kittenFood())
        && Reachable.isInteractable(kitten)) {
      return true;
    }

    cat =
        NPCs.getNearest(
            n ->
                n.getName().equals("Cat")
                    && n.getInteracting() != null
                    && n.getInteracting() == Players.getLocal()
                    && n.hasAction("Pick-up"));

    return cat != null && !Inventory.isFull() && Reachable.isInteractable(cat);
  }

  @Override
  public void execute() {
    if (kitten != null) {
      pickUpKitten();
    } else {
      pickUpCat();
    }
  }

  private void pickUpKitten() {
    MessageUtils.addMessage("No kitten food found, picking up kitten.");
    kitten.interact("Pick-up");
    Time.sleepTicksUntil(() -> Inventory.contains("Pet kitten"), 20);

    KittenTask.handleKitten = false;
  }

  private void pickUpCat() {
    cat.interact("Pick-up");
    Time.sleepTicksUntil(() -> Inventory.contains("Pet cat"), 20);
  }
}
