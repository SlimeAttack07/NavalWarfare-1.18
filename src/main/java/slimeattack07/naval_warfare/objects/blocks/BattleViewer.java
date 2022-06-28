package slimeattack07.naval_warfare.objects.blocks;

import java.util.ArrayList;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.registries.ForgeRegistries;
import slimeattack07.naval_warfare.init.NWBlocks;
import slimeattack07.naval_warfare.init.NWTileEntityTypes;
import slimeattack07.naval_warfare.tileentity.BattleViewerTE;
import slimeattack07.naval_warfare.tileentity.BoardTE;
import slimeattack07.naval_warfare.util.NWBasicMethods;
import slimeattack07.naval_warfare.util.ViewerState;
import slimeattack07.naval_warfare.util.helpers.ShipSaveHelper;
import slimeattack07.naval_warfare.util.properties.ViewerStateProperty;

public class BattleViewer extends Block implements EntityBlock{
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final ViewerStateProperty VIEWER_STATE = ViewerStateProperty.create();

	public BattleViewer() {
		super(Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(0.8f, 2));
		registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(VIEWER_STATE, ViewerState.IDLE));
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(FACING, context.getHorizontalDirection());
	}

	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING);
		builder.add(VIEWER_STATE);
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return NWTileEntityTypes.BATTLE_VIEWER.get().create(pos, state);
	}
	
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
			BlockEntityType<T> type) {
		return level.isClientSide() ? null : (l, s, pos, tile) -> ((BattleViewerTE) tile).tick();
	}
	
	public Direction getFacing(BlockState state) {
		try {
			return state.getValue(FACING);
		} catch(IllegalArgumentException e){
			return Direction.NORTH;
		}
	}
	
	public ViewerState getViewerState(BlockState state) {
		try {
			return state.getValue(VIEWER_STATE);
		} catch(IllegalArgumentException e){
			return ViewerState.IDLE;
		}
	}
	
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if(level.isClientSide())
			return InteractionResult.SUCCESS;
		
		if(player == null || !player.getItemInHand(hand).isEmpty())
			return InteractionResult.PASS;
		
		BlockEntity tile = level.getBlockEntity(pos);
		
		if(tile instanceof BattleViewerTE) {
			BattleViewerTE te = (BattleViewerTE) tile;
			
			if(player.isCrouching()) {
				switch(te.speed + "") {
				case "0.0": te.speed = 1; break; // In case something goes wrong with storing the speed.
				case "0.25": te.speed = 0.5f; break;
				case "0.5": te.speed = 1; break;
				case "1.0": te.speed = 2; break;
				case "2.0": te.speed = 5; break;
				case "5.0": te.speed = 10; break;
				case "10.0": te.speed = 0.25f; break;
				}
				
				NWBasicMethods.messagePlayerCustom(player, NWBasicMethods.getTranslation("message.naval_warfare.viewer_speed").
						replace("MARKER1", te.speed + "x"));
			}
			else {
				te.playing = !te.playing;
				String s = te.playing ? "unpaused" : "paused";
				NWBasicMethods.messagePlayerCustom(player, NWBasicMethods.getTranslation("message.naval_warfare.viewer_status_toggled").
						replace("MARKER1", s));
				
				if(!te.playing && state.getBlock() instanceof BattleViewer)
					level.setBlockAndUpdate(pos, state.setValue(BattleViewer.VIEWER_STATE, ViewerState.IDLE));
			}
		}
		
		return InteractionResult.SUCCESS;
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

				if(!(block.equals(Blocks.AIR) || block.equals(NWBlocks.BATTLE_VIEWER.get()))) {
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
	
	private boolean spawnBoard(Level level, Player player, BattleViewerTE te, BlockPos location, int amount, boolean opponent) {
		Direction dir = getFacing(te.getBlockState());
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
		
		BlockState bstate = NWBlocks.BOARD.get().defaultBlockState();
		BlockState filler = NWBlocks.BOARD_FILLER.get().defaultBlockState();
		
		// Main Board
		for(int z = 0; z < zlength; z++) {
			for(int x = 0; x < xlength; x++) {				
				BlockPos next = nextPosToCheck(dir, cube_origin, x, z);
				
				if(current < amount) {
					level.setBlockAndUpdate(next, bstate);
					current++;
				}
				else
					level.setBlockAndUpdate(next, filler);
			}
		}
		
		// Surrounding with filler tiles
		for(int x = -1; x < xlength; x++) {
			BlockPos next = nextPosToCheck(dir, cube_origin, x, -1);
			setFillerUnlessViewer(level, next);
			next = nextPosToCheck(dir, cube_origin, x, zlength);
			setFillerUnlessViewer(level, next);
		}
		
		for(int z = -1; z < zlength ; z++) {
			BlockPos next = nextPosToCheck(dir, cube_origin, -1, z);
			setFillerUnlessViewer(level, next);
			next = nextPosToCheck(dir, cube_origin, xlength, z);
			setFillerUnlessViewer(level, next);
		}
		
		BlockPos next = nextPosToCheck(dir, cube_origin, xlength, zlength);
		setFillerUnlessViewer(level, next);

		// Setting tile entity data
		BlockState board = level.getBlockState(cube_origin);
		
		if(board.hasBlockEntity()) {
			BlockEntity btile = level.getBlockEntity(cube_origin);
			
			if(btile instanceof BoardTE) {
				BoardTE bte = (BoardTE) btile;
				bte.setControllerAndId(location, 0, dir);
			}
		}
		
		if(opponent)
			te.setOpponentZero(cube_origin);
		else
			te.setZero(cube_origin);
		
		return true;
	}
	
	private void setFillerUnlessViewer(Level level, BlockPos pos) {
		if(!(level.getBlockState(pos).getBlock() instanceof BattleViewer))
			level.setBlockAndUpdate(pos, NWBlocks.BOARD_FILLER.get().defaultBlockState());
	}
	
	private boolean spawnOpponentBoard(Level level, Player player, BattleViewerTE te, int own_size, int opp_size) {
		double sqrt = Math.sqrt(own_size);
		int offset = (int) Math.ceil(sqrt) + 2;
		Direction dir = getFacing(te.getBlockState());
		
		return spawnBoard(level, player, te, te.getBlockPos().relative(dir, offset), opp_size, true);
	}
	
	public boolean spawnBoards(Level level, Player player, BattleViewerTE te, int own_size, int opp_size) {
		return spawnBoard(level, player, te, te.getBlockPos(), own_size, false) ? spawnOpponentBoard(level, player, te, own_size, opp_size) : false;
	}
	
	public void spawnShips(Level level, Direction dir, BlockPos pos, ArrayList<ShipSaveHelper> ships, Direction facing, boolean opponent) {
		for(ShipSaveHelper ssh : ships) {
			Direction new_facing = NWBasicMethods.rotateToMatch(dir, facing, ssh.getDir());
			
			Block block = ForgeRegistries.BLOCKS.getValue(ssh.getShip());
			
			if(block == null || !(block instanceof ShipBlock))
				continue;
			
			ShipBlock ship = (ShipBlock) block;
			BlockPos spos = findBoard(level, pos, ssh.getPos(), opponent);
			
			if(spos != null) {
				boolean success = ship.summonShip(level, spos.above(), ship.defaultBlockState().setValue(ShipBlock.FACING, new_facing), true, false);
				
				if(success)
					ship.propagateAbilityAmount(level, spos.above(), 0);
			}
		}	
	}
	
	public BlockPos findBoard(Level level, BlockPos pos, int id, boolean opponent) {
		BlockEntity tile = level.getBlockEntity(pos);
		
		if(tile instanceof BattleViewerTE) {
			BattleViewerTE te = (BattleViewerTE) tile;
			
			return te.findBoard(id, opponent);
		}
		
		return null;
	}
	
	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if(level.isClientSide())
			return;
		
		if(newState.getBlock() instanceof BattleViewer)
			return;
		
		Direction dir = getFacing(state);
		BlockPos board_pos = pos.relative(dir);
		BlockState board = level.getBlockState(board_pos);
		
		if(board.getBlock() instanceof Board)
			level.removeBlock(board_pos, false);
		
		if(state.hasBlockEntity())
			level.removeBlockEntity(pos);
	}
}
