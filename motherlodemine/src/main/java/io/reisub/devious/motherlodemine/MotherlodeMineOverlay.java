package io.reisub.devious.motherlodemine;

import io.reisub.devious.utils.api.Stats;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Skill;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.unethicalite.api.game.Skills;

public class MotherlodeMineOverlay extends OverlayPanel {
  private final MotherlodeMine plugin;
  private final Config config;

  @Inject
  private MotherlodeMineOverlay(MotherlodeMine plugin, Config config) {
    this.plugin = plugin;
    this.config = config;

    setPosition(OverlayPosition.BOTTOM_LEFT);
    setLayer(OverlayLayer.ABOVE_SCENE);
  }

  @Override
  public Dimension render(Graphics2D graphics) {
    if (!plugin.isRunning() || !config.enableOverlay()) {
      return super.render(graphics);
    }

    panelComponent.setPreferredSize(new Dimension(280, 0));
    panelComponent
        .getChildren()
        .add(TitleComponent.builder().text("Sluwe Motherlode Mine").color(Color.WHITE).build());

    panelComponent
        .getChildren()
        .add(
            LineComponent.builder()
                .left("Running: " + plugin.getTimeRunning())
                .leftColor(Color.YELLOW)
                .build());

    panelComponent
        .getChildren()
        .add(
            LineComponent.builder()
                .left("Pay-dirt: " + plugin.getPayDirt())
                .leftColor(Color.ORANGE)
                .right("Nuggets: " + plugin.getNuggets())
                .rightColor(Color.ORANGE)
                .build());

    panelComponent
        .getChildren()
        .add(
            LineComponent.builder()
                .left("Coal: " + plugin.getCoal())
                .leftColor(Color.YELLOW)
                .right("Gold ore: " + plugin.getGoldOre())
                .rightColor(Color.YELLOW)
                .build());

    panelComponent
        .getChildren()
        .add(
            LineComponent.builder()
                .left("Mithril ore: " + plugin.getMithrilOre())
                .leftColor(Color.ORANGE)
                .right("Adamantite ore: " + plugin.getAdamantiteOre())
                .rightColor(Color.ORANGE)
                .build());

    panelComponent
        .getChildren()
        .add(
            LineComponent.builder()
                .left("Runite ore: " + plugin.getRuniteOre())
                .leftColor(Color.YELLOW)
                .right("Struts fixed: " + plugin.getStrutsFixed())
                .rightColor(Color.YELLOW)
                .build());

    panelComponent
        .getChildren()
        .add(
            LineComponent.builder()
                .left("Sapphires: " + plugin.getSapphires())
                .leftColor(Color.ORANGE)
                .right("Emeralds: " + plugin.getEmeralds())
                .rightColor(Color.ORANGE)
                .build());

    panelComponent
        .getChildren()
        .add(
            LineComponent.builder()
                .left("Rubies: " + plugin.getRubies())
                .leftColor(Color.YELLOW)
                .right("Diamonds: " + plugin.getDiamonds())
                .rightColor(Color.YELLOW)
                .build());

    int miningExperience = plugin.getExperienceGained(Skill.MINING);

    panelComponent
        .getChildren()
        .add(
            LineComponent.builder()
                .left(
                    "Mining: "
                        + Stats.getFormattedExperience(miningExperience)
                        + " ("
                        + Stats.getExperiencePerHourInThousands(
                            miningExperience, plugin.getStartTime())
                        + "k/h)")
                .leftColor(Color.ORANGE)
                .right(
                    Skills.getLevel(Skill.MINING)
                        + " ("
                        + plugin.getLevelsGained(Skill.MINING)
                        + ")")
                .rightColor(Color.ORANGE)
                .build());

    return super.render(graphics);
  }
}
