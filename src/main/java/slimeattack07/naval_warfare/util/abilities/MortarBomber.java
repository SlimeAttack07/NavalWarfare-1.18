package slimeattack07.naval_warfare.util.abilities;

import java.util.ArrayList;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
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
import slimeattack07.naval_warfare.util.helpers.ControllerActionHelper;

public class MortarBomber implements Ability{
	private final int AMOUNT;
	private final int COST;
	private final int DROP;
	private final int LEFT;
	private final int RIGHT;
	private final int UP;
	private final int DOWN;
	private final String NAME;
	private final Block ANIMATION;
	private boolean BOMBER;
	
	public MortarBomber(int amount, int cost, int drop, int left, int right, int up, int down, String name, Block animation, boolean bomber) {
		AMOUNT = amount;
		COST = cost;
		DROP = drop;
		NAME = name;
		LEFT = left;
		RIGHT = right;
		UP = up;
		DOWN = down;
		ANIMATION = animation;
		BOMBER = bomber;
	}

	@Override
	public void activate(Level level, Player player, BoardTE board) {	
		ArrayList<BoardTE> tiles = getTiles(level, board);
		
		if(tiles.isEmpty())
			return;
		
		String playername = "dummy";
		int delay = BOMBER ? 10 : 20;
		
		if(tiles.isEmpty())
			return;
		
		BlockPos pos = tiles.get(0).getController();
		BlockState state = level.getBlockState(pos);
		
		GameController control = (GameController) state.getBlock();
			
			if(!control.validateController(level, pos))
				return;
			
		GameControllerTE controller = (GameControllerTE) level.getBlockEntity(pos);
		playername = controller.getOwner();
		
		if(BOMBER) {
			BoardTE te = findAntiAir(level, controller, tiles);
			
			if(te != null) {
				tiles.remove(te);
				BoardTE bte = tiles.get(0);	
				tiles = bte.selectRandom(DROP - 1, tiles);
				tiles.add(0, te);
			}
			else {
				BoardTE bte = tiles.get(0);
				tiles = bte.selectRandom(DROP, tiles);
			}
		}
		else {
			BoardTE bte = tiles.get(0);	
			tiles = bte.selectRandom(DROP, tiles);
		}
		
		if(!tiles.isEmpty()) {
			Board b = (Board) tiles.get(0).getBlockState().getBlock();
			b.selectTiles(level, tiles);
		}

		for(BoardTE te : tiles) {
			BoardTE matching = control.getOpponentBoardTile(level, controller, te.getId(), false);
			
			if(matching != null) {				
				ControllerActionHelper cah = BOMBER ? ControllerActionHelper.createBomberTarget(delay, matching.getBlockPos(), playername, matching.getBlockPos(), 
						te.getBlockPos(), ANIMATION) : 
							ControllerActionHelper.createMultiTarget(delay, matching.getBlockPos(), playername, matching.getBlockPos(), te.getBlockPos(), true, false);
				
				controller.addAction(cah);
				
				if(!BOMBER) {
					level.playSound(null, te.getBlockPos(), NWSounds.SHOT.get(), SoundSource.MASTER, 1, 0.75f);
					level.playSound(null, matching.getBlockPos(), NWSounds.SHOT.get(), SoundSource.MASTER, 1, 0.75f);
					
					NWBasicMethods.dropBlock(level, te.getBlockPos(), ANIMATION);
					NWBasicMethods.dropBlock(level, matching.getBlockPos(), ANIMATION);
					delay = 0;
				}
			}
		}
	}

	@Override
	public int getAmount() {
		return AMOUNT;
	}
	
	@Nullable
	public BoardTE findAntiAir(Level level, GameControllerTE controller, ArrayList<BoardTE> tiles) {
		GameController control = (GameController) controller.getBlockState().getBlock();
		
		for(int i = 0; i < tiles.size(); i++) {
			BoardTE te = tiles.get(i);
			BoardTE matching = control.getOpponentBoardTile(level, controller, te.getId(), false);
			
			if(level.getBlockState(matching.getBlockPos().above(5)).getBlock().equals(NWBlocks.ANTI_AIR.get()))
				return te;
		}
		
		return null;
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
		String hover = NWBasicMethods.getTranslation("ability.naval_warfare.energy_cost") + ": " + COST + ", ";
		hover += NWBasicMethods.getTranslation("misc.naval_warfare.length") + ": " + (LEFT + RIGHT - 1) + ", ";
		hover += NWBasicMethods.getTranslation("misc.naval_warfare.width") + ": " + (UP + DOWN - 1) + ", ";
		hover += NWBasicMethods.getTranslation("ability.naval_warfare.tiles_targeted") + ": " + DROP;
		
		return NWBasicMethods.hoverableText(name, "gray", hover);
	}
	
	@Override
	public Item getAnimationItem() {
		return BOMBER ? NWItems.BOMBER.get() : NWItems.MORTAR.get();
	}
	
	@Override
	public int energyCost() {
		return COST;
	}
}
