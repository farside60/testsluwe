package io.reisub.devious.tempoross.tasks;

import io.reisub.devious.tempoross.Tempoross;
import io.reisub.devious.utils.tasks.BankTask;
import java.time.Duration;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.unethicalite.api.items.Bank;
import net.unethicalite.api.items.Equipment;
import net.unethicalite.api.items.Inventory;

public class HandleBank extends BankTask {
  @Inject private Tempoross plugin;

  private int bucketCount;

  @Override
  public boolean validate() {
    if (!plugin.isInDesert()) {
      return false;
    }

    bucketCount = Inventory.getCount(ItemID.BUCKET, ItemID.BUCKET_OF_WATER);

    return isLastBankDurationAgo(Duration.ofSeconds(5))
        && (isRopeMissing() || isHammerMissing() || areBucketsMissing());
  }

  @Override
  public void execute() {
    if (!open()) {
      return;
    }

    if (areBucketsMissing()) {
      Bank.withdraw(ItemID.BUCKET, 4 - bucketCount, Bank.WithdrawMode.ITEM);
    }

    if (isHammerMissing()) {
      if (Bank.contains(ItemID.IMCANDO_HAMMER)) {
        Bank.withdraw(ItemID.IMCANDO_HAMMER, 1, Bank.WithdrawMode.ITEM);
      } else {
        Bank.withdraw(ItemID.HAMMER, 1, Bank.WithdrawMode.ITEM);
      }
    }

    if (isRopeMissing()) {
      Bank.withdraw(ItemID.ROPE, 1, Bank.WithdrawMode.ITEM);
    }

    Bank.close();
  }

  private boolean isRopeMissing() {
    return !Inventory.contains(ItemID.ROPE)
        && (!Equipment.contains(ItemID.SPIRIT_ANGLER_BOOTS)
            || !Equipment.contains(ItemID.SPIRIT_ANGLER_HEADBAND)
            || !Equipment.contains(ItemID.SPIRIT_ANGLER_TOP)
            || !Equipment.contains(ItemID.SPIRIT_ANGLER_WADERS));
  }

  private boolean isHammerMissing() {
    return !Inventory.contains(ItemID.HAMMER, ItemID.IMCANDO_HAMMER)
        && !Equipment.contains(ItemID.IMCANDO_HAMMER);
  }

  private boolean areBucketsMissing() {
    bucketCount = Inventory.getCount(ItemID.BUCKET, ItemID.BUCKET_OF_WATER);

    return bucketCount < 4;
  }
}
