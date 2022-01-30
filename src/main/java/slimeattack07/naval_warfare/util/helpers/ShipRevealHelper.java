package slimeattack07.naval_warfare.util.helpers;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class ShipRevealHelper {
	private final BlockState STATE;
	private final int ID;
	private final Direction FACING;
	
	public ShipRevealHelper(BlockState state, int id, Direction facing) {
		STATE = state;
		ID = id;
		FACING = facing;
	}
	
	public BlockState getState() {
		return STATE;
	}
	
	public int getID() {
		return ID;
	}
	
	public Direction getFacing() {
		return FACING;
	}
}
