package slimeattack07.naval_warfare.util.helpers;

import java.util.ArrayList;

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
	public ResourceLocation animation;
	public ShipState ship_state;
	public ArrayList<Integer> positions;
	public SoundEvent sound;
	public float pitch;
	public float volume;
	
	public BattleLogHelper() {
	}
	
	public BattleLogHelper copy() {
		BattleLogHelper blh = new BattleLogHelper();
		
		blh.action = action;
		blh.id = id;
		blh.opponent = opponent;
		blh.board_state = board_state;
		blh.delay = delay;
		blh.animation = animation;
		blh.ship_state = ship_state;
		blh.positions = positions;
		blh.sound = sound;
		blh.pitch = pitch;
		blh.volume = volume;
		
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
		blh.animation = animation;
		blh.id = id;
		blh.opponent = opponent;
		
		return blh;
	}
	
	public static BattleLogHelper createDropBlocks(ArrayList<Integer> positions, boolean opponent, ResourceLocation animation) {
		BattleLogHelper blh = new BattleLogHelper();
		
		blh.action = BattleLogAction.DROP_BLOCKS;
		blh.animation = animation;
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
	
	@Override
	public String toString() {
		CompoundTag nbt = NBTHelper.toNBT(this);
		return nbt.getAsString();
	}
}
