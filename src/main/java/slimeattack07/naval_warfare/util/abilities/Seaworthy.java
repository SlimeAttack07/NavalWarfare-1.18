package slimeattack07.naval_warfare.util.abilities;

import java.util.ArrayList;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import slimeattack07.naval_warfare.tileentity.BoardTE;
import slimeattack07.naval_warfare.util.NWBasicMethods;

public class Seaworthy implements Ability{
	private final String NAME;
	private final int AMOUNT;
	
	public Seaworthy(String name, int amount) {
		NAME = name;
		AMOUNT = amount;
	}

	@Override
	public void activate(Level level, Player player, BoardTE board) {		
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
		return null;
	}

	@Override
	public PassiveType getPassiveType() {
		return PassiveType.CONFIG;
	}
	
	@Override
	public MutableComponent hoverableInfo() {
		String name = ChatFormatting.GREEN + NWBasicMethods.getTranslation(getTranslation()) + ChatFormatting.WHITE;
		String hover = NWBasicMethods.getTranslation("misc.naval_warfare.amount") + ": " + AMOUNT;
		
		return NWBasicMethods.hoverableText(name, "gray", hover);
	}
}
