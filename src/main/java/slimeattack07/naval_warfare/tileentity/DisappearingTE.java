package slimeattack07.naval_warfare.tileentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import slimeattack07.naval_warfare.NavalWarfare;
import slimeattack07.naval_warfare.init.NWTileEntityTypes;
import slimeattack07.naval_warfare.util.helpers.NBTHelper;

public class DisappearingTE extends BlockEntity{
	public int time = 5;

	public DisappearingTE(BlockPos pos, BlockState state) {
		super(NWTileEntityTypes.DISAPPEAR.get(), pos, state);
	}
	
	public void setTime(int ticks) {
		time = ticks;
	}

	public void tick() {
		if(level.isClientSide())
			return;
		
		if(time <= 0)
			level.removeBlock(worldPosition, false);
		else {
			time--;
			setChanged();
		}
	}
	
	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		
		CompoundTag initvalues = compound.getCompound(NavalWarfare.MOD_ID);
		
		if(initvalues != null)
			time = initvalues.getInt("time");
	}
	
	@Override
	public void saveAdditional(CompoundTag compound) {
		compound.put(NavalWarfare.MOD_ID, NBTHelper.toNBT(this));
	}
}
