package io.reisub.sluwe.fletching.tasks;

import io.reisub.devious.utils.Utils;
import io.reisub.devious.utils.api.SluweBank;
import io.reisub.devious.utils.tasks.BankTask;
import io.reisub.sluwe.fletching.Config;
import io.reisub.sluwe.fletching.Fletching;
import io.reisub.sluwe.fletching.data.Product;
import java.time.Duration;
import java.time.Instant;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.unethicalite.api.items.Bank;
import net.unethicalite.api.items.Bank.WithdrawMode;
import net.unethicalite.api.items.Inventory;

public class HandleBank extends BankTask {
  private final Fletching plugin;
  private final Config config;
  private int logsWithdrawn;

  @Inject
  private HandleBank(Fletching plugin, Config config) {
    this.plugin = plugin;
    this.config = config;

    setOpenMainTab(true);
  }

  @Override
  public boolean validate() {
    if (!isLastBankDurationAgo(Duration.ofSeconds(5))) {
      return false;
    }

    if (Utils.isInRegion(Fletching.FOSSIL_ISLAND_SEAWEED_REGION)) {
      return false;
    }

    // stop if we reached the max amount of logs specified in the config, unless we're making arrows
    if (config.amountOfLogsToUse() != 0
        && logsWithdrawn >= config.amountOfLogsToUse()
        && config.product() != Product.ARROWS) {
      plugin.stop("Used " + config.amountOfLogsToUse() + " logs. Stopping plugin.");
    }

    switch (config.product()) {
      case HEADLESS_ARROWS:
        // bank if we need feathers or arrow shafts
        return !Inventory.contains(ItemID.FEATHER) || !Inventory.contains(ItemID.ARROW_SHAFT);
      case BOLTS:
        // bank if we need feathers or unfinished bolts
        return !Inventory.contains(ItemID.FEATHER)
            || !Inventory.contains(config.metalType().getUnfinishedBoltsId());
      case DARTS:
        // bank if we need feathers or unfinished darts
        return !Inventory.contains(ItemID.FEATHER)
            || !Inventory.contains(config.metalType().getUnfinishedDartsId());
      case ARROWS:
        // don't bank if we have a knife and logs
        if (Inventory.contains(config.logType().getId()) && Inventory.contains(ItemID.KNIFE)) {
          return false;
        }

        // bank if we don't have arrow heads
        if (!Inventory.contains(config.metalType().getArrowheadId())) {
          return true;
        }

        // bank if we don't have arrow shafts or feathers or if we don't have headless arrows
        return (!Inventory.contains(ItemID.ARROW_SHAFT) || !Inventory.contains(ItemID.FEATHER))
            && !Inventory.contains(ItemID.HEADLESS_ARROW);
      case BOLTS_TIPPED:
        // bank if we need bolts or tips
        return (!Inventory.contains(config.gemType().getBoltId())
                && !Inventory.contains(ItemID.DRAGON_BOLTS))
            || !Inventory.contains(config.gemType().getTipId());
      case SHORTBOW:
      case LONGBOW:
        // don't bank if we have a knife and logs
        if (Inventory.contains(config.logType().getId()) && Inventory.contains(ItemID.KNIFE)) {
          return false;
        }

        // bank if we need bow strings or unfinished bows
        return !Inventory.contains(ItemID.BOW_STRING)
            || !Inventory.contains(
                config.logType().getShortbowUnfinishedId(),
                config.logType().getLongbowUnfinishedId());
      case ARROW_SHAFTS:
      case SHORTBOW_U:
      case LONGBOW_U:
        // bank if we need logs or a knife
        return !Inventory.contains(config.logType().getId()) || !Inventory.contains(ItemID.KNIFE);
      default:
        return false;
    }
  }

  @Override
  public void execute() {
    open();

    switch (config.product()) {
      case HEADLESS_ARROWS:
      case BOLTS:
      case DARTS:
        Bank.depositInventory();

        checkMaterials(ItemID.FEATHER);
        Bank.withdrawAll(ItemID.FEATHER, WithdrawMode.ITEM);

        if (config.product() == Product.HEADLESS_ARROWS) {
          checkMaterials(ItemID.ARROW_SHAFT);
          Bank.withdrawAll(ItemID.ARROW_SHAFT, WithdrawMode.ITEM);
        } else if (config.product() == Product.BOLTS) {
          checkMaterials(config.metalType().getUnfinishedBoltsId());
          Bank.withdrawAll(config.metalType().getUnfinishedBoltsId(), WithdrawMode.ITEM);
        } else if (config.product() == Product.DARTS) {
          checkMaterials(config.metalType().getUnfinishedDartsId());
          Bank.withdrawAll(config.metalType().getUnfinishedDartsId(), WithdrawMode.ITEM);
        }
        break;
      case SHORTBOW:
      case LONGBOW:
        SluweBank.depositAllExcept(false, ItemID.KNIFE);

        final int unfinishedId =
            config.product() == Product.SHORTBOW
                ? config.logType().getShortbowUnfinishedId()
                : config.logType().getLongbowUnfinishedId();

        if (Bank.contains(unfinishedId)) {
          final int toWithdraw =
              config.farmSeaweedSpores()
                      && Utils.isInRegion(Fletching.FOSSIL_ISLAND_SMALL_ISLAND_REGION)
                  ? 13
                  : 14;

          Bank.withdraw(unfinishedId, toWithdraw, WithdrawMode.ITEM);
          Bank.withdraw(ItemID.BOW_STRING, toWithdraw, WithdrawMode.ITEM);
        } else {
          if (!Inventory.contains(ItemID.KNIFE)) {
            Bank.withdraw(ItemID.KNIFE, 1, WithdrawMode.ITEM);
          }

          checkMaterials(config.logType().getId());
          withdrawLogs(13);
          checkMaterials(ItemID.BOW_STRING);
          Bank.withdraw(ItemID.BOW_STRING, 13, WithdrawMode.ITEM);
        }
        break;
      case ARROW_SHAFTS:
      case SHORTBOW_U:
      case LONGBOW_U:
        SluweBank.depositAllExcept(false, ItemID.KNIFE, ItemID.SEAWEED_SPORE);

        if (!Inventory.contains(ItemID.KNIFE)) {
          Bank.withdraw(ItemID.KNIFE, 1, WithdrawMode.ITEM);
        }

        checkMaterials(config.logType().getId());
        withdrawLogs();
        break;
      case ARROWS:
        if ((config.amountOfLogsToUse() == -1 || logsWithdrawn < config.amountOfLogsToUse())
            && Bank.contains(config.logType().getId())) {
          SluweBank.depositAllExcept(false, ItemID.KNIFE);

          if (!Inventory.contains(ItemID.KNIFE)) {
            Bank.withdraw(ItemID.KNIFE, 1, WithdrawMode.ITEM);
          }

          withdrawLogs();
        } else {
          Bank.depositInventory();

          Bank.withdrawAll(ItemID.HEADLESS_ARROW, WithdrawMode.ITEM);
          Bank.withdrawAll(ItemID.ARROW_SHAFT, WithdrawMode.ITEM);
          Bank.withdrawAll(ItemID.FEATHER, WithdrawMode.ITEM);
          Bank.withdrawAll(config.metalType().getArrowheadId(), WithdrawMode.ITEM);
        }
        break;
      case BOLTS_TIPPED:
        Bank.depositInventory();

        if (config.useDragonBolts()) {
          checkMaterials(ItemID.DRAGON_BOLTS);
          Bank.withdrawAll(ItemID.DRAGON_BOLTS, WithdrawMode.ITEM);
        } else {
          checkMaterials(config.gemType().getBoltId());
          Bank.withdrawAll(config.gemType().getBoltId(), WithdrawMode.ITEM);
        }

        checkMaterials(config.gemType().getTipId());
        Bank.withdrawAll(config.gemType().getTipId(), WithdrawMode.ITEM);
        break;
      default:
    }

    last = Instant.now();
  }

  private void checkMaterials(int id) {
    if (!Bank.contains(id)) {
      plugin.stop("Out of materials. Stopping plugin.");
    }
  }

  private void withdrawLogs(int max) {
    final int toWithdraw =
        config.amountOfLogsToUse() == -1
            ? max
            : Math.min(config.amountOfLogsToUse() - logsWithdrawn, max);

    logsWithdrawn += toWithdraw;

    if (toWithdraw >= 27) {
      if (config.farmSeaweedSpores()
          && Utils.isInRegion(Fletching.FOSSIL_ISLAND_SMALL_ISLAND_REGION)
          && !Inventory.contains(ItemID.SEAWEED_SPORE)) {
        Bank.withdraw(config.logType().getId(), 26, WithdrawMode.ITEM);
      } else {
        Bank.withdrawAll(config.logType().getId(), WithdrawMode.ITEM);
      }
    } else {
      Bank.withdraw(config.logType().getId(), toWithdraw, WithdrawMode.ITEM);
    }
  }

  private void withdrawLogs() {
    withdrawLogs(Integer.MAX_VALUE);
  }
}
