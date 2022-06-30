package slimeattack07.naval_warfare.tileentity;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import slimeattack07.naval_warfare.NavalWarfare;
import slimeattack07.naval_warfare.init.NWTileEntityTypes;
import slimeattack07.naval_warfare.objects.blocks.Board;
import slimeattack07.naval_warfare.util.helpers.NBTHelper;

public class ShipTE extends BlockEntity{
	private int active_amount = 0;
	private int passive_amount = 0;
	private int action_number = -1;
	private BlockPos next = null;

	public ShipTE(BlockPos pos, BlockState state) {
		super(NWTileEntityTypes.SHIP.get(), pos, state);
	}
	
	public void boardBelow() {
		if(!(level.getBlockState(worldPosition.below()).getBlock() instanceof Board)){
			active_amount = 0;
			passive_amount = 0;
		}
	}
	
	public int getActiveAmount() {
		return active_amount;
	}
	
	public void setActiveAmount(int new_active) {
		active_amount = new_active;
	}
	
	public int getPassiveAmount() {
		return passive_amount;
	}
	
	public void setPassiveAmount(int new_passive) {
		passive_amount = new_passive;
	}
	
	public int getActionNumber() {
		return action_number;
	}
	
	public void setActionNumber(int number) {
		action_number = number;
	}
	
	public void setNext(BlockPos n) {
		next = n;
	}
	
	public boolean hasNext() {
		return next != null;
	}
	
	@Nullable
	public BlockPos getNext() {
		return next;
	}
	
	@Override
	public void saveAdditional(CompoundTag compound) {
		compound.put(NavalWarfare.MOD_ID, NBTHelper.toNBT(this));
	}
	
	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		CompoundTag initvalues = compound.getCompound(NavalWarfare.MOD_ID);
		
		if(initvalues != null) {
			active_amount = initvalues.getInt("active_amount");
			passive_amount = initvalues.getInt("passive_amount");
			action_number = initvalues.getInt("action_number");
			next = NBTHelper.readBlockPos(initvalues.getCompound("next"));
		}
	}
}
