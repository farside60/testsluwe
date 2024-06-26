package io.reisub.devious.farming;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import io.reisub.devious.farming.tasks.Clear;
import io.reisub.devious.farming.tasks.Cure;
import io.reisub.devious.farming.tasks.DepositTools;
import io.reisub.devious.farming.tasks.GoToPatch;
import io.reisub.devious.farming.tasks.HandleBank;
import io.reisub.devious.farming.tasks.Note;
import io.reisub.devious.farming.tasks.PickHerb;
import io.reisub.devious.farming.tasks.PickLimpwurt;
import io.reisub.devious.farming.tasks.PlantHerb;
import io.reisub.devious.farming.tasks.PlantLimpwurt;
import io.reisub.devious.farming.tasks.WithdrawCompost;
import io.reisub.devious.farming.tasks.WithdrawTools;
import io.reisub.devious.utils.Constants;
import io.reisub.devious.utils.TickScript;
import io.reisub.devious.utils.Utils;
import io.reisub.devious.utils.api.ConfigList;
import io.reisub.devious.utils.api.SluwePredicates;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.runelite.api.MenuAction;
import net.runelite.api.NPC;
import net.runelite.api.TileObject;
import net.runelite.api.Varbits;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.util.Text;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyListener;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.timetracking.farming.CropState;
import net.runelite.client.plugins.timetracking.farming.Produce;
import net.unethicalite.api.commons.Predicates;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.game.GameThread;
import net.unethicalite.api.game.Vars;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Movement;
import net.unethicalite.api.movement.Reachable;
import net.unethicalite.client.Static;
import org.pf4j.Extension;
import org.slf4j.Logger;

@Extension
@PluginDependency(Utils.class)
@PluginDescriptor(
    name = "Sluwe Farming",
    description = "It's not much but it's honest work",
    enabledByDefault = false)
@Slf4j
public class Farming extends TickScript implements KeyListener {
  public static boolean catherbyThroughHouse;
  private static final Set<Integer> ITEM_OPCODES = ImmutableSet.of(1007, 25, 57);
  private static final int INVENTORY_WIDGET_ID = 9764864;
  private static final int FARMING_GUILD_REGION = 4922;
  @Getter private final Queue<Location> locationQueue = new LinkedList<>();
  @Inject private Config config;
  @Getter private Location currentLocation;
  private ConfigList compostProduceConfigList;
  private boolean harvestAndCompost;

  @Provides
  public Config getConfig(ConfigManager configManager) {
    return configManager.getConfig(Config.class);
  }

  @Override
  public Logger getLogger() {
    return log;
  }

  @Override
  protected void onStart() {
    catherbyThroughHouse = config.catherbyThroughHouse();

    buildLocationQueue();

    addTask(HandleBank.class);
    addTask(GoToPatch.class);
    addTask(WithdrawTools.class);
    addTask(WithdrawCompost.class);
    addTask(Cure.class);
    addTask(Clear.class);
    addTask(PickLimpwurt.class);
    addTask(PlantLimpwurt.class);
    addTask(PickHerb.class);
    addTask(PlantHerb.class);
    addTask(Note.class);
    addTask(DepositTools.class);
  }

  @Override
  protected void onStop() {
    for (Location location : Location.values()) {
      location.setSkip(false);
    }

    locationQueue.clear();
    currentLocation = null;
  }

  @Subscribe
  private void onGameTick(GameTick event) {
    if (harvestAndCompost) {
      harvestAndCompost = false;
      schedule(this::harvestAndCompost);
    }

    if (compostProduceConfigList == null) {
      compostProduceConfigList = ConfigList.parseList(config.oneClickCompostProduce());
    }

    if (locationQueue.isEmpty() || !isRunning()) {
      return;
    }

    if (currentLocation == null || isCurrentLocationDone()) {
      while (!locationQueue.isEmpty()) {
        Location location = locationQueue.poll();

        if (location != null) {
          currentLocation = location;
          break;
        }
      }
    }
  }

  @Subscribe
  private void onMenuEntryAdded(MenuEntryAdded event) {
    if (!config.oneClickMode()
        || !ITEM_OPCODES.contains(event.getType())
        || event.getActionParam1() != INVENTORY_WIDGET_ID
        || event.getIdentifier() != 0) {
      return;
    }

    final Item item = Inventory.getItem(event.getActionParam0());

    if (item == null) {
      return;
    }

    final int itemId = item.getId();

    final CropState compostBinState = getCompostBinState();

    if (OneClick.ONE_CLICK_GAME_OBJECTS_MAP.containsKey(itemId)) {
      if (TileObjects.getNearest(Predicates.ids(OneClick.ONE_CLICK_GAME_OBJECTS_MAP.get(itemId)))
          == null) {
        return;
      }
    } else if (OneClick.ONE_CLICK_ITEMS_MAP.containsKey(itemId)) {
      if (!Inventory.contains(Predicates.ids(OneClick.ONE_CLICK_ITEMS_MAP.get(itemId)))) {
        return;
      }
    } else if (OneClick.ONE_CLICK_NPCS_MAP.containsKey(itemId)) {
      if (config.oneClickNote()
          && NPCs.getNearest(Predicates.ids(OneClick.ONE_CLICK_NPCS_MAP.get(itemId))) == null) {
        return;
      }
    } else if (itemId == ItemID.VOLCANIC_ASH) {
      if (compostBinState != CropState.HARVESTABLE) {
        return;
      }
    } else {
      String name = Text.removeTags(event.getTarget());

      if (!compostProduceConfigList.getStrings().containsKey(name)
          || compostBinState == null
          || compostBinState == CropState.GROWING
          || compostBinState == CropState.HARVESTABLE) {
        return;
      }
    }

    Static.getClient()
        .insertMenuItem(
            OneClick.ONE_CLICK_FARMING,
            "",
            MenuAction.UNKNOWN.getId(),
            event.getIdentifier(),
            event.getActionParam0(),
            event.getActionParam1(),
            true);

    Static.getClient()
        .setTempMenuEntry(
            Arrays.stream(Static.getClient().getMenuEntries())
                .filter(x -> x.getOption().equals(OneClick.ONE_CLICK_FARMING))
                .findFirst()
                .orElse(null));
  }

  @Subscribe
  private void onMenuOptionClicked(MenuOptionClicked event) {
    if (!event.getMenuOption().equals(OneClick.ONE_CLICK_FARMING)) {
      return;
    }

    final Item item = Inventory.getItem(event.getParam0());
    if (item == null) {
      return;
    }

    final int itemId = item.getId();

    final CropState compostBinState = getCompostBinState();

    if (OneClick.ONE_CLICK_GAME_OBJECTS_MAP.containsKey(itemId)) {
      TileObject nearest = null;

      if (Constants.COMPOST_IDS.contains(itemId)) {
        nearest =
            TileObjects.getNearest(
                o ->
                    OneClick.ONE_CLICK_GAME_OBJECTS_MAP.get(itemId).contains(o.getId())
                        && Reachable.getInteractable(o)
                            .contains(Players.getLocal().getWorldLocation()));
      }

      if (nearest == null) {
        nearest =
            TileObjects.getNearest(Predicates.ids(OneClick.ONE_CLICK_GAME_OBJECTS_MAP.get(itemId)));
      }

      if (nearest == null) {
        return;
      }

      item.useOn(nearest);
    } else if (OneClick.ONE_CLICK_ITEMS_MAP.containsKey(itemId)) {
      final Item other =
          Inventory.getFirst(Predicates.ids(OneClick.ONE_CLICK_ITEMS_MAP.get(itemId)));
      if (other == null) {
        return;
      }

      GameThread.invoke(() -> item.useOn(other));
    } else if (itemId == ItemID.VOLCANIC_ASH && compostBinState == CropState.HARVESTABLE) {
      final TileObject bin = TileObjects.getNearest(Predicates.ids(Constants.COMPOST_BIN_IDS));
      if (bin == null) {
        return;
      }

      GameThread.invoke(() -> item.useOn(bin));
    } else if (compostProduceConfigList.getStrings().containsKey(item.getName())
        && (compostBinState == CropState.EMPTY || compostBinState == CropState.FILLING)) {
      final TileObject bin = TileObjects.getNearest(Predicates.ids(Constants.COMPOST_BIN_IDS));
      if (bin == null) {
        return;
      }

      GameThread.invoke(() -> item.useOn(bin));
    } else if (config.oneClickNote() && OneClick.ONE_CLICK_NPCS_MAP.containsKey(itemId)) {
      final NPC nearest = NPCs.getNearest(Predicates.ids(OneClick.ONE_CLICK_NPCS_MAP.get(itemId)));
      if (nearest == null) {
        return;
      }

      GameThread.invoke(() -> item.useOn(nearest));
    }
  }

  @Subscribe
  private void onConfigChanged(ConfigChanged event) {
    if (!event.getGroup().equals("sluwefarming")) {
      return;
    }

    if (event.getKey().equals("oneClickCompostProduce")) {
      compostProduceConfigList = ConfigList.parseList(config.oneClickCompostProduce());
    }
  }

  @Override
  public void keyTyped(KeyEvent e) {}

  @Override
  public void keyPressed(KeyEvent e) {
    if (config.farmingHotkey().matches(e)) {
      e.consume();
      start();
    } else if (config.harvestAndCompostHotkey().matches(e)) {
      e.consume();
      harvestAndCompost = true;
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {}

  private void harvestAndCompost() {
    final TileObject patch = TileObjects.getNearest(Predicates.ids(Constants.ALLOTMENT_PATCH_IDS));
    final TileObject compostBin = TileObjects.getNearest(Predicates.ids(Constants.COMPOST_BIN_IDS));
    Item compostable = Inventory.getFirst(SluwePredicates.itemConfigList(compostProduceConfigList));

    if (patch == null || compostBin == null) {
      return;
    }

    if (compostable == null) {
      patch.interact("Harvest");
      Time.sleepTicksUntil(
          () -> Inventory.contains(SluwePredicates.itemConfigList(compostProduceConfigList)), 10);

      Movement.walk(Players.getLocal());

      compostable = Inventory.getFirst(SluwePredicates.itemConfigList(compostProduceConfigList));
    }

    patch.interact("Harvest");
    Time.sleepTick();
    compostable.useOn(compostBin);
  }

  private CropState getCompostBinState() {
    PatchState patchState;

    if (Utils.isInRegion(FARMING_GUILD_REGION)) {
      int varbit = Vars.getBit(Varbits.FARMING_7912);
      patchState = PatchImplementation.GIANT_COMPOST.forVarbitValue(varbit);
    } else {
      int varbit = Vars.getBit(Varbits.FARMING_4775);
      patchState = PatchImplementation.COMPOST.forVarbitValue(varbit);
    }

    if (patchState == null) {
      return null;
    } else {
      return patchState.getCropState();
    }
  }

  public boolean isCurrentLocationDone() {
    if (!Utils.isInRegion(currentLocation.getRegionId())) {
      return false;
    }

    final int herbVarbitValue = Vars.getBit(currentLocation.getHerbVarbit());
    final PatchState herbPatchState = PatchImplementation.HERB.forVarbitValue(herbVarbitValue);

    final boolean herbReady =
        herbPatchState != null
            && herbPatchState.getProduce() != Produce.WEEDS
            && herbPatchState.getCropState() == CropState.GROWING;

    final boolean herbDiseased =
        herbPatchState != null
            && herbPatchState.getProduce() != Produce.WEEDS
            && herbPatchState.getCropState() == CropState.DISEASED;

    boolean flowerReady;
    boolean flowerDiseased = false;

    if (currentLocation.hasFlowerPatch() && config.limpwurt()) {
      final int flowerVarbitValue = Vars.getBit(currentLocation.getFlowerVarbit());
      final PatchState flowerPatchState =
          PatchImplementation.FLOWER.forVarbitValue(flowerVarbitValue);

      flowerReady =
          flowerPatchState != null
              && flowerPatchState.getProduce() != Produce.WEEDS
              && flowerPatchState.getCropState() == CropState.GROWING;

      flowerDiseased =
          flowerPatchState != null
              && flowerPatchState.getProduce() != Produce.WEEDS
              && flowerPatchState.getCropState() == CropState.DISEASED;
    } else {
      flowerReady = true;
    }

    return (herbReady || (herbDiseased && currentLocation.isSkip()))
        && (flowerReady || (flowerDiseased && currentLocation.isSkip()))
        && !Inventory.contains(Predicates.ids(Constants.GRIMY_HERB_IDS))
        && !Inventory.contains(Predicates.ids(Constants.CLEAN_HERB_IDS))
        && !Inventory.contains(ItemID.LIMPWURT_ROOT);
  }

  private void buildLocationQueue() {
    ConfigList herbOrder = ConfigList.parseList(config.herbOrder());

    for (String name : herbOrder.getStrings().keySet()) {
      for (Location location : Location.values()) {
        if (location.isEnabled(config) && name.equalsIgnoreCase(location.getName())) {
          locationQueue.add(location);
          break;
        }
      }
    }

    for (Location location : Location.values()) {
      if (location.isEnabled(config) && !locationQueue.contains(location)) {
        locationQueue.add(location);
      }
    }
  }
}
