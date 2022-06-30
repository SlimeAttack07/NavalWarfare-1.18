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

public class DotShip extends ShipBlock {
	private final boolean loses_hp;
	
	// I have to use 'createTwo' since properties must have at least 2 values...
	public static final ShipPartProperty SHIP_PART = ShipPartProperty.createTwo();

	public DotShip(Ability active_ability, Ability passive_ability, int tier) {
		super(active_ability, passive_ability, tier);
		registerDefaultState(defaultBlockState().setValue(SHIP_PART, ShipPart.ONE));
		loses_hp = false;
	}
	
	public DotShip() {
		super(null, null, 0);
		registerDefaultState(defaultBlockState().setValue(SHIP_PART, ShipPart.ONE));
		loses_hp = true;
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(SHIP_PART);
	}

	@Override
	public boolean canPlace(Level level, BlockPos pos, BlockState state, boolean include_base, boolean display) {
		Direction facing = getFacing(state);
		
		return !include_base || spaceValid(level, pos, facing, 0, 0, display);
	}
	
	@Override
	public boolean summonShip(Level level, BlockPos pos, BlockState state, boolean include_base, boolean display) {
		boolean placeable = canPlace(level, pos, state, include_base, display);
		
		if(placeable) {
			if(include_base)
				level.setBlockAndUpdate(pos, state);
		}
		else if(!include_base)
			level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
	
		return placeable;
	}
	
	@Nullable
	@Override
	public BlockPos getNext(ShipTE te) {		
		return null;
	}

	@Override
	public boolean isBase(BlockState state) {
		return getShipPart(state, SHIP_PART).equals(ShipPart.ONE);
	}
	
	@Override
	public boolean isMiddle(BlockState state) {
		return getShipPart(state, SHIP_PART).equals(ShipPart.ONE);
	}
	
	@Override
	public int getMaxHP() {
		return loses_hp ? 1 : 0;
	}
	
	@Override
	public String getShapeTranslation() {
		return "dot";
	}
	
	@Override
	public boolean losesHP() {
		return loses_hp;
	}
}
