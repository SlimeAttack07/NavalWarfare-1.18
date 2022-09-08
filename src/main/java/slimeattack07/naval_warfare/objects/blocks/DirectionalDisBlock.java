package slimeattack07.naval_warfare.objects.blocks;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class DirectionalDisBlock extends DisappearingBlock{
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	
	public DirectionalDisBlock() {
		super();
		registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
	}
	
	public DirectionalDisBlock(Properties prop) {
		super(prop);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(FACING, context.getHorizontalDirection());
	}
	
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

}
