package slimeattack07.naval_warfare.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import slimeattack07.naval_warfare.NavalWarfare;
import slimeattack07.naval_warfare.tileentity.BattleRecorderTE;
import slimeattack07.naval_warfare.tileentity.BattleViewerTE;
import slimeattack07.naval_warfare.tileentity.BoardTE;
import slimeattack07.naval_warfare.tileentity.DisappearingTE;
import slimeattack07.naval_warfare.tileentity.EnergyShieldTE;
import slimeattack07.naval_warfare.tileentity.GameControllerTE;
import slimeattack07.naval_warfare.tileentity.PassiveAbilityTE;
import slimeattack07.naval_warfare.tileentity.RandomShipTE;
import slimeattack07.naval_warfare.tileentity.ShipTE;

public class NWTileEntityTypes {
	public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister
			.create(ForgeRegistries.BLOCK_ENTITIES, NavalWarfare.MOD_ID);
	

	public static final RegistryObject<BlockEntityType<GameControllerTE>> GAME_CONTROLLER = TILE_ENTITY_TYPES
			.register("game_controller", () -> BlockEntityType.Builder
					.of(GameControllerTE::new, NWBlocks.GAME_CONTROLLER.get()).build(null));
	
	public static final RegistryObject<BlockEntityType<BoardTE>> BOARD = TILE_ENTITY_TYPES
			.register("board", () -> BlockEntityType.Builder
					.of(BoardTE::new, NWBlocks.BOARD.get(), NWBlocks.BOARD_REDIRECT.get()).build(null));
	
	public static final RegistryObject<BlockEntityType<ShipTE>> SHIP = TILE_ENTITY_TYPES
			.register("ship", () -> BlockEntityType.Builder
					.of(ShipTE::new, NWBlocks.getShips()).build(null));
	
	public static final RegistryObject<BlockEntityType<EnergyShieldTE>> ENERGY_SHIELD = TILE_ENTITY_TYPES
			.register("energy_shield", () -> BlockEntityType.Builder
					.of(EnergyShieldTE::new, NWBlocks.ENERGY_SHIELD.get()).build(null));
	
	public static final RegistryObject<BlockEntityType<PassiveAbilityTE>> PASSIVE_BLOCK = TILE_ENTITY_TYPES
			.register("passive_block", () -> BlockEntityType.Builder
					.of(PassiveAbilityTE::new, NWBlocks.TORPEDO_NET.get(), NWBlocks.ANTI_AIR.get()).build(null));
	
	public static final RegistryObject<BlockEntityType<DisappearingTE>> DISAPPEAR = TILE_ENTITY_TYPES
			.register("disappear", () -> BlockEntityType.Builder
					.of(DisappearingTE::new, NWBlocks.getAnimations()).build(null));
	
	public static final RegistryObject<BlockEntityType<RandomShipTE>> RANDOM_SHIP = TILE_ENTITY_TYPES
			.register("random_ship", () -> BlockEntityType.Builder
					.of(RandomShipTE::new, NWBlocks.RANDOM_SHIP.get()).build(null));
	
	public static final RegistryObject<BlockEntityType<BattleRecorderTE>> BATTLE_RECORDER = TILE_ENTITY_TYPES
			.register("battle_recorder", () -> BlockEntityType.Builder
					.of(BattleRecorderTE::new, NWBlocks.BATTLE_RECORDER.get()).build(null));
	
	public static final RegistryObject<BlockEntityType<BattleViewerTE>> BATTLE_VIEWER = TILE_ENTITY_TYPES
			.register("battle_viewer", () -> BlockEntityType.Builder
					.of(BattleViewerTE::new, NWBlocks.BATTLE_VIEWER.get()).build(null));
}
