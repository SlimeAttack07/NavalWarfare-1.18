package slimeattack07.naval_warfare.tileentity;

import java.util.ArrayList;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import slimeattack07.naval_warfare.NavalWarfare;
import slimeattack07.naval_warfare.init.NWTileEntityTypes;
import slimeattack07.naval_warfare.objects.blocks.BattleViewer;
import slimeattack07.naval_warfare.objects.blocks.Board;
import slimeattack07.naval_warfare.objects.blocks.ShipBlock;
import slimeattack07.naval_warfare.util.NWBasicMethods;
import slimeattack07.naval_warfare.util.helpers.BattleLogHelper;
import slimeattack07.naval_warfare.util.helpers.NBTHelper;

public class BattleViewerTE extends BlockEntity{
	public ArrayList<BattleLogHelper> actions = new ArrayList<>();
	public boolean playing = false; // TODO: Add way to pause / cancel viewing
	public int timer = -1;
	public BlockPos zero = null;
	public BlockPos opponent_zero = null;

	public BattleViewerTE(BlockPos pos, BlockState state) {
		super(NWTileEntityTypes.BATTLE_VIEWER.get(), pos, state);
	}
	
	public ArrayList<BattleLogHelper> getActions(){
		return actions;
	}
	
	public void addAction(BattleLogHelper action) {
		actions.add(action);
	}
	
	public void setActions(ListTag new_actions) {
		actions.clear();
		
		for(Tag tag : new_actions) {
			CompoundTag ctag = (CompoundTag) tag;
			BattleLogHelper blh = NBTHelper.readBLH(ctag);
			actions.add(blh);
		}
	}
	
	public void setPlaying(boolean p) {
		playing = p;
	}
	
	public boolean isPlaying() {
		return playing;
	}
	
	public void reset() {
		actions.clear();
		playing = false;
		zero = null;
		opponent_zero = null;
		timer = 0;
	}
	
	public void setZero(BlockPos new_zero) {
		zero = new_zero;
	}
	
	public void setOpponentZero(BlockPos new_opponent_zero) {
		opponent_zero = new_opponent_zero;
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
			playing = initvalues.getBoolean("playing");
			timer = initvalues.getInt("timer");
			zero = NBTHelper.readBlockPos(initvalues.getCompound("zero"));
			opponent_zero = NBTHelper.readBlockPos(initvalues.getCompound("opponent_zero"));
			
			actions = new ArrayList<>();
			
			if(initvalues.contains("actions")) {
				ListTag list = initvalues.getList("actions", Tag.TAG_COMPOUND);
				
				for(Tag nbt : list) {
					CompoundTag cnbt = (CompoundTag) nbt;
					BattleLogHelper cah = NBTHelper.readBLH(cnbt);
					addAction(cah);
				}
			}
		}
	}
	
	private boolean hasActions() {
		return !actions.isEmpty();
	}
	
	public BlockPos findBoard(int id, boolean opponent) {
		if(zero == null || opponent_zero == null)
			return null;
		
		BlockEntity ztile = opponent ? level.getBlockEntity(opponent_zero) : level.getBlockEntity(zero);
		
		if(ztile instanceof BoardTE) {
			BoardTE zte = (BoardTE) ztile;
			return zte.locateId(zte.getBlockPos(), id, getFacing());
		}
		
		return null;
	}
	
	private Direction getFacing() {
		BattleViewer viewer = (BattleViewer) getBlockState().getBlock();
		
		return viewer.getFacing(getBlockState());
	}
	
	private void removeFirstAction() {
		actions.remove(0);
	}
	
	private void tryAction() {
		BattleLogHelper blh = actions.get(0);
		
		if(blh.action == null) {
			NavalWarfare.LOGGER.warn("BattleViewer BlockEntity at " + worldPosition.toShortString() + " got corrupt action. Skipping action: " + blh);
			removeFirstAction();
			return;
		}
		
		switch(blh.action) {
		case DELAY:
			removeFirstAction();
			break;
		case BOARDSTATE:
			doBoardStateAction(blh);
			break;
		case DROP_BLOCK:
			doDropAction(blh);
			break;
		case DROP_BLOCKS:
			doDropsAction(blh);
			break;
		case SHIPSTATE:
			doShipStateAction(blh);
			break;
		case PLAY_SOUND:
			doSoundAction(blh);
			break;
		case PLAY_SOUNDS:
			doSoundsAction(blh);
			break;
		default:
			NavalWarfare.LOGGER.warn("I don't know how to execute this action! Please report this to the mod author! Action is: " + blh);
			removeFirstAction();
			break;
		
		}
	}
	
	private boolean shouldDoAction() {
		return !actions.isEmpty() && timer >= actions.get(0).delay;
	}
	
	private void doBoardStateAction(BattleLogHelper blh) {
		BlockPos pos = findBoard(blh.id, blh.opponent);
		
		if(pos != null && blh.board_state != null) {
			BlockState state = level.getBlockState(pos);
			
			if(state.getBlock() instanceof Board)
				level.setBlockAndUpdate(pos, state.setValue(Board.STATE, blh.board_state));
		}
		
		removeFirstAction();
	}
	
	private void doShipStateAction(BattleLogHelper blh) {
		BlockPos pos = findBoard(blh.id, blh.opponent);
		
		if(pos != null && blh.ship_state != null) {
			BlockState state = level.getBlockState(pos.above());
			
			if(state.getBlock() instanceof ShipBlock)
				level.setBlockAndUpdate(pos.above(), state.setValue(ShipBlock.SHIP_STATE, blh.ship_state));
		}
		
		removeFirstAction();
	}
	
	private void doDropAction(BattleLogHelper blh) {		
		if(blh.animation != null) {
			Block block = ForgeRegistries.BLOCKS.getValue(blh.animation);
			
			if(block != null) {
				BlockPos pos = findBoard(blh.id, blh.opponent);
				
				if(pos != null)
					NWBasicMethods.dropBlock(level, pos, block);
			}	
		}
		
		removeFirstAction();
	}
	
	private void doDropsAction(BattleLogHelper blh) {		
		if(blh.animation != null && blh.positions != null) {
			Block block = ForgeRegistries.BLOCKS.getValue(blh.animation);
			
			if(block != null) {
				for(int i : blh.positions) {
					BlockPos pos = findBoard(i, blh.opponent);
					
					if(pos != null) {
						NWBasicMethods.dropBlock(level, pos, block);
						NavalWarfare.LOGGER.info("Dropping on id " + i + " located at " + pos.toShortString());
					}
				}
			}	
		}
		
		removeFirstAction();
	}
	
	private void doSoundAction(BattleLogHelper blh) {		
		if(blh.sound != null) {
			BlockPos pos = findBoard(blh.id, blh.opponent);
			
			if(pos != null)
				level.playSound(null, pos, blh.sound, SoundSource.MASTER, blh.volume, blh.pitch);
		}
		
		removeFirstAction();
	}
	
	private void doSoundsAction(BattleLogHelper blh) {		
		if(blh.sound != null && blh.positions != null) {
			for(int i : blh.positions) {
				BlockPos pos = findBoard(i, blh.opponent);
				
				if(pos != null)
					level.playSound(null, pos, blh.sound, SoundSource.MASTER, blh.volume, blh.pitch);
			}
		}
		
		removeFirstAction();
	}
	
	public void tick() {
		if(level.isClientSide())
			return;
		
		if(hasActions()) {
			if(shouldDoAction()) {
				tryAction();
				timer = 0;
			}
			else
				timer++;
			setChanged();
		}
	}
}
