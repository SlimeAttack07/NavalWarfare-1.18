package slimeattack07.naval_warfare.objects.blocks.variators;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import slimeattack07.naval_warfare.objects.blocks.DotShip;
import slimeattack07.naval_warfare.util.Variator;
import slimeattack07.naval_warfare.util.abilities.Ability;
import slimeattack07.naval_warfare.util.properties.VariatorProperty;

public class VariatorTwelveShip extends DotShip{
	public static final VariatorProperty VARIATOR = VariatorProperty.createTwelve();
	
	public VariatorTwelveShip(Ability active, Ability passive, int tier) {
		super(active, passive, tier);

		registerDefaultState(defaultBlockState().setValue(VARIATOR, Variator.ONE));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		
		builder.add(VARIATOR);
	}

	@Override
	public boolean summonShip(Level level, BlockPos pos, BlockState state, boolean include_base, boolean display) {
		boolean placeable = canPlace(level, pos, state, include_base, display);
		
		if(placeable) {	
			BlockState s = state.setValue(VARIATOR, VARIATOR.random());
			
			level.setBlockAndUpdate(pos, s);
		}
		else if(!include_base)
			level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
	
		return placeable;
	}
}
