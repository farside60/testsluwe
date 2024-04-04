package io.reisub.devious.roguesden.tasks;

import io.reisub.devious.roguesden.Config;
import io.reisub.devious.roguesden.RoguesDen;
import io.reisub.devious.utils.Utils;
import io.reisub.devious.utils.api.SluweMovement;
import io.reisub.devious.utils.tasks.BankTask;
import java.time.Duration;
import java.time.Instant;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.items.Bank;
import net.unethicalite.api.items.Equipment;
import net.unethicalite.api.items.Inventory;

public class HandleBank extends BankTask {
  private final RoguesDen plugin;
  private final Config config;

  @Inject
  private HandleBank(RoguesDen plugin, Config config) {
    this.plugin = plugin;
    this.config = config;

    setName("Emerald Benedict");
    setWaitTicks(30);
  }

  @Override
  public boolean validate() {
    return Utils.isInRegion(12109)
        && Players.getLocal().getWorldLocation().getY() < 4992
        && (Inventory.getFreeSlots() < 28 || !Equipment.getAll().isEmpty())
        && isLastBankDurationAgo(Duration.ofSeconds(5));
  }

  @Override
  public void execute() {
    if (NPCs.getNearest(NpcID.EMERALD_BENEDICT) == null) {
      final WorldPoint destination = new WorldPoint(3049, 4978, 1);
      SluweMovement.walk(destination, 2);
      Time.sleepTicksUntil(() -> NPCs.getNearest(NpcID.EMERALD_BENEDICT) != null, 10);
    }

    open();

    Bank.depositInventory();
    Bank.depositEquipment();

    if (config.stopAtFiveCrates()
        && Inventory.getCount(ItemID.ROGUES_EQUIPMENT_CRATE)
        + Bank.getCount(true, ItemID.ROGUES_EQUIPMENT_CRATE)
        >= 5) {
      Bank.close();
      plugin.stop("We have 5 Rogue's equipment crates. Stopping plugin.");
    }

    last = Instant.now();
  }
}
