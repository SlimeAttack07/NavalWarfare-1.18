package slimeattack07.naval_warfare;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.mojang.serialization.Codec;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;
import slimeattack07.naval_warfare.config.NavalWarfareConfig;
import slimeattack07.naval_warfare.init.NWBlocks;
import slimeattack07.naval_warfare.init.NWItems;
import slimeattack07.naval_warfare.init.NWSounds;
import slimeattack07.naval_warfare.init.NWStats;
import slimeattack07.naval_warfare.init.NWTileEntityTypes;
import slimeattack07.naval_warfare.init.NWTriggers;
import slimeattack07.naval_warfare.init.NWVillagers;
import slimeattack07.naval_warfare.network.NavalNetwork;
import slimeattack07.naval_warfare.objects.NWStructures;
import slimeattack07.naval_warfare.objects.items.DisappearingBlockItem;
import slimeattack07.naval_warfare.objects.items.ShipItem;
import slimeattack07.naval_warfare.objects.structures.NWCStructures;

@Mod("naval_warfare")
@Mod.EventBusSubscriber(modid = NavalWarfare.MOD_ID, bus = Bus.MOD)
public class NavalWarfare {

	public static NavalWarfare instance;
	public static final String MOD_ID = "naval_warfare";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	
	
	public static final CreativeModeTab NAVAL_WARFARE = new CreativeModeTab("naval_warfare") {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(NWItems.GAME_INTERACTOR.get());
		}
	};
	
	public static final CreativeModeTab NW_SHIPS = new CreativeModeTab("naval_warfare_ships") {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(NWBlocks.BATTLESHIP.get().asItem());
		}
	};
	
	public static final CreativeModeTab NW_MISC = new CreativeModeTab("naval_warfare_misc") {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(NWItems.SHIP_SUNK_OPPONENT.get());
		}
	};
	
	public static final CreativeModeTab NW_DEPLOYABLES = new CreativeModeTab("naval_warfare_deployables") {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(NWBlocks.LANDGUARD_ACTIVE.get().asItem());
		}
	};
	
	public static final CreativeModeTab NW_ANIMATIONS = new CreativeModeTab("naval_warfare_animations") {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(NWBlocks.SHIP_HERE.get().asItem());
		}
	};
	
	public NavalWarfare() {
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		instance = this;

		NWSounds.SOUNDS.register(modEventBus);
		NWItems.NW_ITEMS.register(modEventBus);
		NWBlocks.NW_BLOCKS.register(modEventBus);
		NWBlocks.NW_ANIMATIONS.register(modEventBus);
		NWBlocks.NW_DEPLOYABLES.register(modEventBus);
		NWBlocks.NW_SHIPS.register(modEventBus);
		NWTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);
		NWVillagers.PROFESSIONS.register(modEventBus);
		NWVillagers.POI_TYPES.register(modEventBus);
		NWStructures.STRUCTURES.register(modEventBus);
		
		modEventBus.addListener(this::setup);
		modEventBus.addListener(this::clientRegistries);
		
		MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, this::lootLoad);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, this::addDimensionalSpacing);
		
		ModLoadingContext.get().registerConfig(Type.COMMON, NavalWarfareConfig.SPEC, "naval_warfare-common.toml");
		
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public static void onRegisterItems(final RegistryEvent.Register<Item> event) {
		final IForgeRegistry<Item> registry = event.getRegistry();

		NWBlocks.NW_BLOCKS.getEntries().stream()
				.map(RegistryObject::get).forEach(block -> {	
					CreativeModeTab group = (block.equals(NWBlocks.SHIP_CLOSE.get()) || block.equals(NWBlocks.SHIP_HERE.get())) ? NW_ANIMATIONS : NAVAL_WARFARE;
					registry.register(new BlockItem(block, new Item.Properties().tab(group)).
						setRegistryName(block.getRegistryName()));
				});
		
		NWBlocks.NW_SHIPS.getEntries().stream()
			.map(RegistryObject::get).forEach(block -> {
				registry.register(new ShipItem(block, NW_SHIPS, true).setRegistryName(block.getRegistryName()));
		});
		
		NWBlocks.NW_DEPLOYABLES.getEntries().stream()
		.map(RegistryObject::get).forEach(block -> {
			registry.register(new ShipItem(block, NW_DEPLOYABLES, false).setRegistryName(block.getRegistryName()));
	});
		
		NWBlocks.NW_ANIMATIONS.getEntries().stream()
			.map(RegistryObject::get).forEach(block -> {
			registry.register(new DisappearingBlockItem(block).setRegistryName(block.getRegistryName()));
	});
	}
	
	private void setup(final FMLCommonSetupEvent event) {
		NavalNetwork.init();
//		NWVillagers.registerPOI();
		NWVillagers.fillTradeData();
		NWStructures.setupStructures();
		NWCStructures.registerConfiguredStructures();
		NWTriggers.registerTriggers();
		NWStats.createStats();
	}

	private void clientRegistries(final FMLClientSetupEvent event) {
		setRenderLayers();
	}
	
	public static void setRenderLayers() {
		NWBlocks.NW_SHIPS.getEntries().stream().map(RegistryObject::get).forEach(block -> {
			ItemBlockRenderTypes.setRenderLayer(block, RenderType.cutout());
		});
		
		NWBlocks.NW_DEPLOYABLES.getEntries().stream().map(RegistryObject::get).forEach(block -> {
			ItemBlockRenderTypes.setRenderLayer(block, RenderType.cutout());
		});
		
		NWBlocks.NW_ANIMATIONS.getEntries().stream().map(RegistryObject::get).forEach(block -> {
			ItemBlockRenderTypes.setRenderLayer(block, RenderType.cutout());
		});
		
		ItemBlockRenderTypes.setRenderLayer(NWBlocks.TORPEDO_NET.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(NWBlocks.ANTI_AIR.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(NWBlocks.ENERGY_SHIELD.get(), RenderType.translucent());
	}
	
	public void lootLoad(LootTableLoadEvent event) {
		if(NavalWarfareConfig.modify_dungeon_loot.get() && matchesDungeonLoot(event.getName().toString())) {
			LootPool.Builder pool_builder = LootPool.lootPool();
			pool_builder.setRolls(ConstantValue.exactly(1));
			
			LootPoolSingletonContainer.Builder<?> builder1 = LootItem.lootTableItem(NWItems.HULL_T1.get());
			builder1.setWeight(NavalWarfareConfig.hull_t1_weight.get());
			LootPoolSingletonContainer.Builder<?> builder2 = LootItem.lootTableItem(NWItems.HULL_T2.get());
			builder2.setWeight(NavalWarfareConfig.hull_t2_weight.get());
			LootPoolSingletonContainer.Builder<?> builder3 = LootItem.lootTableItem(NWItems.HULL_T3.get());
			builder3.setWeight(NavalWarfareConfig.hull_t3_weight.get());
			LootPoolSingletonContainer.Builder<?> builder4 = LootItem.lootTableItem(NWItems.HULL_T4.get());
			builder4.setWeight(NavalWarfareConfig.hull_t4_weight.get());
			LootPoolSingletonContainer.Builder<?> builder5 = LootItem.lootTableItem(NWItems.HULL_T5.get());
			builder5.setWeight(NavalWarfareConfig.hull_t5_weight.get());
			LootPoolSingletonContainer.Builder<?> builder6 = LootItem.lootTableItem(Items.AIR);
			builder6.setWeight(NavalWarfareConfig.air_weight.get());
			
			pool_builder.add(builder1);
			pool_builder.add(builder2);
			pool_builder.add(builder3);
			pool_builder.add(builder4);
			pool_builder.add(builder5);
			pool_builder.add(builder6);
			
			LootPool pool = pool_builder.build();
			event.getTable().addPool(pool);
		}
	}
	
	public boolean matchesDungeonLoot(String event_name) {
		if(event_name.contains("minecraft:chests") && !event_name.contains("village")) {
			return (!(event_name.contains("igloo") || event_name.contains("dispenser") || event_name.contains("pillager") 
					|| event_name.contains("ship") || event_name.contains("bonus")));
		}
		
		return false;
	}
	 
    public void addDimensionalSpacing(final WorldEvent.Load event) {
    	// Huge thanks to TelepathicGrunt for their structure tutorial providing most of this code to use
    	
        if(event.getWorld() instanceof ServerLevel){
            ServerLevel serverLevel = (ServerLevel)event.getWorld();
            ChunkGenerator chunkGenerator = serverLevel.getChunkSource().getGenerator();
            
            if(chunkGenerator instanceof FlatLevelSource && serverLevel.dimension().equals(Level.OVERWORLD))
            	return;
            
            StructureSettings worldStructureConfig = chunkGenerator.getSettings();
            HashMap<StructureFeature<?>, HashMultimap<ConfiguredStructureFeature<?, ?>, ResourceKey<Biome>>> map = new HashMap<>();
            
            for(Map.Entry<ResourceKey<Biome>, Biome> biomeEntry : serverLevel.registryAccess().ownedRegistryOrThrow(Registry.BIOME_REGISTRY).entrySet()) {
            	Biome.BiomeCategory cat = biomeEntry.getValue().getBiomeCategory();
            	
            	if(cat == Biome.BiomeCategory.PLAINS || cat == Biome.BiomeCategory.TAIGA)
            		associateBiomeToConfiguredStructure(map, NWCStructures.CONFIGURED_CAPTAINS_CABIN, biomeEntry.getKey());
            }
            
            ImmutableMap.Builder<StructureFeature<?>, ImmutableMultimap<ConfiguredStructureFeature<?, ?>, ResourceKey<Biome>>> tempMap = ImmutableMap.builder();
            worldStructureConfig.configuredStructures.entrySet().stream().filter(entry -> !map.containsKey(entry.getKey())).forEach(tempMap::put);
            map.forEach((key, value) -> tempMap.put(key, ImmutableMultimap.copyOf(value)));
            worldStructureConfig.configuredStructures = tempMap.build();
            
            try {
                Method method = ObfuscationReflectionHelper.findMethod(ChunkGenerator.class, "codec");
                @SuppressWarnings("unchecked")
				ResourceLocation cgRL = Registry.CHUNK_GENERATOR.getKey((Codec<? extends ChunkGenerator>)
						method.invoke(chunkGenerator));
                if(cgRL != null && cgRL.getNamespace().equals("terraforged")) return;
            }
            catch(Exception e){
                LOGGER.error("Was unable to check if " + serverLevel.dimension().location() + " is using Terraforged's ChunkGenerator.");
            }
            
            Map<StructureFeature<?>, StructureFeatureConfiguration> tempMap_ = new HashMap<>(worldStructureConfig.structureConfig());
            tempMap_.putIfAbsent(NWStructures.CAPTAINS_CABIN.get(), StructureSettings.DEFAULTS.get(NWStructures.CAPTAINS_CABIN.get()));
            worldStructureConfig.structureConfig = tempMap_;
        }
   }
    
    private static void associateBiomeToConfiguredStructure(Map<StructureFeature<?>, HashMultimap<ConfiguredStructureFeature<?, ?>, 
    		ResourceKey<Biome>>> STStructureToMultiMap, ConfiguredStructureFeature<?, ?> configuredStructureFeature, ResourceKey<Biome> biomeRegistryKey) {
    	// Huge thanks to TelepathicGrunt for their structure tutorial providing most of this code to use
    	
        STStructureToMultiMap.putIfAbsent(configuredStructureFeature.feature, HashMultimap.create());
        HashMultimap<ConfiguredStructureFeature<?, ?>, ResourceKey<Biome>> configuredStructureToBiomeMultiMap = STStructureToMultiMap.
        		get(configuredStructureFeature.feature);
        
        if(configuredStructureToBiomeMultiMap.containsValue(biomeRegistryKey)) {
            NavalWarfare.LOGGER.error("""
                    Detected 2 ConfiguredStructureFeatures that share the same base StructureFeature trying to be added to same biome. One will be prevented from spawning.
                    This issue happens with vanilla too and is why a Snowy Village and Plains Village cannot spawn in the same biome because they both use the Village base structure.
                    The two conflicting ConfiguredStructures are: {}, {}
                    The biome that is attempting to be shared: {}
                """,
                    BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(configuredStructureFeature),
                    BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(configuredStructureToBiomeMultiMap.entries().stream().filter
                    		(e -> e.getValue() == biomeRegistryKey).findFirst().get().getKey()),
                    biomeRegistryKey
            );
        }
        else{
            configuredStructureToBiomeMultiMap.put(configuredStructureFeature, biomeRegistryKey);
        }
    }
}
