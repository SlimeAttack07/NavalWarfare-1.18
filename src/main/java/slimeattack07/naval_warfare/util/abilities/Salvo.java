package slimeattack07.naval_warfare.util.abilities;

import java.util.ArrayList;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import slimeattack07.naval_warfare.init.NWBlocks;
import slimeattack07.naval_warfare.init.NWItems;
import slimeattack07.naval_warfare.init.NWSounds;
import slimeattack07.naval_warfare.objects.blocks.Board;
import slimeattack07.naval_warfare.objects.blocks.GameController;
import slimeattack07.naval_warfare.tileentity.BoardTE;
import slimeattack07.naval_warfare.tileentity.GameControllerTE;
import slimeattack07.naval_warfare.util.NWBasicMethods;
import slimeattack07.naval_warfare.util.helpers.BattleLogHelper;
import slimeattack07.naval_warfare.util.helpers.ControllerActionHelper;

public class Salvo implements Ability {
	private final int AMOUNT;
	private final int COST;
	private final int LEFT;
	private final int RIGHT;
	private final String NAME;
	private final boolean SHELLS;
	
	public Salvo(int amount, int cost, int left, int right, boolean shells, String name) {
		AMOUNT = amount;
		COST = cost;
		LEFT = left;
		RIGHT = right;
		SHELLS = shells;
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

	private void fire(Level level, Player player, ArrayList<BoardTE> positions) {		
		GameControllerTE controller = null;
		GameController control = null;
		
		int delay = 20;
		Block block = SHELLS ? NWBlocks.SHELL.get() : NWBlocks.CANNONBALL.get();
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
				
				ControllerActionHelper cah = ControllerActionHelper.createMultiTarget(delay, matching.getBlockPos(), p, matching.getBlockPos(), te.getBlockPos(), true,
						false);
				delay = 0;
				ids.add(te.getId());
				controller.addAction(cah);				
				
				level.playSound(null, te.getBlockPos(), NWSounds.SHOT.get(), SoundSource.MASTER, 1, 1.25f);
				level.playSound(null, matching.getBlockPos(), NWSounds.SHOT.get(), SoundSource.MASTER, 1, 1.25f);
				
				NWBasicMethods.dropBlock(level, te.getBlockPos(), block);
				NWBasicMethods.dropBlock(level, matching.getBlockPos(), block);
			}
		}
		
		if(!ids.isEmpty()) {
			BattleLogHelper blh_drops = BattleLogHelper.createDropBlocks(ids, true, block.getRegistryName());
			BattleLogHelper blh_sounds = BattleLogHelper.createSounds(ids, true, NWSounds.SHOT.get(), 1f, 1.25f);
			controller.recordOnRecorders(blh_sounds);
			controller.recordOnRecorders(blh_drops);
		}
	}
	
	@Override
	public ArrayList<BoardTE> getTiles(Level level, BoardTE te){
		Board board = (Board) te.getBlockState().getBlock();
		Direction dir = board.getControllerFacing(level, te.getBlockPos());
		
		return te.collectTileArea(1, 0, LEFT, RIGHT, dir);
	}
	
	@Override
	public Item getAnimationItem() {
		return NWItems.SALVO.get();
	}
	
	@Override
	public MutableComponent hoverableInfo() {
		String name = ChatFormatting.GREEN + NWBasicMethods.getTranslation(getTranslation()) + ChatFormatting.WHITE;
		String hover = NWBasicMethods.getTranslation("ability.naval_warfare.energy_cost") + ": " + COST + ", ";
		hover += NWBasicMethods.getTranslation("misc.naval_warfare.length") + ": " + (LEFT + RIGHT - 1);
		
		return NWBasicMethods.hoverableText(name, "gray", hover);
	}
	
	@Override
	public int energyCost() {
		return COST;
	}
}
