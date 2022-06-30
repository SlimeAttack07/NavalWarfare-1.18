package slimeattack07.naval_warfare.objects.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import slimeattack07.naval_warfare.NavalWarfare;
import slimeattack07.naval_warfare.config.NavalWarfareConfig;
import slimeattack07.naval_warfare.init.NWBlocks;
import slimeattack07.naval_warfare.init.NWItems;
import slimeattack07.naval_warfare.init.NWSounds;
import slimeattack07.naval_warfare.init.NWStats;
import slimeattack07.naval_warfare.init.NWTileEntityTypes;
import slimeattack07.naval_warfare.init.NWTriggers;
import slimeattack07.naval_warfare.tileentity.BoardTE;
import slimeattack07.naval_warfare.tileentity.GameControllerTE;
import slimeattack07.naval_warfare.tileentity.ShipTE;
import slimeattack07.naval_warfare.util.BoardState;
import slimeattack07.naval_warfare.util.ControllerState;
import slimeattack07.naval_warfare.util.NWBasicMethods;
import slimeattack07.naval_warfare.util.ShipPart;
import slimeattack07.naval_warfare.util.ShipState;
import slimeattack07.naval_warfare.util.abilities.Ability;
import slimeattack07.naval_warfare.util.abilities.PassiveType;
import slimeattack07.naval_warfare.util.abilities.Seaworthy;
import slimeattack07.naval_warfare.util.helpers.BattleLogHelper;
import slimeattack07.naval_warfare.util.helpers.ControllerActionHelper;
import slimeattack07.naval_warfare.util.helpers.ShipRevealHelper;
import slimeattack07.naval_warfare.util.properties.ShipPartProperty;
import slimeattack07.naval_warfare.util.properties.ShipStateProperty;

public abstract class ShipBlock extends Block implements EntityBlock{
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final ShipStateProperty SHIP_STATE = ShipStateProperty.create();
	public final Ability ACTIVE_ABILITY;
	public final Ability PASSIVE_ABILITY;
	public final int TIER;
	
	public ShipBlock(Ability active_ability, Ability passive_ability, int tier) {
		super(Properties.of(Material.STONE).strength(200).destroyTime(99999999).noDrops().color(MaterialColor.COLOR_BLACK));
		registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(SHIP_STATE, ShipState.UNDAMAGED));
		ACTIVE_ABILITY = active_ability;
		PASSIVE_ABILITY = passive_ability;
		TIER = Math.min(Math.max(tier, 1), 6); // Limit tier between 1 and 6
	}
	
	public boolean hasActiveAbility() {
		return ACTIVE_ABILITY != null;
	}
	
	public boolean hasPassiveAbility() {
		return PASSIVE_ABILITY != null;
	}
	
	public int getTier() {
		return TIER;
	}
	
	public void hit(Level level, BlockPos pos, BlockPos matching) {
		level.playSound(null, pos, NWSounds.HIT.get(), SoundSource.MASTER, 2, 1);
		level.playSound(null, matching, NWSounds.HIT.get(), SoundSource.MASTER, 2, 1);
	}
	
	public void destroy(Level level, BlockPos pos, BlockPos matching) {
		level.playSound(null, pos, NWSounds.DESTROY.get(), SoundSource.MASTER, 2, 0.6f);
		level.playSound(null, matching, NWSounds.DESTROY.get(), SoundSource.MASTER, 2, 0.6f);
	}
	
	public boolean losesHP() {
		return true;
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		ShipTE te = NWTileEntityTypes.SHIP.get().create(pos ,state);
		
		if(ACTIVE_ABILITY != null)
			te.setActiveAmount(ACTIVE_ABILITY.getAmount());
		
		te.setNext(getNext(te));
		
		return te;
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(FACING, context.getHorizontalDirection());
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING);
		builder.add(SHIP_STATE);
	}
	
	@Override
	public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_,
			CollisionContext p_60558_) {
		return Block.box(0.1D, 0, 0.1D, 15.9D, 8, 15.9D);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, BlockGetter level, List<Component> tooltip,
			TooltipFlag flagIn) {
		
		tooltip.add(NWBasicMethods.createGrayText("descriptions.naval_warfare.ship_shape", ": " + getShape()));
		tooltip.add(NWBasicMethods.createGrayText("descriptions.naval_warfare.ship_hp", ": " + getMaxHP()));
		boolean header = false;
		
		if(hasActiveAbility()) {
			header = true;
			tooltip.add(NWBasicMethods.createDGreenText("ability.naval_warfare.abilities"));
			String active = NWBasicMethods.getTranslation("ability.naval_warfare.active");
			String ability = NWBasicMethods.getTranslation(ACTIVE_ABILITY.getTranslation());
			tooltip.add(new TextComponent(ChatFormatting.AQUA + "(" + active + ") " + ability));
		}
		if(hasPassiveAbility()) {
			if(!header)
				tooltip.add(NWBasicMethods.createDGreenText("ability.naval_warfare.abilities"));
			
			String passive = NWBasicMethods.getTranslation("ability.naval_warfare.passive");
			String ability = NWBasicMethods.getTranslation(PASSIVE_ABILITY.getTranslation());
			tooltip.add(new TextComponent(ChatFormatting.GREEN + "(" + passive + ") " + ability));
		}
		
		
		super.appendHoverText(stack, level, tooltip, flagIn);
	}
	
	public static Direction getFacing(BlockState state) {
		try {
			return state.getValue(FACING);
		} catch(IllegalArgumentException e){
			return Direction.NORTH;
		}
	}
	
	public static ShipState getState(BlockState state) {
		try {
			return state.getValue(SHIP_STATE);
		} catch(IllegalArgumentException e){
			return ShipState.UNDAMAGED;
		}
	}
	
	public boolean validateShip(Level level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		
		if(!(state.getBlock() instanceof ShipBlock))
			return false;
		
		if(state.hasBlockEntity()) {
			BlockEntity tile = level.getBlockEntity(pos);
			
			return tile instanceof ShipTE;
		}
		
		return false;
	}
	
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult result) {
		if(level.isClientSide())
			return InteractionResult.SUCCESS;
		
		ItemStack stack = player.getItemInHand(handIn);
		Item item = stack.getItem();
		
		if(item instanceof PickaxeItem) {
			breakShip(level, pos, player);
			return InteractionResult.SUCCESS;
		}
		
		return InteractionResult.PASS;
	}
	
	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if(level.isClientSide())
			return;
		
		if(placer instanceof Player) {
			Player player = (Player) placer;
			BlockPos pdown = pos.below();
			BlockState down =  level.getBlockState(pdown);
			String ship = stack.getItem().getRegistryName().toString();
				
			if(down.getBlock() instanceof Board && !(down.getBlock() instanceof BoardRedirect)) {
				Board board = (Board) down.getBlock();
				
				if(board.validateBoard(level, pdown)) {
					BoardTE te = (BoardTE) level.getBlockEntity(pdown);
					
					if(te.isShipRegistered(ship)) {
						if(te.canReceiveShip(ship)) {
							if(!summonShip(level, pos, state, false, false))
								NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.no_room_for_ship");
							else {
								if(player != null && !player.isCreative())
									NWBasicMethods.addOrSpawn(player, stack, level, pos);
								
								return;
							}
						}
						else
							NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.cannot_place");
					}
					else
						NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.not_registered");
				}
				else
					NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.board_corrupt");
			}
			else if(down.getBlock().equals(NWBlocks.SHIP_DISPLAY.get())) {
				if(!summonShip(level, pos, state, false, true))
					NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.no_room_for_ship");
				else {
					propagateAbilityAmount(level, pos, 0);
					return;
				}
			}
			else
				NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.only_place_on_board");
			
			level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			
			if(!player.isCreative())
				NWBasicMethods.addOrSpawn(player, stack, level, pos);
		}	
	}

	public abstract boolean isBase(BlockState state);
	public abstract boolean isMiddle(BlockState state);
	public abstract int getMaxHP();
	public abstract String getShapeTranslation();
	public abstract boolean summonShip(Level level, BlockPos pos, BlockState state, boolean include_base, boolean display);
	public abstract boolean canPlace(Level level, BlockPos pos, BlockState state, boolean include_base, boolean display);
	@Nullable public abstract BlockPos getNext(ShipTE te);
	
	public String getShape() {
		return NWBasicMethods.getTranslation("shapes.naval_warfare." + getShapeTranslation());
	}
	
	// gets pos of board, not of ship!
	public boolean isRegistered(Level level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		
		if(state.getBlock() instanceof Board) {
			Board board = (Board) state.getBlock();
			
			if(board.validateBoard(level, pos)) {
				BoardTE te = (BoardTE) level.getBlockEntity(pos);
				
				return te.isShipRegistered(getRegistryName().toString());
			}
		}
		
		return false;
	}
	
	public void breakShip(Level level, BlockPos pos, @Nullable Player player) {
		BlockEntity tile = level.getBlockEntity(pos.below());
		
		if(tile instanceof BoardTE) {
			BoardTE bte = (BoardTE) tile;
			BlockPos c = bte.getController();
			tile = level.getBlockEntity(c);
			
			if(tile instanceof GameControllerTE) {
				GameControllerTE cte = (GameControllerTE) tile;
				GameController controller = (GameController) cte.getBlockState().getBlock();
				
				if(controller.getState(cte.getBlockState()).equals(ControllerState.SEARCHING) || cte.hasGame())
						NWBasicMethods.messagePlayer(player, "message.naval_warfare.cant_remove");
				else 
					level.removeBlock(pos, false);
			}
		} else {
			NWBasicMethods.addOrSpawn(player, new ItemStack(level.getBlockState(pos).getBlock()), level, pos);
			level.removeBlock(pos, false);
		}
	}
	
	public void propagateAbilityAmount(Level level, BlockPos pos, int active) {
		ArrayList<BlockPos> parts = collectParts(level, pos, getRegistryName());
		
		for(BlockPos part : parts) {
			ShipTE block = (ShipTE) level.getBlockEntity(part);
			block.setActiveAmount(active);
			block.boardBelow();
		}
	}
	
	protected ShipPart getShipPart(BlockState state, ShipPartProperty prop) {
		try {
			return state.getValue(prop);
		} catch(IllegalArgumentException e) {
			return ShipPart.ONE;
		}
	}
	
	protected int getBelowBoard(Level level, BlockPos pos) {
		BlockState state = level.getBlockState(pos.below());
		
		if(state.getBlock() instanceof Board && state.hasBlockEntity()){
			BlockEntity tile = level.getBlockEntity(pos.below());
			
			if(tile instanceof BoardTE)
				return ((BoardTE) tile).getId();
		}
		
		return -1;
	}
	
	protected ShipRevealHelper getBaseState(Level level, BlockState state, BlockPos pos, ResourceLocation ship, Direction[] dirs,
			ArrayList<BlockPos> seen) {
		if(!state.getBlock().getRegistryName().equals(ship))
			return null;
		
		if(isBase(state))
			return new ShipRevealHelper(state, getBelowBoard(level, pos), getFacing(state));
		
		if(seen.contains(pos))
			return null;		
		else {
			seen.add(pos);
			for(Direction dir : dirs) {
				BlockPos new_pos = pos.relative(dir);
				BlockState new_state = level.getBlockState(new_pos);
				ShipRevealHelper end_state = getBaseState(level, new_state, new_pos, ship, dirs, seen);
			
				if(end_state != null)
					return end_state;
			}
			return null;
		}
	}
	
	protected ShipRevealHelper getBaseState(Level level, BlockPos pos) {
		return getBaseState(level, level.getBlockState(pos), pos, level.getBlockState(pos).getBlock().getRegistryName(),
				new Direction[] {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST}, new ArrayList<>());
	}
	
	protected BlockPos offset(Direction dir, BlockPos origin, int x, int z) {
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
	
	protected boolean spaceValid(Level level, BlockPos origin, Direction dir, int x, int z, boolean display) {
		Block offset = level.getBlockState(offset(dir, origin, x, z)).getBlock();
		boolean clear = offset.equals(Blocks.AIR) || offset instanceof DisappearingBlock;
		
		BlockState board_state = level.getBlockState(offset(dir, origin, x, z).below());
		
		return display ? clear && level.getBlockState(offset(dir, origin, x, z).below()).getBlock().equals(NWBlocks.SHIP_DISPLAY.get()) :
				clear && board_state.getBlock() instanceof Board && (losesHP() || !((Board) board_state.getBlock()).getBoardState(board_state).isKnown() 
						|| board_state.getBlock() instanceof BoardRedirect);
	}
	
	public void removeShip(Player player, BlockState state, Level level, BlockPos pos, boolean remove_board, boolean shrink_board) {
		BlockPos board_pos = pos.below();
		BlockPos next = null;
		
		if(remove_board && level.getBlockState(board_pos).getBlock() instanceof Board) 
			level.removeBlock(board_pos, false);
		
		BlockEntity tile = level.getBlockEntity(pos);
		
		if(tile instanceof ShipTE) {
			ShipTE te = (ShipTE) tile;
			
			if(te.hasNext())
				next = te.getNext();
		}
		
		level.removeBlock(pos, false);
		
		if(state.hasBlockEntity())
			level.removeBlockEntity(pos);
		
		if(isBase(state)) {
			if(!remove_board) {
				BlockState below = level.getBlockState(board_pos);
				
				if(below.getBlock().equals(NWBlocks.BOARD.get())) {
					Board board = (Board) below.getBlock();
					boolean seaworthy = shrink_board && hasPassiveAbility() && PASSIVE_ABILITY instanceof Seaworthy;
					board.handleShipBroken(level, board_pos, getRegistryName().toString(), getMaxHP(), seaworthy);
				}
			}
		}
		
		if(next != null && level.getBlockState(next).getBlock().getRegistryName().equals(getRegistryName()))
			level.removeBlock(next, false);
	}
	
	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if(level.isClientSide())
			return;
		
		if(newState.getBlock() instanceof ShipBlock)
			return;
		
		removeShip(null, state, level, pos, false, !isMoving);
	}
	
	protected ArrayList<BlockPos> collectParts(Level level, BlockPos current, ResourceLocation id){
		ArrayList<BlockPos> known = new ArrayList<>();
		known.add(current);
		
		return collectParts(level, known, current, id);
	}
	
	public ArrayList<BlockPos> collectUndamagedParts(Level level, BlockPos pos){
		ArrayList<BlockPos> parts = new ArrayList<>();
		ArrayList<BlockPos> undamaged = new ArrayList<>();
		parts.add(pos);
		parts = collectParts(level, parts, pos, getRegistryName());
		
		for(BlockPos bp : parts) {
			BlockState state = level.getBlockState(bp);
			
			if(state.getBlock() instanceof ShipBlock && !getState(state).isHit())
				undamaged.add(bp);
		}
		
		return undamaged;
	}
	
	protected ArrayList<BlockPos> collectParts(Level level, ArrayList<BlockPos> known, BlockPos start, ResourceLocation id) {
		BlockPos pos = start;
		BlockEntity tile = null;
		ShipTE te = null;
		
		for(int i = 0; i < 20; i++) {
			tile = level.getBlockEntity(pos);
			
			if(!(tile instanceof ShipTE))
				break;
			
			te = (ShipTE) tile;
			pos = te.getNext();
			
			if(pos == null || known.contains(pos) || !te.getBlockState().getBlock().getRegistryName().equals(id))
				break;
			
			known.add(pos);
		}
		
		return known;
	}
	
	protected boolean isDestroyed(Level level, ArrayList<BlockPos> parts) {
		for(BlockPos ship_part : parts) {
			BlockState state = level.getBlockState(ship_part);
			
			if(!getState(state).isHit())
				return false;
		}
		
		return true;
	}
	
	protected void destroyShip(Level level, BlockPos origin, ArrayList<BlockPos> parts, BlockPos matching, Player player) {
		BoardTE te = null;
		
		for(BlockPos part : parts) {
			BlockState state = level.getBlockState(part);
			level.setBlockAndUpdate(part, state.setValue(SHIP_STATE, ShipState.DESTROYED));
			
			if(state.getBlock() instanceof ShipBlock) {
				ShipBlock block = (ShipBlock) state.getBlock();
				
				if(block.isMiddle(state)) {
					BlockEntity tile = level.getBlockEntity(part.below());
					
					if(tile instanceof BoardTE)
						te = (BoardTE) tile;
				}	
			}
		}
		
		destroy(level, origin, matching);

		if(hasPassiveAbility() && PASSIVE_ABILITY.canBeDisabled() && te != null) {
			PASSIVE_ABILITY.detachPassive(level, te);
			
			NWBasicMethods.messagePlayerCustom(player, NWBasicMethods.getTranslation("ability.naval_warfare.passive_disabled")
					+ ": " + ChatFormatting.GREEN + NWBasicMethods.getTranslation(PASSIVE_ABILITY.getTranslation()));
			notifyPassiveDown(player, PASSIVE_ABILITY.hoverableInfo(), te.getController(), level);
			
			level.playSound(null, origin, NWSounds.ENERGY_SHIELD.get(), SoundSource.MASTER, 1, 1.5f);
			level.playSound(null, matching, NWSounds.ENERGY_SHIELD.get(), SoundSource.MASTER, 1, 1.5f);
		}
		
		if(player != null && player instanceof ServerPlayer) {
			try {
				ServerPlayer sp = (ServerPlayer) player;
				sp.awardStat(NWStats.SHIPS_DESTROYED);
				NWTriggers.DESTROY.trigger(sp);
				
				Stat<ResourceLocation> stat = Stats.CUSTOM.get(new ResourceLocation(NavalWarfare.MOD_ID, "ships_destroyed"), StatFormatter.DEFAULT);
				int val = sp.getStats().getValue(stat);
				
				if(val >= 50)
					NWTriggers.DESTROY_50.trigger(sp);
				if(val >= 100)
					NWTriggers.DESTROY_100.trigger(sp);

			} catch(NullPointerException e) {
				NavalWarfare.LOGGER.error("Naval Warfare: Failed to fetch the ships destroyed statistic.");
			}
		}
	}
	
	public boolean damageShip(Level level, Player player, BlockPos pos, int id, BlockPos controller, int damage, BlockPos matching,
			boolean multi_ability, boolean triggers_passives, int action) {
		ArrayList<BlockPos> positions = collectParts(level, pos, getRegistryName());
		int done = 0;
		boolean destroyed = false;
		boolean passive = false;
		
		for(BlockPos shippos : positions) {
			if(done < damage) {
				BlockState state = level.getBlockState(shippos);
				
				if(state.getBlock() instanceof ShipBlock) {
					BlockPos down = shippos.below();
					BlockState boardstate = level.getBlockState(down);
					Board board = (Board) boardstate.getBlock();
					
					if(!board.validateBoard(level, down))
						continue;
								
					BoardTE te = (BoardTE) level.getBlockEntity(down);
					BoardTE otile = (BoardTE) level.getBlockEntity(matching);
					Direction dir = board.getControllerFacing(level, otile.getBlockPos());
					BlockPos location = otile.locateId(matching, te.getId(), dir, true);
					
					int hitresult = hitShip(level, player, shippos, controller, state, te.getId(), location, multi_ability);
					
					if(hitresult >= 0) {
						if(triggers_passives && hasPassiveAbility() && (PASSIVE_ABILITY.getPassiveType().equals(PassiveType.HIT) || 
								(hitresult > 0 && PASSIVE_ABILITY.getPassiveType().equals(PassiveType.DESTROYED))))
							passive = true;
					}
					
					if(hitresult > 0) {
						done++;
						destroyed = true;
						break;
					}
					else if(hitresult == 0)
						done++;
					else 
						board.deselectTile(level, te.getBlockPos(), otile.getBlockPos());
				}
			}
			else
				break;
		}
		
		BlockEntity tile = level.getBlockEntity(matching);
		GameControllerTE gte = null;
		
		if(tile instanceof BoardTE) {
			BoardTE te = (BoardTE) tile;
			Board board = (Board) te.getBlockState().getBlock();
			gte = board.getController(level, te.getBlockPos());
		}
		
		if(gte != null && triggers_passives) {
			gte.increaseStreak(done);
			gte.streakSounds();
		}
		
		if(passive) {
			BlockEntity stile = level.getBlockEntity(pos);
			
			if(stile instanceof ShipTE) {
				ShipTE ste = (ShipTE) stile;
				
				if(action > ste.getActionNumber()) {
					propagateActionNumber(level, pos, action);
					
					String owner = "dummy";
					BlockEntity own_tile = level.getBlockEntity(controller);
					
					if(own_tile instanceof GameControllerTE) {
						GameControllerTE te = (GameControllerTE) own_tile;
						owner = te.getOwner();
					}
						
					if(gte != null) {
						String pname = player == null ? "dummy" : player.getStringUUID();
						String translation = !NavalWarfareConfig.reveal_on_hit_passives.get() && PASSIVE_ABILITY.getPassiveType().equals(PassiveType.HIT) 
								? PASSIVE_ABILITY.getPassiveCategory() : PASSIVE_ABILITY.getTranslation();
						gte.addAction(ControllerActionHelper.createAnnounce(pname, owner,
								PASSIVE_ABILITY.getAnimationItem(), translation, 
								Component.Serializer.toJson(PASSIVE_ABILITY.hoverableInfo())));
						
						gte.addAction(ControllerActionHelper.createAbility(positions.get(0), positions.get(0).below(), gte.getOwner(), false));
						
						try {
							ServerPlayer sp = (ServerPlayer) player;
							sp.awardStat(NWStats.PASSIVE_TRIGGERED);
							NWTriggers.PASSIVE.trigger(sp);
							
							Stat<ResourceLocation> stat = Stats.CUSTOM.get(new ResourceLocation(NavalWarfare.MOD_ID, "passive_triggered"),
									StatFormatter.DEFAULT);
							int val = sp.getStats().getValue(stat);
							
							if(val >= 50)
								NWTriggers.PASSIVE_50.trigger(sp);
							if(val >= 100)
								NWTriggers.PASSIVE_100.trigger(sp);

						} catch(NullPointerException e) {
							NavalWarfare.LOGGER.error("Naval Warfare: Failed to fetch the passives triggered statistic.");
						}
					}
				}
			}
		}
		
		return destroyed;
	}
	
	private void propagateActionNumber(Level level, BlockPos pos, int action) {
		ArrayList<BlockPos> parts = collectParts(level, pos, getRegistryName());
		
		for(BlockPos part : parts) {
			ShipTE block = (ShipTE) level.getBlockEntity(part);
			block.setActionNumber(action);
		}
	}
	
	private int posToId(Level level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		
		if(state.getBlock() instanceof Board) {
			Board board = (Board) state.getBlock();
			
			if(board.validateBoard(level, pos)) {
				BoardTE te = (BoardTE) level.getBlockEntity(pos);
				return te.getId();
			}
		}
		
		return -1;
	}
	
	private int hitShip(Level level, Player player, BlockPos pos, BlockPos controller, BlockState state, int id, BlockPos matching,
			boolean multi_ability) {
		ShipState ship_state = getState(state);
		
		if(!ship_state.isHit()) {
			String shipname = NWBasicMethods.getTranslation(state.getBlock());
			ArrayList<BlockPos> parts = collectParts(level, pos, state.getBlock().getRegistryName());			
			level.setBlockAndUpdate(pos, state.setValue(SHIP_STATE, ShipState.DAMAGED));
			level.setBlockAndUpdate(pos.below(), level.getBlockState(pos.below()).setValue(Board.STATE, BoardState.HIT));
			level.setBlockAndUpdate(matching, level.getBlockState(matching).setValue(Board.STATE, BoardState.HIT));
			
			// This is to remove ShipMarkerBlocks in case they exist above the ship.
			level.setBlockAndUpdate(pos.above(), Blocks.AIR.defaultBlockState());
			level.setBlockAndUpdate(matching.above(2), Blocks.AIR.defaultBlockState());
			
			notifyOwnerOfHit(shipname, controller, level, id, multi_ability);
			
			if(!multi_ability) {
				String message = NWBasicMethods.getOpponentShipHitMessage(id);
				NWBasicMethods.messagePlayerCustom(player, message);	
				recordOnRecorders(level, pos, controller, id, multi_ability, multi_ability);
				recordMessage(level, pos, controller, message, true);
			}
			
			if(isDestroyed(level, parts)) {
				destroyShip(level, pos, parts, matching, player);
				notifyOwnerOfDestruction(shipname, controller, level);
				
				for(BlockPos bp : parts)
					recordOnRecorders(level, bp, controller, posToId(level, bp.below()), true, bp.equals(pos));
				
				return 1;
			}
			else {
				hit(level, pos, matching);
				recordOnRecorders(level, pos, controller, id, false, true);
				return 0;
			}
		}
	
		return -1;
	}
	
	private boolean causedByOpponent(Level level, BlockPos pos, GameControllerTE te) {
		BlockEntity tile = level.getBlockEntity(pos.below());
		boolean opponent = true;
		
		if(tile instanceof BoardTE) {
			BoardTE bte = (BoardTE) tile;
			tile = level.getBlockEntity(bte.getController());
			
			if(tile instanceof GameControllerTE) {
				GameControllerTE cte = (GameControllerTE) tile;
				
				return cte.equals(te);
			}
		}
		
		return opponent;
	}
	
	private void recordOnRecorders(Level level, BlockPos pos, BlockPos controller, int id, boolean destroyed, boolean set_board) {
		BlockEntity tile = level.getBlockEntity(controller);
		
		if(tile instanceof GameControllerTE) {
			GameControllerTE te = (GameControllerTE) tile;	
			// Sounds weird, but opponent boolean indicates if the hit occurred on the opponents board. Therefore, if caused by opponent, it occurred on
			// your own board and hence opponent should be false.
			boolean opponent = !causedByOpponent(level, pos, te);
			ShipState state = destroyed ? ShipState.DESTROYED : ShipState.DAMAGED;
			SoundEvent sound = destroyed ? NWSounds.DESTROY.get() : NWSounds.HIT.get();
			float pitch = destroyed ? 0.6f : 1f;
			
			te.recordOnRecorders(BattleLogHelper.createShipState(id, opponent, state));
			
			if(set_board) {
				te.recordOnRecorders(BattleLogHelper.createBoardState(id, opponent, BoardState.HIT));
				te.recordOnRecorders(BattleLogHelper.createSound(id, opponent, sound, 2f, pitch));
			}	
		}
	}
	
	private void recordMessage(Level level, BlockPos pos, BlockPos controller, String message, boolean opponent) {
		BlockEntity tile = level.getBlockEntity(controller);
		
		if(tile instanceof GameControllerTE) {
			GameControllerTE te = (GameControllerTE) tile;	
			BattleLogHelper blh = BattleLogHelper.createMessage(Component.Serializer.toJson(new TextComponent(message)));
		
			if(opponent)
				te.recordOnOppRecorder(blh);
			else
				te.recordOnRecorder(blh);
		}
	}
	
	public void notifyOwnerOfHit(String ship, BlockPos pos, Level level, int id, boolean multi_ability) {
		if(level.getBlockState(pos).hasBlockEntity()) {
			BlockEntity tile = level.getBlockEntity(pos);
			
			if(tile instanceof GameControllerTE) {
				GameControllerTE te = (GameControllerTE) tile;
				
				if(losesHP())
					te.reduceHP();
				
				String owner = te.getOwner();
				
				if(!multi_ability && !owner.equals("dummy")) {
					Player player = level.getPlayerByUUID(UUID.fromString(owner));
					String message = NWBasicMethods.getOwnShipHitMessage(ship, id);
					NWBasicMethods.messagePlayerCustom(player, message);
					recordMessage(level, pos, pos, message, false);
				}
			}
		}
	}
	
	public void notifyOwnerOfDestruction(String ship, BlockPos pos, Level level) {
		if(level.getBlockState(pos).hasBlockEntity()) {
			BlockEntity tile = level.getBlockEntity(pos);
			
			if(tile instanceof GameControllerTE) {
				GameControllerTE te = (GameControllerTE) tile;
				String owner = te.getOwner();
				
				if(owner.equals("dummy"))
					return;
				
				Player player = level.getPlayerByUUID(UUID.fromString(owner));
				String message = NWBasicMethods.getOwnShipDestroyedMessage(ship);
				NWBasicMethods.messagePlayerCustom(player, message);
				recordMessage(level, pos, pos, message, false);
				NWBasicMethods.animateItemUse(player, NWItems.SHIP_SUNK_OWN.get());
			}
		}
	}
	
	public void notifyPassiveDown(Player player1, MutableComponent ability, BlockPos pos, Level level) {
		if(level.getBlockState(pos).hasBlockEntity()) {
			BlockEntity tile = level.getBlockEntity(pos);
			
			if(tile instanceof GameControllerTE) {
				GameControllerTE te = (GameControllerTE) tile;
				String owner = te.getOwner();
				String user;
					
				
				if(owner.equals("dummy"))
					user = NWBasicMethods.getTranslation("misc.naval_warfare.dummy");
				else {
					Player player2 = level.getPlayerByUUID(UUID.fromString(owner));
					user = player2.getName().getString();
					NWBasicMethods.messagePlayerAbilityUsed(null, player2, "ability.naval_warfare.passive_disabled", user, ability);
				}
				
				if(player1 != null)
					NWBasicMethods.messagePlayerAbilityUsed(te, player1, "ability.naval_warfare.passive_disabled", user, ability);
			}
		}
	}
}
