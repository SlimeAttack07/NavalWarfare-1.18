package slimeattack07.naval_warfare.objects.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class BoardFiller extends Block{

	public BoardFiller() {
		super(Properties.of(Material.STONE).strength(1000).color(MaterialColor.TERRACOTTA_BLUE));
	}
	
	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if(level.isClientSide())
			return;
		
		if(newState.getBlock() instanceof BoardFiller)
			return;
		
		Direction[] dirs = new Direction[] {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
		
		for(Direction dir : dirs) {
			BlockPos board_pos = pos.relative(dir);
			BlockState board = level.getBlockState(board_pos);
			
			if(board.getBlock() instanceof Board || board.getBlock() instanceof BoardFiller)
				level.removeBlock(board_pos, isMoving);
		}
	}
}
