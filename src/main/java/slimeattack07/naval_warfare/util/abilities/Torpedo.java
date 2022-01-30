package slimeattack07.naval_warfare.util.abilities;

import java.util.ArrayList;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import slimeattack07.naval_warfare.init.NWItems;
import slimeattack07.naval_warfare.objects.blocks.Board;
import slimeattack07.naval_warfare.objects.blocks.GameController;
import slimeattack07.naval_warfare.tileentity.BoardTE;
import slimeattack07.naval_warfare.tileentity.GameControllerTE;
import slimeattack07.naval_warfare.util.NWBasicMethods;
import slimeattack07.naval_warfare.util.helpers.ControllerActionHelper;

public class Torpedo implements Ability {
	private final int AMOUNT;
	private final int COST;
	private final int RANGE;
	private final int DAMAGE;
	private final int HEALTH;
	private final String NAME;
	private final int MAX_SEARCH = 500;
	private final Block ANIMATION;
	
	public Torpedo(int amount, int cost, int range, int damage, int health, String name, Block animation) {
		AMOUNT = amount;
		COST = cost;
		RANGE = range;
		DAMAGE = damage;
		HEALTH = health;
		NAME = name;
		ANIMATION = animation;
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

	private void fire(Level level, Player player, ArrayList<BoardTE> positions) {		
		GameControllerTE controller = null;
		GameController control = null;
		
		for(BoardTE te : positions) {
			if(controller == null) {
				BlockPos pos = te.getController();
				BlockState state = level.getBlockState(pos);
				
				if(state.getBlock() instanceof GameController) {
					control = (GameController) state.getBlock();
					
					if(control.validateController(level, pos))
						controller = (GameControllerTE) level.getBlockEntity(pos);
					else
						return;
				}
				else
					return;
			}
			
			BoardTE matching = control.getOpponentBoardTile(level, controller, te.getId(), false);
			
			if(matching != null) {
				String p = "dummy";
				
				if(player != null)
					p = player.getStringUUID();
				
				ControllerActionHelper cah = ControllerActionHelper.createTorpedoTarget(matching.getBlockPos(), p, matching.getBlockPos(), te.getBlockPos(), 
						DAMAGE, HEALTH, ANIMATION);
				
				controller.addAction(cah);
			}
		}
	}
	
	@Override
	public ArrayList<BoardTE> getTiles(Level level, BoardTE te){
		BoardTE real_board = te;
		Board board = (Board) real_board.getBlockState().getBlock();
		Direction dir = board.getControllerFacing(level, real_board.getBlockPos());
		int search = 0;
		
		while(search < MAX_SEARCH) { // just to prevent infinite loop, although it should never reach close to this number anyways.
			BlockPos pos = real_board.getBlockPos().relative(dir.getOpposite());
			Block block = level.getBlockState(pos).getBlock();
			
			if(block instanceof Board && board.validateBoard(level, pos)) {
				real_board = (BoardTE) level.getBlockEntity(pos);
				board = (Board) real_board.getBlockState().getBlock();
				search++;
			}
			else {
				return real_board.collectTiles(RANGE, dir);
			}
				
		}
		
		return te.collectTiles(RANGE, dir);
	}
	
	@Override
	public Item getAnimationItem() {
		return NWItems.TORPEDO.get();
	}
	
	@Override
	public MutableComponent hoverableInfo() {
		String name = ChatFormatting.GREEN + NWBasicMethods.getTranslation(getTranslation()) + ChatFormatting.WHITE;
		String hover = NWBasicMethods.getTranslation("ability.naval_warfare.energy_cost") + ": " + COST + ", ";
		hover += NWBasicMethods.getTranslation("misc.naval_warfare.range") + ": " + RANGE + ", ";
		hover += NWBasicMethods.getTranslation("misc.naval_warfare.damage") + ": " + DAMAGE + ", ";
		hover += NWBasicMethods.getTranslation("misc.naval_warfare.health") + ": " + HEALTH;
		
		return NWBasicMethods.hoverableText(name, "gray", hover);
	}
	
	@Override
	public int energyCost() {
		return COST;
	}
}
