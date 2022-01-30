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
import slimeattack07.naval_warfare.util.helpers.ControllerActionHelper;

public class Missile implements Ability{
	private final int AMOUNT;
	private final int COST;
	private final int DAMAGE;
	private final String NAME;
	private final Block ANIMATION;
	private final boolean PASSIVE;
	
	public Missile(int damage, String name, Block animation) {
		AMOUNT = 1;
		COST = 0;
		DAMAGE = damage;
		NAME = name;
		ANIMATION = animation;
		PASSIVE = true;
	}
	
	public Missile(int amount, int cost, int damage, String name, Block animation) {
		AMOUNT = amount;
		COST = cost;
		DAMAGE = damage;
		NAME = name;
		ANIMATION = animation;
		PASSIVE = false;
	}

	@Override
	public void activate(Level level, Player player, BoardTE board) {	
		ArrayList<BoardTE> tiles = getTiles(level, board);
		GameController control = null;
		GameControllerTE controller = null;
		int delay = 20;
		String playername = "dummy";
		
		if(!tiles.isEmpty()) {
			Board b = (Board) tiles.get(0).getBlockState().getBlock();
			b.selectTiles(level, tiles);
		}

		for(BoardTE te : tiles) {
			BlockPos pos = te.getController();
			BlockState state = level.getBlockState(pos);
			
			if(control == null) {
				control = (GameController) state.getBlock();
				
				if(state.getBlock() instanceof GameController) {
					if(control.validateController(level, pos)) {
						controller = (GameControllerTE) level.getBlockEntity(pos);
						playername = controller.getOwner();
						
						if(PASSIVE) {
							BlockPos opp = controller.getOpponent();
							
							if(!control.validateController(level, opp))
								return;
							
							controller = (GameControllerTE) level.getBlockEntity(opp);
							control = (GameController) controller.getBlockState().getBlock();
						}
					}
				}
				else
					return;	
			}
			
			BoardTE matching = PASSIVE ? control.getBoardTile(level, controller, te.getId()) : 
				control.getOpponentBoardTile(level, controller, te.getId(), false);
			
			if(matching != null) {				
				ControllerActionHelper cah = ControllerActionHelper.createTargetAction(delay, matching.getBlockPos(), playername, matching.getBlockPos(), 
						te.getBlockPos(), DAMAGE, TargetType.NORMAL, PASSIVE, !PASSIVE);
				
				controller.addAction(cah);
				delay = 0;
				
				level.playSound(null, te.getBlockPos(), NWSounds.MISSILE.get(), SoundSource.MASTER, 1, 1);
				level.playSound(null, matching.getBlockPos(), NWSounds.MISSILE.get(), SoundSource.MASTER, 1, 1);
				
				NWBasicMethods.dropBlock(level, te.getBlockPos(), ANIMATION);
				NWBasicMethods.dropBlock(level, matching.getBlockPos(), ANIMATION);
			}
		}
		
		if(PASSIVE && controller != null)
			controller.addAction(ControllerActionHelper.createValidate());
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
		if(PASSIVE) {
			BoardTE bte = te.getOpponentBoardZero();
			
			if(bte == null)
				return new ArrayList<>();
			
			return bte.selectRandomShip(1, true, true, true, 2000, 2000);
		}
		
		ArrayList<BoardTE> tiles =  new ArrayList<>();
		
		tiles.add(te);
		
		return tiles;
	}

	@Override
	public MutableComponent hoverableInfo() {
		String name = ChatFormatting.GREEN + NWBasicMethods.getTranslation(getTranslation()) + ChatFormatting.WHITE;
		String hover = !PASSIVE ? NWBasicMethods.getTranslation("ability.naval_warfare.energy_cost") + ": " + COST + ", " : "";
		hover += NWBasicMethods.getTranslation("misc.naval_warfare.damage") + ": " + DAMAGE;
		
		return NWBasicMethods.hoverableText(name, "gray", hover);
	}
	
	@Override
	public Item getAnimationItem() {
		return PASSIVE ? NWItems.RETALIATION.get() : NWItems.MISSILE.get();
	}
	
	@Override
	public int energyCost() {
		return COST;
	}
	
	@Override
	public PassiveType getPassiveType() {
		return PASSIVE ? PassiveType.DESTROYED : PassiveType.NOT;
	}
}
