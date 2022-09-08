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
import slimeattack07.naval_warfare.util.helpers.BattleLogHelper;
import slimeattack07.naval_warfare.util.helpers.ControllerActionHelper;

public class Sonar implements Ability{
	private final int AMOUNT;
	private final int COST;
	private final int UP;
	private final int DOWN;
	private final int LEFT;
	private final int RIGHT;
	private final String NAME;
	
	public Sonar() {
		double width = (NavalWarfareConfig.sonar_width.get() + 1) / 2d;
		double length = (NavalWarfareConfig.sonar_length.get() + 1) / 2d;
		
		AMOUNT = 1;
		COST = 0;
		
		UP = (int) Math.ceil(length);
		DOWN = (int) Math.floor(length);
		LEFT = (int) Math.ceil(width);
		RIGHT = (int) Math.floor(width);
		NAME = "sonar";
	}
	
	public Sonar(int amount, int cost, int up, int down, int left, int right, String name) {
		AMOUNT = amount;
		COST = cost;
		UP = up;
		DOWN = down;
		LEFT = left;
		RIGHT = right;
		NAME = name;
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
				controller.recordOnRecorders(BattleLogHelper.createSound(te.getId(), true, NWSounds.SONAR.get(), 1, 1.25f));
								
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
		return AMOUNT;
	}

	@Override
	public String getTranslation() {
		return "abilities.naval_warfare." + NAME;
	}

	@Override
	public ArrayList<BoardTE> getTiles(Level level, BoardTE te) {
		Board board = (Board) te.getBlockState().getBlock();
		
		return te.collectTileArea(UP, DOWN, LEFT, RIGHT, board.getControllerFacing(level, te.getBlockPos()));
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
	
	@Override
	public int energyCost() {
		return COST;
	}
}
