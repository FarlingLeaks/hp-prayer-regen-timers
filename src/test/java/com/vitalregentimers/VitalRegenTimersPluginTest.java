package com.vitalregentimers;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class VitalRegenTimersPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(VitalRegenTimersPlugin.class);
		RuneLite.main(args);
	}
}
