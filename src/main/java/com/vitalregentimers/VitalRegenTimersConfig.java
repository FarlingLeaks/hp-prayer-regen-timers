package com.vitalregentimers;

import java.awt.Color;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.FontType;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;

/**
 * Based on the Special Attack Timers plugin by AnuVen (https://github.com/AnuVen/special-attack-timers).
 */
@ConfigGroup(VitalRegenTimersConfig.GROUP)
public interface VitalRegenTimersConfig extends Config
{
	String GROUP = "vitalregentimers";

	@Getter
	@RequiredArgsConstructor
	enum DisplayFormat
	{
		TICKS("Ticks"),
		SECONDS("Seconds"),
		DECIMALS("Decimals");

		private final String name;

		@Override
		public String toString()
		{
			return name;
		}
	}

	@Getter
	@RequiredArgsConstructor
	enum TextStyle
	{
		RUNELITE("Same as RuneLite"),
		OUTLINE("Outline"),
		SHADOW("Shadow"),
		NONE("None");

		private final String name;

		@Override
		public String toString()
		{
			return name;
		}
	}

	@ConfigSection(
		name = "Display",
		description = "How the timers are displayed",
		position = 0
	)
	String displaySection = "display";

	@ConfigItem(
		keyName = "transparentInfoBox",
		name = "Transparent Infobox",
		description = "Show only the timer number, with no icon or background. Alt-drag it onto the orbs.",
		position = 1,
		section = displaySection
	)
	default boolean transparentInfoBox()
	{
		return false;
	}

	@Range(min = 8, max = 48)
	@Units(Units.PIXELS)
	@ConfigItem(
		keyName = "transparentFontSize",
		name = "Transparent Font Size",
		description = "Font size of the Transparent Infobox timers (when Custom Font is off).",
		position = 2,
		section = displaySection
	)
	default int transparentFontSize()
	{
		return 16;
	}

	@ConfigItem(
		keyName = "useCustomFont",
		name = "Custom Font",
		description = "Use the font below for the Transparent Infobox timers instead of RuneLite's infobox font",
		position = 3,
		section = displaySection
	)
	default boolean useCustomFont()
	{
		return false;
	}

	@ConfigItem(
		keyName = "timerFont",
		name = "Font",
		description = "Font, size, bold and italic for the Transparent Infobox timers (when Custom Font is on)",
		position = 4,
		section = displaySection
	)
	default FontType timerFont()
	{
		return FontType.REGULAR;
	}

	@ConfigItem(
		keyName = "textStyle",
		name = "Text Outline",
		description = "Outline, shadow or nothing around the Transparent Infobox timer text",
		position = 5,
		section = displaySection
	)
	default TextStyle textStyle()
	{
		return TextStyle.RUNELITE;
	}

	@ConfigSection(
		name = "Hitpoints Regen Timer",
		description = "Countdown until your next hitpoint regenerates (or decays when overhealed)",
		position = 10
	)
	String hpRegenSection = "hpRegen";

	@ConfigItem(
		keyName = "showHpRegenTimer",
		name = "Show HP Regen Timer",
		description = "Show a countdown until your next hitpoint regenerates (halved with Rapid Heal)",
		position = 11,
		section = hpRegenSection
	)
	default boolean showHpRegenTimer()
	{
		return true;
	}

	@ConfigItem(
		keyName = "hpOnlyWithRapidHeal",
		name = "Only With Rapid Heal",
		description = "Only show the hitpoints timer while Rapid Heal is active",
		position = 12,
		section = hpRegenSection
	)
	default boolean hpOnlyWithRapidHeal()
	{
		return false;
	}

	@ConfigItem(
		keyName = "hpDisplayFormat",
		name = "HP Display Format",
		description = "Ticks (game ticks), Seconds (whole), or Decimals (e.g. 29.4s)",
		position = 13,
		section = hpRegenSection
	)
	default DisplayFormat hpDisplayFormat()
	{
		return DisplayFormat.TICKS;
	}

	@ConfigItem(
		keyName = "hpShowWhenFull",
		name = "Show When HP Is Full",
		description = "Keep showing the hitpoints timer when your hitpoints are full (useful for Rapid Heal flicking)",
		position = 14,
		section = hpRegenSection
	)
	default boolean hpShowWhenFull()
	{
		return true;
	}

	@ConfigItem(
		keyName = "hpShowOverhealDecay",
		name = "Show Overheal Decay",
		description = "When overhealed, show the countdown until your boosted hitpoints decay by 1",
		position = 15,
		section = hpRegenSection
	)
	default boolean hpShowOverhealDecay()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
		keyName = "hpRegenColor",
		name = "HP Regen Color",
		description = "Color of the hitpoints regen timer",
		position = 16,
		section = hpRegenSection
	)
	default Color hpRegenColor()
	{
		return new Color(255, 255, 255, 255);
	}

	@Alpha
	@ConfigItem(
		keyName = "hpDecayColor",
		name = "HP Overheal Decay Color",
		description = "Color of the hitpoints timer while overhealed (counting down to decay)",
		position = 17,
		section = hpRegenSection
	)
	default Color hpDecayColor()
	{
		return new Color(255, 165, 0, 255);
	}

	@ConfigSection(
		name = "Prayer Regen Timer",
		description = "Countdown until your next prayer point from a Prayer regeneration potion",
		position = 20
	)
	String prayerRegenSection = "prayerRegen";

	@ConfigItem(
		keyName = "showPrayerRegenTimer",
		name = "Show Prayer Regen Timer",
		description = "While a Prayer regeneration potion is active, show a countdown until the next prayer point "
			+ "(prayer does not regenerate on its own)",
		position = 21,
		section = prayerRegenSection
	)
	default boolean showPrayerRegenTimer()
	{
		return true;
	}

	@ConfigItem(
		keyName = "prayerDisplayFormat",
		name = "Prayer Display Format",
		description = "Ticks (game ticks), Seconds (whole), or Decimals (e.g. 29.4s)",
		position = 22,
		section = prayerRegenSection
	)
	default DisplayFormat prayerDisplayFormat()
	{
		return DisplayFormat.TICKS;
	}

	@Alpha
	@ConfigItem(
		keyName = "prayerRegenColor",
		name = "Prayer Regen Color",
		description = "Color of the prayer regen timer",
		position = 23,
		section = prayerRegenSection
	)
	default Color prayerRegenColor()
	{
		return new Color(255, 255, 255, 255);
	}
}
