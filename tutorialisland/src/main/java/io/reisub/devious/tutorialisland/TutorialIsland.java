package io.reisub.devious.tutorialisland;

import com.google.inject.Provides;
import io.reisub.devious.autodialog.AutoDialog;
import io.reisub.devious.tutorialisland.tasks.BankGuide;
import io.reisub.devious.tutorialisland.tasks.BecomeIronman;
import io.reisub.devious.tutorialisland.tasks.CombatGuide;
import io.reisub.devious.tutorialisland.tasks.CookingGuide;
import io.reisub.devious.tutorialisland.tasks.CreateCharacter;
import io.reisub.devious.tutorialisland.tasks.GielinorGuide;
import io.reisub.devious.tutorialisland.tasks.MagicGuide;
import io.reisub.devious.tutorialisland.tasks.MiningGuide;
import io.reisub.devious.tutorialisland.tasks.PrayerGuide;
import io.reisub.devious.tutorialisland.tasks.QuestGuide;
import io.reisub.devious.tutorialisland.tasks.StopPlugin;
import io.reisub.devious.tutorialisland.tasks.SurvivalGuide;
import io.reisub.devious.utils.TickScript;
import io.reisub.devious.utils.Utils;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Item;
import net.runelite.api.NPC;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.game.Combat;
import net.unethicalite.api.game.Vars;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.widgets.Widgets;
import org.pf4j.Extension;
import org.slf4j.Logger;

@Extension
@PluginDependency(AutoDialog.class)
@PluginDependency(Utils.class)
@PluginDescriptor(
    name = "Sluwe Tutorial Island",
    description = "We already know how to play the game",
    enabledByDefault = false)
@Slf4j
public class TutorialIsland extends TickScript {
  @Inject private Config config;
  @Getter @Setter private boolean ironman;

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
    addTask(StopPlugin.class);
    addTask(BecomeIronman.class);
    addTask(CreateCharacter.class);
    addTask(GielinorGuide.class);
    addTask(SurvivalGuide.class);
    addTask(CookingGuide.class);
    addTask(QuestGuide.class);
    addTask(MiningGuide.class);
    addTask(CombatGuide.class);
    addTask(BankGuide.class);
    addTask(PrayerGuide.class);
    addTask(MagicGuide.class);
  }

  public static int getProgressVarp() {
    return Vars.getVarp(281);
  }

  public static boolean isProgress(int progress) {
    return getProgressVarp() == progress;
  }

  public static boolean isProgressBetween(int min, int max) {
    return getProgressVarp() >= min && getProgressVarp() <= max;
  }

  public static void sleepUntilProgressUpdate(int sleepTicks) {
    final int currentProgress = getProgressVarp();
    Time.sleepTicksUntil(() -> currentProgress != getProgressVarp(), sleepTicks);
  }

  public static void openTab(WidgetInfo widgetInfo) {
    // we sleep so we don't go too fast
    Time.sleepTick();
    Widgets.get(widgetInfo).interact(0);
    sleepUntilProgressUpdate(5);
  }

  public static void interactWidget(int groupId, int id) {
    final Widget widget = Widgets.get(groupId, id);
    if (!Widgets.isVisible(widget)) {
      return;
    }

    widget.interact(0);
    sleepUntilProgressUpdate(5);
  }

  public static void interactObject(String name, String action) {
    final TileObject object = TileObjects.getNearest(name);
    if (object == null) {
      return;
    }

    object.interact(action);
    sleepUntilProgressUpdate(30);
  }

  public static void interactNpc(String name, String action) {
    final NPC npc = action.equals("Attack") ? Combat.getAttackableNPC(name) : NPCs.getNearest(name);
    if (npc == null) {
      return;
    }

    npc.interact(action);
    TutorialIsland.sleepUntilProgressUpdate(50);
  }

  public static void useItemOnItem(String name1, String name2) {
    final Item item1 = Inventory.getFirst(name1);
    final Item item2 = Inventory.getFirst(name2);
    if (item1 == null || item2 == null) {
      return;
    }

    item1.useOn(item2);
    TutorialIsland.sleepUntilProgressUpdate(10);
  }

  public static void useItemOnObject(String itemName, String objectName) {
    final Item item = Inventory.getFirst(itemName);
    final TileObject object = TileObjects.getNearest(objectName);
    if (item == null || object == null) {
      return;
    }

    item.useOn(object);
    TutorialIsland.sleepUntilProgressUpdate(50);
  }

  public static void talkTo(String name) {
    interactNpc(name, "Talk-to");
  }

  public static void open(String name, WorldPoint location) {
    final TileObject object = TileObjects.getFirstAt(location, name);
    if (object == null) {
      return;
    }

    object.interact("Open");
    TutorialIsland.sleepUntilProgressUpdate(50);
  }

  public static void open(String name) {
    interactObject(name, "Open");
  }
}
