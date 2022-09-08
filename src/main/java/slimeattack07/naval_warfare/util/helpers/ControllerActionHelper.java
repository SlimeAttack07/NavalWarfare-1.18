package slimeattack07.naval_warfare.util.helpers;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import slimeattack07.naval_warfare.util.ControllerAction;
import slimeattack07.naval_warfare.util.FunctionCaller;
import slimeattack07.naval_warfare.util.Spell;
import slimeattack07.naval_warfare.util.TargetType;

public class ControllerActionHelper {
	public BlockPos pos;
	public ControllerAction action;
	public int delay;
	public String player;
	public String opponent;
	public BlockPos board_te;
	public BlockPos matching;
	public int damage;
	public TargetType target_type;
	public int health;
	public Block animation;
	public Item item;
	public boolean multi_ability;
	public String hover;
	public String translation;
	public boolean triggers_passives;
	public Spell spell;
	public FunctionCaller function;
	
	public ControllerActionHelper() {
	}
	
	public static ControllerActionHelper createFunction(FunctionCaller f, BlockPos ship, BlockPos location) {
		ControllerActionHelper cah = new ControllerActionHelper();
		
		cah.delay = 10;
		cah.action = ControllerAction.FUNCTION;
		cah.function = f;
		cah.board_te = ship;
		cah.matching = location;
		
		return cah;
	}
	
	public static ControllerActionHelper createValidate() {
		ControllerActionHelper cah  = new ControllerActionHelper();
		
		cah.action = ControllerAction.VALIDATE;
		cah.delay = 10;
		
		return cah;
	}
	
	public static ControllerActionHelper createEndTurn() {
		ControllerActionHelper cah  = new ControllerActionHelper();
		
		cah.action = ControllerAction.END_TURN;
		cah.delay = 10;
		
		return cah;
	}
	
	public static ControllerActionHelper createEnergyGain(int amount, boolean own) {
		ControllerActionHelper cah  = new ControllerActionHelper();
		
		// Not making a new boolean and int for this thing because its probably going to be the only thing that would use these.
		cah.action = ControllerAction.GAIN_ENERGY;
		cah.delay = 10;
		cah.health = amount;
		cah.multi_ability = own;
		
		return cah;
	}
	
	public static ControllerActionHelper createTargetAction(int delay, BlockPos pos, String player, BlockPos board_te, BlockPos matching, int damage,
			TargetType type, boolean multi_ability, boolean triggers_passives, Block animation) {
		ControllerActionHelper cah  = new ControllerActionHelper();
		
		cah.action = ControllerAction.TARGET;
		cah.delay = delay;
		cah.pos = pos;
		cah.player = player;
		cah.board_te = board_te;
		cah.matching = matching;
		cah.damage = damage;
		cah.target_type = type;
		cah.multi_ability = multi_ability;
		cah.triggers_passives = triggers_passives;
		cah.animation = animation;
		
		return cah;
	}
	
	public static ControllerActionHelper createAbility(BlockPos ship, BlockPos board_te, String player, boolean active) {
		ControllerActionHelper cah  = new ControllerActionHelper();
		
		cah.action = active ? ControllerAction.ACTIVE_ABILITY : ControllerAction.PASSIVE_ABILITY;
		cah.delay = 40;
		cah.pos = ship;
		cah.player = player;
		cah.board_te = board_te;
		
		return cah;
	}
	
	public static ControllerActionHelper createSpell(Spell spell, BlockPos board_te, String player) {
		ControllerActionHelper cah  = new ControllerActionHelper();
		
		cah.action = ControllerAction.SPELL;
		cah.delay = 40;
		cah.spell = spell;
		cah.player = player;
		cah.board_te = board_te;
		
		return cah;
	}
	
	public static ControllerActionHelper createTorpedoTarget(BlockPos pos, String player, BlockPos board_te, BlockPos matching, int damage,
			int health, Block animation) {
		ControllerActionHelper cah  = new ControllerActionHelper();
		
		cah.action = ControllerAction.TORPEDO;
		cah.delay = 10;
		cah.pos = pos;
		cah.player = player;
		cah.board_te = board_te;
		cah.matching = matching;
		cah.damage = damage;
		cah.target_type = TargetType.TORPEDO;
		cah.health = health;
		cah.animation = animation;
		
		return cah;
	}
	
	public static ControllerActionHelper createFragbombTarget(int delay, BlockPos pos, String player, BlockPos board_te, BlockPos matching,
			TargetType type, Block animation, boolean multi_ability) {
		ControllerActionHelper cah  = new ControllerActionHelper();
		
		cah.action = ControllerAction.FRAGBOMB;
		cah.delay = delay;
		cah.pos = pos;
		cah.player = player;
		cah.board_te = board_te;
		cah.matching = matching;
		cah.damage = 1;
		cah.target_type = type;
		cah.animation = animation;
		cah.multi_ability = multi_ability;
		
		return cah;
	}
	
	public static ControllerActionHelper createSpyglassTarget(BlockPos pos, String player, BlockPos board_te, BlockPos matching, int delay, 
			boolean passive) {
		ControllerActionHelper cah  = new ControllerActionHelper();
		
		cah.action = ControllerAction.SPYGLASS;
		cah.delay = delay;
		cah.pos = pos;
		cah.player = player;
		cah.board_te = board_te;
		cah.matching = matching;
		cah.target_type = TargetType.REVEAL;
		cah.multi_ability = passive;
		
		return cah;
	}
	
	public static ControllerActionHelper createFlareTarget(BlockPos pos, String player, BlockPos board_te, BlockPos matching, int delay, 
			boolean passive) {
		ControllerActionHelper cah  = new ControllerActionHelper();
		
		cah.action = ControllerAction.FLARE;
		cah.delay = delay;
		cah.pos = pos;
		cah.player = player;
		cah.board_te = board_te;
		cah.matching = matching;
		cah.target_type = TargetType.REVEAL;
		cah.multi_ability = passive;
		
		return cah;
	}
	
	public static ControllerActionHelper createAnnounce(String player, String opponent, Item item, String translation, String hover) {
		ControllerActionHelper cah  = new ControllerActionHelper();
		
		cah.action = ControllerAction.ANNOUNCE;
		cah.delay = 10;
		cah.player = player;
		cah.opponent = opponent;
		cah.item = item;
		cah.translation = translation;
		cah.hover = hover;
		
		return cah;
	}
	
	public static ControllerActionHelper createMultiTarget(int delay, BlockPos pos, String player, BlockPos board_te, BlockPos matching, 
			int damage, TargetType type, boolean triggers_passives, boolean passive) {
		ControllerActionHelper cah  = new ControllerActionHelper();
		
		cah.action = ControllerAction.MULTI_TARGET;
		cah.delay = delay;
		cah.pos = pos;
		cah.player = player;
		cah.board_te = board_te;
		cah.matching = matching;
		cah.damage = damage;
		cah.target_type = type;
		cah.triggers_passives = triggers_passives;
		cah.multi_ability = passive;
		
		return cah;
	}
	
	public static ControllerActionHelper createBomberTarget(int delay, BlockPos pos, String player, BlockPos board_te, BlockPos matching,
			Block animation) {
		ControllerActionHelper cah  = new ControllerActionHelper();
		
		cah.action = ControllerAction.BOMBER;
		cah.delay = delay;
		cah.pos = pos;
		cah.player = player;
		cah.board_te = board_te;
		cah.matching = matching;
		cah.damage = 1;
		cah.target_type = TargetType.AIRCRAFT;
		cah.animation = animation;
		
		return cah;
	}
	
	public static ControllerActionHelper createNapalmAction(BlockPos pos, String player, BlockPos board_te, BlockPos matching) {
		ControllerActionHelper cah  = new ControllerActionHelper();
		
		cah.action = ControllerAction.NAPALM;
		cah.delay = 20;
		cah.pos = pos;
		cah.player = player;
		cah.board_te = board_te;
		cah.matching = matching;
		cah.damage = 1;
		cah.target_type = TargetType.NORMAL;
		
		return cah;
	}
	
	public static ControllerActionHelper createForcedTarget(BlockPos pos, String player, BlockPos board_te, BlockPos matching) {
		ControllerActionHelper cah  = new ControllerActionHelper();
		
		cah.action = ControllerAction.TURN_DAMAGE;
		cah.delay = 20;
		cah.pos = pos;
		cah.player = player;
		cah.board_te = board_te;
		cah.matching = matching;
		cah.damage = 1;
		cah.target_type = TargetType.UNBLOCKABLE;
		cah.triggers_passives = false;
		
		return cah;
	}
	
	public static ControllerActionHelper createRaft(BlockPos pos, String player, BlockPos board_te, BlockPos matching, int health) {
		ControllerActionHelper cah  = new ControllerActionHelper();
		
		cah.action = ControllerAction.RAFT;
		cah.delay = 10;
		cah.pos = pos;
		cah.player = player;
		cah.board_te = board_te;
		cah.matching = matching;
		cah.health = health;
		
		return cah;
	}
	
	@Override
	public String toString() {
		CompoundTag nbt = NBTHelper.toNBT(this);
		return nbt.getAsString();
	}
}
