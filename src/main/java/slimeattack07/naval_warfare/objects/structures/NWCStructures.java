package slimeattack07.naval_warfare.objects.structures;

import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.PlainVillagePools;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import slimeattack07.naval_warfare.NavalWarfare;
import slimeattack07.naval_warfare.objects.NWStructures;

public class NWCStructures {
	public static ConfiguredStructureFeature<?, ?> CONFIGURED_CAPTAINS_CABIN = NWStructures.CAPTAINS_CABIN.get().configured(new JigsawConfiguration(() -> PlainVillagePools.START, 0));
	
	public static void registerConfiguredStructures() {
		 Registry<ConfiguredStructureFeature<?, ?>> registry = BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE;
		 Registry.register(registry, new ResourceLocation(NavalWarfare.MOD_ID, "configured_captains_cabin"), CONFIGURED_CAPTAINS_CABIN);
  }
}
