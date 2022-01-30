package slimeattack07.naval_warfare.util.abilities;

import java.util.ArrayList;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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
import slimeattack07.naval_warfare.util.helpers.ControllerActionHelper;

public class Napalm implements Ability{
	private final int AMOUNT;
	private final int COST;
	private final String NAME;
	private final Block ANIMATION;
	
	public Napalm(int amount, int cost, String name, Block animation) {
		AMOUNT = amount;
		COST = cost;
		NAME = name;
		ANIMATION = animation;
	}

	@Override
	public void activate(Level level, Player player, BoardTE board) {	
		ArrayList<BoardTE> tiles = getTiles(level, board);
		
		if(tiles.isEmpty())
			return;
		
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
				ControllerActionHelper cah = ControllerActionHelper.createNapalmAction(matching.getBlockPos(), playername, matching.getBlockPos(), 
						te.getBlockPos());
				
				controller.addAction(cah);
				
				NWBasicMethods.dropBlock(level, te.getBlockPos(), ANIMATION);
				NWBasicMethods.dropBlock(level, matching.getBlockPos(), ANIMATION);
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
		String hover = NWBasicMethods.getTranslation("ability.naval_warfare.energy_cost") + ": " + COST;
		
		return NWBasicMethods.hoverableText(name, "gray", hover);
	}
	
	@Override
	public Item getAnimationItem() {
		return NWItems.NAPALM.get();
	}
	
	@Override
	public int energyCost() {
		return COST;
	}
	
	public static String fireDamageText() {
		String name = ChatFormatting.GREEN + NWBasicMethods.getTranslation("ability.naval_warfare.fire_damage") + ChatFormatting.WHITE;
		String hover = NWBasicMethods.getTranslation("misc.naval_warfare.damage") + ": 1";
		
		return Component.Serializer.toJson(NWBasicMethods.hoverableText(name, "gray", hover));
	}
}
