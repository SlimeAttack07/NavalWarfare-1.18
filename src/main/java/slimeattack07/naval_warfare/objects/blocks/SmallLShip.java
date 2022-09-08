package slimeattack07.naval_warfare.objects.blocks;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import slimeattack07.naval_warfare.tileentity.ShipTE;
import slimeattack07.naval_warfare.util.ShipPart;
import slimeattack07.naval_warfare.util.abilities.Ability;
import slimeattack07.naval_warfare.util.properties.ShipPartProperty;

public class SmallLShip extends ShipBlock {
	
	public static final ShipPartProperty SHIP_PART = ShipPartProperty.createThree();

	public SmallLShip(Ability active, Ability passive, int tier) {
		super(active, passive, tier);
		registerDefaultState(defaultBlockState().setValue(SHIP_PART, ShipPart.TWO));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(SHIP_PART);
	}

	@Override
	public boolean canPlace(Level level, BlockPos pos, BlockState state, boolean include_base, boolean display) {
		Direction facing = getFacing(state);
		
		boolean base_valid = !include_base || spaceValid(level, pos, facing, 0, 0, display);
		
		return base_valid && spaceValid(level, pos, facing, 0, 1, display) && spaceValid(level, pos, facing, -1, 0, display);
	}
		
	@Override
	public boolean summonShip(Level level, BlockPos pos, BlockState state, boolean include_base, boolean display) {
		boolean placeable = canPlace(level, pos, state, include_base, display);
		
		if(placeable) {
			Direction dir = getFacing(state);
			
			if(include_base)
				level.setBlockAndUpdate(pos, state);
			
			level.setBlockAndUpdate(offset(dir, pos, 0, 1), state.setValue(SHIP_PART, ShipPart.ONE));
			level.setBlockAndUpdate(offset(dir, pos, -1, 0), state.setValue(SHIP_PART, ShipPart.THREE));
		}
		else if(!include_base)
			level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
		
		return placeable;
	}
	
	@Nullable
	@Override
	public BlockPos getNext(ShipTE te) {
		BlockState state = te.getBlockState();
		ShipBlock ship = (ShipBlock) state.getBlock();
		BlockPos pos = te.getBlockPos();
		Direction dir = getFacing(state);
		
		switch(ship.getShipPart(state, SHIP_PART)) {
		case ONE:
			return offset(dir, pos, 0, -1);
		case TWO:
			return offset(dir, pos, -1, 0);
		case THREE:
			return offset(dir, pos, 1, 1) ;
		default:
			break;	
		}
		
		return null;
	}

	@Override
	public boolean isBase(BlockState state) {
		return getShipPart(state, SHIP_PART).equals(ShipPart.TWO);
	}
	
	@Override
	public boolean isMiddle(BlockState state) {
		return getShipPart(state, SHIP_PART).equals(ShipPart.TWO);
	}
	
	@Override
	public int getMaxHP() {
		return 3;
	}
	
	@Override
	public String getShapeTranslation() {
		return "small_l";
	}
}
