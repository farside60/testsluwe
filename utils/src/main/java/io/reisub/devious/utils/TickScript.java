package io.reisub.devious.utils;

import io.reisub.devious.utils.api.Activity;
import io.reisub.devious.utils.api.SluweMovement;
import io.reisub.devious.utils.api.Stats;
import io.reisub.devious.utils.tasks.Task;
import io.reisub.devious.utils.tasks.randoms.CountCheck;
import io.reisub.devious.utils.tasks.randoms.FreakyForester;
import io.reisub.devious.utils.tasks.randoms.Frog;
import io.reisub.devious.utils.tasks.randoms.Genie;
import io.reisub.devious.utils.tasks.randoms.RandomTask;
import io.reisub.devious.utils.tasks.randoms.Reward;
import io.reisub.devious.utils.tasks.randoms.RickTurpentine;
import java.awt.event.KeyEvent;
import java.time.Duration;
import java.time.Instant;
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
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayManager;
import net.unethicalite.api.commons.Rand;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.game.Skills;
import net.unethicalite.api.input.Keyboard;
import net.unethicalite.api.utils.MessageUtils;
import net.unethicalite.client.Static;
import org.slf4j.Logger;

/**
 * A TickScript is a plugin that allows tasks to be registered which are validated on every game
 * tick. If a task is validated it will be scheduled and executed.
 */
@Slf4j
public abstract class TickScript extends Plugin implements KeyListener {
  /** List of tasks to be validated and executed. */
  protected final List<Task> tasks = new ArrayList<>();

  /**
   * The skills in this map will be checked whenever experience is earned. If the earned experience
   * matches the skill in this map, it will set the current activity to the value of this map entry.
   */
  protected final Map<Skill, Activity> idleCheckSkills = new HashMap<>();

  /** The executor service on which the task executions are scheduled. */
  protected ScheduledExecutorService executor;

  /** Last game tick on which the player has logged in. */
  protected int lastLoginTick = 0;

  /**
   * Last game tick on when an action happened. An action happens when the current activity is set
   * or when the player is not idle (moving or animating).
   */
  protected int lastActionTick = 0;

  /** The timeout in ticks after which we should consider the player to be idle. */
  protected int lastActionTimeout = 5;

  /** Last game tick the player received experience in a skill added in the idleCheckSkills map. */
  protected int lastExperienceTick = 0;

  /** Last game tick when the inventory has been updated. */
  protected int lastInventoryChangeTick = 0;

  /**
   * Flag to check if we should take into account inventory changes to check if the player is idle.
   */
  protected boolean idleCheckInventoryChange = false;

  @Inject private Config utilsConfig;
  @Inject private KeyManager keyManager;
  @Inject private OverlayManager overlayManager;
  @Getter private Activity currentActivity;
  @Getter private Activity previousActivity;
  @Getter private boolean running;
  @Getter @Setter private int lastHopTick;
  private ScheduledFuture<?> current;
  private ScheduledFuture<?> next;
  @Getter private Instant startTime = Instant.now();
  private Map<Skill, Integer> startingLevels;
  private Map<Skill, Integer> startingExperience;
  private Overlay overlay;

  /**
   * This listener will start or stop a script when the start/stop button is clicked.
   *
   * @param configButtonClicked the event fired when a config button is clicked
   */
  @Subscribe
  private void onConfigButtonPressed(ConfigButtonClicked configButtonClicked) {
    final String name = this.getName().replaceAll(" ", "").toLowerCase(Locale.ROOT);

    if (configButtonClicked.getGroup().equals(name)
        && configButtonClicked.getKey().equals("startButton")) {
      if (running) {
        stop();
      } else {
        start();
      }
    }
  }

  /**
   * This is executed on every tick and is the main start point of the script. Every tick a call to
   * the {@link TickScript#tick} is scheduled as a ScheduledFuture task as either the current task
   * (if none is available) or the next task. This allows the script to run smooth and without
   * skipping any ticks.
   *
   * <p>It will also perform a check to see if the player is idle but has a non-idle activity. If
   * this is the case, it will set the activity to idle.
   *
   * <p>Lastly, it performs a check to see if the player is about to log out due to inactivity and
   * makes sure that doesn't happen.
   *
   * @param gameTick the event fired when a new game tick has started
   */
  @Subscribe
  private void onGameTick(GameTick gameTick) {
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

  /**
   * Sets lastExperienceTick to the current game tick count when the skill that has changed is found
   * in the idleCheckSkills map.
   *
   * @param statChanged event fired when a stat has changed (experience has been earned)
   */
  @Subscribe
  private void onStatChanged(StatChanged statChanged) {
    if (!isRunning() || !Utils.isLoggedIn()) {
      return;
    }

    for (Skill skill : idleCheckSkills.keySet()) {
      if (statChanged.getSkill() == skill) {
        setActivity(idleCheckSkills.get(skill));
        lastExperienceTick = Static.getClient().getTickCount();
      }
    }
  }

  /**
   * Sets lastInventoryChangeTick to the current game tick count when the inventory has changed and
   * idleCheckInventoryChange is set to true.
   *
   * @param itemContainerChanged event fired when an item container has changed
   */
  @Subscribe
  private void onItemContainerChanged(ItemContainerChanged itemContainerChanged) {
    if (!isRunning() || !Utils.isLoggedIn()) {
      return;
    }

    if (itemContainerChanged.getItemContainer()
        != Static.getClient().getItemContainer(InventoryID.INVENTORY)) {
      return;
    }

    if (idleCheckInventoryChange) {
      lastInventoryChangeTick = Static.getClient().getTickCount();
    }
  }

  /**
   * Set lastLoginTick to the current game tick count on login.
   *
   * @param gameStateChanged evented fired when the game state has changed
   */
  @Subscribe
  private void onGameStateChanged(GameStateChanged gameStateChanged) {
    if (gameStateChanged.getGameState() == GameState.LOGGED_IN) {
      lastLoginTick = Static.getClient().getTickCount();
    }
  }

  /**
   * Return the logger. We use this method so the correct logger object is used in plugins extending
   * this script. If we don't use this, we don't get the correct class name in the logs.
   *
   * @return the logger object
   */
  public Logger getLogger() {
    return log;
  }

  /**
   * Set the current activity to previousActivity and the passed in activity to currentActivity. It
   * will not overwrite the previous activity if the passed activity is IDLE and the current is
   * already IDLE as well. If the passed activity is not IDLE, it will also set the lastActionTick
   * to the current game tick count.
   *
   * @param activity the new current activity
   */
  public void setActivity(Activity activity) {
    if (activity == Activity.IDLE && currentActivity != Activity.IDLE) {
      previousActivity = currentActivity;
      getLogger().debug("Setting previous activity: {}", previousActivity);
    }

    currentActivity = activity;
    getLogger().debug("Setting current activity: {}", currentActivity);

    if (activity != Activity.IDLE) {
      lastActionTick = Static.getClient().getTickCount();
    }
  }

  /**
   * Check if the current activity is the same as the given one.
   *
   * @param activity the activity to check
   * @return true if the given activity is the current activity
   */
  public final boolean isCurrentActivity(Activity activity) {
    return currentActivity == activity;
  }

  /**
   * Check if the previous activity is the same as the given one.
   *
   * @param activity the activity to check
   * @return true if the given activity was the previous activity
   */
  public final boolean wasPreviousActivity(Activity activity) {
    return previousActivity == activity;
  }

  /**
   * Check if the player has been logged in for more than the given amount of game ticks.
   *
   * @param ticks number of game ticks since last login
   * @return return true if logged in longer than the given amount of game ticks
   */
  public final boolean isLoggedInForLongerThan(int ticks) {
    return Static.getClient().getTickCount() - lastLoginTick > ticks;
  }

  /** Creates a single thread scheduled executor and register the plugin to the key manager. */
  @Override
  protected final void startUp() {
    executor = Executors.newSingleThreadScheduledExecutor();

    Static.getKeyManager().registerKeyListener(this);
  }

  /** Cleans up the executor and unregisters the plugin from the key manager. */
  @Override
  protected final void shutDown() {
    stop();
    executor.shutdownNow();

    Static.getKeyManager().unregisterKeyListener(this);
  }

  /**
   * Starts the plugin by setting the running field to true and resets the previous and current
   * activity, the start time, and the overlay if it's set. Then it calls onStart() which is the
   * entry point for the actual plugin.
   */
  public final void start() {
    getLogger().info("Starting {}", this.getName());
    running = true;

    previousActivity = Activity.IDLE;
    currentActivity = Activity.IDLE;

    startTime = Instant.now();

    if (overlay != null) {
      overlayManager.add(overlay);
    }

    onStart();
  }

  /**
   * Prints a message in the game's chat box and starts the plugin.
   *
   * @param message the message to print in the chat box
   */
  public final void start(String message) {
    MessageUtils.addMessage(message);
    start();
  }

  /**
   * Stops the plugin by setting the running field to false. It unregisters all the tasks from the
   * event bus, clears the tasks and resets the current activity, previous activity, start time and
   * overlay. It also interrupts any movement that was in progress using SluweMovement.
   * Then it calls onStop() so the plugin can do any additional cleanup.
   */
  public final void stop() {
    getLogger().info("Stopping {}", this.getName());
    running = false;

    for (Task task : tasks) {
      Static.getEventBus().unregister(task);
    }

    tasks.clear();

    previousActivity = Activity.IDLE;
    currentActivity = Activity.IDLE;

    startTime = null;

    if (overlay != null) {
      overlayManager.remove(overlay);
    }

    SluweMovement.interrupted = true;

    onStop();
  }

  /**
   * Prints a message in the game's chat box and stops the plugin.
   *
   * @param message the message to print in the chat box
   */
  public final void stop(String message) {
    MessageUtils.addMessage(message);
    stop();
  }

  /**
   * The entry point for a plugin. Use this for adding your tasks and doing any initial setup you'd
   * like to do.
   */
  protected void onStart() {}

  /** The exit point for a plugin. Use this for any additional clean up you'd like to do. */
  protected void onStop() {}

  /**
   * Adds a task to the tasks list and registers it to the event bus.
   *
   * @param task the task to add to the tasks list
   */
  protected final void addTask(Task task) {
    Static.getEventBus().register(task);

    tasks.add(task);
  }

  /**
   * Helper method to automatically use the injector to create an instance of the given class. After
   * instantiation, it will add the task using {@link TickScript#addTask(Task)}
   *
   * @param type the class of the task
   * @param <T> class type which should extend Task
   */
  protected final <T extends Task> void addTask(Class<T> type) {
    addTask(injector.getInstance(type));
  }

  /**
   * Adds all random tasks which have been enabled in the configuration to the task list and
   * registers them to the event bus.
   */
  protected final void addRandomTasks() {
    addRandomTask(Reward.class);
    addRandomTask(CountCheck.class);
    addRandomTask(Genie.class);
    addRandomTask(Frog.class);
    addRandomTask(RickTurpentine.class);
    addRandomTask(FreakyForester.class);
  }

  /**
   * Adds a random task to the tasks list and registers it to the event bus if it's enabled in the
   * configuration.
   *
   * @param type class type extending RandomTask
   */
  protected final <T extends RandomTask> void addRandomTask(Class<T> type) {
    RandomTask task = injector.getInstance(type);

    if (task.isEnabled()) {
      addTask(task);
    }
  }

  /**
   * Add the overlay to the overlay manager.
   *
   * @param overlay the overlay to add to the overlay manager
   */
  protected final void setOverlay(Overlay overlay) {
    this.overlay = overlay;
    overlayManager.add(overlay);
  }

  /**
   * Get the current run time in a string formatted by {@link Stats#getFormattedDuration(Duration)}.
   *
   * @return the run time in a human-readable format
   */
  public final String getTimeRunning() {
    return startTime != null ? Stats.getFormattedDurationBetween(startTime, Instant.now()) : "";
  }

  /**
   * Tracks the experience earned of any given skills during the running of this plugin.
   *
   * @param skills the skills to track
   */
  protected final void trackExperience(Skill... skills) {
    startingLevels = new HashMap<>();
    startingExperience = new HashMap<>();

    for (Skill skill : skills) {
      startingLevels.put(skill, Skills.getLevel(skill));
      startingExperience.put(skill, Skills.getExperience(skill));
    }
  }

  /**
   * Get the levels gained while running this plugin.
   *
   * @param skill the skill of which to get the gained levels
   * @return the amount of levels gained while running this plugin
   */
  public final int getLevelsGained(Skill skill) {
    if (startingLevels == null) {
      return -1;
    }

    return Skills.getLevel(skill) - startingLevels.get(skill);
  }

  /**
   * Get the experience gained while running this plugin.
   *
   * @param skill the skill of which to get the gained experience
   * @return the amount of experience points gained while running this plugin
   */
  public final int getExperienceGained(Skill skill) {
    if (startingExperience == null) {
      return -1;
    }

    return Skills.getExperience(skill) - startingExperience.get(skill);
  }

  /**
   * Checks if the player is idle but still has a current activity different from IDLE. This method
   * makes sure that a plugin using activities does not get stuck when a player becomes idle but the
   * plugin doesn't correctly change the activity to IDLE.
   */
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

  /**
   * Using packets, the client's idle timer never gets reset. We check for this here and send a
   * backspace to reset the idle timer ourselves.
   */
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

  /**
   * Loops through the tasks list, validates every task and executes it if validation returns true.
   * It will break whenever a task validates to true meaning that it will only execute one task per
   * tick. If the task has an activity other than IDLE assigned to it, it will set the current
   * activity to that activity.
   */
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

  /**
   * Schedule a ScheduledFuture task on the executor with a random delay as configured in the Utils
   * configuration panel.
   *
   * @param runnable contains a call to {@link TickScript#tick()}
   * @return a ScheduledFuture representing pending completion of the task and whose get() method
   *     will return null upon completion
   */
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
