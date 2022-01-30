package slimeattack07.naval_warfare.tileentity;

import java.util.ArrayList;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import slimeattack07.naval_warfare.NavalWarfare;
import slimeattack07.naval_warfare.init.NWTileEntityTypes;
import slimeattack07.naval_warfare.util.NWBasicMethods;
import slimeattack07.naval_warfare.util.helpers.NBTHelper;

public class PassiveAbilityTE extends BlockEntity{
	protected ArrayList<String> owners;
	private BlockPos matching;

	public PassiveAbilityTE(BlockPos pos, BlockState state) {
		super(NWTileEntityTypes.PASSIVE_BLOCK.get(), pos, state);
	}
	
	public PassiveAbilityTE(BlockPos pos, BlockState state, boolean shield) {
		super(NWTileEntityTypes.ENERGY_SHIELD.get(), pos, state);
	}
	
	protected void init() {
		owners = new ArrayList<>();
		matching = null;
	}
	
	public BlockPos getMatching() {
		return matching;
	}
	
	public void setMatching(BlockPos match) {
		matching = match;
	}
	
	public boolean hasMatching() {
		return matching != null;
	}
	
	public String getOwner() {
		return owners.isEmpty() ? "null" : owners.get(0);
	}
	
	public ArrayList<String> ownersToString(){
		ArrayList<String> result = new ArrayList<>();
		
		for(String s : getOwners())
			result.add(NWBasicMethods.getTranslation("block." + NavalWarfare.MOD_ID +"." + s));
		
		return result;
	}
	
	public ArrayList<String> getOwners(){
		if(owners == null)
			init();
		
		return owners;
	}
	
	public void addOwner(String owner) {
		if(owners == null)
			init();
		
		if(!owners.contains(owner))
			owners.add(owner);
	}
	
	public void removeOwner(String owner) {
		if(owners == null)
			init();
		else
			owners.remove(owner);
	}
	
	public int ownerAmount() {
		if(owners == null)
			init();
		
		return owners.size();
	}
	
	public boolean hasOwner() {
		return owners != null && !owners.isEmpty();
	}
	
	public void destroy(Level level, BlockPos pos, String owned) {
		if(owners == null)
			init();
		
		else if(owners.contains(owned))
			destroyPropagate(level, pos, owned, new ArrayList<>());
	}
	
	public ArrayList<BlockPos> destroyPropagate(Level level, BlockPos pos, String owned, ArrayList<BlockPos> known) {
		if(known.contains(pos))
			return known;
		
		known.add(pos);
		BlockEntity tile = level.getBlockEntity(pos);
		
		if(tile instanceof PassiveAbilityTE) {
			PassiveAbilityTE te = (PassiveAbilityTE) tile;
			te.removeOwner(owned);
			
			if(!te.hasOwner()) {
				if(te.hasMatching()) {
					level.setBlockAndUpdate(te.getMatching(), Blocks.AIR.defaultBlockState());
				}
				level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			}
		}
		else
			return known;
		
		ArrayList<BlockPos> new_known = destroyPropagate(level, pos.north(), owned, known);
		new_known = destroyPropagate(level, pos.east(), owned, new_known);
		new_known = destroyPropagate(level, pos.south(), owned, new_known);
		new_known = destroyPropagate(level, pos.west(), owned, new_known);
		
		return new_known;
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
			
			ListTag ListTag = initvalues.getList("owners", Tag.TAG_COMPOUND);
			ArrayList<String> new_owners = new ArrayList<>();
			
			for(Tag nbt : ListTag) {
				CompoundTag cnbt = (CompoundTag) nbt;
				new_owners.add(cnbt.getString("owner"));
			}
			
			owners = new_owners;
			
			if(initvalues.contains("matching")) {
				CompoundTag matchnbt = initvalues.getCompound("matching");
				matching = new BlockPos(matchnbt.getInt("x"), matchnbt.getInt("y"), matchnbt.getInt("z"));	
			}
			else
				matching = null;
		}
		else
			init();
	}
}
