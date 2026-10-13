package com.vitalregentimers;

import java.awt.Color;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.InfoBox;

/**
 * InfoBox showing a hitpoints or prayer regeneration countdown.
 * Text, color, visibility and tooltip are supplied by the plugin so the same class
 * can be used for both timers.
 *
 * Based on the Special Attack Timers plugin by AnuVen (https://github.com/AnuVen/special-attack-timers).
 */
public class VitalRegenInfoBox extends InfoBox
{
	private final Supplier<String> text;
	private final Supplier<Color> color;
	private final BooleanSupplier visible;
	private final Supplier<String> tooltip;

	public VitalRegenInfoBox(Plugin plugin, Supplier<String> text, Supplier<Color> color,
		BooleanSupplier visible, Supplier<String> tooltip)
	{
		super(null, plugin);
		this.text = text;
		this.color = color;
		this.visible = visible;
		this.tooltip = tooltip;
	}

	@Override
	public String getText()
	{
		return text.get();
	}

	@Override
	public Color getTextColor()
	{
		return color.get();
	}

	@Override
	public boolean render()
	{
		return visible.getAsBoolean();
	}

	@Override
	public String getTooltip()
	{
		return tooltip.get();
	}
}
