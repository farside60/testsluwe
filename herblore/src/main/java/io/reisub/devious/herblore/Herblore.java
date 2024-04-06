package io.reisub.devious.herblore;

import com.google.inject.Provides;
import io.reisub.devious.herblore.tasks.Clean;
import io.reisub.devious.herblore.tasks.HandleBank;
import io.reisub.devious.herblore.tasks.MakeCoconutMilk;
import io.reisub.devious.herblore.tasks.MakePotion;
import io.reisub.devious.herblore.tasks.MakeUnfinished;
import io.reisub.devious.herblore.tasks.ProcessSecondary;
import io.reisub.devious.herblore.tasks.TarHerbs;
import io.reisub.devious.utils.TickScript;
import io.reisub.devious.utils.Utils;
import io.reisub.devious.utils.api.Activity;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemID;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.client.Static;
import org.pf4j.Extension;
import org.slf4j.Logger;

@Extension
@PluginDependency(Utils.class)
@PluginDescriptor(
    name = "Sluwe Herblore",
    description = "You put the lime in the coconut, you drank them both up",
    enabledByDefault = false)
@Slf4j
public class Herblore extends TickScript {
  public static final Activity CLEANING_HERBS = new Activity("Cleaning herbs");
  public static final Activity CREATING_UNFINISHED_POTIONS =
      new Activity("Creating unfinished potions");
  public static final Activity CREATING_POTIONS = new Activity("Creating potions");
  public static final Activity MAKING_COCONUT_MILK = new Activity("Making coconut milk");
  public static final Activity PROCESSING_SECONDARIES = new Activity("Processing secondaries");
  public static final Activity TARRING_HERBS = new Activity("Tarring herbs");
  @Inject @Getter private Config config;

  @Provides
  Config provideConfig(ConfigManager configManager) {
    return configManager.getConfig(Config.class);
  }

  @Override
  public Logger getLogger() {
    return log;
  }

  @Override
  protected void onStart() {
    super.onStart();

    idleCheckInventoryChange = true;

    addTask(MakeCoconutMilk.class);
    addTask(Clean.class);
    addTask(TarHerbs.class);
    addTask(MakeUnfinished.class);
    addTask(ProcessSecondary.class);
    addTask(MakePotion.class);
    addTask(HandleBank.class);
  }

  @Subscribe
  private void onItemContainerChanged(ItemContainerChanged event) {
    if (!Utils.isLoggedIn()
        || event.getItemContainer() != Static.getClient().getItemContainer(InventoryID.INVENTORY)) {
      return;
    }

    int grimyHerbs = Inventory.getCount(getGrimyHerbIds());
    int cleanHerbs = Inventory.getCount(getCleanHerbIds());
    int bases =
        getBaseIds()[0] == -1
            ? Inventory.getCount(ItemID.SUPER_ATTACK4)
            : Inventory.getCount(getBaseIds());
    int secondaries = Inventory.getCount(config.secondary().getOriginalId());
    int vials = Inventory.getCount(ItemID.VIAL);

    if (grimyHerbs == 0 && isCurrentActivity(CLEANING_HERBS)) {
      setActivity(Activity.IDLE);
    } else if (cleanHerbs == 0
        && (isCurrentActivity(CREATING_UNFINISHED_POTIONS)
            || isCurrentActivity(TARRING_HERBS))) {
      setActivity(Activity.IDLE);
    } else if (bases == 0 && isCurrentActivity(CREATING_POTIONS)) {
      setActivity(Activity.IDLE);
    } else if (secondaries == 0 && isCurrentActivity(PROCESSING_SECONDARIES)) {
      setActivity(Activity.IDLE);
    } else if (vials == 0 && isCurrentActivity(MAKING_COCONUT_MILK)) {
      setActivity(Activity.IDLE);
    }
  }

  public Herb getHerb() {
    if (config.task() == HerbloreTask.MAKE_POTION) {
      return config.potion().getHerb();
    } else {
      return config.herb();
    }
  }

  public int[] getGrimyHerbIds() {
    Herb herb = getHerb();

    if (herb == null) {
      return new int[] {};
    } else if (herb == Herb.ALL) {
      return Herb.getAllGrimyIds();
    } else {
      return new int[] {herb.getGrimyId()};
    }
  }

  public int[] getCleanHerbIds() {
    Herb herb = getHerb();

    if (herb == null) {
      return new int[] {};
    } else if (herb == Herb.ALL) {
      return Herb.getAllCleanIds();
    } else {
      return new int[] {herb.getCleanId()};
    }
  }

  public int[] getGrimyTarHerbIds() {
    Herb herb = getHerb();

    switch (herb) {
      case ALL:
        return new int[] {
          Herb.GUAM_LEAF.getGrimyId(),
          Herb.MARRENTILL.getGrimyId(),
          Herb.TARROMIN.getGrimyId(),
          Herb.HARRALANDER.getGrimyId()
        };
      case GUAM_LEAF:
      case MARRENTILL:
      case TARROMIN:
      case HARRALANDER:
        return new int[] {herb.getGrimyId()};
      default:
        return new int[] {};
    }
  }

  public int[] getCleanTarHerbIds() {
    Herb herb = getHerb();

    switch (herb) {
      case ALL:
        return new int[] {
          Herb.GUAM_LEAF.getCleanId(),
          Herb.MARRENTILL.getCleanId(),
          Herb.TARROMIN.getCleanId(),
          Herb.HARRALANDER.getCleanId()
        };
      case GUAM_LEAF:
      case MARRENTILL:
      case TARROMIN:
      case HARRALANDER:
        return new int[] {herb.getCleanId()};
      default:
        return new int[] {};
    }
  }

  public int[] getBaseSecondaryIds() {
    if (config.secondary() == Secondary.ALL) {
      int[] ids = new int[Secondary.values().length];

      int i = 0;

      for (Secondary secondary : Secondary.values()) {
        if (secondary == Secondary.ALL) {
          continue;
        }

        ids[i++] = secondary.getOriginalId();
      }

      return ids;
    } else {
      return new int[] {config.secondary().getOriginalId()};
    }
  }

  public int[] getBaseIds() {
    Potion potion = config.potion();

    if (potion.getHerb() == null || potion.getSecondaryId() == -1) {
      return new int[] {potion.getBaseId()};
    } else {
      return potion.getHerb().getUnfinishedIds();
    }
  }

  public int[] getSecondaryIds() {
    if (config.potion().getSecondaryId() == -1) {
      return new int[] {config.potion().getHerb().getCleanId()};
    } else {
      return new int[] {config.potion().getSecondaryId()};
    }
  }
}
