package slimeattack07.naval_warfare.util.helpers;

import java.util.ArrayList;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import slimeattack07.naval_warfare.util.BattleLogAction;
import slimeattack07.naval_warfare.util.BoardState;
import slimeattack07.naval_warfare.util.ShipState;

public class BattleLogHelper {
	public BattleLogAction action;
	public int id;
	public boolean opponent;
	public BoardState board_state;
	public int delay;
	public ResourceLocation resource;
	public ShipState ship_state;
	public ArrayList<Integer> positions;
	public SoundEvent sound;
	public float pitch;
	public float volume;
	public Direction dir;
	public int offset;
	public String message;
	
	public BattleLogHelper() {
	}
	
	public BattleLogHelper copy() {
		BattleLogHelper blh = new BattleLogHelper();
		
		blh.action = action;
		blh.id = id;
		blh.opponent = opponent;
		blh.board_state = board_state;
		blh.delay = delay;
		blh.resource = resource;
		blh.ship_state = ship_state;
		blh.positions = positions;
		blh.sound = sound;
		blh.pitch = pitch;
		blh.volume = volume;
		blh.dir = dir;
		blh.offset = offset;
		blh.message = message;
		
		return blh;
	}
	
	public static BattleLogHelper createSetBlock(int id, ResourceLocation block, int offset, boolean opponent) {
		BattleLogHelper blh = new BattleLogHelper();
		
		blh.action = BattleLogAction.SET_BLOCK;
		blh.id = id;
		blh.offset = offset;
		blh.opponent = opponent;
		blh.resource = block;
		
		return blh;
	}
	
	public static BattleLogHelper createSetBlocks(ArrayList<Integer> positions, ResourceLocation block, int offset, boolean opponent) {
		BattleLogHelper blh = new BattleLogHelper();
		
		blh.action = BattleLogAction.SET_BLOCKS;
		blh.positions = positions;
		blh.offset = offset;
		blh.opponent = opponent;
		blh.resource = block;
		
		return blh;
	}
	
	public static BattleLogHelper createSetDisBlock(int id, ResourceLocation block, int offset, boolean opponent, Direction dir, int time) {
		BattleLogHelper blh = new BattleLogHelper();
		
		blh.action = BattleLogAction.SET_DIS_BLOCK;
		blh.id = id;
		blh.offset = offset;
		blh.opponent = opponent;
		blh.dir = dir;
		blh.pitch = time; // Repurposing pitch for now, may rename rework this system later on.
		blh.resource = block;
		
		return blh;
	}
	
	public static BattleLogHelper createDeployable(int id, ResourceLocation ship, Direction dir) {
		BattleLogHelper blh = new BattleLogHelper();
		
		blh.action = BattleLogAction.SUMMON_DEPLOYABLE;
		blh.id = id;
		blh.dir = dir;
		blh.opponent = false;
		blh.resource = ship;
		
		return blh;
	}
	
	public static BattleLogHelper createDelay(int delay) {
		BattleLogHelper blh = new BattleLogHelper();
		
		blh.action = BattleLogAction.DELAY;
		blh.delay = delay;
		
		return blh;
	}
	
	public static BattleLogHelper createDropBlock(int id, boolean opponent, ResourceLocation animation) {
		BattleLogHelper blh = new BattleLogHelper();
		
		blh.action = BattleLogAction.DROP_BLOCK;
		blh.resource = animation;
		blh.id = id;
		blh.opponent = opponent;
		
		return blh;
	}
	
	public static BattleLogHelper createDropBlocks(ArrayList<Integer> positions, boolean opponent, ResourceLocation animation) {
		BattleLogHelper blh = new BattleLogHelper();
		
		blh.action = BattleLogAction.DROP_BLOCKS;
		blh.resource = animation;
		blh.positions = positions;
		blh.opponent = opponent;
		
		return blh;
	}
	
	public static BattleLogHelper createBoardState(int id, boolean opponent, BoardState state) {
		BattleLogHelper blh = new BattleLogHelper();
		
		blh.action = BattleLogAction.BOARDSTATE;
		blh.id = id;
		blh.opponent = opponent;
		blh.board_state = state;
		
		return blh;
	}
	
	public static BattleLogHelper createShipState(int id, boolean opponent, ShipState state) {
		BattleLogHelper blh = new BattleLogHelper();
		
		blh.action = BattleLogAction.SHIPSTATE;
		blh.id = id;
		blh.opponent = opponent;
		blh.ship_state = state;
		
		return blh;
	}
	
	public static BattleLogHelper createSound(int id, boolean opponent, SoundEvent sound, float volume, float pitch) {
		BattleLogHelper blh = new BattleLogHelper();
		
		blh.action = BattleLogAction.PLAY_SOUND;
		blh.id = id;
		blh.opponent = opponent;
		blh.sound = sound;
		blh.pitch = pitch;
		blh.volume = volume;
		
		return blh;
	}
	
	public static BattleLogHelper createSounds(ArrayList<Integer> positions, boolean opponent, SoundEvent sound, float volume, float pitch) {
		BattleLogHelper blh = new BattleLogHelper();
		
		blh.action = BattleLogAction.PLAY_SOUNDS;
		blh.sound = sound;
		blh.positions = positions;
		blh.opponent = opponent;
		blh.pitch = pitch;
		blh.volume = volume;
		
		return blh;
	}
	
	public static BattleLogHelper createMessage(String message) {
		BattleLogHelper blh = new BattleLogHelper();
		
		blh.action = BattleLogAction.MESSAGE;
		blh.message = message;
		
		return blh;
	}
	
	@Override
	public String toString() {
		CompoundTag nbt = NBTHelper.toNBT(this);
		return nbt.getAsString();
	}
}
