package slimeattack07.naval_warfare.util.abilities;

import java.util.ArrayList;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import slimeattack07.naval_warfare.init.NWBlocks;
import slimeattack07.naval_warfare.init.NWItems;
import slimeattack07.naval_warfare.objects.blocks.Board;
import slimeattack07.naval_warfare.objects.blocks.GameController;
import slimeattack07.naval_warfare.tileentity.BoardTE;
import slimeattack07.naval_warfare.tileentity.GameControllerTE;
import slimeattack07.naval_warfare.util.NWBasicMethods;
import slimeattack07.naval_warfare.util.helpers.BattleLogHelper;
import slimeattack07.naval_warfare.util.helpers.ControllerActionHelper;

public class Flare implements Ability {
	private final int AMOUNT;
	private final int COST;
	private final int LEFT;
	private final int RIGHT;
	private final int UP;
	private final int DOWN;
	private final String NAME;
	
	public Flare(int amount, int cost, int left, int right, int up, int down, String name) {
		AMOUNT = amount;
		COST = cost;
		LEFT = left;
		RIGHT = right;
		UP = up;
		DOWN = down;
		NAME = name;
	}
	
	// May be able to turn this into void later on
	@Override
	public void activate(Level level, Player player, BoardTE board) {
		ArrayList<BoardTE> tiles = getTiles(level, board);
		Board b = (Board) board.getBlockState().getBlock();
		b.selectTiles(level, tiles);
		
		fire(level, player, tiles);
	}
	
	@Override
	public String getTranslation() {
		return "abilities.naval_warfare." + NAME;
	}
	
	@Override
	public int getAmount() {
		return AMOUNT;
	}

	private boolean fire(Level level, Player player, ArrayList<BoardTE> positions) {		
		GameControllerTE controller = null;
		GameController control = null;
		
		boolean hit = false;
		int delay = 20;
		ArrayList<Integer> ids = new ArrayList<>();
		
		for(BoardTE te : positions) {
			if(controller == null) {
				BlockPos pos = te.getController();
				BlockState state = level.getBlockState(pos);
				
				if(state.getBlock() instanceof GameController) {
					control = (GameController) state.getBlock();
					
					if(control.validateController(level, pos))
						controller = (GameControllerTE) level.getBlockEntity(pos);
					else
						return false;
				}
				else
					return false;
			}
			
			BoardTE matching = control.getOpponentBoardTile(level, controller, te.getId(), false);
			
			if(matching != null) {
				String p = "dummy";
				
				if(player != null)
					p = player.getStringUUID();
				
				ControllerActionHelper cah = ControllerActionHelper.createFlareTarget(matching.getBlockPos(), p, matching.getBlockPos(), te.getBlockPos(), 
						delay, false);
				delay = 0;
				
				controller.addAction(cah);
				ids.add(te.getId());
				
				NWBasicMethods.dropBlock(level, te.getBlockPos(), NWBlocks.FLARE.get());
				NWBasicMethods.dropBlock(level, matching.getBlockPos(), NWBlocks.FLARE.get());
			}
		}
		
		if(!ids.isEmpty())
			controller.recordOnRecorders(BattleLogHelper.createDropBlocks(ids, true, NWBlocks.FLARE.get().getRegistryName()));
		
		return hit;
	}
	
	@Override
	public ArrayList<BoardTE> getTiles(Level level, BoardTE te){
		BoardTE real_board = te;
		Board board = (Board) real_board.getBlockState().getBlock();
		Direction dir = board.getControllerFacing(level, real_board.getBlockPos());
		
		return te.collectTileArea(UP, DOWN, LEFT, RIGHT, dir);
	}
	
	@Override
	public Item getAnimationItem() {
		return NWItems.FLARE.get();
	}
	
	@Override
	public MutableComponent hoverableInfo() {
		String name = ChatFormatting.GREEN + NWBasicMethods.getTranslation(getTranslation()) + ChatFormatting.WHITE;
		String hover = NWBasicMethods.getTranslation("ability.naval_warfare.energy_cost") + ": " + COST + ", ";
		hover += NWBasicMethods.getTranslation("misc.naval_warfare.length") + ": " + (LEFT + RIGHT - 1) + ", ";
		hover += NWBasicMethods.getTranslation("misc.naval_warfare.width") + ": " + (UP + DOWN - 1);
		
		return NWBasicMethods.hoverableText(name, "gray", hover);
	}
	
	@Override
	public int energyCost() {
		return COST;
	}
}
