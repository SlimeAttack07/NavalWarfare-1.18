package slimeattack07.naval_warfare.tileentity;

import java.util.ArrayList;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;
import slimeattack07.naval_warfare.NavalWarfare;
import slimeattack07.naval_warfare.init.NWTileEntityTypes;
import slimeattack07.naval_warfare.network.NavalNetwork;
import slimeattack07.naval_warfare.network.message.BattleLogMessage;
import slimeattack07.naval_warfare.util.helpers.BattleLogHelper;
import slimeattack07.naval_warfare.util.helpers.NBTHelper;
import slimeattack07.naval_warfare.util.helpers.ShipSaveHelper;

public class BattleRecorderTE extends BlockEntity{
	public ArrayList<BattleLogHelper> actions = new ArrayList<>();
	public int own_size = 0;
	public int opp_size = 0;
	public ArrayList<ShipSaveHelper> own_ships = new ArrayList<>();
	public ArrayList<ShipSaveHelper> opp_ships = new ArrayList<>();

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
	
	public void reset() {
		actions.clear();
		own_ships.clear();
		opp_ships.clear();
		own_size = 0;
		opp_size = 0;
	}
	
	// TODO: Just for testing, this function will move to BattleLog item once that's been implemented.
	public void copyToClipboard(Player player) {
		if(player instanceof ServerPlayer && !actions.isEmpty()) {
			NavalNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new BattleLogMessage(
					NBTHelper.toNBT(this)));
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