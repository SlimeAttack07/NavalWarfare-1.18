package slimeattack07.naval_warfare.util.helpers;

import java.util.ArrayList;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import slimeattack07.naval_warfare.NavalWarfare;
import slimeattack07.naval_warfare.tileentity.BattleRecorderTE;
import slimeattack07.naval_warfare.tileentity.BattleViewerTE;
import slimeattack07.naval_warfare.tileentity.BoardTE;
import slimeattack07.naval_warfare.tileentity.DisappearingTE;
import slimeattack07.naval_warfare.tileentity.EnergyShieldTE;
import slimeattack07.naval_warfare.tileentity.GameControllerTE;
import slimeattack07.naval_warfare.tileentity.PassiveAbilityTE;
import slimeattack07.naval_warfare.tileentity.RandomShipTE;
import slimeattack07.naval_warfare.tileentity.ShipTE;
import slimeattack07.naval_warfare.util.BattleLogAction;
import slimeattack07.naval_warfare.util.BoardState;
import slimeattack07.naval_warfare.util.ControllerAction;
import slimeattack07.naval_warfare.util.FunctionCaller;
import slimeattack07.naval_warfare.util.HitResult;
import slimeattack07.naval_warfare.util.ShipState;
import slimeattack07.naval_warfare.util.Spell;
import slimeattack07.naval_warfare.util.TargetType;

public class NBTHelper {

	public static CompoundTag toNBT(Object o) {
		if (o instanceof ItemStack) {
			return writeItemStack((ItemStack) o);
		}
		
		if(o instanceof GameControllerTE)
			return writeGameController((GameControllerTE) o);
		
		if(o instanceof BoardTE)
			return writeBoard((BoardTE) o);
		
		if(o instanceof ShipTE)
			return writeShip((ShipTE) o);
		
		if(o instanceof EnergyShieldTE)
			return writeShield((EnergyShieldTE) o);
		
		if(o instanceof PassiveAbilityTE)
			return writePassive((PassiveAbilityTE) o);
		
		if(o instanceof DisappearingTE)
			return writeDisappear((DisappearingTE) o);
		
		if(o instanceof ControllerActionHelper)
			return writeCAH((ControllerActionHelper) o);
		
		if(o instanceof RandomShipTE)
			return writeRandomShip((RandomShipTE) o);
		
		if(o instanceof BattleRecorderTE)
			return writeBattleRecorder((BattleRecorderTE) o);
		
		if(o instanceof BattleLogHelper)
			return writeBLH((BattleLogHelper) o);
		
		if(o instanceof BattleViewerTE)
			return writeBattleViewer((BattleViewerTE) o);
		
		return null;
	}
	
	private static CompoundTag writeGameController(GameControllerTE o) {
		CompoundTag compound = new CompoundTag();
		
		compound = safePutString("owner", o.owner, compound);
		compound.put("zero", writeBlockPos(o.zero));
		compound = safePutBoolean("playing_game", o.playing_game, compound);
		compound = safePutBoolean("clear_board", o.clear_board, compound);
		compound.put("opponent", writeBlockPos(o.opponent));
		compound.put("opponent_zero", writeBlockPos(o.opponent_zero));
		compound = safePutBoolean("has_turn", o.has_turn, compound);
		compound = safePutInt("turn_time", o.turn_time, compound);
		compound = safePutInt("hp", o.hp, compound);
		compound = safePutInt("board_size", o.board_size, compound);
		compound = safePutString("name", o.config_name, compound);
		compound = safePutInt("action_time", o.action_time, compound);
		compound = safePutInt("action_number", o.action_number, compound);
		compound = safePutInt("energy", o.energy, compound);
		compound = safePutBoolean("do_turn_actions", o.do_turn_actions, compound);
		compound = safePutInt("turn_action_amount", o.turn_action_amount, compound);
		compound = safePutBoolean("has_spell", o.has_spell, compound);
		compound = safePutInt("cons_timeout_times", o.cons_timeout_times, compound);
		compound = safePutInt("timeout_times", o.timeout_times, compound);
		compound = safePutInt("reg_buffer_time", o.reg_buffer_time, compound);
		compound = safePutBoolean("hit_once", o.hit_once, compound);
		compound = safePutInt("streak", o.streak, compound);
		
		ArrayList<ControllerActionHelper> actions = o.getActions();
		
		if(actions != null) {
			ListTag list = new ListTag();
			
			for(ControllerActionHelper cah : actions)
				list.add(writeCAH(cah));

			compound.put("actions", list);
		}
		
		actions = o.getTurnActions();
		
		if(actions != null) {
			ListTag list = new ListTag();
			
			for(ControllerActionHelper cah : actions)
				list.add(writeCAH(cah));

			compound.put("turn_actions", list);
		}
		
		ArrayList<TargetResultHelper> results = o.getResults();
		
		if(results != null) {
			ListTag list = new ListTag();
			
			for(TargetResultHelper trh : results)
				list.add(writeTRH(trh));

			compound.put("results", list);
		}
		
		ArrayList<String> registered = o.getRegistered();
		
		if(registered != null) {
			ListTag list = new ListTag();
			
			for(String s : registered) {
				CompoundTag cnbt = new CompoundTag();
				cnbt = safePutString("ship", s, cnbt);
				list.add(cnbt);
			}
			compound.put("registered", list);
		}
		
		ArrayList<String> register_buffer = o.getRegisterBuffer();
		
		if(register_buffer != null) {
			ListTag list = new ListTag();
			
			for(String s : register_buffer) {
				CompoundTag cnbt = new CompoundTag();
				cnbt = safePutString("ship", s, cnbt);
				list.add(cnbt);
			}
			compound.put("register_buffer", list);
		}

		return compound;
	}
	
	private static CompoundTag writeBoard(BoardTE o) {
		CompoundTag compound = new CompoundTag();
		
		compound.put("controller", writeBlockPos(o.controller));
		compound = safePutInt("board_id", o.board_id, compound);
		
		return compound;
	}
	
	private static CompoundTag writeShip(ShipTE o) {
		CompoundTag compound = new CompoundTag();
		
		compound = safePutInt("active_amount", o.getActiveAmount(), compound);
		compound = safePutInt("passive_amount", o.getPassiveAmount(), compound);
		compound = safePutInt("action_number", o.getActionNumber(), compound);
		compound.put("next", writeBlockPos(o.getNext()));
		
		return compound;
	}
	
	private static CompoundTag writeShield(EnergyShieldTE o) {
		CompoundTag compound = writePassive((PassiveAbilityTE) o);
		
		compound = safePutInt("hp", o.getHP(), compound);
		compound = safePutInt("last_action", o.getLastAction(), compound);
		
		return compound;
	}
	
	private static CompoundTag writePassive(PassiveAbilityTE o) {
		CompoundTag compound = new CompoundTag();
		ListTag list = new ListTag();
		
		for(String owner : o.getOwners()) {
			CompoundTag nbt = new CompoundTag();
			nbt = safePutString("owner", owner, nbt);
			list.add(nbt);
		}
		
		compound.put("owners", list);
		
		if(o.hasMatching())
			compound.put("matching", writeBlockPos(o.getMatching()));
		
		return compound;
	}
	
	private static CompoundTag writeDisappear(DisappearingTE o) {
		CompoundTag compound = new CompoundTag();
		
		compound = safePutInt("time", o.time, compound);
		
		return compound;
	}
	
	private static CompoundTag writeRandomShip(RandomShipTE o) {
		CompoundTag compound = new CompoundTag();
		
		compound = safePutBoolean("can_spawn", o.can_spawn, compound);
		
		return compound;
	}
	
	private static CompoundTag writeItemStack(ItemStack stack) {
		CompoundTag compound = new CompoundTag();
		
		if(stack == ItemStack.EMPTY) {
			compound.putString("iamempty", "");
			compound.putByte("type", (byte) 0);
			
			return compound;
		}
		
		compound = safePutInt("count", stack.getCount(), compound);
		compound = safePutString("item", stack.getItem().getRegistryName().toString(), "minecraft:air", compound);
		compound.putByte("type", (byte) 0);
		
		if(stack.hasTag())
			compound.put("nbt", stack.getTag());
		
		return compound;
	}
	
	public static CompoundTag writeBlockPos(BlockPos pos) {
		CompoundTag compound = new CompoundTag();
		
		if(pos == null) {
			compound.putString("no_pos", "");
			
			return compound;
		}
		
		compound = safePutInt("x", pos.getX(), compound);
		compound = safePutInt("y", pos.getY(), compound);
		compound = safePutInt("z", pos.getZ(), compound);
		
		return compound;
	}
	
	private static CompoundTag writeTRH(TargetResultHelper helper) {
		CompoundTag compound = new CompoundTag();
		
		compound = safePutInt("id", helper.id, compound);
		compound = safePutString("result", helper.result.toString().toUpperCase(), compound);
		
		return compound;
	}
	
	private static CompoundTag writeCAH(ControllerActionHelper helper) {
		CompoundTag compound = new CompoundTag();
		
		if(helper == null || helper.action == null) {
			NavalWarfare.LOGGER.warn("Encountered corrupt CAH, skipping. CAH or CAH.action == null");
			return compound;
		}
		
		if(helper.board_te != null)
			compound.put("board_te", writeBlockPos(helper.board_te));
		
		if(helper.matching != null) 
			compound.put("matching", writeBlockPos(helper.matching));
		
		if(helper.pos != null)
			compound.put("pos", writeBlockPos(helper.pos));
		
		compound = helper.delay > 0 ? safePutInt("delay", helper.delay, compound) : compound;
		compound = safePutString("action", helper.action.name().toUpperCase(), compound);
		compound = helper.damage > 0 ? safePutInt("damage", helper.damage, compound) : compound;
		compound = helper.player == null ? compound : safePutString("player", helper.player.toString(), compound);
		compound = helper.opponent == null ? compound : safePutString("player", helper.opponent.toString(), compound);
		compound = helper.target_type == null ? compound : safePutString("target_type", helper.target_type.toString(), compound);
		compound = helper.health > 0 || helper.health == -1 ? safePutInt("health", helper.health, compound) : compound;
		compound = helper.animation == null? compound : safePutString("animation", helper.animation.getRegistryName().toString(), compound);
		compound = helper.multi_ability ? safePutBoolean("multi_ability", true, compound) : compound;
		compound = helper.item == null ? compound : safePutString("item", helper.item.getRegistryName().toString(), compound);
		compound = helper.hover == null ? compound : safePutString("hover", helper.hover, compound);
		compound = helper.translation == null ? compound : safePutString("translation", helper.translation, compound);
		compound = helper.triggers_passives ? safePutBoolean("triggers_passives", true, compound) : compound;
		compound = helper.spell == null ? compound : safePutString("spell", helper.spell.toString(), compound);
		compound = helper.function == null ? compound : safePutString("function", helper.function.name().toUpperCase(), compound);
		
		return compound;
	}
	
	private static CompoundTag writeBattleRecorder(BattleRecorderTE o) {
		CompoundTag compound = new CompoundTag();
		
		compound = safePutInt("own_size", o.own_size, compound);
		compound = safePutInt("opp_size", o.opp_size, compound);
		compound = safePutString("own_dir", o.own_dir.name(), compound);
		compound = safePutString("opp_dir", o.opp_dir.name(), compound);
		
		ArrayList<BattleLogHelper> actions = o.getActions();
		
		if(actions != null) {
			ListTag list = new ListTag();
			
			for(BattleLogHelper blh : actions)
				list.add(writeBLH(blh));

			compound.put("actions", list);
		}
		
		ArrayList<ShipSaveHelper> ships = o.getOwnShips();
		
		if(ships != null) {
			ListTag list = new ListTag();
			
			for(ShipSaveHelper ssh : ships)
				list.add(writeSSH(ssh));

			compound.put("own_ships", list);
		}
		
		ships = o.getOppShips();
		
		if(ships != null) {
			ListTag list = new ListTag();
			
			for(ShipSaveHelper ssh : ships)
				list.add(writeSSH(ssh));

			compound.put("opp_ships", list);
		}
		
		return compound;
	}
	
	private static CompoundTag writeBattleViewer(BattleViewerTE o) {
		CompoundTag compound = new CompoundTag();
		
		compound = safePutBoolean("playing", o.playing, compound);
		compound = safePutInt("timer", o.timer, compound);
		compound.put("zero", writeBlockPos(o.zero));
		compound.put("opponent_zero", writeBlockPos(o.opponent_zero));
		compound = safePutFloat("speed", o.speed, compound);
		compound = safePutString("uuid", o.uuid.toString(), compound);
		
		ArrayList<BattleLogHelper> actions = o.getActions();
		
		if(actions != null) {
			ListTag list = new ListTag();
			
			for(BattleLogHelper blh : actions)
				list.add(writeBLH(blh));

			compound.put("actions", list);
		}
		
		return compound;
	}
	
	private static CompoundTag writeBLH(BattleLogHelper helper) {
		CompoundTag compound = new CompoundTag();
		
		if(helper == null || helper.action == null) {
			NavalWarfare.LOGGER.warn("Encountered corrupt BLH, skipping. BLH = " + helper);
			return compound;
		}
		
		compound = safePutString("action", helper.action.name(), compound);
		// No need to save id 0 since compounds default to it when trying to fetch an int with a non-existing key.
		compound = helper.id > 0 ? safePutInt("id", helper.id, compound) : compound; 
		compound = safePutBoolean("opponent", helper.opponent, compound);
		compound = helper.board_state == null ? compound : safePutString("board_state", helper.board_state.name(), compound);
		compound = helper.resource == null ? compound : safePutString("resource", helper.resource.toString(), compound);
		compound = helper.delay > 0 ? safePutInt("delay", helper.delay, compound) : compound;
		compound = helper.ship_state == null ? compound : safePutString("ship_state", helper.ship_state.name(), compound);
		compound = helper.sound == null ? compound : safePutString("sound", helper.sound.getRegistryName().toString(), compound);
		compound = helper.volume > 0 ? safePutFloat("volume", helper.volume, compound) : compound;
		compound = helper.pitch > 0 ? safePutFloat("pitch", helper.pitch, compound) : compound;
		compound = helper.dir == null ? compound : safePutString("dir", helper.dir.getName(), compound);
		compound = helper.offset > 0 ? safePutInt("offset", helper.offset, compound) : compound;
		compound = helper.message == null ? compound : safePutString("message", helper.message, compound);
		
		if(helper.positions != null && !helper.positions.isEmpty()) {
			ListTag list = new ListTag();
			
			for(int i : helper.positions)
				list.add(IntTag.valueOf(i));
			
			compound.put("positions", list);
		}
		
		return compound;
	}
	
	private static CompoundTag writeSSH(ShipSaveHelper helper) {
		CompoundTag compound = new CompoundTag();
		
		compound = safePutString("name", helper.getShip().toString(), compound);
		compound = safePutInt("pos", helper.getPos(), compound);
		compound = safePutString("dir", helper.getDir().name(), compound);
		// Note that we don't save the HP, since we only use this for the battle log system right now, which doesn't need the hp.

		return compound;
	}
	
	@Nullable
	public static ShipSaveHelper readSSH(CompoundTag compound) {
		try {
			return new ShipSaveHelper(compound.getString("name"), compound.getInt("pos"), compound.getString("dir"), 0);
		} catch(NullPointerException e) {
			NavalWarfare.LOGGER.warn("Received a null compound when reading SSH, skipping");
			return null;
		}
	}
	
	@Nullable
	public static BlockPos readBlockPos(CompoundTag compound) {
		if(compound == null || compound.contains("no_pos") || compound.isEmpty())
			return null;
		
		return new BlockPos(compound.getInt("x"), compound.getInt("y"), compound.getInt("z"));
	}
	
	@Nullable
	public static ControllerActionHelper readCAH(CompoundTag compound) {
		if(!compound.contains("action")) {
			NavalWarfare.LOGGER.warn("Tried reading corrupt action with no type!");
			return null;
		}
		try {
			ControllerActionHelper cah = new ControllerActionHelper();
			
			cah.action = ControllerAction.valueOf(compound.getString("action"));
			cah.pos = readBlockPos(compound.getCompound("pos"));
			cah.board_te = readBlockPos(compound.getCompound("board_te"));
			cah.player = compound.contains("player") ? compound.getString("player") : null;
			cah.spell = compound.contains("spell") ? Spell.valueOf(compound.getString("spell")) : null;
			cah.matching = readBlockPos(compound.getCompound("matching"));
			cah.damage = compound.getInt("damage");
			cah.delay = compound.getInt("delay");
			cah.target_type = compound.contains("target_type") ? TargetType.valueOf(compound.getString("target_type")) : null;
			cah.multi_ability = compound.getBoolean("multi_ability");
			cah.triggers_passives = compound.getBoolean("triggers_passives");
			cah.health = compound.getInt("health");
			cah.animation = compound.contains("animation") ? ForgeRegistries.BLOCKS.getValue(new ResourceLocation(compound.getString("animation"))) : 
				null;
			cah.opponent = compound.contains("opponent") ? compound.getString("opponent") : null;
			cah.item = compound.contains("item") ? ForgeRegistries.ITEMS.getValue(new ResourceLocation(compound.getString("item"))) : null;
			cah.translation = compound.contains("translation") ? compound.getString("translation") : null;
			cah.hover = compound.contains("hover") ? compound.getString("hover") : null;
			cah.function = compound.contains("function") ? FunctionCaller.valueOf(compound.getString("function")) : null;
			
			return cah;
		} catch(Exception e) {
			NavalWarfare.LOGGER.warn("Tried reading corrupt action type: " + compound.getString("action").toUpperCase());
			return new ControllerActionHelper();
		}
	}
	
	public static TargetResultHelper readTRH(CompoundTag compound) {
		int id = compound.getInt("id");
		HitResult result = HitResult.valueOf(compound.getString("result"));
		
		return new TargetResultHelper(id, result);
	}
	
	public static BattleLogHelper readBLH(CompoundTag compound) {
		try {
			BattleLogHelper blh = new BattleLogHelper();
			
			blh.action = BattleLogAction.valueOf(compound.getString("action")); // No check here since we want it to fail if the action is invalid
			blh.id = compound.getInt("id");
			blh.opponent = compound.getBoolean("opponent");
			blh.board_state = compound.contains("board_state") ? BoardState.valueOf(compound.getString("board_state")) : null;
			blh.delay = compound.getInt("delay");
			blh.resource = compound.contains("resource") ? new ResourceLocation(compound.getString("resource")) : null;
			blh.ship_state = compound.contains("ship_state") ? ShipState.valueOf(compound.getString("ship_state")) : null;
			blh.sound = compound.contains("sound") ? ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(compound.getString("sound"))) : null;
			blh.volume = compound.getFloat("volume");
			blh.pitch = compound.getFloat("pitch");
			blh.offset = compound.getInt("offset");
			blh.dir = compound.contains("dir") ? Direction.byName(compound.getString("dir")) : null;
			blh.message = compound.contains("message") ? compound.getString("message") : null;
			
			if(compound.contains("positions")) {
				ListTag list = compound.getList("positions", Tag.TAG_INT);
				blh.positions = new ArrayList<>();
				
				for(Tag tag : list) {
					IntTag itag = (IntTag) tag;
					blh.positions.add(itag.getAsInt());
				}
			}
			
			return blh;
			
		} catch(Exception e) {
			NavalWarfare.LOGGER.warn("Tried reading corrupt BLH: " + compound);
			return new BattleLogHelper();
		}
	}
	
	private static CompoundTag safePutBoolean(String name, boolean value, boolean default_val, CompoundTag compound) {
		try {
			compound.putBoolean(name, value);
		} catch (NullPointerException e) {
			compound.putBoolean(name, default_val);
		}
		
		return compound;
	}
	
	private static CompoundTag safePutBoolean(String name, boolean value, CompoundTag compound) {
		return safePutBoolean(name, value, false, compound);
	}
	
	private static CompoundTag safePutInt(String name, int value, int default_val, CompoundTag compound) {
		try {
			compound.putInt(name, value);
		} catch (NullPointerException e) {
			compound.putInt(name, default_val);
		}
		
		return compound;
	}
	
	private static CompoundTag safePutInt(String name, int value, CompoundTag compound) {
		return safePutInt(name, value, 0, compound);
	}	
	
	private static CompoundTag safePutFloat(String name, float value, float default_val, CompoundTag compound) {
		try {
			compound.putFloat(name, value);
		} catch (NullPointerException e) {
			compound.putFloat(name, default_val);
		}
		
		return compound;
	}
	
	private static CompoundTag safePutFloat(String name, float value, CompoundTag compound) {
		return safePutFloat(name, value, 0f, compound);
	}	
	
	private static CompoundTag safePutString(String name, String value, String default_val, CompoundTag compound) {
		try {
			compound.putString(name, value);
		} catch (NullPointerException e) {
			compound.putString(name, default_val);
		}
		
		return compound;
	}
	
	private static CompoundTag safePutString(String name, String value, CompoundTag compound) {
		return safePutString(name, value, "", compound);
	}
}