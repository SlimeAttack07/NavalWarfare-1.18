package slimeattack07.naval_warfare.util.abilities;

import java.util.ArrayList;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import slimeattack07.naval_warfare.config.NavalWarfareConfig;
import slimeattack07.naval_warfare.init.NWItems;
import slimeattack07.naval_warfare.objects.blocks.Board;
import slimeattack07.naval_warfare.objects.blocks.GameController;
import slimeattack07.naval_warfare.tileentity.BoardTE;
import slimeattack07.naval_warfare.tileentity.GameControllerTE;
import slimeattack07.naval_warfare.util.NWBasicMethods;
import slimeattack07.naval_warfare.util.helpers.ControllerActionHelper;

public class EndTurn implements Ability {
	private final String NAME;
	
	public EndTurn(String name) {
		NAME = name;
	}
	
	@Override
	public void activate(Level level, Player player, BoardTE board) {
		Board b = (Board) board.getBlockState().getBlock();
		
		GameControllerTE controller = b.getController(level, board.getBlockPos());
		
		if(controller == null)
			return;
		
		GameController control = (GameController) controller.getBlockState().getBlock();
		BlockPos opp = controller.getOpponent();
		
		if(!control.validateController(level, opp))
			return;
		
		controller = (GameControllerTE) level.getBlockEntity(opp);
		control = (GameController) controller.getBlockState().getBlock();
		
		controller.addAction(ControllerActionHelper.createEndTurn());
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
	public String getPassiveCategory() {
		return "abilities.naval_warfare.turn_end";
	}
	
	@Override
	public PassiveType getPassiveType() {
		return PassiveType.HIT;
	}
	
	@Override
	public MutableComponent hoverableInfo() {
		String translation = NavalWarfareConfig.reveal_on_hit_passives.get() ? getTranslation() : getPassiveCategory();
		String name = ChatFormatting.GREEN + NWBasicMethods.getTranslation(translation) + ChatFormatting.WHITE;
		
		return NWBasicMethods.hoverableText(name, "gray", "");
	}
	
	@Override
	public Item getAnimationItem() {
		return NWItems.END_TURN.get();
	}

	@Override
	public ArrayList<BoardTE> getTiles(Level level, BoardTE te) {
		return new ArrayList<>();
	}
}
