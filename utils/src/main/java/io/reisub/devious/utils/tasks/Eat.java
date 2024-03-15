package io.reisub.devious.utils.tasks;

import io.reisub.devious.utils.api.Activity;
import javax.inject.Inject;
import lombok.Setter;
import net.runelite.api.Item;
import net.runelite.api.Skill;
import net.unethicalite.api.game.Combat;
import net.unethicalite.api.game.Skills;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.client.Static;

/**
 * This is a very simple eating task to be used in any script that requires replenishing a player's
 * health. This does not support more advanced mechanics like 1-tick eating or combining drinks and
 * food. It simply eats or drinks whatever it finds in the inventory.
 */
public class Eat extends Task {
  @Setter private int threshold;
  @Setter private boolean checkMissing;
  private int lastTick;
  private Item consumable;

  /**
   * Create a task that eats or drinks the first item it finds in the inventory once the player's
   * health drops to 50%.
   */
  @Inject
  public Eat() {
    threshold = Skills.getLevel(Skill.HITPOINTS) / 2;
  }

  @Override
  public String getStatus() {
    return "Eating";
  }

  @Override
  public Activity getActivity() {
    return Activity.EATING;
  }

  @Override
  public boolean validate() {
    if (checkMissing) {
      if (Combat.getMissingHealth() < threshold) {
        return false;
      }
    } else {
      if (Combat.getCurrentHealth() > threshold) {
        return false;
      }
    }

    consumable = Inventory.getFirst(i -> i.hasAction("Eat", "Drink"));

    return consumable != null && Static.getClient().getTickCount() - lastTick >= 3;
  }

  @Override
  public void execute() {
    lastTick = Static.getClient().getTickCount();
    consumable.interact("Eat", "Drink");
  }
}
