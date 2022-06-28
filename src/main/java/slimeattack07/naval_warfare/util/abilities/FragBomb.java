package slimeattack07.naval_warfare.util.abilities;

import java.util.ArrayList;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
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

public class FragBomb implements Ability{
	private final int AMOUNT;
	private final int COST;
	private final int FRAG;
	private final String NAME;
	private final Block ANIMATION;
	private final Block FRAG_ANIMATION;
	
	public FragBomb(int amount, int cost, int frag, String name, Block animation, Block frag_animation) {
		AMOUNT = amount;
		COST = cost;
		FRAG = Math.min(frag, 8);
		NAME = name;
		ANIMATION = animation;
		FRAG_ANIMATION = frag_animation;
	}

	@Override
	public void activate(Level level, Player player, BoardTE board) {	
		ArrayList<BoardTE> tiles = getTiles(level, board);
		
		if(tiles.isEmpty())
			return;
		
		BoardTE bte = tiles.get(0);
		Board b = (Board) bte.getBlockState().getBlock();
		
		tiles = bte.collectTileArea(2, 2, 2, 2, b.getControllerFacing(level, bte.getBlockPos()));
		tiles.remove(bte);
		tiles = bte.selectRandom(FRAG, tiles);
		tiles.add(0, bte);
		
		if(!tiles.isEmpty())
			b.selectTiles(level, tiles);
		
		GameController control = null;
		GameControllerTE controller = null;
		String playername = "dummy";
		TargetType type = TargetType.NORMAL;
		int delay = 20;
		boolean multi_ability = false;
		Block animation = ANIMATION;

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
				ControllerActionHelper cah = ControllerActionHelper.createFragbombTarget(delay, matching.getBlockPos(), playername, matching.getBlockPos(), 
						te.getBlockPos(), type, animation, multi_ability);
				
				controller.addAction(cah);
				
				if(type.equals(TargetType.NORMAL)) {
					level.playSound(null, te.getBlockPos(), NWSounds.SHOT.get(), SoundSource.MASTER, 1, 1);
					level.playSound(null, matching.getBlockPos(), NWSounds.SHOT.get(), SoundSource.MASTER, 1, 1);
					NWBasicMethods.dropBlock(level, te.getBlockPos(), animation);
					NWBasicMethods.dropBlock(level, matching.getBlockPos(), animation);
					controller.recordOnRecorders(BattleLogHelper.createDropBlock(te.getId(), true, animation.getRegistryName()));
				}
				
				type = TargetType.UNBLOCKABLE;
				delay = Math.max(0, delay - 10);
				multi_ability = true;
				animation = FRAG_ANIMATION;
			}
		}
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
		ArrayList<BoardTE> tiles = new ArrayList<>();
		
		tiles.add(te);
		
		return tiles;
	}

	@Override
	public MutableComponent hoverableInfo() {
		String name = ChatFormatting.GREEN + NWBasicMethods.getTranslation(getTranslation()) + ChatFormatting.WHITE;
		String hover = NWBasicMethods.getTranslation("ability.naval_warfare.energy_cost") + ": " + COST + ", ";
		hover += NWBasicMethods.getTranslation("ability.naval_warfare.extra_tiles") + ": " + FRAG;
		
		return NWBasicMethods.hoverableText(name, "gray", hover);
	}
	
	@Override
	public Item getAnimationItem() {
		return NWItems.FRAG_BOMB.get();
	}
	
	@Override
	public int energyCost() {
		return COST;
	}
}
