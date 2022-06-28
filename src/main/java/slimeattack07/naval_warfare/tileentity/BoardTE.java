package slimeattack07.naval_warfare.tileentity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.stream.Collectors;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import slimeattack07.naval_warfare.NavalWarfare;
import slimeattack07.naval_warfare.init.NWTileEntityTypes;
import slimeattack07.naval_warfare.objects.blocks.Board;
import slimeattack07.naval_warfare.objects.blocks.GameController;
import slimeattack07.naval_warfare.objects.blocks.ShipBlock;
import slimeattack07.naval_warfare.util.ControllerState;
import slimeattack07.naval_warfare.util.NWBasicMethods;
import slimeattack07.naval_warfare.util.ShipState;
import slimeattack07.naval_warfare.util.abilities.Ability;
import slimeattack07.naval_warfare.util.abilities.PassiveType;
import slimeattack07.naval_warfare.util.helpers.NBTHelper;
import slimeattack07.naval_warfare.util.helpers.ShipInfoHelper;
import slimeattack07.naval_warfare.util.helpers.ShipSaveHelper;

public class BoardTE extends BlockEntity {
	public BlockPos controller = null;
	public int board_id = -1;
	
	public BoardTE(BlockPos pos, BlockState state) {
		super(NWTileEntityTypes.BOARD.get(), pos, state);
	}
	
	public boolean hasController() {
		return controller != null;
	}
	
	public BlockPos getController() {
		return controller;
	}
	
	public int getId() {
		return board_id;
	}
	
	public boolean hasId() {
		return board_id >= 0;
	}
	
	public boolean isShipRegistered(String ship) {
		Board board = (Board) getBlockState().getBlock();
		GameControllerTE te = board.getController(level, worldPosition);
		
		if(te == null)
			return false;
		
		return te.isRegistered(ship);
	}
	
	public boolean canReceiveShip(String ship) {
		Board board = (Board) getBlockState().getBlock();
		GameControllerTE te = board.getController(level, worldPosition);
		
		if(te == null)
			return false;
		
		GameController controller = (GameController) te.getBlockState().getBlock();
		
		return controller.getState(te.getBlockState()).equals(ControllerState.EDIT_CONFIG);
	}

	private int propagateControllerAndId(Level level, BlockPos pos, BlockPos con, int free_id, Direction[] order) {
		BlockState state = level.getBlockState(pos);
		
		if(state.hasBlockEntity()) {
			BlockEntity tile = level.getBlockEntity(pos);
			
			if(tile instanceof BoardTE) {
				BoardTE te = (BoardTE) tile;
				return te.setControllerAndId(con, free_id, order);
			}
		}
		
		return free_id;
	}
	
	public int setControllerAndId(BlockPos con, int free_id, Direction[] order) {	
		if(order.length != 2)
			return -1;
		
		controller = con;
		board_id = free_id;
		int new_free_id = free_id + 1;
		
		new_free_id = propagateControllerAndId(level, worldPosition.relative(order[0]), con, new_free_id, order);
		
		if(!(level.getBlockEntity(worldPosition.relative(order[0].getOpposite())) instanceof BoardTE)) 
			new_free_id = propagateControllerAndId(level, worldPosition.relative(order[1]), con, new_free_id, order);
		
		return new_free_id;
	}
	
	public int setControllerAndId(BlockPos con, int free_id, Direction facing) {
		return setControllerAndId(con, free_id, getPropagationOrder(facing));
	}
	
	public ArrayList<ShipSaveHelper> collectShips(Level level, BlockPos pos, Direction dir, Player player){
		return collectShips(level, pos, getPropagationOrder(dir), new ArrayList<>(), player);
	}
	
	public ArrayList<ShipSaveHelper> collectShips(Level level, BlockPos pos, Direction[] order, ArrayList<ShipSaveHelper> ships,
			Player player){
		
		if(ships == null || order.length != 2)
			return null;
		
		if(!(level.getBlockEntity(pos) instanceof BoardTE))
			return ships;
		
		BlockState state = level.getBlockState(pos.above());
		
		if(state.getBlock() instanceof ShipBlock) {
			ShipBlock ship = (ShipBlock) state.getBlock();
			
			if(ship.isBase(state)){
				if(!ship.isRegistered(level, pos)) {
					ships = null;
					NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.contains_unregistered");
					return null;
				}
				int board_pos = ((BoardTE) level.getBlockEntity(pos)).getId();
				ShipSaveHelper ssh = new ShipSaveHelper(ship.getRegistryName(), board_pos, ShipBlock.getFacing(state), ship.getMaxHP());
				
				if(ships.contains(ssh)) {
					NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.contains_double");
					ships = null;
					return null;
				}
				
				ships.add(ssh);
			}
		}
		
		if(collectShips(level, pos.relative(order[0]), order, ships, player) == null)
			return null;

		if(!(level.getBlockEntity(pos.relative(order[0].getOpposite())) instanceof BoardTE)) {
			if(collectShips(level, pos.relative(order[1]), order, ships, player) == null)
				return null;
		}
			
		return ships;
	}
	
	public ArrayList<ShipInfoHelper> collectShipsInfo(Level level, BlockPos pos, Direction dir){
		return collectShipsInfo(level, pos, getPropagationOrder(dir), new ArrayList<>());
	}
	
	public ArrayList<ShipInfoHelper> collectShipsInfo(Level level, BlockPos pos, Direction[] order, ArrayList<ShipInfoHelper> ships){
		if(order.length != 2)
			return null;
		
		if(!(level.getBlockEntity(pos) instanceof BoardTE))
			return ships;
		
		BlockState state = level.getBlockState(pos.above());
		
		if(state.getBlock() instanceof ShipBlock) {
			ShipBlock ship = (ShipBlock) state.getBlock();
			
			if(ship.isBase(state)){
				String active = ship.hasActiveAbility() ?  NWBasicMethods.getTranslation(ship.ACTIVE_ABILITY.getTranslation())
						: NWBasicMethods.getTranslation("abilities.naval_warfare.none");
				
				String passive = ship.hasPassiveAbility() ?  NWBasicMethods.getTranslation(ship.PASSIVE_ABILITY.getTranslation())
						: NWBasicMethods.getTranslation("abilities.naval_warfare.none");
				
				String ship_state = ShipBlock.getState(state).isAlive() ? "misc.naval_warfare.alive"
						: "misc.naval_warfare.destroyed";
				
				ShipInfoHelper sih = new ShipInfoHelper(NWBasicMethods.getTranslation(ship), active, passive, ship.getShape(), ship_state);
				
				if(!ships.contains(sih))
					ships.add(sih);
			}
		}
		
		collectShipsInfo(level, pos.relative(order[0]), order, ships);
		
		if(!(level.getBlockEntity(pos.relative(order[0].getOpposite())) instanceof BoardTE))
			collectShipsInfo(level, pos.relative(order[1]), order, ships);
		
		return ships;
	}
	
	public void activateStartPassives(Level level, Player player, BlockPos pos, Direction[] order){
		if(!(level.getBlockEntity(pos) instanceof BoardTE) || order.length != 2)
			return;
		
		BlockState state = level.getBlockState(pos.above());		
		
		if(state.getBlock() instanceof ShipBlock) {
			ShipBlock ship = (ShipBlock) state.getBlock();
			
			if(ship.isMiddle(state)){
				if(ship.hasPassiveAbility()) {
					Ability ability = ship.PASSIVE_ABILITY;
					
					if(ability.getPassiveType().equals(PassiveType.START_GAME)) {
						BlockState boardstate = level.getBlockState(pos);
						
						if(boardstate.getBlock() instanceof Board) {
							Board board = (Board) boardstate.getBlock();
							
							if(board.validateBoard(level, pos)) {
								BoardTE te = (BoardTE) level.getBlockEntity(pos);
								ability.activate(level, player, te);
								NWBasicMethods.messagePlayerAbilityUsed(board.getController(level, pos) ,player, "ability.naval_warfare.passive_deployed", 
										null, ability.hoverableInfo());
							}
						}
					}
				}
			}
		}
		
		activateStartPassives(level, player, pos.relative(order[0]), order);

		if(!(level.getBlockEntity(pos.relative(order[0].getOpposite())) instanceof BoardTE))
			activateStartPassives(level, player, pos.relative(order[1]), order);
	}
	
	public BlockPos locateId(BlockPos pos, int target, Direction[] order) {
		if(!(level.getBlockEntity(pos) instanceof BoardTE))
			return null;
		
		if(checkCorrectId(level, pos, target))
			return pos;
		
		BlockPos id = locateId(pos.relative(order[0]), target, order);
		
		if(id != null)
			return id;
		
		if(!(level.getBlockEntity(pos.relative(order[0].getOpposite())) instanceof BoardTE))
			return locateId(pos.relative(order[1]), target, order);
		
		return null;
	}
	
	private BlockPos locateZero(Level level, BlockPos pos, boolean opponent) {
		Block block = level.getBlockState(pos).getBlock();
		
		if(block instanceof Board) {
			Board board = (Board) block;
			
			if(board.validateBoard(level, pos)) {
				BoardTE te = (BoardTE) level.getBlockEntity(pos);
				Block block_c = level.getBlockState(te.getController()).getBlock();
				
				if(block_c instanceof GameController) {
					GameController controller = (GameController) block_c;
					
					if(controller.validateController(level, te.getController())) {
						GameControllerTE gc_te = (GameControllerTE) level.getBlockEntity(te.getController());
						return opponent ? gc_te.getOpponentZero() : gc_te.getZero();
					}
				}
			}
		}
		
		return pos;
	}
	
	public BlockPos locateId(BlockPos pos, int target, Direction dir, boolean opponent) {
		return locateId(locateZero(level, pos, opponent), target, getPropagationOrder(dir));
	}
	
	public BlockPos locateId(BlockPos pos, int target, Direction dir) {
		return locateId(pos, target, dir, false);
	}
	
	private boolean checkCorrectId(Level level, BlockPos pos, int id) {
		BlockEntity tile = level.getBlockEntity(pos);
		
		if(tile instanceof BoardTE) {
			BoardTE te = (BoardTE) tile;
			return te.board_id == id;
		}
		
		return false;
	}
	
	public void activateStartPassives(Level level, Player player, BlockPos pos, Direction dir){
		activateStartPassives(level, player, pos, getPropagationOrder(dir));
	}
	
	private Direction[] getPropagationOrder(Direction facing) {
		switch(facing) {
		case NORTH: return new Direction[] {Direction.NORTH , Direction.WEST};
		case EAST: return new Direction[] {Direction.EAST , Direction.NORTH};
		case SOUTH: return new Direction[] {Direction.SOUTH , Direction.EAST};
		case WEST: return new Direction[] {Direction.WEST , Direction.SOUTH};
		
		default: return new Direction[] {Direction.NORTH , Direction.WEST};
		}
	}
	
	public ArrayList<BoardTE> collectTiles(int amount, Direction dir){
		ArrayList<BoardTE> tiles = new ArrayList<>();
		int seen = 0;
		
		while(seen < amount) {
			BlockPos newpos = worldPosition.relative(dir, seen);
			Block block = getLevel().getBlockState(newpos).getBlock();
			
			if(block instanceof Board) {
				Board board = (Board) block;
				
				if(board.validateBoard(level, newpos)) {
					tiles.add((BoardTE) level.getBlockEntity(newpos));
					seen++;
				}
			}
			else
				break;
		}
		
		return tiles;
	}
	
	public ArrayList<BoardTE> collectUnknownTiles(boolean opponent){
		BlockPos zero = locateZero(level, worldPosition, opponent);
		BlockEntity tile = level.getBlockEntity(zero);
		ArrayList<BoardTE> tiles = new ArrayList<>();
		ArrayList<BoardTE> empty = new ArrayList<>();
		
		if(tile instanceof BoardTE) {
			BoardTE te = (BoardTE) tile;
			Board board = (Board) te.getBlockState().getBlock();
			tiles = te.collectTileArea(1000, 1000, 1000, 1000, board.getControllerFacing(level, te.worldPosition));

			for(BoardTE bte : tiles) {
				Board b = (Board) bte.getBlockState().getBlock();
				
				if(!b.getBoardState(bte.getBlockState()).isKnown())
					empty.add(bte);
			}
		}
		
		return empty;
	}
	
	public ArrayList<BoardTE> collectUndamagedShips(boolean opponent, boolean unique, boolean origin, int length, int width){
		BlockEntity tile = this;
		
		if(origin) {
			BlockPos zero = locateZero(level, worldPosition, opponent);
			tile = level.getBlockEntity(zero);
		}
		
		ArrayList<BoardTE> undamaged = new ArrayList<>();
		
		if(tile instanceof BoardTE) {
			BoardTE te = (BoardTE) tile;
			Board board = (Board) te.getBlockState().getBlock();
			
			GameControllerTE controller = board.getController(level, te.getBlockPos());
			
			if(controller == null)
				return new ArrayList<>();
			
			
			double l = (length + 1) / 2;
			double w = (width + 1) / 2;
			ArrayList<BoardTE> tiles = te.collectTileArea((int) Math.ceil(l), (int) Math.floor(l), (int) Math.ceil(w), (int) Math.floor(w), 
					board.getControllerFacing(level, te.worldPosition));
			GameController control = (GameController) controller.getBlockState().getBlock();
			ArrayList<String> known = new ArrayList<>();
			
			for(BoardTE bte : tiles) {
				
				BoardTE matching = control.getOpponentBoardTile(level, controller, bte.getId(), false);				
				BlockState state = opponent ? level.getBlockState(matching.getBlockPos().above()) : level.getBlockState(bte.getBlockPos().above());
				
				if(state.getBlock() instanceof ShipBlock && ShipBlock.getState(state).equals(ShipState.UNDAMAGED)) {
					known.add(state.getBlock().getRegistryName().toString());
					undamaged.add(bte);
				}
			}

			if(unique)
				undamaged = filterUnique(undamaged, known);
		}
		
		return undamaged;
	}
	
	public ArrayList<BoardTE> filterUnique(ArrayList<BoardTE> tiles, ArrayList<String> names){
		ArrayList<BoardTE> filtered = new ArrayList<>();
		ArrayList<String> unique = new ArrayList<>();
		ArrayList<BoardTE> buffer = new ArrayList<>();
		
		if(tiles.size() != names.size()) 
			return filtered;
		
		for(int i = 0; i < tiles.size(); i++) {
			String name = names.get(i);
			
			if(unique.contains(name))
				continue;
			
			buffer.clear();	
			unique.add(name);
			
			for(int k = 0; k < tiles.size(); k++) {
				if(names.get(k).equals(name))
					buffer.add(tiles.get(k));
			}

			filtered.addAll(selectRandom(1, buffer));
		}

		return filtered;
	}
	
	public ArrayList<BoardTE> collectUnknownEmptyTiles(boolean opponent){
		BlockPos zero = locateZero(level, worldPosition, opponent);
		BlockEntity tile = level.getBlockEntity(zero);
		ArrayList<BoardTE> tiles = new ArrayList<>();
		ArrayList<BoardTE> undamaged = new ArrayList<>();
		
		if(tile instanceof BoardTE) {
			
			BoardTE te = (BoardTE) tile;
			Board board = (Board) te.getBlockState().getBlock();
			
			GameControllerTE controller = board.getController(level, te.getBlockPos());
			
			if(controller == null)
				return new ArrayList<>();
			
			GameController control = (GameController) controller.getBlockState().getBlock();
			
			tiles = te.collectTileArea(1000, 1000, 1000, 1000, board.getControllerFacing(level, te.worldPosition));

			for(BoardTE bte : tiles) {
				BoardTE matching = control.getOpponentBoardTile(level, controller, bte.getId(), false);
				Board b = (Board) matching.getBlockState().getBlock();
				BlockState state = level.getBlockState(matching.getBlockPos().above());
				
				if(!(state.getBlock() instanceof ShipBlock) && !b.getBoardState(matching.getBlockState()).isKnown())
					undamaged.add(bte);
			}
		}
		
		return undamaged;
	}
	
	public ArrayList<BoardTE> selectRandomTiles(int amount, boolean opponent, boolean empty){
		if(amount <= 0)
			return new ArrayList<>();
		
		return empty ? selectRandom(amount, collectUnknownEmptyTiles(opponent)) : selectRandom(amount, collectUnknownTiles(opponent));
	}
	
	public ArrayList<BoardTE> selectRandomShip(int amount, boolean opponent, boolean unique, boolean origin, int length, int width){	
		return selectRandom(amount, collectUndamagedShips(opponent, unique, origin, length, width));
	}
	
	public ArrayList<BoardTE> selectRandom(int amount, ArrayList<BoardTE> tiles){
		if(amount >= tiles.size())
			return tiles;
		
		Collections.shuffle(tiles);
		return((ArrayList<BoardTE>) tiles.stream().limit(amount).collect(Collectors.toList()));
	}
	
	public ArrayList<BoardTE> collectTileArea(int up, int down, int left, int right, Direction dir){
		HashSet<BoardTE> tiles = new HashSet<>();
		tiles.addAll(collectTiles(left, dir.getCounterClockWise()));
		tiles.addAll(collectTiles(right, dir.getClockWise()));
		
		HashSet<BoardTE> tiles_up = new HashSet<>();
		HashSet<BoardTE> tiles_down = new HashSet<>();
		
		for(BoardTE tile : tiles) {
			tiles_up.addAll(tile.collectTiles(up, dir));
			tiles_down.addAll(tile.collectTiles(down, dir.getOpposite()));
		}
		
		// Because searching ALWAYS adds the tile that collectTiles is called on, all tiles in 'tiles' are also in tiles_up and tiles_down.
		tiles_up.addAll(tiles_down); 
		
		return new ArrayList<>(tiles_up);
	}
	
	public BoardTE getOpponentBoardZero() {
		BlockPos bpos = locateZero(level, worldPosition, true);
		
		BlockEntity tile = level.getBlockEntity(bpos);
		
		if(tile instanceof BoardTE)
			return (BoardTE) tile;
		
		return null;
	}
	
	@Override
	public void saveAdditional(CompoundTag compound) {
		compound.put(NavalWarfare.MOD_ID, NBTHelper.toNBT(this));
	}
	
	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		CompoundTag initvalues = compound.getCompound(NavalWarfare.MOD_ID);
		
		if(initvalues != null) {
			CompoundTag control = initvalues.getCompound("controller");
			
			if(control.contains("no_pos"))
				controller = null;
			
			else
				controller = new BlockPos(control.getInt("x"), control.getInt("y"), control.getInt("z"));
			
			board_id = initvalues.getInt("board_id");
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(board_id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BoardTE other = (BoardTE) obj;
		return board_id == other.board_id;
	}
}