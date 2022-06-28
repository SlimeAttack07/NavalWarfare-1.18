package slimeattack07.naval_warfare.util.abilities;

import java.util.ArrayList;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import slimeattack07.naval_warfare.config.NavalWarfareConfig;
import slimeattack07.naval_warfare.init.NWBlocks;
import slimeattack07.naval_warfare.init.NWItems;
import slimeattack07.naval_warfare.init.NWSounds;
import slimeattack07.naval_warfare.objects.blocks.Board;
import slimeattack07.naval_warfare.objects.blocks.GameController;
import slimeattack07.naval_warfare.tileentity.BoardTE;
import slimeattack07.naval_warfare.tileentity.GameControllerTE;
import slimeattack07.naval_warfare.util.NWBasicMethods;
import slimeattack07.naval_warfare.util.TargetType;
import slimeattack07.naval_warfare.util.helpers.BattleLogHelper;
import slimeattack07.naval_warfare.util.helpers.ControllerActionHelper;

public class Heatseaker implements Ability{
	private TargetType type = NavalWarfareConfig.heatseaker_unblockable.get() ? TargetType.UNBLOCKABLE : TargetType.NORMAL;
	
	public Heatseaker() {
	}

	@Override
	public void activate(Level level, Player player, BoardTE board) {	
		ArrayList<BoardTE> tiles = board.selectRandomShip(1, true, true, false, NavalWarfareConfig.heatseaker_length.get(), 
				NavalWarfareConfig.heatseaker_length.get());
		
		if(tiles.isEmpty())
			tiles.add(board);
		
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
						
					}
				}
				else
					return;	
			}
			
			BoardTE matching = control.getOpponentBoardTile(level, controller, te.getId(), false);
			
			if(matching != null) {				
				ControllerActionHelper cah = ControllerActionHelper.createTargetAction(20, matching.getBlockPos(), playername, matching.getBlockPos(), 
						te.getBlockPos(), NavalWarfareConfig.heatseaker_damage.get(), type, false, true, NWBlocks.HEATSEAKER.get());
				
				controller.addAction(cah);
				
				level.playSound(null, te.getBlockPos(), NWSounds.MISSILE.get(), SoundSource.MASTER, 1, 1);
				level.playSound(null, matching.getBlockPos(), NWSounds.MISSILE.get(), SoundSource.MASTER, 1, 1);
				
				NWBasicMethods.dropBlock(level, te.getBlockPos(), NWBlocks.HEATSEAKER.get());
				NWBasicMethods.dropBlock(level, matching.getBlockPos(), NWBlocks.HEATSEAKER.get());
				controller.recordOnRecorders(BattleLogHelper.createDropBlock(te.getId(), true, NWBlocks.HEATSEAKER.get().getRegistryName()));
			}
		}
	}

	@Override
	public int getAmount() {
		return 1;
	}

	@Override
	public String getTranslation() {
		return "abilities.naval_warfare.heatseaker";
	}

	@Override
	public ArrayList<BoardTE> getTiles(Level level, BoardTE te) {	
		Board board = (Board) te.getBlockState().getBlock();
		
		double width = (NavalWarfareConfig.heatseaker_width.get() + 1) / 2d;
		double length = (NavalWarfareConfig.heatseaker_length.get() + 1) / 2d;
		
		return te.collectTileArea((int) Math.ceil(length), (int) Math.floor(length), (int) Math.ceil(width), (int) Math.floor(width), 
				board.getControllerFacing(level, te.getBlockPos()));
	}

	@Override
	public MutableComponent hoverableInfo() {
		String name = ChatFormatting.GREEN + NWBasicMethods.getTranslation(getTranslation()) + ChatFormatting.WHITE;
		String hover =  NWBasicMethods.getTranslation("misc.naval_warfare.length") + ": " + NavalWarfareConfig.heatseaker_length.get() + ", ";
		hover += NWBasicMethods.getTranslation("misc.naval_warfare.width") + ": " + NavalWarfareConfig.heatseaker_width.get()+ ", ";
		
		return NWBasicMethods.hoverableText(name, "gray", hover);
	}
	
	@Override
	public Item getAnimationItem() {
		return NWItems.HEATSEAKER.get();
	}
}
