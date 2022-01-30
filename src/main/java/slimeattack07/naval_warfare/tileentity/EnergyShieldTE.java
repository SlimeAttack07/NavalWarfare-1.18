package slimeattack07.naval_warfare.tileentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import slimeattack07.naval_warfare.NavalWarfare;
import slimeattack07.naval_warfare.util.helpers.NBTHelper;

public class EnergyShieldTE extends PassiveAbilityTE{
	private int hp;
	private int last_action = -1;

	public EnergyShieldTE(BlockPos pos, BlockState state) {
		super(pos, state, true);
	}
	
	@Override
	protected void init() {
		hp = 0;
		last_action = -1;
		super.init();
	}
	
	public int getHP() {
		return hp;
	}
	
	public void initHP(int health) {
		if(health > hp)
			hp = health;
	}
	
	public void setHP(int health){
		hp = health;
	}
	
	public void decreaseHP(int amount) {
		hp -= amount;
	}
	
	public boolean alive() {
		return hp > 0;
	}
	
	public int getLastAction() {
		return last_action;
	}
	
	public void setLastAction(int action) {
		last_action = action;
	}
	
	public boolean mayDamage(int action) {
		return action > last_action;
	}
	
	public void propagateHP(Level level, String owner) {
		hp++;
		propagateHPHelper(level, hp - 1, last_action, owner, 
				new Direction[] {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST});
	}
	
	public void propagateHPHelper(Level level, int health, int action, String owner, Direction[] dirs) {
		if(hp == health || !owners.contains(owner))
			return;
		
		hp = health;
		last_action = action;
		
		for(Direction dir : dirs) {
			BlockEntity tile = level.getBlockEntity(worldPosition.relative(dir));
			
			if(tile instanceof EnergyShieldTE) {
				EnergyShieldTE te = (EnergyShieldTE) tile;
				te.propagateHPHelper(level, health, action, owner, dirs);
			}
		}
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
			hp = initvalues.getInt("hp");
			last_action = initvalues.getInt("last_action");
		}
		else
			init();
	}
}
