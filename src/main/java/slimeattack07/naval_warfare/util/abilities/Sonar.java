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
import slimeattack07.naval_warfare.init.NWItems;
import slimeattack07.naval_warfare.init.NWSounds;
import slimeattack07.naval_warfare.objects.blocks.Board;
import slimeattack07.naval_warfare.objects.blocks.GameController;
import slimeattack07.naval_warfare.objects.blocks.ShipBlock;
import slimeattack07.naval_warfare.tileentity.BoardTE;
import slimeattack07.naval_warfare.tileentity.GameControllerTE;
import slimeattack07.naval_warfare.util.NWBasicMethods;
import slimeattack07.naval_warfare.util.helpers.ControllerActionHelper;

public class Sonar implements Ability{
	
	public Sonar() {
	}

	@Override
	public void activate(Level level, Player player, BoardTE board) {	
		ArrayList<BoardTE> tiles = findDeployables(level, board);
		GameController control = null;
		GameControllerTE controller = null;
		String playername = "dummy";
		int delay = 20;

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
				ControllerActionHelper cah = ControllerActionHelper.createSpyglassTarget(matching.getBlockPos(), playername, matching.getBlockPos(),
								te.getBlockPos(), delay, false);
				
				level.playSound(null, te.getBlockPos(), NWSounds.SONAR.get(), SoundSource.MASTER, 1, 1.25f);
				level.playSound(null, matching.getBlockPos(), NWSounds.SONAR.get(), SoundSource.MASTER, 1, 1.25f);
								
				controller.addAction(cah);
				delay = 0;
			}
		}
	}
	
	private ArrayList<BoardTE> findDeployables(Level level, BoardTE te){
		Board board = (Board) te.getBlockState().getBlock();		
		ArrayList<BoardTE> matching = new ArrayList<>();
		
		for(BoardTE bte : board.matchTiles(level, getTiles(level, te), false)) {
			BlockState state = level.getBlockState(bte.getBlockPos().above());
			
			if(state.getBlock() instanceof ShipBlock) {
				ShipBlock ship = (ShipBlock) state.getBlock();
				
				if(!ship.losesHP())
					matching.add(bte);
			}
		}
		
		ArrayList<BoardTE> targets = board.matchTiles(level, matching, true);
		
		if(targets.size() == matching.size())
			return targets;
		
		return new ArrayList<>();
	}

	@Override
	public int getAmount() {
		return 1;
	}

	@Override
	public String getTranslation() {
		return "abilities.naval_warfare.sonar";
	}

	@Override
	public ArrayList<BoardTE> getTiles(Level level, BoardTE te) {
		Board board = (Board) te.getBlockState().getBlock();
		
		double width = (NavalWarfareConfig.sonar_width.get() + 1) / 2d;
		double length = (NavalWarfareConfig.sonar_length.get() + 1) / 2d;
		
		return te.collectTileArea((int) Math.ceil(length), (int) Math.floor(length), (int) Math.ceil(width), (int) Math.floor(width), 
				board.getControllerFacing(level, te.getBlockPos()));
	}

	@Override
	public MutableComponent hoverableInfo() {
		String name = ChatFormatting.GREEN + NWBasicMethods.getTranslation(getTranslation()) + ChatFormatting.WHITE;
		String hover =  NWBasicMethods.getTranslation("misc.naval_warfare.length") + ": " + NavalWarfareConfig.sonar_length.get() + ", ";
		hover += NWBasicMethods.getTranslation("misc.naval_warfare.width") + ": " + NavalWarfareConfig.sonar_width.get()+ ", ";
		
		return NWBasicMethods.hoverableText(name, "gray", hover);
	}
	
	@Override
	public Item getAnimationItem() {
		return NWItems.SONAR.get();
	}
}
