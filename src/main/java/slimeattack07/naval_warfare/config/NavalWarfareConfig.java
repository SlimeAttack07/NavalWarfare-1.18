package slimeattack07.naval_warfare.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class NavalWarfareConfig {
	public static final ForgeConfigSpec SPEC;
	
	// Energy Values
	public static ForgeConfigSpec.IntValue energy_gain_rate; //Default: 5
	public static ForgeConfigSpec.IntValue base_energy; //Default: 7
	public static ForgeConfigSpec.IntValue max_energy; //Default: 16
	
	// Game Values
	public static ForgeConfigSpec.IntValue max_fleet_hp; //Default: 24
	public static ForgeConfigSpec.IntValue default_board_size; //Default: 100
	public static ForgeConfigSpec.IntValue max_turn_time; //Default: 1200
	public static ForgeConfigSpec.IntValue clear_board_time; //Default: 100
	public static ForgeConfigSpec.BooleanValue reveal_on_hit_passives; //Default: true
	public static ForgeConfigSpec.BooleanValue show_on_hit_passives; //Default: true
	public static ForgeConfigSpec.IntValue max_cons_timeouts; //Default: 3
	public static ForgeConfigSpec.IntValue max_timeouts; //Default: 5
	
	// Ability & Spell values
	public static ForgeConfigSpec.IntValue raft_timeout; //Default: 3
	public static ForgeConfigSpec.IntValue heatseaker_damage; //Default: 1
	public static ForgeConfigSpec.IntValue heatseaker_width; //Default: 3
	public static ForgeConfigSpec.IntValue heatseaker_length; //Default: 3
	public static ForgeConfigSpec.BooleanValue heatseaker_unblockable; //Default: true
	public static ForgeConfigSpec.IntValue magic_shield_health; //Default: 3
	public static ForgeConfigSpec.IntValue magic_shield_width; //Default: 4
	public static ForgeConfigSpec.IntValue magic_shield_length; //Default: 4
	public static ForgeConfigSpec.IntValue sonar_width; //Default: 5
	public static ForgeConfigSpec.IntValue sonar_length; //Default: 5
	
	// Structure Values
	public static ForgeConfigSpec.BooleanValue generate_captains_cabin; //Default: true
	public static ForgeConfigSpec.IntValue captains_cabin_av_dist; //Default: 10
	public static ForgeConfigSpec.IntValue captains_cabin_min_dist; //Default: 5
	public static ForgeConfigSpec.IntValue cc_ship_t1_weight; //Default: 60
	public static ForgeConfigSpec.IntValue cc_ship_t2_weight; //Default: 20
	public static ForgeConfigSpec.IntValue cc_ship_t3_weight; //Default: 10
	public static ForgeConfigSpec.IntValue cc_ship_t4_weight; //Default: 7
	public static ForgeConfigSpec.IntValue cc_ship_t5_weight; //Default: 3
	
	// Loot Values
	public static ForgeConfigSpec.BooleanValue modify_dungeon_loot; //Default: true
	public static ForgeConfigSpec.IntValue hull_t1_weight; //Default: 3
	public static ForgeConfigSpec.IntValue hull_t2_weight; //Default: 3
	public static ForgeConfigSpec.IntValue hull_t3_weight; //Default: 2
	public static ForgeConfigSpec.IntValue hull_t4_weight; //Default: 1
	public static ForgeConfigSpec.IntValue hull_t5_weight; //Default: 1
	public static ForgeConfigSpec.IntValue air_weight; //Default: 10
	
	static {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

		builder.push("NavalWarfare Config");
		builder.push("Energy Values");
		
		base_energy = builder.comment("Base Energy (Default: 7)").defineInRange("base_energy", 7, 0, 1000);
		max_energy = builder.comment("Maximum Energy (Default: 16)").defineInRange("max_energy", 16, 0, 1000);
		energy_gain_rate = builder.comment("Energy restored per round (Default: 5)").defineInRange("energy_gain_rate", 5, 0, 1000);
		
		builder.pop();
		builder.push("Ship Configuration Values");
		
		max_fleet_hp = builder.comment("Maximum fleet HP (Default: 24)").defineInRange("max_fleet_hp", 24, 2, 1000);
		
		builder.pop();
		builder.push("Game Values");
		
		default_board_size = builder.comment("Default board size (Default: 100)").defineInRange("default_board_size", 100, 2, 10000);
		max_turn_time = builder.comment("Maximum duration of a turn in ticks (Default: 1200, equal to 60 seconds)").
				defineInRange("max_turn_time", 1200, 20, 12000);
		clear_board_time = builder.comment("Time in ticks between game end and boards being removed (Default: 100, equal to 5 seconds)").
				defineInRange("clear_board_time", 100, 0, 1200);
		reveal_on_hit_passives = builder.comment("Whether or not 'On Hit' passive abilities should reveal their name instead of their category (Default: true)").
				define("reveal_on_hit_passives", true);
		show_on_hit_passives = builder.comment("Whether or not 'On Hit' passive abilities should their own animation instead of a default shell animation (Default: true)").
				define("show_on_hit_passives", true);
		max_cons_timeouts = builder.comment("Amount of consecutive timeouts to lose a game (Default: 3)").
				defineInRange("max_cons_timeouts", 3, 1, 100);
		max_timeouts = builder.comment("Amount of total timeouts to lose a game (Default: 5)").
				defineInRange("max_timeouts", 5, 1, 100);
		
		builder.pop();
		builder.push("Ability & Spell Values");
		
		raft_timeout = builder.comment("Amount of turns that a raft can stay alive (Default: 3)").defineInRange("raft_timeout", 3, 0, 100);
		heatseaker_damage = builder.comment("Damage dealt by Heatseaker spell (Default: 1)").defineInRange("heatseaker_damage", 1, 1, 10);
		heatseaker_width = builder.comment("Amount of horizontal tiles targeted by the Heatseaker spell (Default: 3)").
				defineInRange("heatseaker_width", 3, 1, 100);
		heatseaker_length = builder.comment("Amount of vertical tiles targeted by the Heatseaker spell (Default: 3)").
				defineInRange("heatseaker_length", 3, 1, 100);
		heatseaker_unblockable = builder.comment("Whether or not the Heatseaker spell ignores shields (Default: true)").define("heatseaker_unblockable", true);
		magic_shield_health = builder.comment("Magic shield HP (Default: 3)").defineInRange("magic_shield_health", 3, 1, 10);
		magic_shield_width = builder.comment("Amount of horizontal tiles protected by the Magic Shield spell (Default: 4)").
				defineInRange("magic_shield_width", 4, 1, 100);
		magic_shield_length = builder.comment("Amount of vertical tiles protected by the Magic Shield spell (Default: 4)").
				defineInRange("magic_shield_length", 4, 1, 100);
		sonar_width = builder.comment("Amount of horizontal tiles targeted by the Sonar spell (Default: 5)").
				defineInRange("sonar_width", 5, 1, 100);
		sonar_length = builder.comment("Amount of vertical tiles targeted by the Sonar spell (Default: 5)").
				defineInRange("sonar_length", 5, 1, 100);
		
		builder.pop();
		builder.push("Structure Values");
		
		generate_captains_cabin = builder.comment("Whether or not the Captain's Cabin structure generates (Default: true)").define("generate_captains_cabin", true);
		captains_cabin_av_dist = builder.comment("Captain's Cabin average distance apart in chunks between spawn attempts (Default: 50)").
				defineInRange("captains_cabin_av_dist", 50, 1, 100);
		captains_cabin_min_dist = builder.comment("Captain's Cabin minimum distance apart in chunks between spawn attempts, must be less than 'captains_cabin_av_dist' (Default: 30)").
				defineInRange("captains_cabin_min_dist", 30, 1, 100);
		cc_ship_t1_weight = builder.comment("Weight of 'Ship T1' spawning inside Captain's Cabin. Actual odds are 'cc_ship_t1_weight / (all 5 ship weights)' (Default: 60)").
				defineInRange("cc_ship_t1_weight", 60, 0, 1000);
		cc_ship_t2_weight = builder.comment("Weight of 'Ship T2' spawning inside Captain's Cabin. Actual odds are 'cc_ship_t2_weight / (all 5 ship weights)' (Default: 20)").
				defineInRange("cc_ship_t2_weight", 20, 0, 1000);
		cc_ship_t3_weight = builder.comment("Weight of 'Ship T3' spawning inside Captain's Cabin. Actual odds are 'cc_ship_t3_weight / (all 5 ship weights)' (Default: 10)").
				defineInRange("cc_ship_t3_weight", 10, 0, 1000);
		cc_ship_t4_weight = builder.comment("Weight of 'Ship T4' spawning inside Captain's Cabin. Actual odds are 'cc_ship_t4_weight / (all 5 ship weights)' (Default: 7)").
				defineInRange("cc_ship_t4_weight", 7, 0, 1000);
		cc_ship_t5_weight = builder.comment("Weight of 'Ship T5' spawning inside Captain's Cabin. Actual odds are 'cc_ship_t5_weight / (all 5 ship weights)' (Default: 3)").
				defineInRange("cc_ship_t5_weight", 3, 0, 1000);
		
		builder.pop();
		builder.push("Loot Values");
		
		modify_dungeon_loot = builder.comment("Whether or not Hulls are added as dungeon loot (Default: true)").define("modify_dungeon_loot", true);
		hull_t1_weight = builder.comment("Weight of 'Hull T1'. Actual odds are 'hull_t1_weight / (all 5 hull weights + air_weight)' (Default: 3)").
				defineInRange("hull_t1_weight", 3, 0, 1000);
		hull_t2_weight = builder.comment("Weight of 'Hull T2'. Actual odds are 'hull_t2_weight / (all 5 hull weights + air_weight)' (Default: 3)").
				defineInRange("hull_t2_weight", 3, 0, 1000);
		hull_t3_weight = builder.comment("Weight of 'Hull T3'. Actual odds are 'hull_t3_weight / (all 5 hull weights + air_weight)' (Default: 2)").
				defineInRange("hull_t3_weight", 2, 0, 1000);
		hull_t4_weight = builder.comment("Weight of 'Hull T4'. Actual odds are 'hull_t4_weight / (all 5 hull weights + air_weight)' (Default: 1)").
				defineInRange("hull_t4_weight", 1, 0, 1000);
		hull_t5_weight = builder.comment("Weight of 'Hull T5'. Actual odds are 'hull_t5_weight / (all 5 hull weights + air_weight)' (Default: 1)").
				defineInRange("hull_t5_weight", 1, 0, 1000);
		air_weight = builder.comment("Weight of nothing dropping. Actual odds are 'air_weight / (all 5 hull weights + air_weight)' (Default: 10)").
				defineInRange("air_weight", 10, 0, 1000);
		
		builder.pop();
		SPEC = builder.build();
	}
}
