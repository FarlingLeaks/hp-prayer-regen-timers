package com.vitalregentimers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.client.config.RuneLiteConfig;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.TextComponent;

/**
 * Transparent (number-only) version of the hitpoints/prayer regen infobox,
 * used when "Transparent Infobox" is enabled. Movable with Alt-drag, so it can be
 * placed directly on top of the hitpoints or prayer orb.
 *
 * Subclassed once per timer so each overlay keeps its own saved position.
 *
 * Based on the Special Attack Timers plugin by AnuVen (https://github.com/AnuVen/special-attack-timers).
 */
public abstract class VitalRegenTextOverlay extends Overlay
{
	private final Client client;
	private final VitalRegenTimersConfig config;
	private final RuneLiteConfig runeLiteConfig;
	private final TextComponent textComponent = new TextComponent();

	protected VitalRegenTextOverlay(Client client, VitalRegenTimersPlugin plugin,
		VitalRegenTimersConfig config, RuneLiteConfig runeLiteConfig)
	{
		super(plugin);
		this.client = client;
		this.config = config;
		this.runeLiteConfig = runeLiteConfig;

		setPosition(OverlayPosition.TOP_LEFT);
		setPriority(PRIORITY_LOW);
		// Not snappable so it can be dropped directly onto the minimap orbs
		setSnappable(false);
	}

	/**
	 * @return whether this timer is enabled in the config
	 */
	protected abstract boolean isEnabled();

	/**
	 * @return whether the timer currently has something to show
	 */
	protected abstract boolean isVisible();

	protected abstract String getTimerText();

	protected abstract Color getTimerColor();

	protected abstract VitalRegenTimersConfig.DisplayFormat getDisplayFormat();

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return null;
		}

		if (!isEnabled() || !config.transparentInfoBox() || !isVisible())
		{
			return null;
		}

		String text = getTimerText();
		Font font = runeLiteConfig.infoboxFont().getFont().deriveFont((float) config.transparentFontSize());
		graphics.setFont(font);
		FontMetrics metrics = graphics.getFontMetrics();

		// Fixed width so the number doesn't shift while counting down
		int boxWidth = getMaxTextWidth(metrics);
		int x = (boxWidth - metrics.stringWidth(text)) / 2;

		textComponent.setText(text);
		textComponent.setFont(font);
		textComponent.setColor(getTimerColor());
		textComponent.setOutline(runeLiteConfig.infoBoxTextOutline());
		textComponent.setPosition(x, metrics.getAscent());
		textComponent.render(graphics);

		return new Dimension(boxWidth, metrics.getHeight());
	}

	/**
	 * Width of the widest text the timer can show: three digits for ticks (up to 100),
	 * two for seconds (up to 60), or "00.0s" for decimals.
	 */
	private int getMaxTextWidth(FontMetrics metrics)
	{
		int maxDigitWidth = 0;
		for (char digit = '0'; digit <= '9'; digit++)
		{
			maxDigitWidth = Math.max(maxDigitWidth, metrics.charWidth(digit));
		}

		switch (getDisplayFormat())
		{
			case DECIMALS:
				return 3 * maxDigitWidth + metrics.charWidth('.') + metrics.charWidth('s');
			case SECONDS:
				return 2 * maxDigitWidth;
			case TICKS:
			default:
				return 3 * maxDigitWidth;
		}
	}
}
