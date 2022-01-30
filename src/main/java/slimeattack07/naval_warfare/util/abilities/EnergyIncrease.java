package slimeattack07.naval_warfare.util.abilities;

import java.util.ArrayList;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import slimeattack07.naval_warfare.init.NWItems;
import slimeattack07.naval_warfare.objects.blocks.Board;
import slimeattack07.naval_warfare.objects.blocks.GameController;
import slimeattack07.naval_warfare.tileentity.BoardTE;
import slimeattack07.naval_warfare.tileentity.GameControllerTE;
import slimeattack07.naval_warfare.util.NWBasicMethods;
import slimeattack07.naval_warfare.util.helpers.ControllerActionHelper;

public class EnergyIncrease implements Ability {
	private final String NAME;
	private final int AMOUNT;
	private final int ENERGY;
	private final PassiveType TYPE;
	
	public EnergyIncrease(String name, int amount, int energy, PassiveType type) {
		NAME = name;
		AMOUNT = amount;
		ENERGY = energy;
		
		switch(type) {
		case START_GAME:
		case DESTROYED:
		case NOT: 
			TYPE = type;
			break;
		default: TYPE = PassiveType.NOT;
		}
	}
	
	@Override
	public void activate(Level level, Player player, BoardTE board) {
		Board b = (Board) board.getBlockState().getBlock();
		
		GameControllerTE controller = b.getController(level, board.getBlockPos());
		
		if(controller == null)
			return;
		
		
		if(TYPE.equals(PassiveType.DESTROYED)) {
			GameController control = (GameController) controller.getBlockState().getBlock();
			BlockPos opp = controller.getOpponent();
			
			if(!control.validateController(level, opp))
				return;
			
			controller = (GameControllerTE) level.getBlockEntity(opp);
			control = (GameController) controller.getBlockState().getBlock();
		}
		
		controller.addAction(ControllerActionHelper.createEnergyGain(ENERGY, !TYPE.equals(PassiveType.DESTROYED)));
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
	public PassiveType getPassiveType() {
		return TYPE;
	}
	
	@Override
	public MutableComponent hoverableInfo() {
		String name = ChatFormatting.GREEN + NWBasicMethods.getTranslation(getTranslation()) + ChatFormatting.WHITE;
		String hover = TYPE.equals(PassiveType.NOT) ? NWBasicMethods.getTranslation("misc.naval_warfare.energy_cost") + ": " + ENERGY + ", " : "";
		hover = NWBasicMethods.getTranslation("misc.naval_warfare.amount") + ": " + ENERGY;
		
		return NWBasicMethods.hoverableText(name, "gray", hover);
	}
	
	@Override
	public Item getAnimationItem() {
		switch(TYPE) {
		case DESTROYED: return NWItems.ENERGY_GRANT.get();
		case START_GAME: return NWItems.ENERGY_START.get();
		default: return NWItems.ENERGY_GAIN.get();
		}
	}

	@Override
	public ArrayList<BoardTE> getTiles(Level level, BoardTE te) {
		return new ArrayList<>();
	}
	
	@Override
	public boolean needsTarget() {
		return false;
	}
}
