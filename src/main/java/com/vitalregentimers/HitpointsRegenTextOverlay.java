package com.vitalregentimers;

import java.awt.Color;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.client.config.RuneLiteConfig;

/**
 * Transparent hitpoints regen timer (number only). Alt-drag it onto the hitpoints orb.
 */
public class HitpointsRegenTextOverlay extends VitalRegenTextOverlay
{
	private final VitalRegenTimersPlugin plugin;
	private final VitalRegenTimersConfig config;

	@Inject
	public HitpointsRegenTextOverlay(Client client, VitalRegenTimersPlugin plugin,
		VitalRegenTimersConfig config, RuneLiteConfig runeLiteConfig)
	{
		super(client, plugin, config, runeLiteConfig);
		this.plugin = plugin;
		this.config = config;
	}

	@Override
	protected boolean isEnabled()
	{
		return config.showHpRegenTimer();
	}

	@Override
	protected boolean isVisible()
	{
		return plugin.isHpRegenTimerVisible();
	}

	@Override
	protected String getTimerText()
	{
		return plugin.getHpRegenTimerText();
	}

	@Override
	protected Color getTimerColor()
	{
		return plugin.getHpRegenTimerColor();
	}

	@Override
	protected VitalRegenTimersConfig.DisplayFormat getDisplayFormat()
	{
		return config.hpDisplayFormat();
	}
}
