package io.reisub.devious.barroniteminer.tasks;

import io.reisub.devious.utils.api.SluweBank;
import io.reisub.devious.utils.api.SluweMovement;
import io.reisub.devious.utils.tasks.BankTask;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.items.Inventory;

public class HandleBank extends BankTask {
  @Override
  public boolean validate() {
    return Inventory.contains(i -> i.getName().startsWith("Uncut")) && Inventory.isFull();
  }

  @Override
  public void execute() {
    final WorldPoint destination = new WorldPoint(2970, 5797, 0);
    if (Players.getLocal().distanceTo(destination) > 8) {
      SluweMovement.walkTo(destination);
    }

    open();

    SluweBank.depositAll(false, i -> i.getName().startsWith("Uncut"));
  }
}
