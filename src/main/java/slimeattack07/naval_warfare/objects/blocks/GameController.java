package slimeattack07.naval_warfare.objects.blocks;

import java.util.ArrayList;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.ForgeRegistries;
import slimeattack07.naval_warfare.NavalWarfare;
import slimeattack07.naval_warfare.config.NavalWarfareConfig;
import slimeattack07.naval_warfare.init.NWBlocks;
import slimeattack07.naval_warfare.init.NWItems;
import slimeattack07.naval_warfare.init.NWSounds;
import slimeattack07.naval_warfare.init.NWStats;
import slimeattack07.naval_warfare.init.NWTileEntityTypes;
import slimeattack07.naval_warfare.init.NWTriggers;
import slimeattack07.naval_warfare.objects.items.AbilityWand;
import slimeattack07.naval_warfare.objects.items.GameInteractor;
import slimeattack07.naval_warfare.objects.items.ShipConfiguration;
import slimeattack07.naval_warfare.objects.items.SpellWand;
import slimeattack07.naval_warfare.tileentity.BoardTE;
import slimeattack07.naval_warfare.tileentity.GameControllerTE;
import slimeattack07.naval_warfare.util.ControllerState;
import slimeattack07.naval_warfare.util.InteractorMode;
import slimeattack07.naval_warfare.util.NWBasicMethods;
import slimeattack07.naval_warfare.util.abilities.Ability;
import slimeattack07.naval_warfare.util.helpers.ControllerActionHelper;
import slimeattack07.naval_warfare.util.helpers.ShipSaveHelper;
import slimeattack07.naval_warfare.util.properties.ControllerStateProperty;

public class GameController extends Block implements EntityBlock{
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final ControllerStateProperty STATE = ControllerStateProperty.create();
	public static final int DEFAULT_BOARD_SIZE = NavalWarfareConfig.default_board_size.get(); // Default: 100
	public static final ArrayList<BlockPos> CONTROLLERS = new ArrayList<>();
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(FACING, context.getHorizontalDirection());
	}

	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING);
		builder.add(STATE);
	}
	
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
			BlockEntityType<T> type) {
		return level.isClientSide() ? null : (l, s, pos, tile) -> ((GameControllerTE) tile).tick();
	}

	public GameController() {
		super(Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(0.8f, 2).explosionResistance(1000));
		registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(STATE, ControllerState.INACTIVE));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return NWTileEntityTypes.GAME_CONTROLLER.get().create(pos, state);
	}
	
	public void handleInteraction(BlockState state, Level level, BlockPos pos,
			Player player, ItemStack itemstack) {
		
		GameInteractor interactor = (GameInteractor) itemstack.getItem();
		BlockEntity tile = level.getBlockEntity(pos);
		
		if(tile instanceof GameControllerTE) {
			GameControllerTE te = (GameControllerTE) tile;
			
			if(!te.hasOwner() && player == null)
				te.setOwner("dummy");
			
			if(!ownsBlock(player, te.getOwner())) {
				NWBasicMethods.messagePlayer(player, "message.naval_warfare.not_owner_c");
				return;
			}
			
			if(te.hasRegInBuffer()) {
				NWBasicMethods.messagePlayer(player, "message.naval_warfare.still_randomizing");
				return;
			}
			
			InteractorMode mode = interactor.getMode(itemstack);
			ControllerState c_state = getState(state);
			
			switch(mode) {
			case LOAD_SHIP_CONFIG:
				if(validateInteraction(c_state, player, mode)) {
					if(te.hasBoard())
						removeBoardAndShips(level, te);
					
					level.setBlockAndUpdate(pos, state.setValue(STATE, ControllerState.EDIT_CONFIG));
					te.resetBoardValues();
				}
				else
					NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.controller_wrong_state");
				break;
			case NEW_SHIP_CONFIG:
				if(validateInteraction(c_state, player, mode)) {
					if(te.hasBoard()) 
						removeBoardAndShips(level, te);
					
					te.resetBoardValues();
					spawnBoard(level, player, te, te.getBlockPos(), DEFAULT_BOARD_SIZE, false);
					interactor.setMode(itemstack, InteractorMode.SAVE_CONFIG);
					level.setBlockAndUpdate(pos, state.setValue(STATE, ControllerState.EDIT_CONFIG));
				}
				else
					NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.controller_wrong_state");
				break;
			case TARGET_TILE:
				NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.unsupported_operation");
				break;
			case SAVE_CONFIG:
				if(validateInteraction(c_state, player, mode)) {
					ItemStack config = saveConfig(level, state, te.getZero(), te.board_size, te.getName(), player);
					
					if(config == null)
						break;
					
					interactor.setMode(itemstack, InteractorMode.NEW_SHIP_CONFIG);
					NWBasicMethods.messagePlayerActionbarBack(player, "message.naval_warfare.config_saved", ": " + 
							config.getDisplayName().getString().replace("[", "").replace("]", ""));
					level.setBlockAndUpdate(pos, state.setValue(STATE, ControllerState.INACTIVE));
					NWBasicMethods.addOrSpawn(player, config, level, pos);

					removeBoardAndShips(level, te);
					te.resetBoardValues();
				}
				else
					NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.controller_wrong_state");
				break;
			case REQUEST_GAME:
				if(validateInteraction(c_state, player, mode) && readyForGame(te, player)) {
					ItemStack config = saveConfig(level, state, te.getZero(), te.board_size, te.getName(), player);
					
					if(config == null)
						return;
					
					if(!requestGame(level, player, pos))
						level.setBlockAndUpdate(pos, state.setValue(STATE, ControllerState.SEARCHING));
				}
				else
					NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.controller_wrong_state");
				break;
			case CANCEL_REQUEST_GAME:
				if(validateInteraction(c_state, player, mode)) {
					if(requestCancelGame(level, player, pos))
						level.setBlockAndUpdate(pos, state.setValue(STATE, ControllerState.EDIT_CONFIG));
				}
				else
					NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.controller_wrong_state");
				break;
			case FORFEIT_GAME:
				if(validateInteraction(c_state, player, mode))
					forfeitGame(level, player, pos);
				else
					NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.controller_wrong_state");
				break;
			default:
				NavalWarfare.LOGGER.warn("Ignoring unhandled interactor mode in game controller, report this to the mod author! Mode is: " + mode);
				break;
			}
		}
	}
	
	public boolean activateActiveAbility(Level level, Player player, BlockPos pos, BoardTE board, Ability ability, ItemStack wand, boolean spell) {
		if(validateController(level, pos)) {
			GameControllerTE te = (GameControllerTE) level.getBlockEntity(pos);
			
			if(!ownsBlock(player, te.getOwner())) {
				NWBasicMethods.messagePlayer(player, "message.naval_warfare.not_owner_c");
				return true;
			}
			
			if(te.action_time > 0) {
				NWBasicMethods.messagePlayer(player, "message.naval_warfare.busy");
				return true;
			}
			
			if(te.hasGame() && te.hasTurn()) {
				if(!spell) {
					AbilityWand awand = (AbilityWand) wand.getItem();
					awand.decreaseActiveAmount(level, wand);
				}
				
				String translation = spell ? "spell.naval_warfare.activated" : "ability.naval_warfare.activated";
				
				NWBasicMethods.messagePlayerAbilityUsed(player, translation, player.getName().getString(), 
						ability.hoverableInfo());
				NWBasicMethods.messagePlayerTitle(pos, player, level, translation, "dark_green", 
						ability.getTranslation(), "green");
				NWBasicMethods.animateItemUse(player, ability.getAnimationItem());
				
				BlockPos opos = te.getOpponent();
				
				if(validateController(level, opos)) {
					GameControllerTE ote = (GameControllerTE) level.getBlockEntity(opos);
					String oowner = ote.getOwner();
					
					if(!oowner.equals("dummy")) {
						UUID uuid = UUID.fromString(oowner);
						Player opponent = level.getPlayerByUUID(uuid);
						NWBasicMethods.messagePlayerAbilityUsed(opponent, translation, player.getName().getString(), 
								ability.hoverableInfo());
						NWBasicMethods.messagePlayerTitle(opos, opponent, level, translation, "dark_green", 
								ability.getTranslation(), "green");
						NWBasicMethods.animateItemUse(opponent, ability.getAnimationItem());
					}
				}
				
				if(!spell) {
					te.consumeEnergy(ability.energyCost());
					te.addAction(ControllerActionHelper.createAbility(((AbilityWand) wand.getItem()).getShip(wand), board.getBlockPos(),
							player.getStringUUID(), true));
				}
				else {
					SpellWand swand = (SpellWand) wand.getItem();
					te.addAction(ControllerActionHelper.createSpell(swand.getSpell(wand), board.getBlockPos(), player.getStringUUID()));
				}		
				
				statsAndAdvancements(player, spell);
				
				return true;
			}
			else {
				NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.not_turn");
				return true;
			}
		}
		
		return false;
	}
	
	private void statsAndAdvancements(Player player, boolean spell) {
		if(!(player instanceof ServerPlayer))
			return;
		
		ServerPlayer sp = (ServerPlayer) player;
		
		if(spell) {
			try {
				sp.awardStat(NWStats.SPELLS_USED);
				NWTriggers.SPELL.trigger(sp);
				
				Stat<ResourceLocation> stat = Stats.CUSTOM.get(new ResourceLocation(NavalWarfare.MOD_ID, "spells_used"), StatFormatter.DEFAULT);
				int val = sp.getStats().getValue(stat);
				
				if(val >= 10)
					NWTriggers.SPELL_10.trigger(sp);
				if(val >= 50)
					NWTriggers.SPELL_50.trigger(sp);

			} catch(NullPointerException e) {
				NavalWarfare.LOGGER.error("Failed to fetch the spells used statistic.");
			}
		} else {
			try {
				sp.awardStat(NWStats.ACTIVE_USED);
				NWTriggers.ACTIVE.trigger(sp);
				
				Stat<ResourceLocation> stat = Stats.CUSTOM.get(new ResourceLocation(NavalWarfare.MOD_ID, "active_used"), StatFormatter.DEFAULT);
				int val = sp.getStats().getValue(stat);
				
				if(val >= 50)
					NWTriggers.ACTIVE_50.trigger(sp);
				if(val >= 100)
					NWTriggers.ACTIVE_100.trigger(sp);

			} catch(NullPointerException e) {
				NavalWarfare.LOGGER.error("Failed to fetch the active used statistic.");
			}
		}
	}
	
	private boolean readyForGame(GameControllerTE te, Player player) {
		if(te.getHP() <= 0 || te.getBoardSize() <= 0) {
			if(player != null)
				NWBasicMethods.messagePlayer(player, "message.naval_warfare.needs_valid_board");
			
			return false;
		}
		
		return true;
	}
	
	/** Checks if the given interaction can be performed in the given state
	 * 
	 * @param state The state of the controller
	 * @param player The player that initiated the interaction (can be null)
	 * @param mode The mode of the interactor
	 * @return True if the interaction can be performed, false otherwise.
	 */
	private boolean validateInteraction(ControllerState state, @Nullable Player player, InteractorMode mode) {
		switch(state) {
		case EDIT_CONFIG:
			return mode.equals(InteractorMode.SAVE_CONFIG) || mode.equals(InteractorMode.NEW_SHIP_CONFIG) || mode.equals(InteractorMode.LOAD_SHIP_CONFIG)
					|| mode.equals(InteractorMode.REQUEST_GAME);
		case INACTIVE:
			return mode.equals(InteractorMode.NEW_SHIP_CONFIG) || mode.equals(InteractorMode.LOAD_SHIP_CONFIG);
		case PLAYING_GAME:
			return mode.equals(InteractorMode.FORFEIT_GAME);
		case SEARCHING:
			return mode.equals(InteractorMode.CANCEL_REQUEST_GAME);
		default:
			NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.illegal_action");
			return false;
		}
	}
	
	private boolean spawnOpponentBoard(Level level, Player player, GameControllerTE te, int own_size, int opp_size) {
		double sqrt = Math.sqrt(own_size);
		int offset = (int) Math.ceil(sqrt) + 2;
		Direction dir = getFacing(te.getBlockState());
		
		return spawnBoard(level, player, te, te.getBlockPos().relative(dir, offset), opp_size, true);
	}
	
	private void startGame(Level level, GameControllerTE own_te, GameControllerTE opp_te) {
		Player own_p = own_te.getOwner().equals("dummy") ? null : level.getPlayerByUUID(UUID.fromString(own_te.owner));
		Player opp_p = opp_te.getOwner().equals("dummy") ? null : level.getPlayerByUUID(UUID.fromString(opp_te.owner));
		boolean spawned_own_opp = spawnOpponentBoard(level, own_p, own_te, own_te.getBoardSize(), opp_te.getBoardSize());
		boolean spawned_opp_opp = spawnOpponentBoard(level, opp_p, opp_te, opp_te.getBoardSize(), own_te.getBoardSize());
		
		if(!spawned_own_opp || !spawned_opp_opp) {
			if(!spawned_own_opp) {
				NWBasicMethods.messagePlayer(own_p, "message.naval_warfare.game_start_failure_own");
				NWBasicMethods.messagePlayer(opp_p, "message.naval_warfare.game_start_failure_opponent");
			}
			
			if(!spawned_opp_opp) {
				NWBasicMethods.messagePlayer(own_p, "message.naval_warfare.game_start_failure_opponent");
				NWBasicMethods.messagePlayer(opp_p, "message.naval_warfare.game_start_failure_own");
			}
			
			removeBoardAndShips(level, own_te);
			removeBoardAndShips(level, opp_te);
			level.setBlockAndUpdate(own_te.getBlockPos(), own_te.getBlockState().setValue(STATE, ControllerState.EDIT_CONFIG));
			level.setBlockAndUpdate(opp_te.getBlockPos(), opp_te.getBlockState().setValue(STATE, ControllerState.EDIT_CONFIG));
			
			return;
		}
		
		level.setBlockAndUpdate(own_te.getBlockPos(), own_te.getBlockState().setValue(STATE, ControllerState.PLAYING_GAME));
		level.setBlockAndUpdate(opp_te.getBlockPos(), opp_te.getBlockState().setValue(STATE, ControllerState.PLAYING_GAME));
		own_te.setOpponent(opp_te.getBlockPos());
		opp_te.setOpponent(own_te.getBlockPos());
		own_te.setGame(true);
		opp_te.setGame(true);
		own_te.action_number = 0;
		opp_te.action_number = 0;
		
		own_te.cons_timeout_times = 0;
		opp_te.cons_timeout_times = 0;
		own_te.timeout_times = 0;
		opp_te.timeout_times = 0;
		
		own_te.deployStartingPassiveAbilities(level, own_p);
		opp_te.deployStartingPassiveAbilities(level, opp_p);
		
		int own_hp = own_te.getHP();
		int opp_hp = opp_te.getHP();
		boolean you_start = own_hp <= opp_hp;
		
		own_te.setEnergy(GameControllerTE.BASE_ENERGY);
		opp_te.setEnergy(GameControllerTE.BASE_ENERGY);
		own_te.setTurn(you_start, false);
		opp_te.setTurn(!you_start, false);
		
		String energy = NWBasicMethods.getTranslation("message.naval_warfare.energy_start").replace("MARKER1", "" + GameControllerTE.BASE_ENERGY);
		
		if(you_start) {
			if(own_p != null) {
				NWBasicMethods.sendGameStatusToPlayer(level, own_te.getBlockPos(), own_te.getOwner(), "message.naval_warfare.game_found", "blue",
					"message.naval_warfare.your_turn_first", "aqua");
				NWBasicMethods.messagePlayerCustom(own_p, ChatFormatting.YELLOW + energy);
				own_p.playNotifySound(NWSounds.GAME_FOUND.get(), SoundSource.MASTER, 1, 1);
			}
			if(opp_p != null) {
				NWBasicMethods.sendGameStatusToPlayer(level, opp_te.getBlockPos(), opp_te.getOwner(), "message.naval_warfare.game_found", "blue",
					"message.naval_warfare.opponent_turn_first", "aqua");
				opp_p.playNotifySound(NWSounds.GAME_FOUND.get(), SoundSource.MASTER, 1, 1);
			}
		}
		else {
			if(own_p != null) {
				NWBasicMethods.sendGameStatusToPlayer(level, own_te.getBlockPos(), own_te.getOwner(), "message.naval_warfare.game_found", "blue",
					"message.naval_warfare.opponent_turn_first", "aqua");
				own_p.playNotifySound(NWSounds.GAME_FOUND.get(), SoundSource.MASTER, 1, 1);
			}
			if(opp_p != null) {
				NWBasicMethods.sendGameStatusToPlayer(level, opp_te.getBlockPos(), opp_te.getOwner(), "message.naval_warfare.game_found", "blue",
					"message.naval_warfare.your_turn_first", "aqua");
				NWBasicMethods.messagePlayerCustom(opp_p, ChatFormatting.YELLOW + energy);
				opp_p.playNotifySound(NWSounds.GAME_FOUND.get(), SoundSource.MASTER, 1, 1);
			}
		}
		
		ArrayList<ShipSaveHelper> own_ships = collectShips(level, own_te.zero);
		ArrayList<ShipSaveHelper> opp_ships = collectShips(level, opp_te.zero);
		
		own_te.recordOnRecorder(own_ships, opp_ships, own_te.board_size, opp_te.board_size);
		opp_te.recordOnRecorder(opp_ships, own_ships, opp_te.board_size, own_te.board_size);
	}
	
	private ArrayList<ShipSaveHelper> collectShips(Level level, BlockPos zero) {
		BlockEntity tile = level.getBlockEntity(zero);
		
		if(tile instanceof BoardTE) {
			BoardTE bte = (BoardTE) tile;
			Board board = (Board) bte.getBlockState().getBlock();
			
			return bte.collectShips(level, zero, board.getControllerFacing(level, bte.getBlockPos()), null);
		}
		
		return new ArrayList<>();
	}
	
	private boolean requestGame(Level level, Player player, BlockPos pos) {		
		if(!CONTROLLERS.contains(pos))
			CONTROLLERS.add(pos);
		
		if(player != null)
			NWBasicMethods.messagePlayer(player, "message.naval_warfare.requested_game");
		
		if(CONTROLLERS.size() > 1) {
			BlockPos p1 = CONTROLLERS.get(0);
			BlockPos p2 = CONTROLLERS.get(1);
			
			if(!validateController(level, p1)) {
				CONTROLLERS.remove(p1);
				return false;
			}
			
			if(!validateController(level, p2)) {
				CONTROLLERS.remove(p2);
				return false;
			}
			
			BlockState s1 = level.getBlockState(p1);
			GameControllerTE t1 = (GameControllerTE) level.getBlockEntity(p1);
			GameControllerTE t2 = (GameControllerTE) level.getBlockEntity(p2);
			GameController c = (GameController) s1.getBlock();
			
			c.startGame(level, t1, t2);
			CONTROLLERS.remove(p1);
			CONTROLLERS.remove(p2);
			return true;
		}
		
		return false;
	}
	
	public void forfeitGame(Level level, Player player, BlockPos pos) {
		if(!validateController(level, pos))
			return;
		
		GameControllerTE te_own = (GameControllerTE) level.getBlockEntity(pos);
		BlockPos opponent = te_own.getOpponent();
		
		if(!validateController(level, opponent))
			return;
		
		GameControllerTE te_opp = (GameControllerTE) level.getBlockEntity(opponent);
		
		String op_string = te_opp.getOwner();
		Player opp_player = op_string.equals("dummy") ? null : level.getPlayerByUUID(UUID.fromString(op_string));
		
		NWBasicMethods.messagePlayer(player, "message.naval_warfare.forfeit_own");
		NWBasicMethods.messagePlayer(opp_player, "message.naval_warfare.forfeit_opponent");
		
		te_own.endGame(level, te_own, false, player, false);
		te_opp.endGame(level, te_opp, true, opp_player, true);
	}
	
	private void forfeitGame(Level level, Player player, GameControllerTE te_own) {
		BlockPos opponent = te_own.getOpponent();
		
		if(!validateController(level, opponent)) {
			level.removeBlockEntity(te_own.getBlockPos());
			return;
		}
		
		GameControllerTE te_opp = (GameControllerTE) level.getBlockEntity(opponent);
		
		String op_string = te_opp.getOwner();
		Player opp_player = op_string.equals("dummy") ? null : level.getPlayerByUUID(UUID.fromString(op_string));
		
		NWBasicMethods.messagePlayer(player, "message.naval_warfare.forfeit_own");
		NWBasicMethods.messagePlayer(opp_player, "message.naval_warfare.forfeit_opponent");
		
		te_own.endGame(level, te_own, false, player, false, false);
		te_opp.endGame(level, te_opp, true, opp_player, true);
		
		level.removeBlockEntity(te_own.getBlockPos());
	}
	
	private boolean requestCancelGame(Level level, Player player, BlockPos pos) {
		boolean cancelled = !CONTROLLERS.contains(pos) || CONTROLLERS.remove(pos);
		
		if(cancelled)
			NWBasicMethods.messagePlayer(player, "message.naval_warfare.cancelled_request_game");
		
		return cancelled;
	}
	
	public boolean validateController(Level level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		
		if(!(state.getBlock() instanceof GameController))
			return false;
		
		if(state.hasBlockEntity()) {
			BlockEntity tile = level.getBlockEntity(pos);
			
			return tile instanceof GameControllerTE;
		}
		
		return false;
	}
	
	public void removeBoardAndShips(Level level, GameControllerTE te) {
		removeBoardAndShips(level, te, true);
	}
	
	public void removeBoardAndShips(Level level, GameControllerTE te, boolean ships) {
		BlockPos pos = te.getZero();
		BlockState state = level.getBlockState(pos);
		
		if(state.getBlock() instanceof Board) {
			Board board = (Board) state.getBlock();
			board.removeBoardAndShips(level, pos, ships);
		}
	}
	
	public InteractionResult handleShipConfig(BlockState state, Level level, BlockPos pos, 
			Player player, ItemStack itemstack) {
		
		if(!validateController(level, pos))
			return InteractionResult.SUCCESS;
		
		GameControllerTE te = (GameControllerTE) level.getBlockEntity(pos);
		
		switch(getState(state)) {
		case EDIT_CONFIG:{
			if(!te.clear_board) {
				if(te.hasBoard() && !te.clear_board)
					removeBoardAndShips(level, te);
				handleShipConfigLoad(state, level, pos, player, itemstack, te);
				return InteractionResult.SUCCESS;
			}
		}
		default:
			NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.config_unavailable");
			return InteractionResult.SUCCESS;
		}
	}
	
	public void handleShipConfigLoad(BlockState state, Level level, BlockPos pos, 
			Player player, ItemStack itemstack, GameControllerTE te) {
		
		ShipConfiguration config = (ShipConfiguration) itemstack.getItem();
			
		if(!ownsBlock(player, te.getOwner())) {
			NWBasicMethods.messagePlayer(player, "message.naval_warfare.not_owner_c");
			return;
		}
		
		if(getState(state).equals(ControllerState.EDIT_CONFIG) && config.isValid(itemstack)) {
			CompoundTag nbt = itemstack.getOrCreateTag();
			CompoundTag nw = nbt.getCompound(NavalWarfare.MOD_ID);
			int board_size = nw.getInt("board_size");
			boolean spawned = spawnBoard(level, player, te, te.getBlockPos(), board_size, false);
			
			if(!spawned) 
				return;
			
			Direction dir = getFacing(state);
			BlockPos zero = te.getZero();
			BlockState bstate = level.getBlockState(zero);
			BoardTE bte;
			
			if(bstate.hasBlockEntity()) {
				BlockEntity t = level.getBlockEntity(zero);
				
				if(t instanceof BoardTE)
					bte = (BoardTE) t;
				else
					return;
			}
			else
				return;
			
			Direction facing = Direction.valueOf(nw.getString("facing").toUpperCase());
			ArrayList<ShipSaveHelper> ssh = config.readShips(nw);
			
			for(ShipSaveHelper ship : ssh) {
				Block block = ForgeRegistries.BLOCKS.getValue(ship.getShip());
				
				if(block != null && block instanceof ShipBlock) {
					ShipBlock ship_block = (ShipBlock) block;
					BlockPos ship_pos = bte.locateId(zero, ship.getPos(), dir).above();
					spawned = ship_block.summonShip(level, ship_pos, ship_block.defaultBlockState().
							setValue(FACING, NWBasicMethods.rotateToMatch(facing, dir, ship.getDir())), true, false);
					
					if(!spawned) {
						removeBoardAndShips(level, te);
						te.clearRegistered();
						NWBasicMethods.messagePlayerCustom(player, NWBasicMethods.getTranslation("message.naval_warfare.ship_spawn_failure").
								replace("MARKER1", ship_pos.toShortString()));
					}
					
					te.register(ship_block.getRegistryName().toString());
				}
			}
			
			te.setBoardSize(board_size);
			te.setHP(nw.getInt("hp"));
			String disname = itemstack.getDisplayName().getString().replace("[", "").replace("]", "");
			te.setName(disname);
			
			NWBasicMethods.messagePlayerActionbarBack(player, "message.naval_warfare.config_loaded", ": " + disname);
		}
	}
	
	private ItemStack saveConfig(Level level, BlockState state, BlockPos pos,  int board_size, String name, Player player) {
		Direction dir = getFacing(state);
		BlockState bstate = level.getBlockState(pos);
		
		if(bstate.hasBlockEntity()) {
			BlockEntity tile = level.getBlockEntity(pos);
			
			if(tile instanceof BoardTE) {
				BoardTE te = (BoardTE) tile;
				ArrayList<ShipSaveHelper> ssh = te.collectShips(level, pos, dir, player);
				
				if(ssh != null) {
					if(validateController(level, te.getController())) {
						GameControllerTE cte = (GameControllerTE) level.getBlockEntity(te.getController());
						
						if(cte.getRegistered() == null) {
							NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.empty_config");
							return null;
						}
						
						if(cte.getRegistered().size() != ssh.size()) {
							NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.missing_registered");
							return null;
						}
					}
				}
				else 
					return null;
				
				ItemStack stack = new ItemStack(NWItems.SHIP_CONFIGURATION.get());
				ShipConfiguration sc = (ShipConfiguration) stack.getItem();
				sc.saveShipConfiguration(stack, board_size, ssh, dir);
				stack.setHoverName(new TextComponent(name));
				
				return stack;
			}
		}
		
		return null;
	}
	
	public boolean spawnBoard(Level level, Player player, GameControllerTE tile, BlockPos location, int amount, boolean redirect) {
		Direction dir = getFacing(tile.getBlockState());
		double root = Math.sqrt(amount);
		double floor = Math.floor(root);
		double diff = root - floor;
		int zlength = diff == 0 ? (int) floor : (int) floor + 1;
		int xlength = diff >= 0.5 ? (int) floor + 1 : (int) floor;
		int current = 0;
		
		int center = xlength / 2;
		BlockPos cube_origin = nextPosToCheck(dir, location, -center, 1);
		
		if(!spaceChecker(level, player, dir, cube_origin, xlength, zlength))
			return false;
		
		BlockState state = redirect ? NWBlocks.BOARD_REDIRECT.get().defaultBlockState() : NWBlocks.BOARD.get().defaultBlockState();
		BlockState filler = NWBlocks.BOARD_FILLER.get().defaultBlockState();
		
		// Main Board
		for(int z = 0; z < zlength; z++) {
			for(int x = 0; x < xlength; x++) {				
				BlockPos next = nextPosToCheck(dir, cube_origin, x, z);
				
				if(current < amount) {
					level.setBlockAndUpdate(next, state);
					current++;
				}
				else
					level.setBlockAndUpdate(next, filler);
			}
		}
		
		// Surrounding with filler tiles
		for(int x = -1; x < xlength; x++) {
			BlockPos next = nextPosToCheck(dir, cube_origin, x, -1);
			setFillerUnlessController(level, next);
			next = nextPosToCheck(dir, cube_origin, x, zlength);
			setFillerUnlessController(level, next);
		}
		
		for(int z = -1; z < zlength ; z++) {
			BlockPos next = nextPosToCheck(dir, cube_origin, -1, z);
			setFillerUnlessController(level, next);
			next = nextPosToCheck(dir, cube_origin, xlength, z);
			setFillerUnlessController(level, next);
		}
		
		BlockPos next = nextPosToCheck(dir, cube_origin, xlength, zlength);
		setFillerUnlessController(level, next);

		// Setting tile entity data
		BlockState board = level.getBlockState(cube_origin);
		
		if(board.hasBlockEntity()) {
			BlockEntity btile = level.getBlockEntity(cube_origin);
			
			if(btile instanceof BoardTE) {
				BoardTE bte = (BoardTE) btile;
				bte.setControllerAndId(tile.getBlockPos(), 0, dir);
			}
		}
		
		if(redirect)
			tile.setOpponentZero(cube_origin);
		else {
			tile.setZero(cube_origin);
			tile.setBoardSize(amount);
		}
		
		return true;
	}
	
	private void setFillerUnlessController(Level level, BlockPos pos) {
		if(!(level.getBlockState(pos).getBlock() instanceof GameController))
			level.setBlockAndUpdate(pos, NWBlocks.BOARD_FILLER.get().defaultBlockState());
	}
	
	public Direction getFacing(BlockState state) {
		try {
			return state.getValue(FACING);
		} catch(IllegalArgumentException e){
			return Direction.NORTH;
		}
	}
	
	public ControllerState getState(BlockState state) {
		try {
			return state.getValue(STATE);
		} catch(IllegalArgumentException e){
			return ControllerState.INACTIVE;
		}
	}
	
	private BlockPos nextPosToCheck(Direction dir, BlockPos origin, int x, int z) {
		int y = origin.getY();
		
		switch (dir) {
		case NORTH:
			return new BlockPos(origin.getX() - x, y, origin.getZ() - z);
		case EAST:
			return new BlockPos(origin.getX() + z, y, origin.getZ() - x);
		case SOUTH:
			return new BlockPos(origin.getX() + x, y, origin.getZ() + z);
		case WEST:
			return new BlockPos(origin.getX() - z, y, origin.getZ() + x);

		default:
			return origin;
		}
	}
	
	private boolean spaceChecker(Level level, Player player, Direction dir, BlockPos origin, int xdim, int zdim) {
		for(int z = -1; z < zdim + 1; z++) {
			for(int x = -1; x < xdim + 1; x++) {
				BlockPos pos = nextPosToCheck(dir, origin, x, z);
				Block block = level.getBlockState(pos).getBlock();

				if(!(block.equals(Blocks.AIR) || block.equals(NWBlocks.GAME_CONTROLLER.get()))) {
					if(player != null) {
						String translation = NWBasicMethods.getTranslation("message.naval_warfare.board_size_not_clear");
						translation = translation.replace("MARKER1", pos.getX() + "").replace("MARKER2", pos.getY() + "")
								.replace("MARKER3", pos.getZ() + "").replace("MARKER4", block.getRegistryName().toString());
						
						NWBasicMethods.messagePlayerCustom(player, translation);
					}
					return false;
				}
			}
		}
		
		return true;
	}
	
	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if(level.isClientSide())
			return;
		
		if(newState.getBlock() instanceof GameController)
			return;
		
		Direction dir = getFacing(state);
		BlockPos board_pos = pos.relative(dir);
		BlockState board = level.getBlockState(board_pos);
		
		if(CONTROLLERS.contains(pos))
			CONTROLLERS.remove(pos);
		
		if(board.getBlock() instanceof Board)
			level.removeBlock(board_pos, false);
		
		if(state.hasBlockEntity()) {
			BlockEntity tile = level.getBlockEntity(pos);
			
			if(tile instanceof GameControllerTE) {
				GameControllerTE te = (GameControllerTE) tile;
				
				if(te.playing_game) {
					String owner = te.getOwner();
					Player player = null;
					
					if(!owner.equals("dummy"))
						player = level.getPlayerByUUID(UUID.fromString(owner));
					
					forfeitGame(level, player, te);
					return;
				}
			}
			
			level.removeBlockEntity(pos);
		}
	}
	
	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if(level.isClientSide())
			return;

		if(state.hasBlockEntity()) {
			BlockEntity tile = level.getBlockEntity(pos);
			
			if(tile instanceof GameControllerTE) {
				GameControllerTE te = (GameControllerTE) tile;
				
				if(placer instanceof Player) {
					Player player = (Player) placer;
					te.setOwner(player.getStringUUID());
				}
			}
		}
	}
	
	public boolean ownsBlock(Player player, String owner) {
		String uuid = "dummy";
		
		if(player != null)
			uuid = player.getStringUUID();
		
		return uuid.equals(owner) || owner.equals("dummy");
	}
	
	public BoardTE getOpponentBoardTile(Level level, GameControllerTE te, int id, boolean yours) {
		BlockPos pos = te.getOpponent();
		
		if(validateController(level, pos)) {
			GameControllerTE ote = (GameControllerTE) level.getBlockEntity(pos);
			BlockPos zpos = ote.getZero();
			BlockState zstate = level.getBlockState(zpos);
			
			if(zstate.getBlock() instanceof Board && zstate.hasBlockEntity()) {
				BlockEntity ztile = level.getBlockEntity(zpos);
				
				if(ztile instanceof BoardTE) {
					BoardTE zte = (BoardTE) ztile;
					GameController ocontroller = (GameController) ote.getBlockState().getBlock();
					BlockPos ipos = zte.locateId(zpos, id, ocontroller.getFacing(ote.getBlockState()), yours);
					
					if(ipos == null) 
						return null;
					
					BlockState istate = level.getBlockState(ipos);
					
					if(istate.getBlock() instanceof Board && istate.hasBlockEntity()) {
						BlockEntity itile = level.getBlockEntity(ipos);
						
						if(itile instanceof BoardTE)
							return (BoardTE) itile;
					}
				}
			}
		}
		
		return null;
	}
	
	public BoardTE getBoardTile(Level level, GameControllerTE te, int id) {		
		BlockEntity tile = level.getBlockEntity(te.getZero());
		
		if(tile instanceof BoardTE) {
			BoardTE bte = (BoardTE) tile;
			BlockPos pos = bte.locateId(bte.getBlockPos(), id, getFacing(te.getBlockState()));
			Board board = (Board) bte.getBlockState().getBlock();
			
			if(board.validateBoard(level, pos))
				return (BoardTE) level.getBlockEntity(pos);
		}
		
		return null;
	}
}
