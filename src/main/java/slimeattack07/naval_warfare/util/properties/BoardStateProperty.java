package slimeattack07.naval_warfare.util.properties;

import com.google.common.collect.Lists;

import net.minecraft.world.level.block.state.properties.EnumProperty;
import slimeattack07.naval_warfare.util.BoardState;

public class BoardStateProperty extends EnumProperty<BoardState>{

	protected BoardStateProperty() {
		super("board_state", BoardState.class, Lists.newArrayList(BoardState.values()));
	}
	
	public static BoardStateProperty create() {
		return new BoardStateProperty();
	}
}
