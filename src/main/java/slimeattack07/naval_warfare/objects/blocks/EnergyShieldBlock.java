package slimeattack07.naval_warfare.objects.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import slimeattack07.naval_warfare.init.NWTileEntityTypes;
import slimeattack07.naval_warfare.tileentity.EnergyShieldTE;

public class EnergyShieldBlock extends PassiveAbilityBlock{

	public EnergyShieldBlock(Properties properties) {
		super(properties);
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return NWTileEntityTypes.ENERGY_SHIELD.get().create(pos, state);
	}
	
	/** Deal damage to an Energy Shield
	 * 
	 * @param level The level
	 * @param pos The position of the Energy Shield
	 * @param action The action number. Used to stop attacks like Salvo's or Mortar's from instantly destroying shields.
	 * @param amount The amount of damage to deal.
	 * @return Whether or not the shield survived the hit.
	 */
	public static boolean hit(Level level, BlockPos pos, int action, int amount) {
		BlockEntity tile = level.getBlockEntity(pos);
		
		if(tile instanceof EnergyShieldTE) {
			EnergyShieldTE te = (EnergyShieldTE) tile;
			
			if(te.mayDamage(action)) {
				te.decreaseHP(amount);
				te.setLastAction(action);
			}
			String owner = te.getOwner();
			
			if(!te.alive()) {
				destroy(level, pos, owner, 4);
				return false;
			}
			else
				te.propagateHP(level, te.getOwner());	
		}
		
		return true;
	}	
}
