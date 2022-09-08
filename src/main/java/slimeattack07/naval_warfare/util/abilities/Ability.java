package slimeattack07.naval_warfare.util.abilities;

import java.util.ArrayList;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import slimeattack07.naval_warfare.tileentity.BoardTE;

public interface Ability {

	public default void activate(Level level, Player player, BoardTE board, BlockPos ship) {
		activate(level, player, board);
	}
	
	public void activate(Level level, Player player, BoardTE board);
	
	public int getAmount();
	
	public default int energyCost() {
		return 0;
	}
	
	public default PassiveType getPassiveType() {
		return PassiveType.NOT;
	}
	
	public default void detachPassive(Level level, BoardTE board) {};
	
	public String getTranslation();
	
	public default String getPassiveCategory() {
		return getTranslation();
	}
	
	public ArrayList<BoardTE> getTiles(Level level, BoardTE te);
	
	public default Item getAnimationItem() {
		return null;
	};
	
	public MutableComponent hoverableInfo();
	
	public default boolean needsTarget() {
		return true;
	}
	
	public default boolean canBeDisabled() {
		return false;
	}
	
	public default boolean targetDefensive() {
		return false;
	}
}
