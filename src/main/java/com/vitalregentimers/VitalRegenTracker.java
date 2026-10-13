package com.vitalregentimers;

import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Prayer;
import net.runelite.api.Skill;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.eventbus.Subscribe;

/**
 * Tracks the hitpoints regeneration timer and the Prayer regeneration potion timer,
 * so they can be shown as a tick countdown.
 *
 * Hitpoints:
 * - Regenerates (or, when overhealed, decays) 1 HP every 100 ticks (60 seconds).
 * - Rapid Heal halves the regen interval to 50 ticks.
 * - Toggling Rapid Heal resets the timer.
 * This mirrors the timing used by RuneLite's built-in Regeneration Meter plugin.
 *
 * Prayer:
 * - Prayer does not regenerate on its own. While a Prayer regeneration potion is active,
 *   1 prayer point is restored every 12 ticks (7.2 seconds).
 * - The timer resyncs every time the game updates the potion varbit (once per restored point),
 *   the same approach used by the Prayer Regeneration Helper plugin.
 */
@Singleton
public class VitalRegenTracker
{
	/**
	 * Hitpoints regenerate 1 HP every 100 game ticks (60 seconds).
	 */
	public static final int HP_REGEN_TICKS = 100;

	/**
	 * Rapid Heal halves the hitpoints regeneration interval.
	 */
	public static final int RAPID_HEAL_REGEN_TICKS = HP_REGEN_TICKS / 2;

	/**
	 * A Prayer regeneration potion restores 1 prayer point every 12 game ticks.
	 */
	public static final int PRAYER_REGEN_POTION_TICKS = 12;

	private final Client client;

	/**
	 * Game ticks since the last hitpoints regen/decay. Counts up to the current cycle length, then wraps.
	 * Starts at -2 after logging in or hopping, matching RuneLite's Regeneration Meter.
	 */
	private int ticksSinceHpRegen = 0;

	/**
	 * Current hitpoints regen cycle length in ticks (100, or 50 with Rapid Heal).
	 */
	@Getter
	private int hpRegenCycleTicks = HP_REGEN_TICKS;

	/**
	 * Value used when no Prayer regeneration potion is active.
	 */
	private static final int NO_PRAYER_REGEN = -1;

	/**
	 * Ticks until the next prayer point from a Prayer regeneration potion.
	 * Set to 12 whenever the potion varbit updates, then counts down each game tick.
	 */
	private int prayerTicksUntilRegen = NO_PRAYER_REGEN;

	@Inject
	public VitalRegenTracker(Client client)
	{
		this.client = client;
	}

	/**
	 * Resets all tracking state.
	 */
	public void reset()
	{
		ticksSinceHpRegen = 0;
		hpRegenCycleTicks = HP_REGEN_TICKS;
		prayerTicksUntilRegen = NO_PRAYER_REGEN;
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.HOPPING || event.getGameState() == GameState.LOGIN_SCREEN)
		{
			// Same offset RuneLite's Regeneration Meter uses after logging in/hopping
			ticksSinceHpRegen = -2;
			prayerTicksUntilRegen = NO_PRAYER_REGEN;
		}
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		if (event.getVarbitId() == VarbitID.PRAYER_RAPIDHEAL)
		{
			// Toggling Rapid Heal resets the hitpoints timer
			ticksSinceHpRegen = 0;
		}
		else if (event.getVarbitId() == VarbitID.PRAYER_REGENERATION_POTION_TIMER)
		{
			// The game updates this varbit every time the potion restores a prayer point,
			// so every change > 0 marks the start of a new 12 tick cycle.
			// Same approach as the "Prayer Regeneration Helper" plugin.
			if (event.getValue() > 0)
			{
				prayerTicksUntilRegen = PRAYER_REGEN_POTION_TICKS;
			}
			else
			{
				prayerTicksUntilRegen = NO_PRAYER_REGEN;
			}
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		hpRegenCycleTicks = client.isPrayerActive(Prayer.RAPID_HEAL) ? RAPID_HEAL_REGEN_TICKS : HP_REGEN_TICKS;
		ticksSinceHpRegen = (ticksSinceHpRegen + 1) % hpRegenCycleTicks;

		if (prayerTicksUntilRegen >= 0)
		{
			prayerTicksUntilRegen--;
		}
	}

	/**
	 * @return game ticks until the next hitpoint regenerates (or decays when overhealed)
	 */
	public int getHpTicksRemaining()
	{
		return remaining(ticksSinceHpRegen, hpRegenCycleTicks);
	}

	/**
	 * @return game ticks until the next prayer point is restored by a Prayer regeneration potion
	 */
	public int getPrayerTicksRemaining()
	{
		// Counts 12 -> 1 (like Prayer Regeneration Helper) instead of 11 -> 0
		return prayerTicksUntilRegen + 1;
	}

	/**
	 * @return true while a Prayer regeneration potion is active
	 */
	public boolean isPrayerRegenActive()
	{
		return prayerTicksUntilRegen >= 0;
	}

	/**
	 * @return true if current hitpoints are above the real hitpoints level (overheal decays instead of regenerating)
	 */
	public boolean isOverhealed()
	{
		return client.getBoostedSkillLevel(Skill.HITPOINTS) > client.getRealSkillLevel(Skill.HITPOINTS);
	}

	/**
	 * @return true if hitpoints are exactly at the real level, meaning the timer has no visible effect
	 */
	public boolean isHpFull()
	{
		return client.getBoostedSkillLevel(Skill.HITPOINTS) == client.getRealSkillLevel(Skill.HITPOINTS);
	}

	/**
	 * Converts "ticks since last regen" into "ticks until next regen".
	 * Shows 0 on the regen tick itself instead of the full cycle length,
	 * like a regular countdown timer.
	 */
	private static int remaining(int ticksSince, int cycle)
	{
		int ticks = cycle - Math.floorMod(ticksSince, cycle);
		return ticks == cycle ? 0 : ticks;
	}

	/**
	 * Formats a tick count using the configured display format.
	 */
	public static String format(int ticks, VitalRegenTimersConfig.DisplayFormat format)
	{
		switch (format)
		{
			case SECONDS:
				return String.valueOf((int) Math.ceil(ticks * 0.6));
			case DECIMALS:
				return String.format("%.1fs", ticks * 0.6);
			case TICKS:
			default:
				return String.valueOf(ticks);
		}
	}
}
