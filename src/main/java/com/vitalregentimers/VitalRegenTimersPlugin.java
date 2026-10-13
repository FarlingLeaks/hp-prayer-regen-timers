package com.vitalregentimers;

import com.google.inject.Provides;
import java.awt.Color;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.runelite.api.SpriteID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

@PluginDescriptor(
	name = "HP & Prayer Regen Timers",
	description = "Tick countdowns for hitpoints regeneration (with Rapid Heal and overheal decay) and Prayer regeneration potions",
	tags = {"hitpoints", "hp", "prayer", "regen", "regeneration", "timer", "ticks", "rapid", "heal", "overheal", "potion"}
)
public class VitalRegenTimersPlugin extends Plugin
{
	@Inject
	private VitalRegenTimersConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private InfoBoxManager infoBoxManager;

	@Inject
	private SpriteManager spriteManager;

	@Inject
	private ItemManager itemManager;

	@Inject
	private EventBus eventBus;

	@Inject
	private VitalRegenTracker vitalRegenTracker;

	@Inject
	private HitpointsRegenTextOverlay hpRegenTextOverlay;

	@Inject
	private PrayerRegenTextOverlay prayerRegenTextOverlay;

	/**
	 * Hitpoints regen timer infobox, created when showHpRegenTimer is enabled (non-transparent mode).
	 */
	private VitalRegenInfoBox hpRegenInfoBox;

	/**
	 * Prayer regen timer infobox, created when showPrayerRegenTimer is enabled (non-transparent mode).
	 */
	private VitalRegenInfoBox prayerRegenInfoBox;

	@Provides
	VitalRegenTimersConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(VitalRegenTimersConfig.class);
	}

	@Override
	protected void startUp()
	{
		vitalRegenTracker.reset();
		eventBus.register(vitalRegenTracker);
		overlayManager.add(hpRegenTextOverlay);
		overlayManager.add(prayerRegenTextOverlay);
		updateInfoBoxes();
	}

	@Override
	protected void shutDown()
	{
		eventBus.unregister(vitalRegenTracker);
		overlayManager.remove(hpRegenTextOverlay);
		overlayManager.remove(prayerRegenTextOverlay);
		removeInfoBoxes();
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!VitalRegenTimersConfig.GROUP.equals(event.getGroup()))
		{
			return;
		}

		switch (event.getKey())
		{
			case "transparentInfoBox":
			case "showHpRegenTimer":
			case "showPrayerRegenTimer":
				updateInfoBoxes();
				break;
			default:
				break;
		}
	}

	/**
	 * Adds or removes the hitpoints and prayer regen infoboxes based on config.
	 * In transparent mode, the text overlays are shown instead.
	 */
	private void updateInfoBoxes()
	{
		boolean transparent = config.transparentInfoBox();

		if (config.showHpRegenTimer() && !transparent)
		{
			if (hpRegenInfoBox == null)
			{
				hpRegenInfoBox = new VitalRegenInfoBox(this,
					this::getHpRegenTimerText,
					this::getHpRegenTimerColor,
					this::isHpRegenTimerVisible,
					() -> vitalRegenTracker.isOverhealed() ? "Hitpoints Decay Timer" : "Hitpoints Regen Timer");
				spriteManager.getSpriteAsync(SpriteID.MINIMAP_ORB_HITPOINTS_ICON, 0, img ->
				{
					if (img != null && hpRegenInfoBox != null)
					{
						hpRegenInfoBox.setImage(img);
						infoBoxManager.addInfoBox(hpRegenInfoBox);
					}
				});
			}
		}
		else if (hpRegenInfoBox != null)
		{
			infoBoxManager.removeInfoBox(hpRegenInfoBox);
			hpRegenInfoBox = null;
		}

		if (config.showPrayerRegenTimer() && !transparent)
		{
			if (prayerRegenInfoBox == null)
			{
				prayerRegenInfoBox = new VitalRegenInfoBox(this,
					this::getPrayerRegenTimerText,
					config::prayerRegenColor,
					this::isPrayerRegenTimerVisible,
					() -> "Prayer Regen Timer");
				prayerRegenInfoBox.setImage(itemManager.getImage(ItemID.PRAYER_REGENERATION_POTION4));
				infoBoxManager.addInfoBox(prayerRegenInfoBox);
			}
		}
		else if (prayerRegenInfoBox != null)
		{
			infoBoxManager.removeInfoBox(prayerRegenInfoBox);
			prayerRegenInfoBox = null;
		}
	}

	private void removeInfoBoxes()
	{
		if (hpRegenInfoBox != null)
		{
			infoBoxManager.removeInfoBox(hpRegenInfoBox);
			hpRegenInfoBox = null;
		}
		if (prayerRegenInfoBox != null)
		{
			infoBoxManager.removeInfoBox(prayerRegenInfoBox);
			prayerRegenInfoBox = null;
		}
	}

	/**
	 * @return whether the hitpoints regen timer has something to show right now
	 */
	public boolean isHpRegenTimerVisible()
	{
		if (!config.showHpRegenTimer())
		{
			return false;
		}
		if (vitalRegenTracker.isOverhealed())
		{
			return config.hpShowOverhealDecay();
		}
		return !vitalRegenTracker.isHpFull() || config.hpShowWhenFull();
	}

	public String getHpRegenTimerText()
	{
		return VitalRegenTracker.format(vitalRegenTracker.getHpTicksRemaining(), config.hpDisplayFormat());
	}

	public Color getHpRegenTimerColor()
	{
		return vitalRegenTracker.isOverhealed() ? config.hpDecayColor() : config.hpRegenColor();
	}

	/**
	 * @return whether the prayer regen timer has something to show right now
	 */
	public boolean isPrayerRegenTimerVisible()
	{
		return config.showPrayerRegenTimer() && vitalRegenTracker.isPrayerRegenActive();
	}

	public String getPrayerRegenTimerText()
	{
		return VitalRegenTracker.format(vitalRegenTracker.getPrayerTicksRemaining(), config.prayerDisplayFormat());
	}
}
