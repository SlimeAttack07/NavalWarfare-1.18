package slimeattack07.naval_warfare.util.abilities;

import java.util.ArrayList;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import slimeattack07.naval_warfare.init.NWItems;
import slimeattack07.naval_warfare.objects.blocks.GameController;
import slimeattack07.naval_warfare.tileentity.BoardTE;
import slimeattack07.naval_warfare.tileentity.GameControllerTE;
import slimeattack07.naval_warfare.util.NWBasicMethods;
import slimeattack07.naval_warfare.util.helpers.BattleLogHelper;
import slimeattack07.naval_warfare.util.helpers.ControllerActionHelper;

public class Insight implements Ability{
	private final String NAME;
	private final Block ANIMATION;
	private final boolean HINT;
	
	public Insight(String name, Block animation, boolean hint) {
		NAME = name;
		ANIMATION = animation;
		HINT = hint;
	}

	@Override
	public void activate(Level level, Player player, BoardTE board) {	
		ArrayList<BoardTE> tiles = getTiles(level, board);
		GameController control = null;
		GameControllerTE controller = null;
		String playername = "dummy";

		for(BoardTE te : tiles) {
			BlockPos pos = te.getController();
			BlockState state = level.getBlockState(pos);
			
			if(control == null) {
				control = (GameController) state.getBlock();
				
				if(state.getBlock() instanceof GameController) {
					if(control.validateController(level, pos)) {
						controller = (GameControllerTE) level.getBlockEntity(pos);
						playername = controller.getOwner();
						
						BlockPos opp = controller.getOpponent();
							
						if(!control.validateController(level, opp))
							return;
						
						controller = (GameControllerTE) level.getBlockEntity(opp);
						control = (GameController) controller.getBlockState().getBlock();
					}
				}
				else
					return;	
			}
			
			BoardTE matching = control.getBoardTile(level, controller, te.getId());
			
			if(matching != null) {		
				ControllerActionHelper cah = HINT ? ControllerActionHelper.createFlareTarget(matching.getBlockPos(), playername, matching.getBlockPos(),
						te.getBlockPos(), 20, true) : ControllerActionHelper.createSpyglassTarget(matching.getBlockPos(), playername, matching.getBlockPos(),
								te.getBlockPos(), 20, true);
								
				controller.addAction(cah);
				
				if(!HINT && ANIMATION != null) {
					NWBasicMethods.dropBlock(level, te.getBlockPos(), ANIMATION);
					NWBasicMethods.dropBlock(level, matching.getBlockPos(), ANIMATION);
					
					controller.recordOnRecorders(BattleLogHelper.createDropBlock(te.getId(), false, ANIMATION.getRegistryName()));
				}
			}
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
	public ArrayList<BoardTE> getTiles(Level level, BoardTE te) {
		BoardTE bte = te.getOpponentBoardZero();
		
		if(bte == null)
			return new ArrayList<>();
		
		return bte.selectRandomShip(1, true, true, true, 2000, 2000);
	}

	@Override
	public MutableComponent hoverableInfo() {
		String name = ChatFormatting.GREEN + NWBasicMethods.getTranslation(getTranslation()) + ChatFormatting.WHITE;
		
		return NWBasicMethods.hoverableText(name, "gray", "");
	}
	
	@Override
	public Item getAnimationItem() {
		return HINT ? NWItems.MINOR_INSIGHT.get() : NWItems.MAJOR_INSIGHT.get();
	}
	
	@Override
	public PassiveType getPassiveType() {
		return PassiveType.DESTROYED;
	}
}
