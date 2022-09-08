package slimeattack07.naval_warfare.init;

import java.util.ArrayList;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import slimeattack07.naval_warfare.NavalWarfare;
import slimeattack07.naval_warfare.objects.blocks.BattleRecorder;
import slimeattack07.naval_warfare.objects.blocks.BattleViewer;
import slimeattack07.naval_warfare.objects.blocks.BigLShip;
import slimeattack07.naval_warfare.objects.blocks.Board;
import slimeattack07.naval_warfare.objects.blocks.BoardFiller;
import slimeattack07.naval_warfare.objects.blocks.BoardRedirect;
import slimeattack07.naval_warfare.objects.blocks.CrossShip;
import slimeattack07.naval_warfare.objects.blocks.DirectionalDisBlock;
import slimeattack07.naval_warfare.objects.blocks.DisappearingBlock;
import slimeattack07.naval_warfare.objects.blocks.DotShip;
import slimeattack07.naval_warfare.objects.blocks.DoubleLineShip;
import slimeattack07.naval_warfare.objects.blocks.EnergyShieldBlock;
import slimeattack07.naval_warfare.objects.blocks.GameController;
import slimeattack07.naval_warfare.objects.blocks.LargeLShip;
import slimeattack07.naval_warfare.objects.blocks.LargeTShip;
import slimeattack07.naval_warfare.objects.blocks.LargeZigShip;
import slimeattack07.naval_warfare.objects.blocks.MediumLShip;
import slimeattack07.naval_warfare.objects.blocks.Mothership;
import slimeattack07.naval_warfare.objects.blocks.PassiveAbilityBlock;
import slimeattack07.naval_warfare.objects.blocks.QuadrupleLineShip;
import slimeattack07.naval_warfare.objects.blocks.QuintupleLineShip;
import slimeattack07.naval_warfare.objects.blocks.RandomShipBlock;
import slimeattack07.naval_warfare.objects.blocks.ShipMarkerBlock;
import slimeattack07.naval_warfare.objects.blocks.SmallBShip;
import slimeattack07.naval_warfare.objects.blocks.SmallLShip;
import slimeattack07.naval_warfare.objects.blocks.SmallTShip;
import slimeattack07.naval_warfare.objects.blocks.SmallZigShip;
import slimeattack07.naval_warfare.objects.blocks.SquareShip;
import slimeattack07.naval_warfare.objects.blocks.TripleLineShip;
import slimeattack07.naval_warfare.objects.blocks.variators.VariatorFiveDDB;
import slimeattack07.naval_warfare.objects.blocks.variators.VariatorTwelveShip;
import slimeattack07.naval_warfare.util.abilities.AntiAir;
import slimeattack07.naval_warfare.util.abilities.CounterFire;
import slimeattack07.naval_warfare.util.abilities.Deployable;
import slimeattack07.naval_warfare.util.abilities.EndTurn;
import slimeattack07.naval_warfare.util.abilities.EnergyIncrease;
import slimeattack07.naval_warfare.util.abilities.EnergyOverloader;
import slimeattack07.naval_warfare.util.abilities.EnergyShield;
import slimeattack07.naval_warfare.util.abilities.Flare;
import slimeattack07.naval_warfare.util.abilities.FragBomb;
import slimeattack07.naval_warfare.util.abilities.Guardian;
import slimeattack07.naval_warfare.util.abilities.Insight;
import slimeattack07.naval_warfare.util.abilities.Missile;
import slimeattack07.naval_warfare.util.abilities.MortarBomber;
import slimeattack07.naval_warfare.util.abilities.MultipleDeployable;
import slimeattack07.naval_warfare.util.abilities.Napalm;
import slimeattack07.naval_warfare.util.abilities.PassiveType;
import slimeattack07.naval_warfare.util.abilities.Salvo;
import slimeattack07.naval_warfare.util.abilities.Seaworthy;
import slimeattack07.naval_warfare.util.abilities.Sonar;
import slimeattack07.naval_warfare.util.abilities.Spyglass;
import slimeattack07.naval_warfare.util.abilities.Torpedo;
import slimeattack07.naval_warfare.util.abilities.TorpedoNet;
import slimeattack07.naval_warfare.util.abilities.Whirlpool;
import slimeattack07.naval_warfare.util.abilities.motherships.Amphion;

public class NWBlocks {

	public static final DeferredRegister<Block> NW_BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
			NavalWarfare.MOD_ID);
	
	public static final DeferredRegister<Block> NW_SHIPS = DeferredRegister.create(ForgeRegistries.BLOCKS,
			NavalWarfare.MOD_ID);
	
	public static final DeferredRegister<Block> NW_ANIMATIONS = DeferredRegister.create(ForgeRegistries.BLOCKS,
			NavalWarfare.MOD_ID);
	
	public static final DeferredRegister<Block> NW_DEPLOYABLES = DeferredRegister.create(ForgeRegistries.BLOCKS,
			NavalWarfare.MOD_ID);

	// Blocks
	public static final RegistryObject<Block> GAME_CONTROLLER = NW_BLOCKS.register("game_controller",
			() -> new GameController());
	
	public static final RegistryObject<Block> BOARD = NW_BLOCKS.register("board",
			() -> new Board());
	
	public static final RegistryObject<Block> BOARD_REDIRECT = NW_BLOCKS.register("board_redirect",
			() -> new BoardRedirect());
	
	public static final RegistryObject<Block> BOARD_FILLER = NW_BLOCKS.register("board_filler", () ->
		new BoardFiller());
	
	public static final RegistryObject<Block> TORPEDO_NET = NW_BLOCKS.register("torpedo_net", () -> 
		new PassiveAbilityBlock(Properties.copy(Blocks.COBWEB).strength(1000).noCollission().color(MaterialColor.COLOR_LIGHT_GREEN)));
	
	public static final RegistryObject<Block> ANTI_AIR = NW_BLOCKS.register("anti_air", () -> 
		new PassiveAbilityBlock(Properties.copy(Blocks.COBWEB).strength(1000).noCollission().color(MaterialColor.TERRACOTTA_ORANGE)));
	
	public static final RegistryObject<Block> ENERGY_SHIELD = NW_BLOCKS.register("energy_shield", () -> 
		new EnergyShieldBlock(Properties.copy(Blocks.COBWEB).strength(1000).noCollission().color(MaterialColor.COLOR_LIGHT_BLUE)));
	
	public static final RegistryObject<Block> SHIP_DISPLAY = NW_BLOCKS.register("ship_display", () -> 
		new Block(Properties.copy(Blocks.DARK_OAK_PLANKS)));
	
	public static final RegistryObject<Block> RANDOM_SHIP = NW_BLOCKS.register("random_ship", () ->
		new RandomShipBlock());
	
	public static final RegistryObject<Block> BATTLE_RECORDER = NW_BLOCKS.register("battle_recorder", () ->
		new BattleRecorder());
	
	public static final RegistryObject<Block> BATTLE_VIEWER = NW_BLOCKS.register("battle_viewer", () ->
		new BattleViewer());
	
	// Animations
	public static final RegistryObject<Block> SHIP_CLOSE = NW_BLOCKS.register("ship_close", () -> 
		new ShipMarkerBlock());

	public static final RegistryObject<Block> SHIP_HERE = NW_BLOCKS.register("ship_here", () -> 
		new ShipMarkerBlock());
	
	public static final RegistryObject<Block> AMPHION_ACTIVE = NW_BLOCKS.register("amphion_active", () -> 
		new DirectionalDisBlock(Properties.copy(Blocks.BEDROCK)));
	
	public static final RegistryObject<Block> SHELL = NW_ANIMATIONS.register("shell", () -> 
		new DisappearingBlock());
	public static final RegistryObject<Block> CANNONBALL = NW_ANIMATIONS.register("cannonball", () -> 
		new DisappearingBlock());
	public static final RegistryObject<Block> MISSILE = NW_ANIMATIONS.register("missile", () -> 
		new DisappearingBlock());
	public static final RegistryObject<Block> HEATSEAKER = NW_ANIMATIONS.register("heatseaker", () -> 
		new DisappearingBlock());
	public static final RegistryObject<Block> CYBELE_ACTIVE = NW_ANIMATIONS.register("cybele_active", () -> 
		new DisappearingBlock());
	public static final RegistryObject<Block> EQUILIBRIUM_PASSIVE = NW_ANIMATIONS.register("equilibrium_passive", () -> 
		new DisappearingBlock());
	public static final RegistryObject<Block> IGNITION_ACIVE = NW_ANIMATIONS.register("ignition_active", () -> 
		new DisappearingBlock());
	public static final RegistryObject<Block> LEOPARDS_WHELP_ACTIVE = NW_ANIMATIONS.register("leopards_whelp_active", () -> 
		new DisappearingBlock());
	public static final RegistryObject<Block> PYRRHUS_PASSIVE = NW_ANIMATIONS.register("pyrrhus_passive", () -> 
		new DisappearingBlock());
	public static final RegistryObject<Block> THE_SCORCHER_ACTIVE = NW_ANIMATIONS.register("the_scorcher_active", () -> 
		new DisappearingBlock());
	public static final RegistryObject<Block> VANQUISHER_ACTIVE = NW_ANIMATIONS.register("vanquisher_active", () -> 
		new DisappearingBlock());
	public static final RegistryObject<Block> VENDETTA_ACTIVE = NW_ANIMATIONS.register("vendetta_active", () -> 
		new DisappearingBlock());
	public static final RegistryObject<Block> VENDETTA_PASSIVE = NW_ANIMATIONS.register("vendetta_passive", () -> 
		new DisappearingBlock());
	public static final RegistryObject<Block> WARRIORS_ACTIVE = NW_ANIMATIONS.register("warriors_active", () -> 
		new DisappearingBlock());
	public static final RegistryObject<Block> WARRIORS_ACTIVE_FRAGMENTS = NW_ANIMATIONS.register("warriors_active_fragments", () -> 
		new DisappearingBlock());
	public static final RegistryObject<Block> CARCASS_PASSIVE = NW_ANIMATIONS.register("carcass_passive", () -> 
		new DisappearingBlock());
	public static final RegistryObject<Block> RAIDER_PASSIVE = NW_ANIMATIONS.register("raider_passive", () -> 
		new DisappearingBlock());
	public static final RegistryObject<Block> CYBELE_PASSIVE = NW_ANIMATIONS.register("cybele_passive", () -> 
		new DisappearingBlock());
	public static final RegistryObject<Block> NARVIK_ACTIVE_PASSIVE = NW_ANIMATIONS.register("narvik_active_passive", () -> 
		new DisappearingBlock());
	public static final RegistryObject<Block> MEEANNE_ACTIVE_PASSIVE = NW_ANIMATIONS.register("meeanne_active_passive", () -> 
		new DisappearingBlock());
	public static final RegistryObject<Block> GUSTAV_ACTIVE = NW_ANIMATIONS.register("gustav_active", () -> 
		new DisappearingBlock());
	public static final RegistryObject<Block> GUSTAV_ACTIVE_FRAGMENTS = NW_ANIMATIONS.register("gustav_active_fragments", () -> 
		new DisappearingBlock());
	public static final RegistryObject<Block> WIRNPA_ACTIVE = NW_ANIMATIONS.register("wirnpa_active", () -> 
		new DisappearingBlock());
	public static final RegistryObject<Block> GHOSTNOVA_PASSIVE = NW_ANIMATIONS.register("ghostnova_passive", () -> 
		new DisappearingBlock());
	public static final RegistryObject<Block> GLENEARN_PASSIVE = NW_ANIMATIONS.register("glenearn_passive", () -> 
		new DisappearingBlock());
	public static final RegistryObject<Block> AQUILON_ACTIVE = NW_ANIMATIONS.register("aquilon_active", () -> 
		new DisappearingBlock());
	public static final RegistryObject<Block> DEVASTATOR_ACTIVE = NW_ANIMATIONS.register("devastator_active", () -> 
		new DisappearingBlock());
	public static final RegistryObject<Block> MINI_NUKE = NW_ANIMATIONS.register("mini_nuke", () -> 
		new DisappearingBlock());
	public static final RegistryObject<Block> NEUTRON_ACTIVE = NW_ANIMATIONS.register("neutron_active", () -> 
		new DisappearingBlock());

	public static final RegistryObject<Block> CARCASS_ACTIVE = NW_ANIMATIONS.register("carcass_active", () -> 
		new DirectionalDisBlock());
	public static final RegistryObject<Block> FLARE = NW_ANIMATIONS.register("flare", () -> 
		new DirectionalDisBlock());
	public static final RegistryObject<Block> NIGHTHAWK_ACTIVE = NW_ANIMATIONS.register("nighthawk_active", () -> 
		new DirectionalDisBlock());
	public static final RegistryObject<Block> PSYCHE_PASSIVE = NW_ANIMATIONS.register("psyche_passive", () -> 
		new DirectionalDisBlock());
	public static final RegistryObject<Block> RAIDER_ACTIVE = NW_ANIMATIONS.register("raider_active", () -> 
		new DirectionalDisBlock());
	public static final RegistryObject<Block> SPYGLASS = NW_ANIMATIONS.register("spyglass", () -> 
		new DirectionalDisBlock());
	public static final RegistryObject<Block> THUNDERBOLT_ACTIVE = NW_ANIMATIONS.register("thunderbolt_active", () -> 
		new DirectionalDisBlock());
	public static final RegistryObject<Block> BOMB = NW_ANIMATIONS.register("bomb", () -> 
		new DirectionalDisBlock());
	public static final RegistryObject<Block> GLENEARN_ACTIVE = NW_ANIMATIONS.register("glenearn_active", () -> 
		new DirectionalDisBlock());
	public static final RegistryObject<Block> GHOSTNOVA_ACTIVE = NW_ANIMATIONS.register("ghostnova_active", () -> 
		new DirectionalDisBlock());
	public static final RegistryObject<Block> TOWERING_TERROR_ACTIVE = NW_ANIMATIONS.register("towering_terror_active", () -> 
		new DirectionalDisBlock());
	public static final RegistryObject<Block> HYPERION_ACTIVE_ACTIVE = NW_ANIMATIONS.register("hyperion_active_active", () -> 
		new DirectionalDisBlock());
	
	public static final RegistryObject<Block> AMPHION_FRAGMENT = NW_ANIMATIONS.register("amphion_fragment", () -> 
		new VariatorFiveDDB());
		
	
	// Deployables
	
	public static final RegistryObject<Block> NARVIK_ACTIVE = NW_DEPLOYABLES.register("narvik_active",
		() -> new DotShip(null, new CounterFire("explosive_flares", 4, NARVIK_ACTIVE_PASSIVE.get(), false), 2));
		
	public static final RegistryObject<Block> MEEANNE_ACTIVE = NW_DEPLOYABLES.register("meeanne_active",
			() -> new DotShip(null, new CounterFire("protected_area", 7, MEEANNE_ACTIVE_PASSIVE.get(), false), 3));
	
	public static final RegistryObject<Block> LANDGUARD_ACTIVE = NW_DEPLOYABLES.register("landguard_active",
			() -> new DotShip(new Salvo(1000, 8, 2, 3, SHELL.get(), "e54_salvo", false), null, 3));
	
	public static final RegistryObject<Block> PARADOX_ACTIVE = NW_DEPLOYABLES.register("paradox_active",
			() -> new TripleLineShip());
	
	public static final RegistryObject<Block> RAFT = NW_DEPLOYABLES.register("raft",
			() -> new DotShip());
	
	public static final RegistryObject<Block> AMARANTHUS_ACTIVE = NW_DEPLOYABLES.register("amaranthus_active",
			() -> new DotShip(null, new AntiAir("sam_hovercraft", "amaranthus_active", 1, 1, 1, 1, true), 1));
	
	public static final RegistryObject<Block> BARCROSS_ACTIVE = NW_DEPLOYABLES.register("barcross_active",
			() -> new DotShip(null, new TorpedoNet("torpedo_decoy", "barcross_active", 1, true), 1));
	
	public static final RegistryObject<Block> HYPERION_ACTIVE = NW_DEPLOYABLES.register("hyperion_active",
			() -> new DotShip(new MortarBomber(1000, 6, 3, 1, 2, 2, 2, "tactical_bombardment", HYPERION_ACTIVE_ACTIVE.get(), true), null, 3));
	
	public static final RegistryObject<Block> AVERNUS_PASSIVE = NW_DEPLOYABLES.register("avernus_passive",
			() -> new VariatorTwelveShip(null, null, 1));
	
	// Ships
	public static final RegistryObject<Block> DESTROYER = NW_SHIPS.register("destroyer",
			() -> new DoubleLineShip(null, null, 1));
	
	public static final RegistryObject<Block> CRUISER = NW_SHIPS.register("cruiser",
			() -> new TripleLineShip(null, null, 1));
	
	public static final RegistryObject<Block> SUBMARINE = NW_SHIPS.register("submarine",
			() -> new TripleLineShip(null, null, 1));
	
	public static final RegistryObject<Block> BATTLESHIP = NW_SHIPS.register("battleship",
			() -> new QuadrupleLineShip(null, null, 1));
	
	public static final RegistryObject<Block> AIRCRAFT_CARRIER = NW_SHIPS.register("aircraft_carrier",
			() -> new QuintupleLineShip(null, null, 1));
	
	public static final RegistryObject<Block> KILGOBNET = NW_SHIPS.register("kilgobnet",
			() -> new TripleLineShip(new Salvo(2, 5, 2, 2, CANNONBALL.get(), "burst_fire", false), 
					new CounterFire("counter_fire", 3, CANNONBALL.get(), false), 2));
	
	public static final RegistryObject<Block> VANQUISHER = NW_SHIPS.register("vanquisher",
			() -> new QuadrupleLineShip(new Missile(1, 16, 10, "absolute_overkill", VANQUISHER_ACTIVE.get()), 
					new CounterFire("wildfire", 8, CANNONBALL.get(), true), 5));
	
	public static final RegistryObject<Block> CYBELE = NW_SHIPS.register("cybele",
			() -> new DoubleLineShip(new EnergyOverloader(3, 2, "charged_shell", CYBELE_ACTIVE.get()), 
					new CounterFire("energy_outlash", 2, CYBELE_PASSIVE.get(), false), 2));
	
	public static final RegistryObject<Block> CARCASS = NW_SHIPS.register("carcass",
			() -> new TripleLineShip(new Torpedo(1, 8, 7, 3, 1, "bone_torpedo", CARCASS_ACTIVE.get()), 
					new CounterFire("wreckage_ejection", 3, CARCASS_PASSIVE.get(), false), 4));
	
	public static final RegistryObject<Block> BOUNDLESS = NW_SHIPS.register("boundless",
			() -> new SquareShip(new MortarBomber(2, 8, 4, 2, 2, 2, 2, "mortar", CANNONBALL.get(), false), 
					new EnergyIncrease("lifesaver", 0, 6, PassiveType.DESTROYED), 3));
	
	public static final RegistryObject<Block> NARVIK = NW_SHIPS.register("narvik",
			() -> new MediumLShip(new Deployable(4, 6, "deploy_bait_ship", NARVIK_ACTIVE.get(), false), 
					new TorpedoNet("nanocarbon_net", "narvik", 3, false), 3));
	
	public static final RegistryObject<Block> THE_ULTIMATUM = NW_SHIPS.register("the_ultimatum",
			() -> new QuintupleLineShip(new Missile(8, 12, 2, "everlasting_missiles", MISSILE.get()),
					new EndTurn("suppressing_crystals"), 5));
	
	public static final RegistryObject<Block> THE_SCORCHER = NW_SHIPS.register("the_scorcher",
			() -> new TripleLineShip(new Napalm(2, 12, "inferno", THE_SCORCHER_ACTIVE.get()), 
					new Seaworthy("menacing", 5), 3));
	
	public static final RegistryObject<Block> MAORI = NW_SHIPS.register("maori",
			() -> new TripleLineShip(new Spyglass(2, 6, 3, 3, "spyglass"), new Seaworthy("seaworthy", 7), 2));
	
	public static final RegistryObject<Block> EQUILIBRIUM = NW_SHIPS.register("equilibrium",
			() -> new CrossShip(new EnergyIncrease("sailors_gratitude", 2, 4, PassiveType.NOT), 
					new Whirlpool("lightshow", 10, EQUILIBRIUM_PASSIVE.get()), 3));
	
	public static final RegistryObject<Block> LANDGUARD = NW_SHIPS.register("landguard",
			() -> new QuadrupleLineShip(new Deployable(2, 10, "deploy_e54", LANDGUARD_ACTIVE.get(), false), 
					new AntiAir("anti_air_battery", "landguard", 4, 3, 2, 2, false), 4));
	
	public static final RegistryObject<Block> PSYCHE = NW_SHIPS.register("psyche",
			() -> new DoubleLineShip(new Flare(2, 6, 2, 2, 2, 2, "glowstone_powder"),
					new Insight("blinding_explosion", PSYCHE_PASSIVE.get(), true), 4));
	
	public static final RegistryObject<Block> NIGHTHAWK = NW_SHIPS.register("nighthawk",
			() -> new SquareShip(new MortarBomber(2, 6, 3, 1, 2, 2, 3, "bombing_run", NIGHTHAWK_ACTIVE.get(), true), 
					new Seaworthy("old_but_gold", 10), 2));
	
	public static final RegistryObject<Block> GOLIATH = NW_SHIPS.register("goliath",
			() -> new QuintupleLineShip(new Salvo(1, 12, 5, 6, CANNONBALL.get(), "no_escape", false), 
					new CounterFire("sore_loser", 10, CANNONBALL.get(), true), 5));
	
	public static final RegistryObject<Block> PATHFINDER = NW_SHIPS.register("pathfinder",
			() -> new TripleLineShip(new Spyglass(3, 4, 1, 2, "tactical_scout"), 
					new Seaworthy("aerial_supremacy", 6), 2));
	
	public static final RegistryObject<Block> PARADOX = NW_SHIPS.register("paradox",
			() -> new BigLShip(new Deployable(1, 14, "haunting_past", PARADOX_ACTIVE.get(), false), 
					new EnergyIncrease("dying_breath", 0, 6, PassiveType.DESTROYED), 3));
	
	public static final RegistryObject<Block> PYRRHUS = NW_SHIPS.register("pyrrhus",
			() -> new TripleLineShip(new Missile(2, 8, 2, "missile", MISSILE.get()), 
					new Missile(3, "missile_barrage", PYRRHUS_PASSIVE.get()), 4));
	
	public static final RegistryObject<Block> POOLE = NW_SHIPS.register("poole",
			() -> new SquareShip(new EnergyIncrease("healing_burst", 2, 3, PassiveType.NOT), 
					new EnergyIncrease("energized_water", 0, 4, PassiveType.START_GAME), 2));
	
	public static final RegistryObject<Block> RAIDER = NW_SHIPS.register("raider",
			() -> new TripleLineShip(new Torpedo(2, 6, 5, 1, 1, "torpedo", RAIDER_ACTIVE.get()), 
					new CounterFire("shattering_hull", 2, RAIDER_PASSIVE.get(), false), 3));
	
	public static final RegistryObject<Block> THUNDERBOLT = NW_SHIPS.register("thunderbolt",
			() -> new QuadrupleLineShip(new MortarBomber(2, 8, 5, 1, 2, 3, 4, "precision_strike", THUNDERBOLT_ACTIVE.get(), true), 
					new AntiAir("anti_air_guns", "thunderbolt", 4, 3, 2, 2, false), 3));
	
	public static final RegistryObject<Block> LEOPARDS_WHELP = NW_SHIPS.register("leopards_whelp",
			() -> new SmallLShip(new EnergyOverloader(2, 3, "energetic_cannonball", LEOPARDS_WHELP_ACTIVE.get()), 
					new Guardian("guardian", "leopards_whelp_passive", 2, 2), 5));
	
	public static final RegistryObject<Block> WARRIORS = NW_SHIPS.register("warriors",
			() -> new LargeLShip(new FragBomb(2, 8, 3, "shrapnel_bomb", WARRIORS_ACTIVE.get(), WARRIORS_ACTIVE_FRAGMENTS.get()), 
					new CounterFire("final_honor", 8, CANNONBALL.get(), true), 3));
	
	public static final RegistryObject<Block> TYRANT = NW_SHIPS.register("tyrant",
			() -> new QuintupleLineShip(new MortarBomber(2, 9, 6, 2, 3, 2, 3, "trebuchet", CANNONBALL.get(), false), 
					new CounterFire("tit_for_tat", 3, CANNONBALL.get(), false), 4));
	
	public static final RegistryObject<Block> VERVAIN = NW_SHIPS.register("vervain",
			() -> new TripleLineShip(new Missile(2, 7, 2, "missile", MISSILE.get()), 
					new EnergyShield("shield_generator", "vervain", 3, 3, 3, 2, 2), 4));
	
	public static final RegistryObject<Block> MEEANNE = NW_SHIPS.register("meeanne",
			() -> new SmallZigShip(new Deployable(2, 14, "create_protected_area", MEEANNE_ACTIVE.get(), false), 
					new Seaworthy("endangered_species", 10), 4));
	
	public static final RegistryObject<Block> IGNITION = NW_SHIPS.register("ignition",
			() -> new DoubleLineShip(new Napalm(1, 8, "fireball", IGNITION_ACIVE.get()), 
					new Seaworthy("seaworthy", 8), 3));
	
	public static final RegistryObject<Block> SAFEGUARD = NW_SHIPS.register("safeguard",
			() -> new SmallTShip(new EnergyShield(1, 12, "shield_projector", "safeguard_active", 2, 2, 2, 2, 2), 
					new EnergyShield("mini_shield_dome", "safeguard", 3, 2, 2, 2, 2), 5));
	
	public static final RegistryObject<Block> CASTLEREAGH = NW_SHIPS.register("castlereagh",
			() -> new SquareShip(new MortarBomber(1, 16, 9, 3, 3, 3, 3, "mega_mortar", CANNONBALL.get(), false), 
					new EnergyIncrease("energetic_release", 0, 4, PassiveType.DESTROYED), 4));
	
	public static final RegistryObject<Block> GALLANT = NW_SHIPS.register("gallant",
			() -> new QuadrupleLineShip(new Salvo(2, 8, 3, 3, CANNONBALL.get(), "salvo", false), 
					new TorpedoNet("protective_net", "gallant", 5, false), 3));
	
	public static final RegistryObject<Block> VENDETTA = NW_SHIPS.register("vendetta",
			() -> new TripleLineShip(new Missile(1, 12, 3, "crude_missile", VENDETTA_ACTIVE.get()), 
					new Missile(2, "self_destruct", VENDETTA_PASSIVE.get()), 3));
	
	public static final RegistryObject<Block> BARCROSS = NW_SHIPS.register("barcross",
			() -> new QuadrupleLineShip(new Deployable(2, 8, "torpedo_decoy", BARCROSS_ACTIVE.get(), false), 
					new Insight("intel_share", SPYGLASS.get(), false), 4));
	
	public static final RegistryObject<Block> AMARANTHUS = NW_SHIPS.register("amaranthus",
			() -> new LargeTShip(new Deployable(2, 8, "sam_hovercraft", AMARANTHUS_ACTIVE.get(), false), 
					new CounterFire("missile_madness", 7, MISSILE.get(), true), 4));
	
	public static final RegistryObject<Block> GUSTAV = NW_SHIPS.register("gustav",
			() -> new LargeZigShip(new FragBomb(2, 16, 8, "mega_shell", GUSTAV_ACTIVE.get() , GUSTAV_ACTIVE_FRAGMENTS.get()), 
					new CounterFire("cover_fire", 3, SHELL.get(), false), 5));
	
	public static final RegistryObject<Block> STERLING = NW_SHIPS.register("sterling",
			() -> new TripleLineShip(new Salvo(4, 5, 2, 1,CANNONBALL.get(), "burst_fire", true), 
					new EndTurn("smoke_screen"), 2));
	
	public static final RegistryObject<Block> WIRNPA = NW_SHIPS.register("wirnpa",
			() -> new QuadrupleLineShip(new Missile(2, 10, 2, "spikeball", WIRNPA_ACTIVE.get()), 
					new Seaworthy("old_but_gold", 6), 3));
	
	public static final RegistryObject<Block> GLENEARN = NW_SHIPS.register("glenearn",
			() -> new SmallBShip(new MortarBomber(2, 12, 7, 2, 2, 2, 3, "bombardment", GLENEARN_ACTIVE.get(), true), 
					new CounterFire("crash_landing", 2, GLENEARN_PASSIVE.get(), true), 4));
	
	public static final RegistryObject<Block> DULVERTON = NW_SHIPS.register("dulverton",
			() -> new LargeLShip(new MortarBomber(2, 13, 7, 2, 3, 2, 3, "large_cannon", CANNONBALL.get(), false), 
					new CounterFire("counterfire", 3, SHELL.get(), false), 4));
	
	public static final RegistryObject<Block> EPHIRA = NW_SHIPS.register("ephira",
			() -> new LargeTShip(new MortarBomber(1, 16, 12, 3, 4, 3, 4, "bomb_hatch", BOMB.get(), false), 
					new CounterFire("counterfire", 3, CANNONBALL.get(), false), 4));
	
	public static final RegistryObject<Block> GHOSTNOVA = NW_SHIPS.register("ghostnova",
			() -> new CrossShip(new Torpedo(1, 16, 100, 1, 100, "thruster_drill", GHOSTNOVA_ACTIVE.get()), 
					new Missile(2, "kamikaze", GHOSTNOVA_PASSIVE.get()), 5));
	
	public static final RegistryObject<Block> ARETHUSE = NW_SHIPS.register("arethuse",
			() -> new QuintupleLineShip(new Salvo(2, 7, 2, 3, CANNONBALL.get(), "deterrent", false), 
					new AntiAir("upwards_fire", "arethuse", 4, 4, 2, 2, false), 5));
	
	public static final RegistryObject<Block> AVERNUS = NW_SHIPS.register("avernus",
			() -> new QuintupleLineShip(new EnergyIncrease("update_position", 4, 3, PassiveType.NOT), 
					new MultipleDeployable(1, 0, "cargo_chaos", AVERNUS_PASSIVE.get(), true, 3), 5));
	
	public static final RegistryObject<Block> DEVASTATOR = NW_SHIPS.register("devastator",
			() -> new QuadrupleLineShip(new Missile(2, 16, 4, "devastate", DEVASTATOR_ACTIVE.get()), 
					new EndTurn("speedster"), 5));
	
	public static final RegistryObject<Block> TOWERING_TERROR = NW_SHIPS.register("towering_terror",
			() -> new SquareShip(new Salvo(3, 6, 2, 2, TOWERING_TERROR_ACTIVE.get(), "flyby", true), 
					new EnergyShield("shielded", "towering_terror", 2, 2, 1, 1, 2), 4));
	
	public static final RegistryObject<Block> TRYPHON = NW_SHIPS.register("tryphon",
			() -> new TripleLineShip(new Sonar(2, 6, 3, 2, 3, 2, "scanner"), 
					new Seaworthy("hidden", 6), 3));
	
	public static final RegistryObject<Block> NEUTRON = NW_SHIPS.register("neutron",
			() -> new QuintupleLineShip(new FragBomb(2, 9, 4, "scouting_fire", NEUTRON_ACTIVE.get(), SHELL.get()), 
					new Insight("radar", null, false), 3));
	
	public static final RegistryObject<Block> AQUILON = NW_SHIPS.register("aquilon",
			() -> new TripleLineShip(new Salvo(1, 16, 6, 5, AQUILON_ACTIVE.get(), "energy_railgun", true), 
					new EnergyIncrease("energy_reserves", 1, 8, PassiveType.START_GAME), 5));
	
	public static final RegistryObject<Block> HYPERION = NW_SHIPS.register("hyperion",
			() -> new QuintupleLineShip(new Deployable(2, 14, "air_support", HYPERION_ACTIVE.get(), false), 
					new EnergyIncrease("supply_spill", 1, 6, PassiveType.DESTROYED), 4));
	
	// Motherships
	
	public static final RegistryObject<Block> AMPHION = NW_SHIPS.register("amphion",
			() -> new Mothership(new Amphion("stealth_bomber", AMPHION_ACTIVE.get(), MINI_NUKE.get(), null, false), 
					new Amphion("reactor_meltdown", null, null, AMPHION_FRAGMENT.get(), true)));
	
	
	public static Block[] getShips(){
		ArrayList<Block> ships = new ArrayList<>();
		
		NW_SHIPS.getEntries().forEach(obj -> ships.add(obj.get()));
		
		return ships.toArray(new Block[0]);
	}
	
	public static ResourceLocation rl(String location) {
		return new ResourceLocation(NavalWarfare.MOD_ID, location);
	}
	
	public static Block[] getAnimations() {
		ArrayList<Block> blocks = new ArrayList<>();
		
		NW_ANIMATIONS.getEntries().forEach(obj -> blocks.add(obj.get()));
		
		return blocks.toArray(new Block[0]);
	}
	
	public static ArrayList<ItemStack> getTiles(){
		ArrayList<ItemStack> tiles = new ArrayList<>();
		
		tiles.add(new ItemStack(BOARD.get()));
		tiles.add(new ItemStack(BOARD_REDIRECT.get()));
		tiles.add(new ItemStack(BOARD_FILLER.get()));
		
		return tiles;
	}
	
	public static ArrayList<ItemStack> getAnimationItems(){
		ArrayList<ItemStack> animations = new ArrayList<>();
		
		NW_ANIMATIONS.getEntries().forEach(obj -> animations.add(new ItemStack(obj.get())));
		
		return animations;
	}
	
	public static ArrayList<ItemStack> getShipItems(){
		ArrayList<ItemStack> ships = new ArrayList<>();
		
		NW_SHIPS.getEntries().forEach(obj -> ships.add(new ItemStack(obj.get())));
		
		return ships;
	}
	
	public static ArrayList<ItemStack> getDeployableItems(){
		ArrayList<ItemStack> deployables = new ArrayList<>();
		
		NW_DEPLOYABLES.getEntries().forEach(obj -> deployables.add(new ItemStack(obj.get())));
		
		deployables.add(new ItemStack(TORPEDO_NET.get()));
		deployables.add(new ItemStack(ENERGY_SHIELD.get()));
		deployables.add(new ItemStack(ANTI_AIR.get()));
		
		return deployables;
	}
}
