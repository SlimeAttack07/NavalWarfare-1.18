package slimeattack07.naval_warfare.objects.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements.Type;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import slimeattack07.naval_warfare.init.NWTileEntityTypes;
import slimeattack07.naval_warfare.tileentity.PassiveAbilityTE;

public class PassiveAbilityBlock extends Block implements EntityBlock{
	public PassiveAbilityBlock(Properties properties) {
		super(properties);
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return NWTileEntityTypes.PASSIVE_BLOCK.get().create(pos, state);
	}
	
	@Override
	public boolean isValidSpawn(BlockState state, BlockGetter world, BlockPos pos, Type type,
			EntityType<?> entityType) {
		return false;
	}
	
	@Override
	public boolean isPossibleToRespawnInThis() {
		return true;
	}
	
	@Override
	public boolean propagatesSkylightDown(BlockState p_49928_, BlockGetter p_49929_, BlockPos p_49930_) {
		return true;
	}
	
	@Override
	public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_,
			CollisionContext p_60558_) {
		return Block.box(0.1D, 0, 0.1D, 15.9D, 8, 15.9D);
	}
	
	public static void destroy(Level level, BlockPos pos, String owner) {
		BlockEntity tile = level.getBlockEntity(pos);
		
		if(tile instanceof PassiveAbilityTE) {
			PassiveAbilityTE te = (PassiveAbilityTE) tile;
			te.destroy(level, pos, owner);
		}
	}
	
	public static void setMatching(Level level, BlockPos pos, BlockPos matching) {
		BlockEntity tile = level.getBlockEntity(pos);
		
		if(tile instanceof PassiveAbilityTE) {
			PassiveAbilityTE te = (PassiveAbilityTE) tile;
			te.setMatching(matching);
		}
	}
	
	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if(level.isClientSide())
			return;
		
		if(!(newState.getBlock() instanceof PassiveAbilityBlock) && state.hasBlockEntity())
			level.removeBlockEntity(pos);
	}
}
