package slimeattack07.naval_warfare.util.abilities;

import java.util.ArrayList;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import slimeattack07.naval_warfare.init.NWBlocks;
import slimeattack07.naval_warfare.objects.blocks.Board;
import slimeattack07.naval_warfare.tileentity.BoardTE;
import slimeattack07.naval_warfare.tileentity.GameControllerTE;
import slimeattack07.naval_warfare.tileentity.PassiveAbilityTE;
import slimeattack07.naval_warfare.util.NWBasicMethods;
import slimeattack07.naval_warfare.util.helpers.BattleLogHelper;

public class TorpedoNet implements Ability {
	private final String NAME;
	private final String OWNER;
	private final int LENGTH; // Note: Affected area is 2 * LENGTH - 1
	private final int MAX_SEARCH = 500;
	private final PassiveType TYPE;
	
	public TorpedoNet(String name, String owner, int length, boolean deployable) {
		NAME = name;
		OWNER = owner;
		LENGTH = length;
		TYPE = deployable ? PassiveType.DEPLOYED : PassiveType.START_GAME;
	}
	
	@Override
	public void activate(Level level, Player player, BoardTE board) {
		ArrayList<BoardTE> tiles = getTiles(level, board);
		ArrayList<Integer> ids = new ArrayList<>();
		
		for(BoardTE te : tiles) {
			BlockPos pos = te.getBlockPos().above(3);
			level.setBlockAndUpdate(pos, NWBlocks.TORPEDO_NET.get().defaultBlockState());
			BlockEntity ptile = level.getBlockEntity(pos);
			
			if(ptile instanceof PassiveAbilityTE) {
				PassiveAbilityTE pte = (PassiveAbilityTE) ptile;
				pte.addOwner(OWNER);
			}
			
			ids.add(te.getId());
		}
		
		if(!ids.isEmpty()) {
			Board b = (Board) board.getBlockState().getBlock();
			GameControllerTE controller = b.getController(level, board.getBlockPos());
			
			if(controller != null)
				controller.recordOnRecorders(BattleLogHelper.createSetBlocks(ids, NWBlocks.TORPEDO_NET.get().getRegistryName(), 3, false));
		}
	}

	@Override
	public int getAmount() {
		return 1;
	}

	@Override
	public String getTranslation() {
		return "abilities.naval_warfare." + NAME;
	}

	@Override
	public ArrayList<BoardTE> getTiles(Level level, BoardTE te){
		if(te == null)
			return new ArrayList<>();
		
		BoardTE real_board = te;
		Board board = (Board) real_board.getBlockState().getBlock();
		Direction dir = board.getControllerFacing(level, real_board.getBlockPos());
		int search = 0;
		
		if(TYPE.equals(PassiveType.DEPLOYED)) {
			ArrayList<BoardTE> tiles_a = real_board.collectTiles(LENGTH, dir.getClockWise());
			ArrayList<BoardTE> tiles_b = real_board.collectTiles(LENGTH, dir.getCounterClockWise());
		
			if(!tiles_b.isEmpty())				
				tiles_b.remove(0);
			
			tiles_a.addAll(tiles_b);
			
			return tiles_a;
		}
		
		while(search < MAX_SEARCH) { // just to prevent infinite loop, although it should never reach close to this number anyways.
			BlockPos pos = real_board.getBlockPos().relative(dir.getOpposite());
			Block block = level.getBlockState(pos).getBlock();
			
			if(block instanceof Board && board.validateBoard(level, pos)) {
				real_board = (BoardTE) level.getBlockEntity(pos);
				board = (Board) real_board.getBlockState().getBlock();
				search++;
			}
			else {
				ArrayList<BoardTE> tiles_a = real_board.collectTiles(LENGTH, dir.getClockWise());
				ArrayList<BoardTE> tiles_b = real_board.collectTiles(LENGTH, dir.getCounterClockWise());
			
				if(!tiles_b.isEmpty())				
					tiles_b.remove(0);
				
				tiles_a.addAll(tiles_b);
				return tiles_a;
			}
				
		}
		
		return new ArrayList<>();
	}
	
	@Override
	public PassiveType getPassiveType() {
		return TYPE;
	}
	
	@Override
	public void detachPassive(Level level, BoardTE board) {		
		ArrayList<BoardTE> tiles = getTiles(level, board);
	
		if(!tiles.isEmpty()) {
			BlockPos pos = tiles.get(0).getBlockPos().above(3);
			BlockEntity tile = level.getBlockEntity(pos);
			
			if(tile instanceof PassiveAbilityTE) {
				PassiveAbilityTE pte = (PassiveAbilityTE) tile;
				Board b = (Board) board.getBlockState().getBlock();
				pte.destroy(level, pos, OWNER, b.getController(level, board.getBlockPos()), 3);
			}
		}
	}
	
	@Override
	public MutableComponent hoverableInfo() {
		String name = ChatFormatting.GREEN + NWBasicMethods.getTranslation(getTranslation()) + ChatFormatting.WHITE;
		String hover = NWBasicMethods.getTranslation("misc.naval_warfare.range") + ": " + (2 * LENGTH - 1);
		
		return NWBasicMethods.hoverableText(name, "gray", hover);
	}
	
	@Override
	public boolean canBeDisabled() {
		return true;
	}
}
