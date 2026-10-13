package com.vitalregentimers;

import java.awt.Color;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.client.config.RuneLiteConfig;

/**
 * Transparent prayer regen timer (number only). Alt-drag it onto the prayer orb.
 */
public class PrayerRegenTextOverlay extends VitalRegenTextOverlay
{
	private final VitalRegenTimersPlugin plugin;
	private final VitalRegenTimersConfig config;

	@Inject
	public PrayerRegenTextOverlay(Client client, VitalRegenTimersPlugin plugin,
		VitalRegenTimersConfig config, RuneLiteConfig runeLiteConfig)
	{
		super(client, plugin, config, runeLiteConfig);
		this.plugin = plugin;
		this.config = config;
	}

	@Override
	protected boolean isEnabled()
	{
		return config.showPrayerRegenTimer();
	}

	@Override
	protected boolean isVisible()
	{
		return plugin.isPrayerRegenTimerVisible();
	}

	@Override
	protected String getTimerText()
	{
		return plugin.getPrayerRegenTimerText();
	}

	@Override
	protected Color getTimerColor()
	{
		return config.prayerRegenColor();
	}

	@Override
	protected VitalRegenTimersConfig.DisplayFormat getDisplayFormat()
	{
		return config.prayerDisplayFormat();
	}
}
