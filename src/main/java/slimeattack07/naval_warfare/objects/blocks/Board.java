package slimeattack07.naval_warfare.objects.blocks;

import java.util.ArrayList;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import slimeattack07.naval_warfare.init.NWBlocks;
import slimeattack07.naval_warfare.init.NWItems;
import slimeattack07.naval_warfare.init.NWSounds;
import slimeattack07.naval_warfare.init.NWTileEntityTypes;
import slimeattack07.naval_warfare.objects.items.GameInteractor;
import slimeattack07.naval_warfare.tileentity.BoardTE;
import slimeattack07.naval_warfare.tileentity.GameControllerTE;
import slimeattack07.naval_warfare.util.BoardState;
import slimeattack07.naval_warfare.util.ControllerState;
import slimeattack07.naval_warfare.util.HitResult;
import slimeattack07.naval_warfare.util.NWBasicMethods;
import slimeattack07.naval_warfare.util.TargetType;
import slimeattack07.naval_warfare.util.helpers.ControllerActionHelper;
import slimeattack07.naval_warfare.util.helpers.ShipRevealHelper;
import slimeattack07.naval_warfare.util.properties.BoardStateProperty;

public class Board extends Block implements EntityBlock{
	public static final BoardStateProperty STATE = BoardStateProperty.create();
	
	public Board() {
		super(Properties.of(Material.STONE).strength(1000).color(MaterialColor.COLOR_BLUE));
		
		registerDefaultState(defaultBlockState().setValue(STATE, BoardState.NEUTRAL));
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(STATE, BoardState.NEUTRAL);
	}

	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(STATE);
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return NWTileEntityTypes.BOARD.get().create(pos, state);
	}
	
	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if(level.isClientSide())
			return;
		
		if(placer instanceof Player) {
			Player player = (Player) placer;
			NWBasicMethods.messagePlayer(player, "message.naval_warfare.dont_place");
			level.removeBlock(pos, false);
		}
	}
	
	/** Handles interactions with the Game Interactor
	 * 
	 * @param state
	 * @param level
	 * @param pos The block that the interactor was redirected to (should be a Board)
	 * @param player
	 * @param stack The game interactor
	 * @param matching The block that the interactor was used on (should be a BoardRedirect)
	 */
	public void handleInteraction(BlockState state, Level level, BlockPos pos, Player player,
			ItemStack stack, BlockPos matching) {
		
		if (level.isClientSide())
			return;
		
		BlockEntity tile = level.getBlockEntity(pos);
		
		if(!(tile instanceof BoardTE))
			return;
		
		BoardTE te = (BoardTE) tile;
		
		GameInteractor interactor = (GameInteractor) stack.getItem();
		
		switch(interactor.getMode(stack)) {
		case TARGET_TILE:
			targetTile(level, player, state, pos, te, matching, true, 1, TargetType.NORMAL);
			
			return;
		default:
			NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.unsupported_operation");
			return;
		}
	}
	
	/** Highlights/selects two tiles
	 * 
	 * @param level
	 * @param pos
	 * @param matching
	 */
	public void selectTile(Level level, BlockPos pos, BlockPos matching) {
		BlockState state = level.getBlockState(pos);
		BlockState matching_state = level.getBlockState(matching);
		
		level.setBlockAndUpdate(pos, state.setValue(STATE, getBoardState(state).select()));
		level.setBlockAndUpdate(matching, matching_state.setValue(STATE, getBoardState(matching_state).select()));
	}
	
	/** Unhighlights/deselects two tiles
	 * 
	 * @param level
	 * @param pos
	 * @param matching
	 */
	public void deselectTile(Level level, BlockPos pos, BlockPos matching) {
		BlockState state = level.getBlockState(pos);
		BlockState matching_state = level.getBlockState(matching);
		
		level.setBlockAndUpdate(pos, state.setValue(STATE, getBoardState(state).deselect()));
		level.setBlockAndUpdate(matching, matching_state.setValue(STATE, getBoardState(matching_state).deselect()));
	}
	
	/** Tries to match each tile in a list to the matching opponent tile.
	 * 
	 * @param level
	 * @param tiles
	 * @return The list of matched tiles. Should be equal to tiles.size(), but may shorter if errors occur.
	 */
	public ArrayList<BoardTE> matchTiles(Level level, ArrayList<BoardTE> tiles, boolean yours) {
		ArrayList<BoardTE> matching = new ArrayList<>();
		GameControllerTE controller = null;
		
		for(BoardTE tile : tiles) {
			if(controller == null) {
				controller = getController(level, tile.getBlockPos());
				
				// If getting the controller fails, then don't do anything
				if(controller == null)
					return matching;
			}
			
			GameController control = (GameController) controller.getBlockState().getBlock();
			BoardTE te = control.getOpponentBoardTile(level, controller, tile.getId(), yours);
			
			if(te != null)
				matching.add(te);
		}
		
		return matching;
	}
	
	/** Select all tiles in two lists of tiles. The second list is automatically computed based on the first list.
	 * 
	 * @param level
	 * @param tiles The first list of tiles to select
	 */
	public void selectTiles(Level level, ArrayList<BoardTE> tiles) {
		ArrayList<BoardTE> matching = matchTiles(level, tiles, false);
		
		if(tiles.size() != matching.size())
			return;
		
		for(int i = 0; i < tiles.size(); i++)
			selectTile(level, tiles.get(i).getBlockPos(), matching.get(i).getBlockPos());
	}
	
	/** Deselect all tiles in two lists of tiles. The second list is automatically computed based on the first list.
	 * 
	 * @param level
	 * @param tiles The first list of tiles to deselect
	 */
	public void deselectTiles(Level level, ArrayList<BoardTE> tiles) {
		ArrayList<BoardTE> matching = matchTiles(level, tiles, false);
		
		if(tiles.size() != matching.size())
			return;
		
		for(int i = 0; i < tiles.size(); i++)
			deselectTile(level, tiles.get(i).getBlockPos(), matching.get(i).getBlockPos());
	}
	
	/** Manually target a board tile. This will only play the animation and inform the game controller that it should target something.
	 * 
	 * @param level
	 * @param player
	 * @param state
	 * @param pos The tile to target
	 * @param te The tile entity of the target (here for convenience)
	 * @param matching The matching tile on the opponent's side
	 * @param message Whether or not info messages are given. Only false if called by the dummy version of the game interactor, true otherwise.
	 * @param damage The amount of damage to deal to the tile
	 * @param type The type of damage, used for determining whether or not the attack will be blocked
	 */
	public void targetTile(Level level, Player player, BlockState state, BlockPos pos, BoardTE te, BlockPos matching,
			boolean message, int damage, TargetType type) {
		if(getBoardState(state).isKnown()) {
			if(message)
				NWBasicMethods.messagePlayer(player, "message.naval_warfare.already_targeted");
			
			return;
		}
		
		String player_uuid = "dummy";
		
		if(player != null)
			player_uuid = player.getStringUUID();
		
		ControllerActionHelper cah = ControllerActionHelper.createTargetAction(20, pos, player_uuid, te.getBlockPos(), matching, 
				damage, type, false, true);
		
		selectTile(level, pos, matching);
		
		NWBasicMethods.dropBlock(level, pos, NWBlocks.SHELL.get());
		NWBasicMethods.dropBlock(level, matching, NWBlocks.SHELL.get());
		
		level.playSound(null, pos, NWSounds.SHOT.get(), SoundSource.MASTER, 1, 1);
		level.playSound(null, matching, NWSounds.SHOT.get(), SoundSource.MASTER, 1, 1);
		
		addActionToController(level, matching, cah);
	}
	
	/** Target a board tile. Should be called only by the game controller.
	 * 
	 * @param level
	 * @param player
	 * @param state
	 * @param pos Tile to target
	 * @param te BlockEntity of the tile to target
	 * @param matching The matching tile on the opponent's side
	 * @param damage The damage to deal to the tile
	 * @param type The type of damage, used to determine if the attack is blocked or not
	 * @param multi_ability Whether or not this function will be called again soon. Used to stop abilities that target more than 1 tile from ending your turn.
	 * @param action The action number of the game controller. Used to stop shields from being insta-killed by abilities that damage more than 1 tile.
	 * @param animation The block to 'drop from the sky' as a crude animation. May be null, in which case none is dropped.
	 * @param triggers_passives Whether or not this action may trigger a ship's passive if it hits a ship. Used to stop cascading passive effects wiping out half the board in one turn.
	 * @return The result of the action
	 */
	public HitResult targetTileAction(Level level, Player player, BlockState state, BlockPos pos, BoardTE te, BlockPos matching,
			int damage, TargetType type, boolean multi_ability, int action, @Nullable Block animation, boolean triggers_passives, boolean force) {
		if(animation != null) {
			NWBasicMethods.dropBlock(level, pos, 6, 30, animation);
			NWBasicMethods.dropBlock(level, matching, 6, 30, animation);
		}
		
		HitResult result = attackBlocked(level, player, pos, matching, te.getController(), type, action);

		if(result.isBlocked()) {
			if(!multi_ability)
				controllerKeepsTurn(level, te.getController(), false);
			
			deselectTile(level, pos, matching);
			
			return result;
		}
		
		if(!force && getBoardState(state).isKnown()) {
			deselectTile(level, pos, matching);
			return HitResult.KNOWN;
		}
		
		HitResult cont = HitResult.MISS;
		BoardState newstate = null;
		BlockState up = level.getBlockState(pos.above());
		
		if(up.getBlock() instanceof ShipBlock) {
			if(!force && ShipBlock.getState(up).isHit()) {
				deselectTile(level, pos, matching);
				return HitResult.KNOWN;
			}
			
			ShipBlock ship = (ShipBlock) up.getBlock();
			cont = HitResult.HIT;
			boolean destroyed = ship.damageShip(level, player, pos.above(), te.getId(), te.getController(), damage, matching, 
					multi_ability, triggers_passives, action);			
			
			if(destroyed) {
				NWBasicMethods.messagePlayerCustom(player, NWBasicMethods.getOpponentShipDestroyedMessage(NWBasicMethods.getTranslation(ship)));
				NWBasicMethods.animateItemUse(player, NWItems.SHIP_SUNK_OPPONENT.get());
				ShipRevealHelper srh = ship.getBaseState(level, pos.above());
				
				if(srh != null)
					revealShip(level, te.getController(), ship, srh);
			}
		}
		else {
			newstate = BoardState.EMPTY;
			level.playSound(null, pos, NWSounds.MISS.get(), SoundSource.MASTER, 1, 1);
			level.playSound(null, matching, NWSounds.MISS.get(), SoundSource.MASTER, 1, 1);
			
			if(!multi_ability) {		
				notifyOwnerOfMiss(te.getController(), level, te.getId());
				NWBasicMethods.messagePlayerCustom(player, NWBasicMethods.getOpponentShipMissedMessage(te.getId()) + " ");
			}
		}
		
		if(newstate != null) {
			level.setBlockAndUpdate(pos, state.setValue(STATE, newstate));
			level.setBlockAndUpdate(matching, level.getBlockState(matching).setValue(STATE, newstate));
		}
		
		if(!multi_ability)
			controllerKeepsTurn(level, te.getController(), cont.equals(HitResult.HIT));
		
		if(level.getBlockState(pos.above(2)).getBlock() instanceof ShipMarkerBlock) {
			level.setBlockAndUpdate(pos.above(2), Blocks.AIR.defaultBlockState());
			level.setBlockAndUpdate(matching.above(2), Blocks.AIR.defaultBlockState());
		}
		
		return cont;
	}
	
	/** Reveals a tile, as in doesn't damage it but does say whether or not a ship is there/closeby
	 * 
	 * @param level
	 * @param player
	 * @param state
	 * @param pos The tile that is targeted
	 * @param te The tile entity of the targeted tile
	 * @param matching The matching tile on the opponent's side
	 * @param type The type of the target
	 * @param action The action number of the controller
	 * @return
	 */
	public HitResult revealTileAction(Level level, Player player, BlockState state, BlockPos pos, BoardTE te, BlockPos matching,
			TargetType type, int action) {
		HitResult result = HitResult.MISS;

		if(attackBlocked(level, player, pos, matching, te.getController(), type, action).isBlocked())
			result = HitResult.BLOCKED;
		
		else if(getBoardState(state).isKnown())
			result = HitResult.KNOWN;
		
		else {
			BlockState up = level.getBlockState(pos.above());
			if(up.getBlock() instanceof ShipBlock) {
				if(ShipBlock.getState(up).isHit()) 
					result = HitResult.KNOWN;
				else 
					result = HitResult.HIT;
			}
		}
		
		deselectTile(level, pos, matching);
		
		return result;
	}
	
	private HitResult attackBlocked(Level level, Player player, BlockPos pos, BlockPos matching, BlockPos owner, TargetType type,
			int action) {
		switch(type) {
		case TORPEDO: 
			if(level.getBlockState(pos.above(3)).getBlock().equals(NWBlocks.TORPEDO_NET.get())) {
				NWBasicMethods.messagePlayerCustom(player, ChatFormatting.DARK_RED +
						NWBasicMethods.getTranslation("message.naval_warfare.torpedo_destroyed"));
				messageOwner(owner, level, "message.naval_warfare.torpedo_destroyed");
				
				NWBasicMethods.animateItemUse(player, NWItems.TORPEDO_NET.get().asItem());
				showOwnerAnimation(owner, level, NWItems.TORPEDO_NET.get().asItem());
				
				level.playSound(null, pos, NWSounds.TORPEDO_NET.get(), SoundSource.MASTER, 1, 1);
				level.playSound(null, matching, NWSounds.TORPEDO_NET.get(), SoundSource.MASTER, 1, 1);
				level.setBlockAndUpdate(matching.above(3), NWBlocks.TORPEDO_NET.get().defaultBlockState());
				PassiveAbilityBlock.setMatching(level, pos.above(3), matching.above(3));
				
				return HitResult.NULLIFIED;
			}
			
			return HitResult.HIT;
		case NORMAL:
			if(level.getBlockState(pos.above(4)).getBlock().equals(NWBlocks.ENERGY_SHIELD.get())) {
				NWBasicMethods.messagePlayerCustom(player, ChatFormatting.DARK_RED +
						NWBasicMethods.getTranslation("message.naval_warfare.attack_blocked"));
				messageOwner(owner, level, "message.naval_warfare.attack_blocked");
				
				NWBasicMethods.animateItemUse(player, NWItems.ENERGY_SHIELD.get().asItem());
				showOwnerAnimation(owner, level, NWItems.ENERGY_SHIELD.get().asItem());
				
				level.playSound(null, pos, NWSounds.ENERGY_SHIELD.get(), SoundSource.MASTER, 1, 1);
				level.playSound(null, matching, NWSounds.ENERGY_SHIELD.get(), SoundSource.MASTER, 1, 1);
				
				boolean result = EnergyShieldBlock.hit(level, pos.above(4), action, 1);
				
				if(result) {
					level.setBlockAndUpdate(matching.above(4), NWBlocks.ENERGY_SHIELD.get().defaultBlockState());
					PassiveAbilityBlock.setMatching(level, pos.above(4), matching.above(4));
				}
				else {
					NWBasicMethods.messagePlayerCustom(player, ChatFormatting.DARK_RED +
							NWBasicMethods.getTranslation("ability.naval_warfare.shield_destroyed"));
					messageOwner(owner, level, "ability.naval_warfare.shield_destroyed");
					level.playSound(null, pos, NWSounds.ENERGY_SHIELD.get(), SoundSource.MASTER, 1, .5f);
					level.playSound(null, matching, NWSounds.ENERGY_SHIELD.get(), SoundSource.MASTER, 1, .5f);
				}
				
				return HitResult.BLOCKED;
			}
			
			return HitResult.HIT;
		case OVERLOADER:
			if(level.getBlockState(pos.above(4)).getBlock().equals(NWBlocks.ENERGY_SHIELD.get())) {
				boolean result = EnergyShieldBlock.hit(level, pos.above(4), action, 100);
				
				if(result) {
					level.setBlockAndUpdate(matching.above(4), NWBlocks.ENERGY_SHIELD.get().defaultBlockState());
					PassiveAbilityBlock.setMatching(level, pos.above(4), matching.above(4));
				}
				else {
					NWBasicMethods.messagePlayerCustom(player, ChatFormatting.DARK_RED +
							NWBasicMethods.getTranslation("ability.naval_warfare.shield_destroyed"));
					messageOwner(owner, level, "ability.naval_warfare.shield_destroyed");
					level.playSound(null, pos, NWSounds.ENERGY_SHIELD.get(), SoundSource.MASTER, 1, .5f);
					level.playSound(null, matching, NWSounds.ENERGY_SHIELD.get(), SoundSource.MASTER, 1, .5f);
				}
			}
			
			return HitResult.HIT;
		case AIRCRAFT: 
			if(level.getBlockState(pos.above(5)).getBlock().equals(NWBlocks.ANTI_AIR.get())) {
				NWBasicMethods.messagePlayerCustom(player, ChatFormatting.DARK_RED +
						NWBasicMethods.getTranslation("message.naval_warfare.aircraft_shot_down"));
				messageOwner(owner, level, "message.naval_warfare.aircraft_shot_down");
				
				NWBasicMethods.animateItemUse(player, NWItems.ANTI_AIR.get().asItem());
				showOwnerAnimation(owner, level, NWItems.ANTI_AIR.get().asItem());
				
				level.playSound(null, pos, NWSounds.ANTI_AIR.get(), SoundSource.MASTER, 1, 1);
				level.playSound(null, matching, NWSounds.ANTI_AIR.get(), SoundSource.MASTER, 1, 1);
				level.setBlockAndUpdate(matching.above(5), NWBlocks.ANTI_AIR.get().defaultBlockState());
				PassiveAbilityBlock.setMatching(level, pos.above(5), matching.above(5));
				
				return HitResult.NULLIFIED;
			}
			else if(level.getBlockState(pos.above(4)).getBlock().equals(NWBlocks.ENERGY_SHIELD.get())) {
				NWBasicMethods.messagePlayerCustom(player, ChatFormatting.DARK_RED +
						NWBasicMethods.getTranslation("message.naval_warfare.attack_blocked"));
				messageOwner(owner, level, "message.naval_warfare.attack_blocked");
				
				NWBasicMethods.animateItemUse(player, NWItems.ENERGY_SHIELD.get().asItem());
				showOwnerAnimation(owner, level, NWItems.ENERGY_SHIELD.get().asItem());
				
				level.playSound(null, pos, NWSounds.ENERGY_SHIELD.get(), SoundSource.MASTER, 1, 1);
				level.playSound(null, matching, NWSounds.ENERGY_SHIELD.get(), SoundSource.MASTER, 1, 1);
				
				boolean result = EnergyShieldBlock.hit(level, pos.above(4), action, 1);
				
				if(result) {
					level.setBlockAndUpdate(matching.above(4), NWBlocks.ENERGY_SHIELD.get().defaultBlockState());
					PassiveAbilityBlock.setMatching(level, pos.above(4), matching.above(4));
				}
				else {
					NWBasicMethods.messagePlayerCustom(player, ChatFormatting.DARK_RED +
							NWBasicMethods.getTranslation("ability.naval_warfare.shield_destroyed"));
					messageOwner(owner, level, "ability.naval_warfare.shield_destroyed");
					level.playSound(null, pos, NWSounds.ENERGY_SHIELD.get(), SoundSource.MASTER, 1, 0.5F);
					level.playSound(null, matching, NWSounds.ENERGY_SHIELD.get(), SoundSource.MASTER, 1, 0.5F);
				}
				
				return HitResult.BLOCKED;
			}
			
			return HitResult.HIT;
		
		default: return HitResult.HIT;
		}
	}
	
	private void revealShip(Level level, BlockPos controller, ShipBlock ship, ShipRevealHelper srh) {
		if(validateController(level, controller)) {
			GameControllerTE cte = (GameControllerTE) level.getBlockEntity(controller);
			BlockPos opp = cte.getOpponent();
			
			if(validateController(level, opp)) {
				GameControllerTE ote = (GameControllerTE) level.getBlockEntity(opp);
				BlockState ostate = level.getBlockState(opp);
				GameController oc = (GameController) ostate.getBlock();
				BlockPos board = ote.getOpponentZero();
				
				if(validateBoard(level, board)) {
					BoardTE tte = (BoardTE) level.getBlockEntity(board);
					BlockPos tpos = tte.locateId(board, srh.getID(), oc.getFacing(level.getBlockState(opp)), true);
					Direction facing = NWBasicMethods.rotateToMatch(oc.getFacing(level.getBlockState(controller)), 
							oc.getFacing(ostate), srh.getFacing());
					ship.summonShip(level, tpos.above(), srh.getState().setValue(ShipBlock.FACING, facing), true, false);
				}
			}
		}
	}
	
	/** Validate a board tile
	 * 
	 * @param level
	 * @param pos
	 * @return True if the block at given position is a valid board tile
	 */
	public boolean validateBoard(Level level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		
		if(!(state.getBlock() instanceof Board))
			return false;
		
		if(state.hasBlockEntity()) {
			BlockEntity tile = level.getBlockEntity(pos);
			
			return tile instanceof BoardTE;
		}
		
		return false;
	}
	
	private boolean validateController(Level level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		
		if(!(state.getBlock() instanceof GameController))
			return false;
		
		if(state.hasBlockEntity()) {
			BlockEntity tile = level.getBlockEntity(pos);
			
			return tile instanceof GameControllerTE;
		}
		
		return false;
	}
	
	/** Get the game controller that a board tile is connected to
	 * 
	 * @param level
	 * @param pos Board position
	 * @return A game controller tile entity or null if an error occurs
	 */
	@Nullable
	public GameControllerTE getController(Level level, BlockPos pos) {
		if(!validateBoard(level, pos))
			return null;
		
		BoardTE board = (BoardTE) level.getBlockEntity(pos);
		BlockPos cpos = board.getController();
		
		if(!validateController(level, cpos))
			return null;
		
		return (GameControllerTE) level.getBlockEntity(cpos);
	}
	
	/** Gets the direction that a game controller is facing. Convenience method.
	 * 
	 * @param level
	 * @param pos Board tile position
	 * @return
	 */
	public Direction getControllerFacing(Level level, BlockPos pos) {
		GameControllerTE te = getController(level, pos);
		
		if(te != null) {
			BlockState state = te.getBlockState();
			GameController controller = (GameController) state.getBlock();
			return controller.getFacing(state);
		}
		
		
		return Direction.NORTH;
	}

	public void addActionToController(Level level, BlockPos pos, ControllerActionHelper cah) {
		GameControllerTE te = getController(level, pos);
		
		if(te != null) 
			te.addAction(cah);		
	}
	
	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if(level.isClientSide())
			return;
		
		if(newState.getBlock() instanceof Board)
			return;
		
		removeBoardAndShips(level, pos, !isMoving);
		
		if(state.hasBlockEntity())
			level.removeBlockEntity(pos);
	}
	
	public BoardState getBoardState(BlockState state) {
		try {
			return state.getValue(STATE);
		} catch(IllegalArgumentException e){
			return BoardState.NEUTRAL;
		}
	}
	
	/** Checks whether or not the given player owns the controller this board belongs to
	 * 
	 * @param player
	 * @param level
	 * @param pos Controller position
	 * @return True if the given player owns the controller this board belongs to, false otherwise.
	 */
	public boolean ownsBlock(Player player, Level level, BlockPos pos) {
		BlockState blockstate = level.getBlockState(pos);
		Block block = blockstate.getBlock();
		
		if(!(block instanceof GameController) || !blockstate.hasBlockEntity())
			return false;
		
		GameController controller = (GameController) block;
		BlockEntity tile = level.getBlockEntity(pos);
		
		if(!(tile instanceof GameControllerTE))
			return false;
		
		GameControllerTE te = (GameControllerTE) tile;
		
		return controller.ownsBlock(player, te.getOwner());
		
	}
	
	public boolean hasTurn(Player player, Level level, BlockPos pos) {
		BlockState blockstate = level.getBlockState(pos);
		Block block = blockstate.getBlock();
		
		if(!(block instanceof GameController) || !blockstate.hasBlockEntity())
			return false;
		
		BlockEntity tile = level.getBlockEntity(pos);
		
		if(!(tile instanceof GameControllerTE))
			return false;
		
		GameControllerTE te = (GameControllerTE) tile;
		
		return te.hasTurn();
	}
	
	/** Determines whether or not the game controller can accept a ship to register/deregister
	 * 
	 * @param player
	 * @param level
	 * @param pos
	 * @return
	 */
	public boolean canAccept(Player player, Level level, BlockPos pos) {
		BlockState blockstate = level.getBlockState(pos);
		Block block = blockstate.getBlock();
		
		if(!(block instanceof GameController) || !blockstate.hasBlockEntity())
			return false;
		
		BlockEntity tile = level.getBlockEntity(pos);
		
		if(!(tile instanceof GameControllerTE))
			return false;
		
		GameControllerTE te = (GameControllerTE) tile;
		
		return te.action_time <= 0;
	}
	
	public void controllerKeepsTurn(Level level, BlockPos pos, boolean cont) {
		if(!validateController(level, pos))
			return;
		
		GameControllerTE te = (GameControllerTE) level.getBlockEntity(pos);
		
		
		if(!validateController(level, te.opponent))
			return;
			
		te = (GameControllerTE) level.getBlockEntity(te.opponent);
		
		te.notifyResult(cont, false, cont);
	}
	
	public void notifyOwnerOfMiss(BlockPos pos, Level level, int id) {
		if(level.getBlockState(pos).hasBlockEntity()) {
			BlockEntity tile = level.getBlockEntity(pos);
			
			if(tile instanceof GameControllerTE) {
				GameControllerTE te = (GameControllerTE) tile;
				String owner = te.getOwner();
				
				if(owner.equals("dummy"))
					return;
				
				Player player = level.getPlayerByUUID(UUID.fromString(owner));
				
				if(player != null) {
					NWBasicMethods.messagePlayerCustom(player, NWBasicMethods.getOwnShipMissedMessage(id));
				}
			}
		}
	}
	
	public void messageOwner(BlockPos pos, Level level, String message) {
		if(level.getBlockState(pos).hasBlockEntity()) {
			BlockEntity tile = level.getBlockEntity(pos);
			
			if(tile instanceof GameControllerTE) {
				GameControllerTE te = (GameControllerTE) tile;
				String owner = te.getOwner();
				
				if(owner.equals("dummy"))
					return;
				
				Player player = level.getPlayerByUUID(UUID.fromString(owner));
				
				if(player != null)
					NWBasicMethods.messagePlayerCustom(player, ChatFormatting.DARK_RED + NWBasicMethods.getTranslation(message));
			}
		}
	}
	
	public void showOwnerAnimation(BlockPos pos, Level level, Item item) {
		if(level.getBlockState(pos).hasBlockEntity()) {
			BlockEntity tile = level.getBlockEntity(pos);
			
			if(tile instanceof GameControllerTE) {
				GameControllerTE te = (GameControllerTE) tile;
				String owner = te.getOwner();
				
				if(owner.equals("dummy"))
					return;
				
				Player player = level.getPlayerByUUID(UUID.fromString(owner));
				
				if(player != null) 
					NWBasicMethods.animateItemUse(player, item);
			}
		}
	}
	
	public void removeBoardAndShips(Level level, BlockPos pos, boolean ships) {
		BlockPos ship_pos = pos.above();
		int[] passives = new int[] {3, 4, 5};
		
		if(ships && level.getBlockState(ship_pos).getBlock() instanceof ShipBlock) {
			ShipBlock ship = (ShipBlock) level.getBlockState(ship_pos).getBlock();
			ship.removeShip(null, level.getBlockState(ship_pos), level, ship_pos, true, true);
		}
		
		for(int i : passives) {
			if(level.getBlockState(pos.above(i)).getBlock() instanceof PassiveAbilityBlock)
				level.removeBlock(pos.above(i), false);
		}
		
		if(level.getBlockState(pos.above(2)).getBlock() instanceof ShipMarkerBlock)
			level.removeBlock(pos.above(2), false);
		
		Direction[] dirs = new Direction[] {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
		
		for(Direction dir : dirs) {
			BlockPos board_pos = pos.relative(dir);
			BlockState board = level.getBlockState(board_pos);
			
			if(board.getBlock() instanceof Board || board.getBlock() instanceof BoardFiller)
				level.removeBlock(board_pos, !ships); // Little hack: We're using the 'isMoving' flag to carry our ships flag to the next board tile
		}
	}
	
	public void handleShipBroken(Level level, BlockPos pos, String ship, int hp, boolean seaworthy) {
		GameControllerTE te = getController(level, pos);
		
		if(te == null)
			return;
		
		GameController c = (GameController) te.getBlockState().getBlock();
		Player player = null;
		
		String owner = te.getOwner();
		
		if(!owner.equals("dummy"))
			player = level.getPlayerByUUID(UUID.fromString(owner));
		
		if(te.playing_game)			
			c.forfeitGame(level, player, te.getBlockPos());
		else {
			if(c.getState(te.getBlockState()).equals(ControllerState.SEARCHING)) {
				if(GameController.CONTROLLERS.contains(te.getBlockPos()))
					GameController.CONTROLLERS.remove(te.getBlockPos());
				
				level.setBlockAndUpdate(te.getBlockPos(), te.getBlockState().setValue(GameController.STATE, ControllerState.EDIT_CONFIG));
			}
			te.deregister(null, ship, hp, seaworthy);
		}
	}
}
