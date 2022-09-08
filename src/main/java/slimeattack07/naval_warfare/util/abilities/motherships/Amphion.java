package slimeattack07.naval_warfare.util.abilities.motherships;

import java.util.ArrayList;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import slimeattack07.naval_warfare.init.NWBlocks;
import slimeattack07.naval_warfare.init.NWSounds;
import slimeattack07.naval_warfare.objects.blocks.Board;
import slimeattack07.naval_warfare.objects.blocks.GameController;
import slimeattack07.naval_warfare.tileentity.BoardTE;
import slimeattack07.naval_warfare.tileentity.GameControllerTE;
import slimeattack07.naval_warfare.util.FunctionCaller;
import slimeattack07.naval_warfare.util.NWBasicMethods;
import slimeattack07.naval_warfare.util.TargetType;
import slimeattack07.naval_warfare.util.abilities.Ability;
import slimeattack07.naval_warfare.util.abilities.PassiveType;
import slimeattack07.naval_warfare.util.helpers.BattleLogHelper;
import slimeattack07.naval_warfare.util.helpers.ControllerActionHelper;

public class Amphion implements Ability{
	private final int AMOUNT = 1;
	private final int COST = 16;
	private final int DROP = 5;
	private final int LEFT = 3;
	private final int RIGHT = 3;
	private final int UP = 3;
	private final int DOWN = 3;
	private final String NAME;
	private final Block NUKE;
	private final Block PLANE;
	private final Block FRAGMENT;
	private boolean PASSIVE;
	
	public Amphion(String name, @Nullable Block plane, @Nullable Block nuke, @Nullable Block fragment, boolean passive) {
		NAME = name;
		PLANE = plane;
		NUKE = nuke;
		FRAGMENT = fragment;
		PASSIVE = passive;
	}
	
	public void dropNuke(Level level, BoardTE board) {
		ArrayList<BoardTE> tiles = getTiles(level, board);
		
		if(tiles.isEmpty())
			return;
		
		String playername = "dummy";
		int delay = 20;
		ArrayList<Integer> ids = new ArrayList<>();
		
		BlockPos pos = tiles.get(0).getController();
		BlockState state = level.getBlockState(pos);
		
		GameController control = (GameController) state.getBlock();
			
		if(!control.validateController(level, pos))
			return;
			
		GameControllerTE controller = (GameControllerTE) level.getBlockEntity(pos);
		playername = controller.getOwner();
		
		if(!tiles.isEmpty()) {
			Board b = (Board) tiles.get(0).getBlockState().getBlock();
			b.selectTiles(level, tiles);
		}

		boolean drop = true;
		
		for(BoardTE te : tiles) {
			BoardTE matching = control.getOpponentBoardTile(level, controller, te.getId(), false);
			
			if(matching != null) {				
				ControllerActionHelper cah = ControllerActionHelper.createMultiTarget(delay, matching.getBlockPos(), playername, matching.getBlockPos(), 
						te.getBlockPos(), 1, TargetType.OVERLOADER, true, false);
				
				controller.addAction(cah);
				
				if(drop) {
					level.playSound(null, te.getBlockPos(), NWSounds.BOMBER.get(), SoundSource.MASTER, 1, 0.65f);
					level.playSound(null, matching.getBlockPos(), NWSounds.BOMBER.get(), SoundSource.MASTER, 1, 0.65f);
					
					NWBasicMethods.dropBlock(level, te.getBlockPos(), NUKE);
					NWBasicMethods.dropBlock(level, matching.getBlockPos(), NUKE);
					delay = 0;
					drop = false;
					controller.recordOnRecorders(BattleLogHelper.createDropBlock(te.getId(), true, NUKE.getRegistryName()));
					controller.recordOnRecorders(BattleLogHelper.createSound(te.getId(), true, NWSounds.BOMBER.get(), 1f, 0.65f));
					level.setBlockAndUpdate(te.getBlockPos().above(6), Blocks.AIR.defaultBlockState());
					level.setBlockAndUpdate(matching.getBlockPos().above(6), Blocks.AIR.defaultBlockState());
				}
				else {
					level.playSound(null, te.getBlockPos(), NWSounds.DESTROY.get(), SoundSource.MASTER, 0.5f, 0.75f);
					level.playSound(null, matching.getBlockPos(), NWSounds.DESTROY.get(), SoundSource.MASTER, 0.5f, 0.75f);
					ids.add(te.getId());
				}
			}
		}
		
		if(!ids.isEmpty())
			controller.recordOnRecorders(BattleLogHelper.createSounds(ids, true, NWSounds.DESTROY.get(), 0.5f, 0.75f));
	}
	
	public void detonateShip(Level level, Player player, BoardTE board) {
		ArrayList<BoardTE> tiles = getTiles(level, board);
		
		if(tiles.isEmpty())
			return;
		
		String playername = "dummy";
		int delay = 0;
		ArrayList<Integer> ids = new ArrayList<>();
		
		BlockPos pos = tiles.get(0).getController();
		BlockState state = level.getBlockState(pos);
		
		GameController control = (GameController) state.getBlock();
			
		if(!control.validateController(level, pos))
			return;
			
		GameControllerTE controller = (GameControllerTE) level.getBlockEntity(pos);
		playername = controller.getOwner();
		
		if(!tiles.isEmpty()) {
			Board b = (Board) tiles.get(0).getBlockState().getBlock();
			b.selectTiles(level, tiles);
		}
		
		for(int i = 0; i < tiles.size() - 5; i++) {
			BoardTE te = tiles.get(i);
			BoardTE matching = control.getOpponentBoardTile(level, controller, te.getId(), false);
			
			if(matching != null) {				
				ControllerActionHelper cah = ControllerActionHelper.createMultiTarget(delay, matching.getBlockPos(), playername, matching.getBlockPos(), 
						te.getBlockPos(), 1, TargetType.OVERLOADER, true, false);
				
				controller.addAction(cah);
				
				level.playSound(null, te.getBlockPos(), NWSounds.DESTROY.get(), SoundSource.MASTER, 0.5f, 0.75f);
				level.playSound(null, matching.getBlockPos(), NWSounds.DESTROY.get(), SoundSource.MASTER, 0.5f, 0.75f);
				ids.add(te.getId());
			}
		}
		
		if(!ids.isEmpty()) {
			controller.recordOnRecorders(BattleLogHelper.createSounds(ids, true, NWSounds.DESTROY.get(), 0.5f, 0.75f));
			ids.clear();
		}
		
		delay = 5;
		
		for(int i = tiles.size() - 5; i < tiles.size(); i++) {
			BoardTE te = tiles.get(i);
			BoardTE matching = control.getOpponentBoardTile(level, controller, te.getId(), false);
			
			if(matching != null) {				
				ControllerActionHelper cah = ControllerActionHelper.createMultiTarget(delay, matching.getBlockPos(), playername, matching.getBlockPos(), 
						te.getBlockPos(), 3, TargetType.NORMAL, true, false);
				
				controller.addAction(cah);
				
				level.playSound(null, te.getBlockPos(), NWSounds.BOMBER.get(), SoundSource.MASTER, 1, 0.85f);
				level.playSound(null, matching.getBlockPos(), NWSounds.BOMBER.get(), SoundSource.MASTER, 1, 0.85f);
				
				NWBasicMethods.dropBlock(level, te.getBlockPos(), FRAGMENT);
				NWBasicMethods.dropBlock(level, matching.getBlockPos(), FRAGMENT);
				controller.recordOnRecorders(BattleLogHelper.createDropBlock(te.getId(), true, FRAGMENT.getRegistryName()));
				controller.recordOnRecorders(BattleLogHelper.createSound(te.getId(), true, NWSounds.BOMBER.get(), 1f, 0.85f));
			}
		}
	}

	@Override
	public void activate(Level level, Player player, BoardTE board, BlockPos ship) {	
		if(PASSIVE) {
			detonateShip(level, player, board);
			
			return;
		}
		
		BlockPos pos = board.getController();
		BlockState state = level.getBlockState(pos);
		
		GameController control = (GameController) state.getBlock();
			
			if(!control.validateController(level, pos))
				return;
			
		GameControllerTE controller = (GameControllerTE) level.getBlockEntity(pos);
		BoardTE matching = control.getOpponentBoardTile(level, controller, board.getId(), false);
		
		if(matching != null)
			level.setBlockAndUpdate(matching.getBlockPos().above(6), PLANE.defaultBlockState());
		
		level.setBlockAndUpdate(board.getBlockPos().above(6), PLANE.defaultBlockState());

		controller.addTurnAction(ControllerActionHelper.createFunction(FunctionCaller.AMPHION_DROPNUKE, ship, board.getBlockPos()));
		controller.recordOnRecorders(BattleLogHelper.createSetBlock(board.getId(), PLANE.getRegistryName(), 6, false));		
	}
	
	@Override
	public void activate(Level level, Player player, BoardTE board) {
	}

	@Override
	public int getAmount() {
		return AMOUNT;
	}

	@Override
	public String getTranslation() {
		return "abilities.naval_warfare." + NAME;
	}

	@Override
	public ArrayList<BoardTE> getTiles(Level level, BoardTE te) {
		Board board = (Board) te.getBlockState().getBlock();
		
		ArrayList<BoardTE> tiles = te.collectTileArea(UP, DOWN, LEFT, RIGHT, board.getControllerFacing(level, te.getBlockPos()));
		ArrayList<BoardTE> drop = new ArrayList<>();
		
		if(PASSIVE) {
			while(drop.size() < DROP) {
				for (BoardTE t : te.selectRandomTiles(DROP, false, false)) {
					if(drop.size() < DROP) {
						if(!tiles.contains(t) && !drop.contains(t))
							drop.add(t);
					}
					else
						break;
				}
			}
			
			tiles.addAll(drop);
		}
		
		tiles.remove(te);
		tiles.add(0, te);
		
		return tiles;
	}

	@Override
	public MutableComponent hoverableInfo() {
		String name = ChatFormatting.GREEN + NWBasicMethods.getTranslation(getTranslation()) + ChatFormatting.WHITE;
		String hover = PASSIVE ? NWBasicMethods.getTranslation("ability.naval_warfare.passive_if_active") : 
			NWBasicMethods.getTranslation("ability.naval_warfare.energy_cost") + ": " + COST + ", ";
		hover += NWBasicMethods.getTranslation("misc.naval_warfare.length") + ": " + (LEFT + RIGHT - 1) + ", ";
		hover += NWBasicMethods.getTranslation("misc.naval_warfare.width") + ": " + (UP + DOWN - 1) + ", ";
		
		if(PASSIVE)
			hover += NWBasicMethods.getTranslation("ability.naval_warfare.extra_targets") + ": " + DROP;
		
		return NWBasicMethods.hoverableText(name, "gray", hover);
	}
	
	@Override
	public Item getAnimationItem() {
		return PASSIVE ? NWBlocks.AMPHION_FRAGMENT.get().asItem() : NWBlocks.AMPHION_ACTIVE.get().asItem();
	}
	
	@Override
	public int energyCost() {
		return COST;
	}
	
	@Override
	public PassiveType getPassiveType() {
		return PASSIVE ? PassiveType.DESTROYED_IF_ACTIVE : PassiveType.NOT;
	}
}
