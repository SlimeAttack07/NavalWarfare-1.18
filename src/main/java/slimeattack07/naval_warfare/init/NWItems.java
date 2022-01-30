package slimeattack07.naval_warfare.init;

import java.util.ArrayList;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import slimeattack07.naval_warfare.NavalWarfare;
import slimeattack07.naval_warfare.objects.items.AbilityWand;
import slimeattack07.naval_warfare.objects.items.GameInteractor;
import slimeattack07.naval_warfare.objects.items.InfoTool;
import slimeattack07.naval_warfare.objects.items.ShipConfiguration;
import slimeattack07.naval_warfare.objects.items.SpellWand;
import slimeattack07.naval_warfare.objects.items.StarterKit;
import slimeattack07.naval_warfare.objects.items.UtilityTool;
import slimeattack07.naval_warfare.util.InteractorType;

public class NWItems {

	// Tools
	public static final DeferredRegister<Item> NW_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, NavalWarfare.MOD_ID);
	
	public static final RegistryObject<Item> GAME_INTERACTOR = NW_ITEMS.register("game_interactor", () -> 
		new GameInteractor(InteractorType.NORMAL));
	public static final RegistryObject<Item> GAME_INTERACTOR_DUMMY = NW_ITEMS.register("game_interactor_dummy", () -> 
		new GameInteractor(InteractorType.DUMMY));
	public static final RegistryObject<Item> ABILITY_WAND = NW_ITEMS.register("ability_wand", () ->
		new AbilityWand());
	public static final RegistryObject<Item> SPELL_WAND = NW_ITEMS.register("spell_wand", () ->
		new SpellWand());
	public static final RegistryObject<Item> INFO_TOOL = NW_ITEMS.register("info_tool", () ->
		new InfoTool());
	public static final RegistryObject<Item> STARTER_KIT = NW_ITEMS.register("starter_kit", () ->
		new StarterKit());
	public static final RegistryObject<Item> UTILITY_TOOL = NW_ITEMS.register("utility_tool", () ->
		new UtilityTool());
	
	// Icons
	public static final RegistryObject<Item> SHIP_SUNK_OPPONENT = NW_ITEMS.register("icon_ship_sunk_opponent", () -> misc());
	public static final RegistryObject<Item> SHIP_SUNK_OWN = NW_ITEMS.register("icon_ship_sunk_own", () -> misc());
	public static final RegistryObject<Item> FIRE_DAMAGE = NW_ITEMS.register("icon_fire_damage", () -> misc());
	
	public static final RegistryObject<Item> TORPEDO = NW_ITEMS.register("icon_torpedo", () -> misc());
	public static final RegistryObject<Item> SALVO = NW_ITEMS.register("icon_salvo", () -> misc());
	public static final RegistryObject<Item> SPYGLASS = NW_ITEMS.register("icon_spyglass", () -> misc());
	public static final RegistryObject<Item> FLARE = NW_ITEMS.register("icon_flare", () -> misc());
	public static final RegistryObject<Item> MISSILE = NW_ITEMS.register("icon_missile", () -> misc());
	public static final RegistryObject<Item> COUNTERFIRE = NW_ITEMS.register("icon_counterfire", () -> misc());
	public static final RegistryObject<Item> RETALIATION = NW_ITEMS.register("icon_retaliation", () -> misc());
	public static final RegistryObject<Item> SUPPRESSION = NW_ITEMS.register("icon_suppression", () -> misc());
	public static final RegistryObject<Item> MORTAR = NW_ITEMS.register("icon_mortar", () -> misc());
	public static final RegistryObject<Item> END_TURN = NW_ITEMS.register("icon_end_turn", () -> misc());
	public static final RegistryObject<Item> ENERGY_START = NW_ITEMS.register("icon_energy_start", () -> misc());
	public static final RegistryObject<Item> ENERGY_GAIN = NW_ITEMS.register("icon_energy_gain", () -> misc());
	public static final RegistryObject<Item> ENERGY_GRANT = NW_ITEMS.register("icon_energy_grant", () -> misc());
	public static final RegistryObject<Item> WHIRLPOOL = NW_ITEMS.register("icon_whirlpool", () -> misc());
	public static final RegistryObject<Item> MINOR_INSIGHT = NW_ITEMS.register("icon_minor_insight", () -> misc());
	public static final RegistryObject<Item> MAJOR_INSIGHT = NW_ITEMS.register("icon_major_insight", () -> misc());
	public static final RegistryObject<Item> ENERGY_OVERLOADER = NW_ITEMS.register("icon_energy_overloader", () -> misc());
	public static final RegistryObject<Item> FRAG_BOMB = NW_ITEMS.register("icon_frag_bomb", () -> misc());
	public static final RegistryObject<Item> BOMBER = NW_ITEMS.register("icon_bomber", () -> misc());
	public static final RegistryObject<Item> SHIELD_PLACER = NW_ITEMS.register("icon_shield_placer", () -> misc());
	public static final RegistryObject<Item> GUARDIAN = NW_ITEMS.register("icon_guardian", () -> misc());
	public static final RegistryObject<Item> NAPALM = NW_ITEMS.register("icon_napalm", () -> misc());
	public static final RegistryObject<Item> SEA_MINE = NW_ITEMS.register("icon_sea_mine", () -> misc());
	public static final RegistryObject<Item> TURRET = NW_ITEMS.register("icon_turret", () -> misc());
	public static final RegistryObject<Item> GHOST_SHIP = NW_ITEMS.register("icon_ghost_ship", () -> misc());
	public static final RegistryObject<Item> TORPEDO_NET = NW_ITEMS.register("icon_torpedo_net", () -> misc());
	public static final RegistryObject<Item> ENERGY_SHIELD = NW_ITEMS.register("icon_energy_shield", () -> misc());
	public static final RegistryObject<Item> ANTI_AIR = NW_ITEMS.register("icon_anti_air", () -> misc());
	public static final RegistryObject<Item> SEAWORTHY = NW_ITEMS.register("icon_seaworthy", () -> misc());
	public static final RegistryObject<Item> DEPLOYABLE_AA = NW_ITEMS.register("icon_deployable_aa", () -> misc());
	public static final RegistryObject<Item> TORPEDO_DECOY = NW_ITEMS.register("icon_torpedo_decoy", () -> misc());
	
	public static final RegistryObject<Item> RAFT = NW_ITEMS.register("icon_raft", () -> misc());
	public static final RegistryObject<Item> HEATSEAKER = NW_ITEMS.register("icon_heatseaker", () -> misc());
	public static final RegistryObject<Item> SONAR = NW_ITEMS.register("icon_sonar", () -> misc());
	
	public static final RegistryObject<Item> BATTLESHIP = NW_ITEMS.register("icon_battleship", () -> misc());
	public static final RegistryObject<Item> DESTROYER = NW_ITEMS.register("icon_destroyer", () -> misc());
	public static final RegistryObject<Item> CARRIER = NW_ITEMS.register("icon_carrier", () -> misc());
	public static final RegistryObject<Item> SUBMARINE = NW_ITEMS.register("icon_submarine", () -> misc());
	public static final RegistryObject<Item> SCOUT = NW_ITEMS.register("icon_scout", () -> misc());
	public static final RegistryObject<Item> SUPPORT = NW_ITEMS.register("icon_support", () -> misc());
	public static final RegistryObject<Item> MOTHERSHIP = NW_ITEMS.register("icon_mothership", () -> misc());
	
	public static final RegistryObject<Item> ICON_PASTE = NW_ITEMS.register("icon_paste", () -> misc());
	
	public static final RegistryObject<Item> HULL_T1 = NW_ITEMS.register("hull_t1", () -> misc());
	public static final RegistryObject<Item> HULL_T2 = NW_ITEMS.register("hull_t2", () -> misc());
	public static final RegistryObject<Item> HULL_T3 = NW_ITEMS.register("hull_t3", () -> misc());
	public static final RegistryObject<Item> HULL_T4 = NW_ITEMS.register("hull_t4", () -> misc());
	public static final RegistryObject<Item> HULL_T5 = NW_ITEMS.register("hull_t5", () -> misc());
	public static final RegistryObject<Item> HULL_T6 = NW_ITEMS.register("hull_t6", () -> misc());
	
	public static final RegistryObject<Item> HULL_PART = NW_ITEMS.register("hull_part", () -> misc());
	public static final RegistryObject<Item> HULL_DOUBLE = NW_ITEMS.register("hull_double", () -> misc());
	public static final RegistryObject<Item> HULL_TRIPLE = NW_ITEMS.register("hull_triple", () -> misc());
	public static final RegistryObject<Item> HULL_QUADRUPLE = NW_ITEMS.register("hull_quadruple", () -> misc());
	public static final RegistryObject<Item> HULL_QUINTUPLE = NW_ITEMS.register("hull_quintuple", () -> misc());
	public static final RegistryObject<Item> HULL_SMALL_L = NW_ITEMS.register("hull_small_l", () -> misc());
	public static final RegistryObject<Item> HULL_MEDIUM_L = NW_ITEMS.register("hull_medium_l", () -> misc());
	public static final RegistryObject<Item> HULL_LARGE_L = NW_ITEMS.register("hull_large_l", () -> misc());
	public static final RegistryObject<Item> HULL_BIG_L = NW_ITEMS.register("hull_big_l", () -> misc());
	public static final RegistryObject<Item> HULL_SQUARE = NW_ITEMS.register("hull_square", () -> misc());
	public static final RegistryObject<Item> HULL_SMALL_T = NW_ITEMS.register("hull_small_t", () -> misc());
	public static final RegistryObject<Item> HULL_LARGE_T = NW_ITEMS.register("hull_large_t", () -> misc());
	public static final RegistryObject<Item> HULL_SMALL_ZIG = NW_ITEMS.register("hull_small_zig", () -> misc());
	public static final RegistryObject<Item> HULL_LARGE_ZIG = NW_ITEMS.register("hull_large_zig", () -> misc());
	public static final RegistryObject<Item> HULL_CROSS = NW_ITEMS.register("hull_cross", () -> misc());
	public static final RegistryObject<Item> HULL_SMALL_B = NW_ITEMS.register("hull_small_b", () -> misc());
	public static final RegistryObject<Item> HULL_MOTHER = NW_ITEMS.register("hull_mother", () -> misc());
	
	
	// Misc
	public static final RegistryObject<Item> SHIP_CONFIGURATION = NW_ITEMS.register("ship_configuration", () -> new ShipConfiguration());
	
	private static Item misc() {
		return new Item(new Item.Properties().tab(NavalWarfare.NW_MISC));
	}
	
	public static ArrayList<ItemStack> getHulls(){
		ArrayList<ItemStack> hulls = new ArrayList<>();
		
		hulls.add(new ItemStack(HULL_T1.get()));
		hulls.add(new ItemStack(HULL_T2.get()));
		hulls.add(new ItemStack(HULL_T3.get()));
		hulls.add(new ItemStack(HULL_T4.get()));
		hulls.add(new ItemStack(HULL_T5.get()));
		
		return hulls;
	}
}
