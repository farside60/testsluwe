package io.reisub.devious.utils;

import io.reisub.devious.utils.api.Activity;
import io.reisub.devious.utils.api.SluweMovement;
import io.reisub.devious.utils.tasks.Task;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.Skill;
import net.runelite.api.events.ConfigButtonClicked;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.StatChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.unethicalite.api.commons.Rand;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.input.Keyboard;
import net.unethicalite.api.utils.MessageUtils;
import net.unethicalite.client.Static;
import org.slf4j.Logger;

@Slf4j
public abstract class TickScript extends Plugin implements KeyListener {
  protected final List<Task> tasks = new ArrayList<>();
  protected final Map<Skill, Activity> idleCheckSkills = new HashMap<>();
  protected ScheduledExecutorService executor;
  protected int lastLoginTick = 0;
  protected int lastActionTick = 0;
  protected int lastActionTimeout = 5;
  protected int lastExperienceTick = 0;
  protected int lastInventoryChangeTick = 0;
  protected boolean idleCheckInventoryChange = false;
  @Inject private Config utilsConfig;
  @Inject private KeyManager keyManager;
  @Getter private Activity currentActivity;
  @Getter private Activity previousActivity;
  @Getter private boolean running;
  @Getter @Setter private int lastHopTick;
  private ScheduledFuture<?> current;
  private ScheduledFuture<?> next;

  @Subscribe
  private void onConfigButtonPressed(ConfigButtonClicked event) {
    String name = this.getName().replaceAll(" ", "").toLowerCase(Locale.ROOT);

    if (event.getGroup().equals(name) && event.getKey().equals("startButton")) {
      if (running) {
        stop();
      } else {
        start();
      }
    }
  }

  @Subscribe
  private void onGameTick(GameTick event) {
    if (!running) {
      return;
    }

    try {
      if (current == null) {
        current = schedule(this::tick);
      } else {
        if (current.isDone()) {
          if (next == null) {
            current = schedule(this::tick);
          } else {
            current = next;
            next = null;
          }
        } else {
          if (next == null) {
            next = schedule(this::tick);
          }
        }
      }

      checkActionTimeout();
      checkIdleLogout();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Subscribe
  private void onStatChanged(StatChanged event) {
    if (!isRunning() || !Utils.isLoggedIn()) {
      return;
    }

    for (Skill skill : idleCheckSkills.keySet()) {
      if (event.getSkill() == skill) {
        setActivity(idleCheckSkills.get(skill));
        lastExperienceTick = Static.getClient().getTickCount();
      }
    }
  }

  @Subscribe
  private void onItemContainerChanged(ItemContainerChanged event) {
    if (!isRunning() || !Utils.isLoggedIn()) {
      return;
    }

    if (event.getItemContainer() != Static.getClient().getItemContainer(InventoryID.INVENTORY)) {
      return;
    }

    if (idleCheckInventoryChange) {
      lastInventoryChangeTick = Static.getClient().getTickCount();
    }
  }

  @Subscribe
  private void onGameStateChanged(GameStateChanged event) {
    if (event.getGameState() == GameState.LOGGED_IN) {
      lastLoginTick = Static.getClient().getTickCount();
    }
  }

  public Logger getLogger() {
    return log;
  }

  public void setActivity(Activity activity) {
    if (activity == Activity.IDLE && currentActivity != Activity.IDLE) {
      previousActivity = currentActivity;
      getLogger().debug("Setting previous activity: " + previousActivity);
    }

    currentActivity = activity;
    getLogger().debug("Setting current activity: " + currentActivity);

    if (activity != Activity.IDLE) {
      lastActionTick = Static.getClient().getTickCount();
    }
  }

  public final boolean isCurrentActivity(Activity activity) {
    return currentActivity == activity;
  }

  public final boolean wasPreviousActivity(Activity activity) {
    return previousActivity == activity;
  }

  public final boolean isLoggedInForLongerThan(int ticks) {
    return Static.getClient().getTickCount() - lastLoginTick > ticks;
  }

  @Override
  protected final void startUp() {
    executor = Executors.newSingleThreadScheduledExecutor();

    Static.getKeyManager().registerKeyListener(this);
  }

  @Override
  protected final void shutDown() {
    stop();
    executor.shutdownNow();

    Static.getKeyManager().unregisterKeyListener(this);
  }

  public final void start() {
    getLogger().info("Starting " + this.getName());
    running = true;

    previousActivity = Activity.IDLE;
    currentActivity = Activity.IDLE;

    onStart();
  }

  public final void start(String msg) {
    MessageUtils.addMessage(msg);
    start();
  }

  public final void stop() {
    getLogger().info("Stopping " + this.getName());
    running = false;

    for (Task task : tasks) {
      Static.getEventBus().unregister(task);
    }

    tasks.clear();

    previousActivity = Activity.IDLE;
    currentActivity = Activity.IDLE;

    onStop();
  }

  public final void stop(String msg) {
    MessageUtils.addMessage(msg);
    stop();
  }

  protected void onStart() {}

  protected void onStop() {}

  protected final void addTask(Task task) {
    Static.getEventBus().register(task);

    tasks.add(task);
  }

  protected final <T extends Task> void addTask(Class<T> type) {
    addTask(injector.getInstance(type));
  }

  protected void checkActionTimeout() {
    if (currentActivity == Activity.IDLE) {
      return;
    }

    final int currentTick = Static.getClient().getTickCount();

    if (currentTick - lastExperienceTick < lastActionTimeout
        || currentTick - lastInventoryChangeTick < lastActionTimeout) {
      return;
    }

    if (!Players.getLocal().isIdle()) {
      lastActionTick = currentTick;
      return;
    }

    if (currentTick - lastActionTick >= lastActionTimeout) {
      setActivity(Activity.IDLE);
    }
  }

  private void checkIdleLogout() {
    int idleClientTicks = Static.getClient().getKeyboardIdleTicks();

    if (Static.getClient().getMouseIdleTicks() < idleClientTicks) {
      idleClientTicks = Static.getClient().getMouseIdleTicks();
    }

    if (idleClientTicks > 12500) {
      getLogger().debug("Resetting idle");

      Keyboard.type((char) KeyEvent.VK_BACK_SPACE);

      Static.getClient().setKeyboardIdleTicks(0);
      Static.getClient().setMouseIdleTicks(0);
    }
  }

  protected void tick() {
    for (Task t : tasks) {
      if (t.validate()) {
        getLogger().info(t.getStatus());
        if (t.getActivity() != Activity.IDLE) {
          setActivity(t.getActivity());
        }
        t.execute();
        break;
      }
    }
  }

  protected ScheduledFuture<?> schedule(Runnable runnable) {
    final int minDelay = Math.min(utilsConfig.minDelay(), utilsConfig.maxDelay());
    final int maxDelay = Math.max(utilsConfig.minDelay(), utilsConfig.maxDelay());

    return executor.schedule(runnable, Rand.nextInt(minDelay, maxDelay), TimeUnit.MILLISECONDS);
  }

  @Override
  public void keyTyped(KeyEvent e) {}

  @Override
  public void keyPressed(KeyEvent e) {
    if (utilsConfig.walkingInterruptHotkey().matches(e)) {
      SluweMovement.interrupted = true;
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {}
}
