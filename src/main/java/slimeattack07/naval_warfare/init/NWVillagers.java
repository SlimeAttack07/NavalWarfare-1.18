package slimeattack07.naval_warfare.init;

import java.util.Random;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import slimeattack07.naval_warfare.NavalWarfare;
import slimeattack07.naval_warfare.util.NWBasicMethods;

public class NWVillagers {
	public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(ForgeRegistries.POI_TYPES, NavalWarfare.MOD_ID);
	public static final DeferredRegister<VillagerProfession> PROFESSIONS = DeferredRegister.create(ForgeRegistries.PROFESSIONS, NavalWarfare.MOD_ID);
	
	public static final RegistryObject<PoiType> DISPLAY_POI = POI_TYPES.register("ship_display", 
			() -> new PoiType("ship_display", PoiType.getBlockStates(NWBlocks.SHIP_DISPLAY.get()), 1, 1));
	
	public static final RegistryObject<VillagerProfession> RETIRED_CAPTAIN = PROFESSIONS.register("retired_captain", 
			() -> new VillagerProfession("retired_captain", DISPLAY_POI.get(), ImmutableSet.of(), ImmutableSet.of(), 
					SoundEvents.VILLAGER_WORK_WEAPONSMITH));
	
	public static void fillTradeData() {
		VillagerTrades.ItemListing[] level1 = new VillagerTrades.ItemListing[] {new CustomTrade(CustomTrade.RC.I1B), new CustomTrade(CustomTrade.RC.I1S)};
		VillagerTrades.ItemListing[] level2 = new VillagerTrades.ItemListing[] {new CustomTrade(CustomTrade.RC.I2B), new CustomTrade(CustomTrade.RC.I2S)};
		VillagerTrades.ItemListing[] level3 = new VillagerTrades.ItemListing[] {new CustomTrade(CustomTrade.RC.I3B), new CustomTrade(CustomTrade.RC.I3S)};
		VillagerTrades.ItemListing[] level4 = new VillagerTrades.ItemListing[] {new CustomTrade(CustomTrade.RC.H1B), new CustomTrade(CustomTrade.RC.H2B)};
		VillagerTrades.ItemListing[] level5 = new VillagerTrades.ItemListing[] {new CustomTrade(CustomTrade.RC.S1B), new CustomTrade(CustomTrade.RC.S2B)};
		
		VillagerTrades.TRADES.put(RETIRED_CAPTAIN.get(), 
				getAsIntMap(ImmutableMap.of(1, level1, 2, level2, 3, level3, 4, level4, 5, level5)));
	}
	
	private static Int2ObjectMap<VillagerTrades.ItemListing[]> getAsIntMap(ImmutableMap<Integer, VillagerTrades.ItemListing[]> map) {
		return new Int2ObjectOpenHashMap<>(map);
	}
	
	public static class CustomTrade implements VillagerTrades.ItemListing {
		public enum RC {
				I1B,
				I1S,
				I2B,
				I2S,
				I3B,
				I3S,
				H1B,
				H2B,
				S1B,
				S2B
		}
		
		private final RC TYPE;
		
		public CustomTrade(RC rc) {
			TYPE = rc;
		}

		@Override
		public MerchantOffer getOffer(Entity trader, Random rand) {
			ItemStack tagged = ItemStack.EMPTY;
			ItemStack emerald = new ItemStack(Items.EMERALD, 1);
			
			switch(TYPE) {
			case I1B:
				tagged = NWBasicMethods.getRandomTaggedItem("icons_t1", rand, NWItems.NW_ITEMS);
				tagged.setCount(4);
				return new MerchantOffer(tagged, emerald, 8, 4, 0);
			case I1S:
				tagged = NWBasicMethods.getRandomTaggedItem("icons_t1", rand, NWItems.NW_ITEMS);
				tagged.setCount(3);
				return new MerchantOffer(emerald, tagged , 8, 3, 0);
			case I2B:
				tagged = NWBasicMethods.getRandomTaggedItem("icons_t2", rand, NWItems.NW_ITEMS);
				tagged.setCount(4);
				emerald.setCount(3);
				return new MerchantOffer(tagged, emerald, 8, 5, 0);
			case I2S:
				tagged = NWBasicMethods.getRandomTaggedItem("icons_t2", rand, NWItems.NW_ITEMS);
				tagged.setCount(3);
				emerald.setCount(3);
				return new MerchantOffer(emerald, tagged, 8, 4, 0);
			case I3B:
				tagged = NWBasicMethods.getRandomTaggedItem("icons_t3", rand, NWItems.NW_ITEMS);
				tagged.setCount(4);
				emerald.setCount(5);
				return new MerchantOffer(tagged, emerald, 8, 6, 0);
			case I3S:
				tagged = NWBasicMethods.getRandomTaggedItem("icons_t3", rand, NWItems.NW_ITEMS);
				tagged.setCount(3);
				emerald.setCount(5);
				return new MerchantOffer(emerald, tagged, 8, 5, 0);
			case H1B:
				switch(rand.nextInt(3)) {
				case 1:
					emerald.setCount(5);
					return new MerchantOffer(emerald, new ItemStack(NWItems.HULL_T1.get()), 4, 6, 0);
				case 2:
					emerald.setCount(10);
					return new MerchantOffer(emerald, new ItemStack(NWItems.HULL_T2.get()), 4, 7, 0);
				default:
					emerald.setCount(20);
					return new MerchantOffer(emerald, new ItemStack(NWItems.HULL_T3.get()), 4, 8, 0);
				}
			case H2B:
				switch(rand.nextInt(3)) {
				case 1:
					emerald.setCount(20);
					return new MerchantOffer(emerald, new ItemStack(NWItems.HULL_T3.get()), 4, 8, 0);
				case 2:
					emerald.setCount(40);
					return new MerchantOffer(emerald, new ItemStack(NWItems.HULL_T4.get()), 4, 9, 0);
				default:
					emerald.setCount(40);
					return new MerchantOffer(emerald, emerald, new ItemStack(NWItems.HULL_T5.get()), 4, 10, 0);
				}
			case S1B:
				switch(rand.nextInt(3)) {
				case 1:
					tagged = blockToItem(NWBasicMethods.getRandomTaggedBlock("ships_t1", rand, NWBlocks.NW_SHIPS));
					return new MerchantOffer(new ItemStack(Items.IRON_INGOT, 32), tagged, 1, 20, 0);
				case 2:
					tagged = blockToItem(NWBasicMethods.getRandomTaggedBlock("ships_t2", rand, NWBlocks.NW_SHIPS));
					return new MerchantOffer(new ItemStack(Items.GOLD_INGOT, 48), tagged, 1, 30, 0);
				default:
					tagged = blockToItem(NWBasicMethods.getRandomTaggedBlock("ships_t3", rand, NWBlocks.NW_SHIPS));
					return new MerchantOffer(new ItemStack(Items.DIAMOND, 16), tagged, 1, 40, 0);
				}
			default:	//Default: S2B
				switch(rand.nextInt(3)) {
				case 1:
					tagged = blockToItem(NWBasicMethods.getRandomTaggedBlock("ships_t3", rand, NWBlocks.NW_SHIPS));
					return new MerchantOffer(new ItemStack(Items.DIAMOND, 16), tagged, 1, 40, 0);
				case 2:
					tagged = blockToItem(NWBasicMethods.getRandomTaggedBlock("ships_t4", rand, NWBlocks.NW_SHIPS));
					return new MerchantOffer(new ItemStack(Items.NETHERITE_INGOT, 8), tagged, 1, 50, 0);
				default:
					tagged = blockToItem(NWBasicMethods.getRandomTaggedBlock("ships_t5", rand, NWBlocks.NW_SHIPS));
					return new MerchantOffer(new ItemStack(Items.NETHER_STAR, 2), tagged, 1, 60, 0);
				}
			}
		}		
		
		private ItemStack blockToItem(Block block) {
			return block == null ? ItemStack.EMPTY : new ItemStack(block);
		}
	}
}
