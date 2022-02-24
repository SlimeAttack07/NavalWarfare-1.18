package slimeattack07.naval_warfare.tileentity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import slimeattack07.naval_warfare.NavalWarfare;
import slimeattack07.naval_warfare.init.NWItems;
import slimeattack07.naval_warfare.init.NWTileEntityTypes;
import slimeattack07.naval_warfare.objects.items.BattleLog;
import slimeattack07.naval_warfare.util.NWBasicMethods;
import slimeattack07.naval_warfare.util.helpers.BattleLogHelper;
import slimeattack07.naval_warfare.util.helpers.NBTHelper;
import slimeattack07.naval_warfare.util.helpers.ShipSaveHelper;

public class BattleRecorderTE extends BlockEntity{
	public ArrayList<BattleLogHelper> actions = new ArrayList<>();
	public int own_size = 0;
	public int opp_size = 0;
	public ArrayList<ShipSaveHelper> own_ships = new ArrayList<>();
	public ArrayList<ShipSaveHelper> opp_ships = new ArrayList<>();
	public Direction own_dir = Direction.NORTH;
	public Direction opp_dir = Direction.NORTH;

	public BattleRecorderTE(BlockPos pos, BlockState state) {
		super(NWTileEntityTypes.BATTLE_RECORDER.get(), pos, state);
	}
	
	public ArrayList<BattleLogHelper> getActions(){
		return actions;
	}
	
	public ArrayList<ShipSaveHelper> getOwnShips(){
		return own_ships;
	}
	
	public ArrayList<ShipSaveHelper> getOppShips(){
		return opp_ships;
	}
	
	public void addAction(BattleLogHelper action) {
		actions.add(action);
	}
	
	public void addOwnShip(ShipSaveHelper ship) {
		own_ships.add(ship);
	}
	
	public void addOppShip(ShipSaveHelper ship) {
		opp_ships.add(ship);
	}
	
	public void setOwnShips(ArrayList<ShipSaveHelper> ssh) {
		own_ships = ssh;
	}
	
	public void setOppShips(ArrayList<ShipSaveHelper> ssh) {
		opp_ships = ssh;
	}
	
	public void setOwnSize(int size) {
		own_size = size;
	}
	
	public void setOppSize(int size) {
		opp_size = size;
	}
	
	public void setOwnDir(Direction dir) {
		own_dir = dir;
	}
	
	public void setOppDir(Direction dir) {
		opp_dir = dir;
	}
	
	public void reset() {
		actions.clear();
		own_ships.clear();
		opp_ships.clear();
		own_size = 0;
		opp_size = 0;
		own_dir = Direction.NORTH;
		opp_dir = Direction.NORTH;
	}
	
	public void generateLog(Player player, String p1, String p2) {
		if(!actions.isEmpty()) {
			ItemStack stack = new ItemStack(NWItems.BATTLE_LOG.get());
		    Date now = new Date();
		    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			stack.setHoverName(new TextComponent(p1 + " vs " + p2 + " (" + format.format(now) + ")"));
			BattleLog log = (BattleLog) stack.getItem();
			log.setLog(stack, NBTHelper.toNBT(this));
			NWBasicMethods.addOrSpawn(player, stack, level, worldPosition.above());
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
			own_size = initvalues.getInt("own_size");
			opp_size = initvalues.getInt("opp_size");
			own_dir = Direction.valueOf(initvalues.getString("own_dir"));
			opp_dir = Direction.valueOf(initvalues.getString("opp_dir"));
			
			actions = new ArrayList<>();
			
			if(initvalues.contains("actions")) {
				ListTag list = initvalues.getList("actions", Tag.TAG_COMPOUND);
				
				for(Tag nbt : list) {
					CompoundTag cnbt = (CompoundTag) nbt;
					BattleLogHelper cah = NBTHelper.readBLH(cnbt);
					addAction(cah);
				}
			}
			
			own_ships = new ArrayList<>();
			
			if(initvalues.contains("own_ships")) {
				ListTag list = initvalues.getList("own_ships", Tag.TAG_COMPOUND);
				
				for(Tag nbt : list) {
					CompoundTag cnbt = (CompoundTag) nbt;
					ShipSaveHelper ssh = NBTHelper.readSSH(cnbt);
					addOwnShip(ssh);
				}
			}
			
			opp_ships = new ArrayList<>();
			
			if(initvalues.contains("opp_ships")) {
				ListTag list = initvalues.getList("opp_ships", Tag.TAG_COMPOUND);
				
				for(Tag nbt : list) {
					CompoundTag cnbt = (CompoundTag) nbt;
					ShipSaveHelper ssh = NBTHelper.readSSH(cnbt);
					addOppShip(ssh);
				}
			}
		}
	}
}
