package slimeattack07.naval_warfare.objects.blocks.variators;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import slimeattack07.naval_warfare.objects.blocks.DirectionalDisBlock;
import slimeattack07.naval_warfare.util.Variator;
import slimeattack07.naval_warfare.util.properties.VariatorProperty;

public class VariatorFiveDDB extends DirectionalDisBlock{
	public static final VariatorProperty VARIATOR = VariatorProperty.createFive();
	
	// Because Minecraft is an idiot that only accepts properties if they're public static final, it's impossible to use a single class for different variators.
	public VariatorFiveDDB() {
		super();
		registerDefaultState(defaultBlockState().setValue(VARIATOR, Variator.ONE));
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(FACING, context.getHorizontalDirection()).setValue(VARIATOR, VARIATOR.random());
	}
	
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING);
		builder.add(VARIATOR);
	}

}
