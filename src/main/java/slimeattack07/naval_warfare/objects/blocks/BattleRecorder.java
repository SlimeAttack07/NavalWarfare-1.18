package slimeattack07.naval_warfare.objects.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import slimeattack07.naval_warfare.init.NWTileEntityTypes;

public class BattleRecorder extends Block implements EntityBlock{

	public BattleRecorder() {
		super(Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(0.8f, 2));
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return NWTileEntityTypes.BATTLE_RECORDER.get().create(pos, state);
	}
}
