package slimeattack07.naval_warfare.init;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import slimeattack07.naval_warfare.NavalWarfare;

public class NWStats {
	public static ResourceLocation SHIPS_DESTROYED = null;
	public static ResourceLocation STREAK = null;
	public static ResourceLocation WINS = null;
	public static ResourceLocation ACTIVE_USED = null;
	public static ResourceLocation PASSIVE_TRIGGERED = null;
	public static ResourceLocation SPELLS_USED = null;
	public static ResourceLocation GAMES_PLAYED = null;

	private static ResourceLocation makeCustomStat(String name) {
	     ResourceLocation resourcelocation = new ResourceLocation(NavalWarfare.MOD_ID, name);
	     Registry.register(Registry.CUSTOM_STAT, NavalWarfare.MOD_ID + ":" + name, resourcelocation);
	     Stats.CUSTOM.get(resourcelocation, StatFormatter.DEFAULT);
	     
	     return resourcelocation;
	}
	 
	public static void createStats() {
		SHIPS_DESTROYED = makeCustomStat("ships_destroyed");
		STREAK = makeCustomStat("streak");
		WINS = makeCustomStat("wins");
		ACTIVE_USED = makeCustomStat("active_used");
		PASSIVE_TRIGGERED = makeCustomStat("passive_triggered");
		SPELLS_USED = makeCustomStat("spells_used");
		GAMES_PLAYED = makeCustomStat("games_played");
	}
}
