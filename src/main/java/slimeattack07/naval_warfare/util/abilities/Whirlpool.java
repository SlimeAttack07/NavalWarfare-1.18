package slimeattack07.naval_warfare.util.abilities;

import java.util.ArrayList;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import slimeattack07.naval_warfare.init.NWItems;
import slimeattack07.naval_warfare.objects.blocks.Board;
import slimeattack07.naval_warfare.objects.blocks.GameController;
import slimeattack07.naval_warfare.tileentity.BoardTE;
import slimeattack07.naval_warfare.tileentity.GameControllerTE;
import slimeattack07.naval_warfare.util.NWBasicMethods;
import slimeattack07.naval_warfare.util.TargetType;
import slimeattack07.naval_warfare.util.helpers.ControllerActionHelper;

public class Whirlpool implements Ability {
	private final String NAME;
	private final int AMOUNT;
	private final Block ANIMATION;
	
	public Whirlpool(String name, int amount, Block animation) {
		NAME = name;
		AMOUNT = amount;
		ANIMATION = animation;
	}
	
	@Override
	public void activate(Level level, Player player, BoardTE board) {
		GameControllerTE controller = null;
		GameController control = null;
		ArrayList<BoardTE> tiles = getTiles(level, board);
		int delay = 20;
		String playername = "dummy";
		
		if(!tiles.isEmpty()) {
			Board b = (Board) tiles.get(0).getBlockState().getBlock();
			b.selectTiles(level, tiles);
		}
		
		
		for(BoardTE te : tiles) {
			if(controller == null) {
				Board b = (Board) te.getBlockState().getBlock();
				controller = b.getController(level, te.getBlockPos());
				
				if(controller == null)
					return;
				
				playername = controller.getOwner();
				control = (GameController) controller.getBlockState().getBlock();
				BlockPos opp = controller.getOpponent();
				
				if(!control.validateController(level, opp))
					return;
				
				controller = (GameControllerTE) level.getBlockEntity(opp);
				control = (GameController) controller.getBlockState().getBlock();
			}

			BoardTE matching = control.getBoardTile(level, controller, te.getId());
			
			if(matching != null) {			
				ControllerActionHelper cah = ControllerActionHelper.createTargetAction(delay, matching.getBlockPos(), playername, 
						matching.getBlockPos(), te.getBlockPos(), 1, TargetType.REVEAL, true, false, ANIMATION);
				
				controller.addAction(cah);
				delay = 0;
								
				NWBasicMethods.dropBlock(level, te.getBlockPos(), ANIMATION);
				NWBasicMethods.dropBlock(level, matching.getBlockPos(), ANIMATION);
			}
		}
		
		controller.addAction(ControllerActionHelper.createValidate());
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
		BoardTE bte = te.getOpponentBoardZero();
		
		if(bte == null)
			return new ArrayList<>();
		
		return bte.selectRandomTiles(AMOUNT, true, true);
	}
	
	@Override
	public PassiveType getPassiveType() {
		return PassiveType.DESTROYED;
	}
	
	@Override
	public MutableComponent hoverableInfo() {
		String name = ChatFormatting.GREEN + NWBasicMethods.getTranslation(getTranslation()) + ChatFormatting.WHITE;
		String hover = NWBasicMethods.getTranslation("misc.naval_warfare.amount") + ": " + AMOUNT;
		
		return NWBasicMethods.hoverableText(name, "gray", hover);
	}
	
	@Override
	public Item getAnimationItem() {
		return NWItems.WHIRLPOOL.get();
	}
}
