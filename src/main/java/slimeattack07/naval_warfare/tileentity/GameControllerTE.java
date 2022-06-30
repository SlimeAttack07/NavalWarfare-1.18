package slimeattack07.naval_warfare.tileentity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import slimeattack07.naval_warfare.NavalWarfare;
import slimeattack07.naval_warfare.config.NavalWarfareConfig;
import slimeattack07.naval_warfare.init.NWBlocks;
import slimeattack07.naval_warfare.init.NWItems;
import slimeattack07.naval_warfare.init.NWSounds;
import slimeattack07.naval_warfare.init.NWStats;
import slimeattack07.naval_warfare.init.NWTileEntityTypes;
import slimeattack07.naval_warfare.init.NWTriggers;
import slimeattack07.naval_warfare.objects.blocks.Board;
import slimeattack07.naval_warfare.objects.blocks.DirectionalDisBlock;
import slimeattack07.naval_warfare.objects.blocks.GameController;
import slimeattack07.naval_warfare.objects.blocks.ShipBlock;
import slimeattack07.naval_warfare.objects.blocks.ShipMarkerBlock;
import slimeattack07.naval_warfare.objects.items.ShipConfiguration;
import slimeattack07.naval_warfare.objects.items.SpellWand;
import slimeattack07.naval_warfare.util.BattleLogAction;
import slimeattack07.naval_warfare.util.BoardState;
import slimeattack07.naval_warfare.util.ControllerAction;
import slimeattack07.naval_warfare.util.ControllerState;
import slimeattack07.naval_warfare.util.HitResult;
import slimeattack07.naval_warfare.util.NWBasicMethods;
import slimeattack07.naval_warfare.util.ShipState;
import slimeattack07.naval_warfare.util.TargetType;
import slimeattack07.naval_warfare.util.abilities.Ability;
import slimeattack07.naval_warfare.util.abilities.Napalm;
import slimeattack07.naval_warfare.util.abilities.Seaworthy;
import slimeattack07.naval_warfare.util.helpers.BattleLogHelper;
import slimeattack07.naval_warfare.util.helpers.ControllerActionHelper;
import slimeattack07.naval_warfare.util.helpers.NBTHelper;
import slimeattack07.naval_warfare.util.helpers.ShipSaveHelper;
import slimeattack07.naval_warfare.util.helpers.TargetResultHelper;

public class GameControllerTE extends BlockEntity{
	public static int ENERGY_GAIN_RATE = NavalWarfareConfig.energy_gain_rate.get(); // Default: 5
	public static int BASE_ENERGY = NavalWarfareConfig.base_energy.get(); // Default: 7
	public static int MAX_ENERGY = NavalWarfareConfig.max_energy.get(); // Default: 16
	public static final int MAX_TURN_TIME = NavalWarfareConfig.max_turn_time.get(); // Default: 600
	public static final int CLEAR_BOARD_TIME = NavalWarfareConfig.clear_board_time.get(); // Default: 100
	
	public String owner = null;
	public BlockPos zero = null;
	public boolean playing_game = false;
	public boolean clear_board = false;
	public BlockPos opponent = null;
	public BlockPos opponent_zero = null;
	public boolean has_turn = false;
	public int turn_time = 0;
	public int board_size = 0;
	public int hp = 0;
	public String config_name = null;
	public int action_time = 0;
	public ArrayList<ControllerActionHelper> actions = new ArrayList<>();
	public ArrayList<ControllerActionHelper> turn_actions = new ArrayList<>();
	public int action_number = 0;
	public ArrayList<TargetResultHelper> results = new ArrayList<>();
	public ArrayList<String> registered = new ArrayList<>();
	public ArrayList<String> register_buffer = new ArrayList<>();
	public int energy = -1;
	public boolean do_turn_actions = false;
	public int turn_action_amount = 0;
	public boolean has_spell = true;
	public int timeout_times = 0;
	public int cons_timeout_times = 0;
	public int reg_buffer_time = 0;
	public boolean hit_once = false;
	public int streak = 0;

	public GameControllerTE(BlockPos pos, BlockState state) {
		super(NWTileEntityTypes.GAME_CONTROLLER.get(), pos, state);
	}

	public boolean hasOwner() {
		return owner != null && !owner.isEmpty();
	}
	
	public String getOwner() {
		if(hasOwner())
			return owner;
		
		return "";
	}
	
	public BlockPos getZero() {
		return zero;
	}
	
	public void setZero(BlockPos new_zero) {
		zero = new_zero;
	}
	
	public BlockPos getOpponentZero() {
		return opponent_zero;
	}
	
	public void setOpponentZero(BlockPos new_zero) {
		opponent_zero = new_zero;
	}
	
	public void setOwner(UUID uuid) {
		owner = uuid.toString();
	}
	
	public void setOwner(String uuid) {
		owner = uuid;
	}
	
	public void setGame(boolean game) {
		playing_game = game;
		has_spell = game;
	}
	
	public void setSpell(boolean spell) {
		has_spell = spell;
	}
	
	public void setOpponent(BlockPos opp) {
		opponent = opp;
	}
	
	public void setHP(int health) {
		hp = health;
	}
	
	public void setBoardSize(int size) {
		board_size = size;
	}
	
	public void setClearBoard(boolean remove) {
		clear_board = remove;
	}
	
	public void resetBoardValues() {
		clearRegistered();
		setBoardSize(0);
		setHP(0);
		setName(NWBasicMethods.getTranslation("item.naval_warfare.ship_configuration"));
	}
	
	public void resetActions() {
		actions.clear();
		turn_actions.clear();
	}
	
	public void resetTurnTime() {
		turn_time  = 0;
	}
	
	public void setEnergy(int amount) {
		energy = amount;
	}
	
	public void setActionNumber(int number) {
		action_number = number;
	}
	
	public void increaseStreak(int amount) {
		streak += amount;
	}
	
	public void consumeEnergy(int amount) {
		energy = Math.max(0, energy - amount);
		String message = ChatFormatting.YELLOW + NWBasicMethods.getTranslation("ability.naval_warfare.energy") + ": " + energy + " (-" + 
				(amount) + ")";
		NWBasicMethods.messagePlayerCustomRecord(this, getPlayer(), message, false);
	}
	
	public void gainEnergy(int amount) {
		energy = Math.min(MAX_ENERGY, energy + amount);
		String message = ChatFormatting.YELLOW + NWBasicMethods.getTranslation("ability.naval_warfare.energy") + ": " + energy + " (+" + 
				(amount) + ")";
		NWBasicMethods.messagePlayerCustomRecord(this, getPlayer(), message, false);
	}
	
	public int getHP() {
		return hp;
	}
	
	public int getBoardSize() {
		return board_size;
	}
	
	public BlockPos getOpponent() {
		return opponent;
	}
	
	public void setTurn(boolean turn, boolean gain_energy) {
		has_turn = turn;
		do_turn_actions = turn;
		turn_time = 0;
		
		if(!turn) {
			updatePlayerStreakStat();
			streak = 0;
		}
		
		if(gain_energy) {
			int old = energy;
			energy = Math.min(energy + ENERGY_GAIN_RATE, MAX_ENERGY);
			String message = ChatFormatting.YELLOW + NWBasicMethods.getTranslation("ability.naval_warfare.energy") + ": " + energy + " (+" +
					(energy - old) + ")";
			NWBasicMethods.messagePlayerCustomRecord(this, getPlayer(), message, false);
		}
	}
	
	private void updatePlayerStreakStat() {
		Player player = getPlayer();
		
		if(player != null && player instanceof ServerPlayer) {
			ServerPlayer sp = (ServerPlayer) player;
			
			try {
				Stat<ResourceLocation> stat = Stats.CUSTOM.get(new ResourceLocation(NavalWarfare.MOD_ID, "streak"), StatFormatter.DEFAULT);
				int old_streak = sp.getStats().getValue(stat);
				
				if(streak > old_streak) {
					sp.getStats().setValue(sp, stat, streak);
					old_streak = streak;
				}
				
				if(old_streak >= 4)
					NWTriggers.STREAK_4.trigger(sp);
				if(old_streak >= 8)
					NWTriggers.STREAK_8.trigger(sp);
				if(old_streak >= 12)
					NWTriggers.STREAK_12.trigger(sp);

			} catch(NullPointerException e) {
				NavalWarfare.LOGGER.error("Naval Warfare: Failed to fetch the streak statistic.");
			}
		}
	}
	
	public boolean hasSpell() {
		return has_spell;
	}
	
	public boolean hasTurn() {
		return has_turn && playing_game;
	}
	
	public void reduceHP() {
		hp--;
	}
	
	public void addHP(int amount) {
		hp += amount;
	}
	
	public boolean isAlive() {
		return hp > 0;
	}
	
	public boolean hasGame() {
		return playing_game;
	}
	
	public String getName() {
		return config_name;
	}
	
	public void setName(String name) {
		config_name = name;
	}
	
	public int getEnergy() {
		return energy;
	}
	
	public ArrayList<ControllerActionHelper> getActions(){
		return actions;
	}
	
	public ArrayList<ControllerActionHelper> getTurnActions(){
		return turn_actions;
	}
	
	public ArrayList<TargetResultHelper> getResults(){
		return results;
	}
	
	public ArrayList<String> getRegistered(){
		return registered;
	}
	
	public ArrayList<String> getRegisterBuffer(){
		return register_buffer;
	}
	
	public void addTurnAction(ControllerActionHelper action) {
		turn_actions.add(action);
		setChanged();
	}
	
	public void addAction(ControllerActionHelper action) {		
		actions.add(action);
		setChanged();
	}
	
	public void addResult(TargetResultHelper result) {	
		results.add(result);
		setChanged();
	}
	
	public boolean register(String ship, Player player, ShipBlock block) {		
		if(registered.contains(ship))
			return false;
		
		if(block != null) {
			if(hp + block.getMaxHP() > ShipConfiguration.MAX_HEALTH_ALLOWED)
				return false;
			
			if(block.hasPassiveAbility() && block.PASSIVE_ABILITY instanceof Seaworthy) {
				board_size += block.PASSIVE_ABILITY.getAmount();
				GameController controller = (GameController) getBlockState().getBlock();
				controller.removeBoardAndShips(level, this, false);
				controller.spawnBoard(level, player, this, worldPosition, board_size, false);
			}
			
			registered.add(ship);
			hp += block.getMaxHP();
			
			setChanged();
		}

		return true;
	}
	
	public void register(String ship) {
		if(!registered.contains(ship)) {
			registered.add(ship);
			setChanged();
		}
	}
	
	public boolean deregister(String ship, Player player, ShipBlock block) {
		if(!registered.contains(ship))
			return false;
		
		if(block != null) {
			if(block.hasPassiveAbility() && block.PASSIVE_ABILITY instanceof Seaworthy) {
				GameController controller = (GameController) getBlockState().getBlock();
				controller.removeBoardAndShips(level, this);
				board_size = GameController.DEFAULT_BOARD_SIZE;
				controller.spawnBoard(level, player, this, worldPosition, board_size, false);
				clearRegistered();
				hp = 0;
				NWBasicMethods.messagePlayer(player, "message.naval_warfare.deregistered_seaworthy");
			}
			else {
				registered.remove(ship);
				hp -= block.getMaxHP();
			}
		}

		setChanged();
		
		return true;
	}
	
	public void deregister(Player player, String ship, int ship_hp, boolean seaworthy) {
		if(seaworthy) {
			GameController controller = (GameController) getBlockState().getBlock();
			controller.removeBoardAndShips(level, this);
			board_size = GameController.DEFAULT_BOARD_SIZE;
			controller.spawnBoard(level, player, this, worldPosition, board_size, false);
			clearRegistered();
			hp = 0;
			NWBasicMethods.messagePlayer(player, "message.naval_warfare.deregistered_seaworthy");
		}
		else {
			if(registered.contains(ship)) {
				registered.remove(ship);
				setChanged();
			}
			
			hp -= ship_hp;
		}
	}
	
	public boolean isRegistered(String ship) {
		return registered.contains(ship);
	}
	
	public void clearRegistered() {
		registered.clear();
		setChanged();
	}
	
	public void clearRegisterBuffer() {
		register_buffer.clear();
		setChanged();
	}
	
	public void addRegisterToBuffer(String ship) {
		if(!register_buffer.contains(ship)) {
			register_buffer.add(ship);
			setChanged();
		}
	}
	
	public boolean hasRegInBuffer() {
		return !register_buffer.isEmpty();
	}
	
	public boolean hasBoard() {
		return board_size > 0;
	}
	
	private boolean hasTurnWork() {
		return (do_turn_actions || turn_action_amount > 0) && !turn_actions.isEmpty();
	}
	
	private boolean shouldDoTurnWork() {
		return turn_actions.get(0).delay <= action_time;
	}
	
	private boolean hasWork() {
		return !actions.isEmpty();
	}
	
	private boolean shouldDoWork() {
		return actions.get(0).delay <= action_time;
	}
	
	private void removeFirstAction() {
		if(!actions.isEmpty())
			actions.remove(0);
	}
	
	private void removeFirstTurnAction() {
		if(!turn_actions.isEmpty())
			turn_actions.remove(0);
	}
	
	private void setAnimationTime(BlockPos pos, int time) {
		BlockEntity tile = level.getBlockEntity(pos);
		
		if(tile instanceof DisappearingTE) {
			DisappearingTE te = (DisappearingTE) tile;
			te.setTime(time);
		}
	}
	
	private void doActionTarget(ControllerActionHelper cah) {
		BlockEntity tile = level.getBlockEntity(cah.board_te);
		
		if(tile instanceof BoardTE) {
			BoardTE te = (BoardTE) tile;
			Board board = (Board) te.getBlockState().getBlock();
			
			Player player = null;
			
			if(!cah.player.equals("dummy"))
				player = level.getPlayerByUUID(UUID.fromString(cah.player));
			
			if(cah.animation != null) {
				BattleLogHelper blh_drop = BattleLogHelper.createDropBlock(te.getId(), !cah.multi_ability, cah.animation.getRegistryName());
				recordOnRecorders(blh_drop);
			}
			
			recordOnRecorders(BattleLogHelper.createDelay(cah.delay));

			HitResult result = board.targetTileAction(level, player, te.getBlockState(), te.getBlockPos(), te, cah.matching, cah.damage, cah.target_type, 
					cah.multi_ability, action_number, null, cah.triggers_passives, false);
			
			if(result.equals(HitResult.MISS))
				recordOnRecorders(BattleLogHelper.createBoardState(te.getId(), !cah.multi_ability, BoardState.EMPTY));
		}
		
		removeFirstAction();
	}
	
	private void doActionAbility(ControllerActionHelper cah, boolean active) {
		BlockEntity tile = level.getBlockEntity(cah.board_te);
		Block block = level.getBlockState(cah.pos).getBlock();
		
		if(tile instanceof BoardTE && block instanceof ShipBlock) {
			BoardTE te = (BoardTE) tile;
			ShipBlock ship = (ShipBlock) block;
			
			Player player = null;
			
			if(!cah.player.equals("dummy"))
				player = level.getPlayerByUUID(UUID.fromString(cah.player));

			if(active)
				ship.ACTIVE_ABILITY.activate(level, player, te);
			else
				ship.PASSIVE_ABILITY.activate(level, player, te);

			action_number++;
		}
		
		removeFirstAction();
	}
	
	private void doActionSpell(ControllerActionHelper cah) {
		BlockEntity tile = level.getBlockEntity(cah.board_te);
		
		if(tile instanceof BoardTE) {
			BoardTE te = (BoardTE) tile;
			
			Player player = null;
			
			if(!cah.player.equals("dummy"))
				player = level.getPlayerByUUID(UUID.fromString(cah.player));

			if(cah.spell != null) {
				Ability ability = SpellWand.spellToAbility(cah.spell);
				
				if(ability != null) {
					ability.activate(level, player, te);
					setSpell(false);
				}
			}
	
			action_number++;
		}
		
		removeFirstAction();
	}
	
	private void doActionAnnounce(ControllerActionHelper cah, boolean turn) {
		MutableComponent component = Component.Serializer.fromJson(cah.hover);
		
		if(component == null)
			component = new TextComponent("");
		
		if(cah.player != null && !cah.player.equals("dummy")) {
			Player player = level.getPlayerByUUID(UUID.fromString(cah.player));
			NWBasicMethods.messagePlayerAbilityUsed(this, player, "ability.naval_warfare.triggered", player.getName().getString(), 
					component);
			NWBasicMethods.messagePlayerTitle(player, level, "ability.naval_warfare.triggered", "dark_green", 
					cah.translation, "green");
			NWBasicMethods.animateItemUse(player, cah.item);
		}
		
		if(cah.opponent != null && !cah.opponent.equals("dummy")) {
			Player opponent = level.getPlayerByUUID(UUID.fromString(cah.opponent));
			NWBasicMethods.messagePlayerAbilityUsed(null, opponent, "ability.naval_warfare.triggered", opponent.getName().getString(), 
					component);
			NWBasicMethods.messagePlayerTitle(opponent, level, "ability.naval_warfare.triggered", "dark_green", 
					cah.translation, "green");
			NWBasicMethods.animateItemUse(opponent, cah.item);
		}
		
		if(turn)
			removeFirstTurnAction();
		else
			removeFirstAction();
		
		action_number++;
		
		recordOnRecorders(BattleLogHelper.createDelay(cah.delay));
	}
	
	private void cancelActions(ControllerAction action) {
		ArrayList<ControllerActionHelper> keep = new ArrayList<>();
		boolean end = false;
		
		for(ControllerActionHelper cah : actions) {
			if(end)
				keep.add(cah);
			else if(!cah.action.equals(action)) {
				keep.add(cah);
				end = true;
			}
			else {
				BlockState state = level.getBlockState(cah.pos);
				
				if(state.getBlock() instanceof Board) {
					Board board = (Board) state.getBlock();
					level.setBlockAndUpdate(cah.pos, state.setValue(Board.STATE, board.getBoardState(state).deselect()));
				}
				
				state = level.getBlockState(cah.matching);
				
				if(state.getBlock() instanceof Board) {
					Board board = (Board) state.getBlock();
					level.setBlockAndUpdate(cah.matching, state.setValue(Board.STATE, board.getBoardState(state).deselect()));
				}
			}
		}
		
		actions = keep;
	}
	
	private void messageOpponent(Component message) {	
		BlockEntity tile = level.getBlockEntity(opponent);
		
		if(tile instanceof GameControllerTE) {
			GameControllerTE opponent = (GameControllerTE) tile;
			String owner = opponent.getOwner();
			
			if(owner.equals("dummy"))
				return;
			
			Player player = level.getPlayerByUUID(UUID.fromString(owner));
			
			if(player != null)
				player.sendMessage(message, Util.NIL_UUID);
		}
	}
	
	private void informOpponentOfTimer(Level level, String translation, SoundEvent sound) {	
		Player player = getOpponentPlayer();
		
		if(player != null) {
			player.playNotifySound(sound, SoundSource.MASTER, 1, 0.75f);
			NWBasicMethods.messagePlayerActionbar(player, translation);
		}
	}
	
	private void informSelfOfTimer(Level level, String translation, SoundEvent sound) {	
		Player player = getPlayer();
		
		if(player != null) {
			player.playNotifySound(sound, SoundSource.MASTER, 1, 0.75f);
			NWBasicMethods.messagePlayerActionbar(player, translation);
		}
	}
	
	private void consumeResults(Player player, boolean targeted) {
		consumeResults(player, targeted, false, true, false);
	}
	
	private void consumeResults(Player player, boolean targeted, boolean hint, boolean must_hit, boolean passive) {
		MutableComponent text = null;
		boolean cont = !must_hit;
		
		for(TargetResultHelper trh : results) {
			String color = "white";
			String hover = "error";
			
			switch(trh.result) {
			case BLOCKED:
				color = "green";
				hover = NWBasicMethods.getTranslation("misc.naval_warfare.blocked");
				break;
			case NULLIFIED:
				color = "dark_green";
				hover = NWBasicMethods.getTranslation("misc.naval_warfare.nullified");
				break;
			case HIT:
				color = "dark_red";
				hover = targeted ? NWBasicMethods.getTranslation("misc.naval_warfare.hit") :
					NWBasicMethods.getTranslation("misc.naval_warfare.ship_here");
				
				if(hint)
					hover =  NWBasicMethods.getTranslation("misc.naval_warfare.ship_close");
				
				cont = true;
				break;
			case KNOWN:
				color = "aqua";
				hover = hint ? NWBasicMethods.getTranslation("misc.naval_warfare.unknown") :
					NWBasicMethods.getTranslation("misc.naval_warfare.known");
				break;
			case MISS:
				color = "blue";
				hover = targeted ? NWBasicMethods.getTranslation("misc.naval_warfare.miss") :
					 NWBasicMethods.getTranslation("misc.naval_warfare.empty");
				
				if(hint)
					hover = NWBasicMethods.getTranslation("misc.naval_warfare.unknown");
				
				break;
			case UNKOWN:
				color = "blue";
				hover = NWBasicMethods.getTranslation("misc.naval_warfare.unknown");
			default:
				break;
			}
			
			if(text == null) {
				text = new TextComponent(NWBasicMethods.getTranslation("message.naval_warfare.ability_results") + ": [");
				text.append(NWBasicMethods.hoverableText(trh.id + "", color, hover));
			}
			else {
				text.append(new TextComponent(", "));
				text.append(NWBasicMethods.hoverableText(trh.id + "", color, hover));
			}	
		}
		
		if(text != null) {
			text.append(new TextComponent("]"));
			
			if(player != null)
				player.sendMessage(text, Util.NIL_UUID);

			if(passive) {
				player = getPlayer();
				
				if(player != null)
					player.sendMessage(text, Util.NIL_UUID);
				
			}else {
				messageOpponent(text);
			}
			
			notifyResult(cont, false, cont);
			recordOnRecorders(BattleLogHelper.createMessage(Component.Serializer.toJson(text)));
			
			results.clear();			
		}
	}
	
	private void doActionTorpedo(ControllerActionHelper cah) {
		BlockEntity tile = level.getBlockEntity(cah.board_te);
		
		if(tile instanceof BoardTE) {
			BoardTE te = (BoardTE) tile;
			Board board = (Board) te.getBlockState().getBlock();
			
			Player player = null;
			
			if(!cah.player.equals("dummy"))
				player = level.getPlayerByUUID(UUID.fromString(cah.player));
			
			if(cah.health == -1) {
				board.deselectTile(level, te.getBlockPos(), cah.matching);
				removeFirstAction();
				return;
			}

			HitResult result = board.targetTileAction(level, player, te.getBlockState(), te.getBlockPos(), te, cah.matching, cah.damage, 
					cah.target_type, true, action_number, null, true, false);
			
			addResult(new TargetResultHelper(te.getId(), result));
			
			switch(result) {
			case NULLIFIED:
				cancelActions(ControllerAction.TORPEDO);
				consumeResults(player, true);
				break;
			case HIT:
				level.playSound(null, te.getBlockPos(), NWSounds.TORPEDO.get(), SoundSource.MASTER, 1, 1);
				level.playSound(null, cah.matching, NWSounds.TORPEDO.get(), SoundSource.MASTER, 1, 1);
				
				BattleLogHelper blh_sound = BattleLogHelper.createSound(te.getId(), true, NWSounds.TORPEDO.get(), 1, 1);
				recordOnRecorders(blh_sound);
				
				if(cah.health == 1) {
					cancelActions(ControllerAction.TORPEDO);
					consumeResults(player, true);
					action_number++;
				}
				else {
					for(ControllerActionHelper helper : actions) {
						if(helper.action.equals(ControllerAction.TORPEDO))
							helper.health--;
						else
							break;
					}
				}
				break;
			default:
				if(cah.animation != null && !board.getBoardState(te.getBlockState()).isHit()) {
					BlockState own_state = cah.animation.defaultBlockState();
					BlockState opp_state = cah.animation.defaultBlockState();
					
					if(cah.animation instanceof DirectionalDisBlock) {
						Direction own_dir = board.getControllerFacing(level, te.getBlockPos());
						Direction opp_dir = board.getControllerFacing(level, cah.matching);
						own_state = own_state.setValue(DirectionalDisBlock.FACING, own_dir);
						opp_state = opp_state.setValue(DirectionalDisBlock.FACING, opp_dir);
						
						recordOnRecorder(BattleLogHelper.createSetDisBlock(te.getId(), cah.animation.getRegistryName(), 1, true, 
								own_dir, cah.delay));
						recordOnOppRecorder(BattleLogHelper.createSetDisBlock(te.getId(), cah.animation.getRegistryName(), 1, false, 
								opp_dir, cah.delay));
					}
					
					level.setBlockAndUpdate(te.getBlockPos().above(), own_state);
					level.setBlockAndUpdate(cah.matching.above(), opp_state);
					setAnimationTime(te.getBlockPos().above(), cah.delay);
					setAnimationTime(cah.matching.above(), cah.delay);
				}
				
				break;
			}
			
			if(actions.size() == 1 || (actions.size() > 1 && !actions.get(1).action.equals(ControllerAction.TORPEDO))) 
				consumeResults(player, true);
		}
		recordOnRecorders(BattleLogHelper.createDelay(cah.delay));
		removeFirstAction();
	}
	
	private void doActionFragbomb(ControllerActionHelper cah) {
		BlockEntity tile = level.getBlockEntity(cah.board_te);
		
		if(tile instanceof BoardTE) {
			BoardTE te = (BoardTE) tile;
			Board board = (Board) te.getBlockState().getBlock();
			
			Player player = null;
			Block animation = cah.target_type.equals(TargetType.NORMAL) ? null : cah.animation;
			
			if(!cah.player.equals("dummy"))
				player = level.getPlayerByUUID(UUID.fromString(cah.player));

			HitResult result = board.targetTileAction(level, player, te.getBlockState(), te.getBlockPos(), te, cah.matching, cah.damage, 
					cah.target_type, true, action_number, animation, true, false);
			
			addResult(new TargetResultHelper(te.getId(), result));
			
			switch(result) {
			case HIT:
				streak++;
				break;
			default:
				if(cah.target_type.equals(TargetType.NORMAL)) {
					cancelActions(ControllerAction.FRAGBOMB);
					consumeResults(player, true);
				}				
				break;
			}
			
			if(actions.size() == 1 || (actions.size() > 1 && !actions.get(1).action.equals(ControllerAction.FRAGBOMB))) 
				consumeResults(player, true);
			
			if(result.equals(HitResult.MISS))
				recordOnRecorders(BattleLogHelper.createBoardState(te.getId(), true, BoardState.EMPTY));
		}
		
		removeFirstAction();
	}

	
	private void doActionNapalm(ControllerActionHelper cah) {
		BlockEntity tile = level.getBlockEntity(cah.board_te);
		
		if(tile instanceof BoardTE) {
			BoardTE te = (BoardTE) tile;
			Board board = (Board) te.getBlockState().getBlock();
			
			Player player = null;
			
			if(!cah.player.equals("dummy"))
				player = level.getPlayerByUUID(UUID.fromString(cah.player));

			HitResult result = board.targetTileAction(level, player, te.getBlockState(), te.getBlockPos(), te, cah.matching, cah.damage, 
					cah.target_type, false, action_number, null, true, false);
			
			switch(result) {
			case HIT:
				streak++;
				
				if(!ShipBlock.getState(level.getBlockState(te.getBlockPos().above())).equals(ShipState.DESTROYED)) {
					addTurnAction(ControllerActionHelper.createAnnounce(cah.player, getOpponentName(cah.pos), NWItems.FIRE_DAMAGE.get(), 
							"ability.naval_warfare.fire_damage", Napalm.fireDamageText()));
					addTurnAction(ControllerActionHelper.createForcedTarget(cah.pos, cah.player, cah.board_te, cah.matching));
					addTurnAction(ControllerActionHelper.createValidate());
					
					level.playSound(null, te.getBlockPos(), NWSounds.NAPALM.get(), SoundSource.MASTER, 1, 1);
					level.playSound(null, cah.matching, NWSounds.NAPALM.get(), SoundSource.MASTER, 1, 1);
				}
				break;
			default:			
				break;
			}
		}
		
		removeFirstAction();
	}
	
	private void doTurnDamage(ControllerActionHelper cah){
		BlockEntity tile = level.getBlockEntity(cah.board_te);
		
		if(tile instanceof BoardTE) {
			BoardTE te = (BoardTE) tile;
			Board board = (Board) te.getBlockState().getBlock();
			
			Player player = null;
			
			if(!cah.player.equals("dummy"))
				player = level.getPlayerByUUID(UUID.fromString(cah.player));

			HitResult result = board.targetTileAction(level, player, te.getBlockState(), te.getBlockPos(), te, cah.matching, cah.damage, 
					cah.target_type, true, action_number, null, cah.triggers_passives, true);
			
			switch(result) {
			case HIT:
				streak++;
				BlockState state = level.getBlockState(te.getBlockPos().above());
				
				if(!(state.getBlock() instanceof ShipBlock))
					break;
				
				if(ShipBlock.getState(state).isAlive()) {
					addTurnAction(ControllerActionHelper.createAnnounce(cah.player, getOpponentName(cah.pos), NWItems.FIRE_DAMAGE.get(), 
							"ability.naval_warfare.fire_damage", ""));
					addTurnAction(ControllerActionHelper.createForcedTarget(cah.pos, cah.player, cah.board_te, cah.matching));
				}
				
				addTurnAction(ControllerActionHelper.createValidate());
				break;
			default:			
				break;
			}
		}
		
		removeFirstTurnAction();
	}
	
	private void doMultiTarget(ControllerActionHelper cah) {
		BlockEntity tile = level.getBlockEntity(cah.board_te);
		
		if(tile instanceof BoardTE) {
			BoardTE te = (BoardTE) tile;
			Board board = (Board) te.getBlockState().getBlock();
			
			Player player = null;
			
			if(!cah.player.equals("dummy"))
				player = level.getPlayerByUUID(UUID.fromString(cah.player));

			HitResult result = board.targetTileAction(level, player, te.getBlockState(), te.getBlockPos(), te, cah.matching, cah.damage, 
					cah.target_type, true, action_number, null, cah.triggers_passives, false);
			
			addResult(new TargetResultHelper(te.getId(), result));
			
			if(actions.size() == 1 || (actions.size() > 1 && (!actions.get(1).action.equals(ControllerAction.MULTI_TARGET)))) {
				consumeResults(player, true, !cah.target_type.equals(TargetType.REVEAL), !cah.multi_ability, cah.multi_ability);
				action_number++;
			}
			
			if(result.equals(HitResult.MISS))
				recordOnRecorders(BattleLogHelper.createBoardState(te.getId(), !cah.multi_ability, BoardState.EMPTY));
		}
		
		removeFirstAction();
	}
	
	private void doActionBomber(ControllerActionHelper cah) {
		BlockEntity tile = level.getBlockEntity(cah.board_te);
		
		if(tile instanceof BoardTE) {
			BoardTE te = (BoardTE) tile;
			Board board = (Board) te.getBlockState().getBlock();
			
			Player player = null;
			
			if(!cah.player.equals("dummy"))
				player = level.getPlayerByUUID(UUID.fromString(cah.player));
			
			level.playSound(null, te.getBlockPos(), NWSounds.BOMBER.get(), SoundSource.MASTER, 1, 1);
			level.playSound(null, cah.matching, NWSounds.BOMBER.get(), SoundSource.MASTER, 1, 1);
			recordOnRecorders(BattleLogHelper.createSound(te.getId(), true, NWSounds.BOMBER.get(), 1, 1));

			HitResult result = board.targetTileAction(level, player, te.getBlockState(), te.getBlockPos(), te, cah.matching, cah.damage, 
					cah.target_type, true, action_number, NWBlocks.BOMB.get(), true, false);
			
			if(cah.animation != null) {
				BlockState own_state = cah.animation.defaultBlockState();
				BlockState opp_state = cah.animation.defaultBlockState();
				
				if(cah.animation instanceof DirectionalDisBlock) {
					Direction own_dir = board.getControllerFacing(level, te.getBlockPos());
					Direction opp_dir = board.getControllerFacing(level, cah.matching);
					own_state = own_state.setValue(DirectionalDisBlock.FACING, own_dir);
					opp_state = opp_state.setValue(DirectionalDisBlock.FACING, opp_dir);
					
					recordOnRecorder(BattleLogHelper.createSetDisBlock(te.getId(), cah.animation.getRegistryName(), 6, true, 
							own_dir, cah.delay));
					recordOnOppRecorder(BattleLogHelper.createSetDisBlock(te.getId(), cah.animation.getRegistryName(), 6, false, 
							opp_dir, cah.delay));
				}
				
				level.setBlockAndUpdate(te.getBlockPos().above(6), own_state);
				level.setBlockAndUpdate(cah.matching.above(6), opp_state);
				setAnimationTime(te.getBlockPos().above(6), cah.delay);
				setAnimationTime(cah.matching.above(6), cah.delay);
				
				
			}
			
			addResult(new TargetResultHelper(te.getId(), result));
			
			switch(result) {
			case NULLIFIED:
				cancelActions(ControllerAction.BOMBER);
				consumeResults(player, true);
				break;
			default:
				break;
			}
			
			if(actions.size() == 1 || (actions.size() > 1 && !actions.get(1).action.equals(ControllerAction.BOMBER))) 
				consumeResults(player, true);
		}
		
		recordOnRecorders(BattleLogHelper.createDelay(cah.delay));
		removeFirstAction();
	}	
	
	private void computeFlareTile(Player player, BoardTE te, boolean passive) {
		ArrayList<TargetResultHelper> hits = new ArrayList<>();
		
		for(TargetResultHelper trh : results) {
			if(trh.result.equals(HitResult.HIT))
				hits.add(trh);
		}
		
		if(hits.size() > 0) {
			ThreadLocalRandom rand = ThreadLocalRandom.current();
			int ship = rand.nextInt(hits.size());
			
			TargetResultHelper trh = hits.get(ship);
			GameController controller = (GameController) getBlockState().getBlock();
			Board board = (Board) te.getBlockState().getBlock();
			Direction dir_opp = board.getControllerFacing(level, te.getBlockPos());
			Direction dir_own = controller.getFacing(getBlockState());
			BlockPos pos = te.locateId(te.getBlockPos(), trh.id, dir_opp, false);
			
			if(pos == null)
				return;
			
			BlockEntity tile = level.getBlockEntity(pos);
			
			if(tile instanceof BoardTE) {
				BoardTE bte = (BoardTE) tile;
				ArrayList<BoardTE> locations = bte.collectTileArea(2, 2, 2, 2, dir_opp);
				ArrayList<BoardTE> useable = new ArrayList<>();

				for(BoardTE t : locations) {
					BlockState state = level.getBlockState(t.getBlockPos());
					
					if(state.getBlock() instanceof Board) {
						board = (Board) state.getBlock();
						
						if(!board.getBoardState(state).isKnown())
							useable.add(t);
					}
				}
				
				if(useable.size() > 0) {
					int hint = rand.nextInt(useable.size());
					BoardTE hint_te = useable.get(hint); // block to show hint
					BlockEntity own_board = level.getBlockEntity(opponent_zero);
					
					if(own_board instanceof BoardTE) {
						BoardTE own_boardte = (BoardTE) own_board;
						BlockPos matching;
						
						if(passive) {
							BoardTE passive_bte = controller.getOpponentBoardTile(level, this, hint_te.getId(), true);
							
							if(passive_bte == null)
								return;
							
							matching = passive_bte.getBlockPos();
						}
						else {
							matching = own_boardte.locateId(own_boardte.getBlockPos(), hint_te.getId(), dir_own, true);
						}
							
						level.setBlockAndUpdate(matching.above(2), NWBlocks.SHIP_CLOSE.get().defaultBlockState().setValue(ShipMarkerBlock.FACING, 
								dir_own.getOpposite()));
						level.setBlockAndUpdate(hint_te.getBlockPos().above(2), NWBlocks.SHIP_CLOSE.get().defaultBlockState().setValue(ShipMarkerBlock.FACING, 
								dir_opp.getOpposite()));
						
						results = new ArrayList<>();
						results.add(trh);
						
						recordOnRecorders(BattleLogHelper.createSetBlock(hint_te.getId(), NWBlocks.SHIP_CLOSE.get().getRegistryName(), 2, !passive));
					}
				}
				else {
					results = new ArrayList<>();
					results.add(new TargetResultHelper(-1, HitResult.UNKOWN));
				}
			}
			else {
				results = new ArrayList<>();
				results.add(new TargetResultHelper(-1, HitResult.UNKOWN));
			}
		}
		else {
			results = new ArrayList<>();
			results.add(new TargetResultHelper(-1, HitResult.UNKOWN));
		}
		
		consumeResults(player, false, true, false, passive);
	}
	
	private void doActionSpyglass(ControllerActionHelper cah) {
		BlockEntity tile = level.getBlockEntity(cah.board_te);
		
		if(tile instanceof BoardTE) {
			BoardTE te = (BoardTE) tile;
			Board board = (Board) te.getBlockState().getBlock();
			
			Player player = null;
			
			if(!cah.player.equals("dummy"))
				player = level.getPlayerByUUID(UUID.fromString(cah.player));

			HitResult result = board.revealTileAction(level, player, te.getBlockState(), te.getBlockPos(), te, cah.matching,
					cah.target_type, action_number);
			
			addResult(new TargetResultHelper(te.getId(), result));
			
			if(result.equals(HitResult.HIT)) {
				Direction dir = board.getControllerFacing(level, te.getBlockPos());
				level.setBlockAndUpdate(te.getBlockPos().above(2), NWBlocks.SHIP_HERE.get().defaultBlockState().setValue(ShipMarkerBlock.FACING, 
						dir.getOpposite()));
				BlockEntity opp = level.getBlockEntity(cah.matching);
				recordOnRecorders(BattleLogHelper.createSetBlock(te.getId(), NWBlocks.SHIP_HERE.get().getRegistryName(), 2, !cah.multi_ability));
				
				if(opp instanceof BoardTE) {
					BoardTE oppte = (BoardTE) opp;
					dir = board.getControllerFacing(level, te.getBlockPos());
					level.setBlockAndUpdate(oppte.getBlockPos().above(2), NWBlocks.SHIP_HERE.get().defaultBlockState().setValue(ShipMarkerBlock.FACING, 
							dir.getOpposite()));
				}
			}
			
			if(actions.size() == 1 || (actions.size() > 1 && !actions.get(1).action.equals(ControllerAction.SPYGLASS))) {
				consumeResults(player, false, false, false, cah.multi_ability);
				action_number++;
			}
		}
		
		removeFirstAction();
	}
	
	private void doActionFlare(ControllerActionHelper cah) {
		BlockEntity tile = level.getBlockEntity(cah.board_te);
		
		if(tile instanceof BoardTE) {
			BoardTE te = (BoardTE) tile;
			Board board = (Board) te.getBlockState().getBlock();
			
			Player player = null;
			
			if(!cah.player.equals("dummy"))
				player = level.getPlayerByUUID(UUID.fromString(cah.player));

			HitResult result = board.revealTileAction(level, player, te.getBlockState(), te.getBlockPos(), te, cah.matching,
					cah.target_type, action_number);
			
			addResult(new TargetResultHelper(te.getId(), result));
			
			if(actions.size() == 1 || (actions.size() > 1 && !actions.get(1).action.equals(ControllerAction.FLARE))) {
				computeFlareTile(player, te, cah.multi_ability);
				action_number++;
			}
		}
		
		removeFirstAction();
	}
	
	private void doActionEnergy(ControllerActionHelper cah) {	
		if(cah.multi_ability)
			gainEnergy(cah.health);
		else {
			GameController control = (GameController) getBlockState().getBlock();
			
			if(control.validateController(level, opponent)) {
				GameControllerTE te = (GameControllerTE) level.getBlockEntity(opponent);
				te.gainEnergy(cah.health);
			}
		}
		
		removeFirstAction();
	}
	
	private String getOpponentName(BlockPos pos) {
		BlockEntity tile = level.getBlockEntity(pos);
		
		if(tile instanceof BoardTE) {
			BoardTE te = (BoardTE) tile;
			Board board = (Board) te.getBlockState().getBlock();
			GameControllerTE gte = board.getController(level, te.getBlockPos());
			
			if(gte != null) 
				return gte.getOwner();
		}
		
		return "dummy";
	}
	
	public void streakSounds() {
		if(streak > 2) {
			String message = ChatFormatting.GOLD + NWBasicMethods.getTranslation("message.naval_warfare.streak").replace("MARKER1", "" + streak);
			Player player = getPlayer();
			SoundEvent sound = NWSounds.STREAK1.get();
			
			if(streak > 8)
				sound = NWSounds.STREAK3.get();
			else if(streak > 5)
				sound = NWSounds.STREAK2.get();
			
			if(player != null) {
				NWBasicMethods.messagePlayerCustomRecord(this, player, message, false);
				player.playNotifySound(sound, SoundSource.MASTER, 1, 1);
				
				recordOnRecorders(BattleLogHelper.createSound((int) Math.ceil(board_size) / 2, true, sound, 1, 1));
			}
			
			player = getOpponentPlayer();
			
			if(player != null) {
				player.playNotifySound(sound, SoundSource.MASTER, 1, 1);
				NWBasicMethods.messagePlayerCustomRecord(this, player, message, true);	
			}
		}
		
	}
	
	private void doTurnWork() {
		if(turn_actions.size() < 1)
			return;
		
		ControllerActionHelper cah = turn_actions.get(0);
		
		if(cah.action == null) {
			NavalWarfare.LOGGER.warn("GameController BlockEntity at " + worldPosition.toShortString() + " got corrupt turn action. Skipping action: " + cah);
			removeFirstTurnAction();
			return;
		}
		
		switch(cah.action) {
		case TURN_DAMAGE:
			doTurnDamage(cah);
			break;
		case ANNOUNCE:
			doActionAnnounce(cah, true);
			break;
		case VALIDATE:
			notifyResult(true, false, false);
			removeFirstTurnAction();
			break;
		case RAFT:
			if(cah.health > 0) {
				cah.health--;
				addTurnAction(cah);
			}
			else {
				addTurnAction(ControllerActionHelper.createForcedTarget(cah.pos, cah.player, cah.board_te, cah.matching));
				addTurnAction(ControllerActionHelper.createValidate());
			}
			removeFirstTurnAction();
			break;
		default:
			NavalWarfare.LOGGER.warn("I don't know how to execute this turn action! Please report this to the mod author! Turn action is: " + cah);
			removeFirstTurnAction();
			break;
		}
	}
	
	private void doWork() {
		if(actions.size() < 1)
			return;
		
		ControllerActionHelper cah = actions.get(0);
		
		if(cah.action == null) {
			NavalWarfare.LOGGER.warn("GameController BlockEntity at " + worldPosition.toShortString() + " got corrupt action. Skipping action: " + cah);
			removeFirstAction();
			return;
		}
		
		switch(cah.action) {
		case ACTIVE_ABILITY:
			doActionAbility(cah, true);
			break;
		case PASSIVE_ABILITY:
			doActionAbility(cah, false);
			break;
		case SPELL:
			doActionSpell(cah);
			break;
		case TARGET:
			doActionTarget(cah);
			break;
		case TORPEDO:
			doActionTorpedo(cah);
			break;
		case MULTI_TARGET:
			doMultiTarget(cah);
			break;
		case SPYGLASS:
			doActionSpyglass(cah);
			break;
		case FLARE:
			doActionFlare(cah);
			break;
		case ANNOUNCE:
			doActionAnnounce(cah, false);
			break;
		case VALIDATE:
			notifyResult(true, false, false);
			removeFirstAction();
			break;
		case END_TURN:
			notifyResult(false, false, false);
			removeFirstAction();
			break;
		case GAIN_ENERGY:
			doActionEnergy(cah);
			break;
		case FRAGBOMB:
			doActionFragbomb(cah);
			break;
		case BOMBER:
			doActionBomber(cah);
			break;
		case NAPALM:
			doActionNapalm(cah);
			break;
		default:
			NavalWarfare.LOGGER.warn("I don't know how to execute this action! Please report this to the mod author! Action is: " + cah);
			removeFirstAction();
			break;
		}
		
//		recordOnRecorder(cah);
	}
	
	public void recordOnOppRecorder(BattleLogHelper blh) {
		if(opponent == null)
			return;
		
		BlockEntity tile = level.getBlockEntity(opponent);
		
		if(tile instanceof GameControllerTE) {
			GameControllerTE te = (GameControllerTE) tile;
			
			BattleLogHelper new_blh = blh.copy();
			new_blh.opponent = !new_blh.opponent;
			
			te.recordOnRecorder(new_blh);
		}
	}
	
	public void recordOnRecorder(BattleLogHelper blh) {
		BattleRecorderTE te = getRecorder();
		
		if(te != null) 
			te.addAction(blh);
	}
	
	public void recordOnRecorders(BattleLogHelper blh) {
		if(blh.action.equals(BattleLogAction.DELAY) && blh.delay <= 0)
			return;
		
		recordOnRecorder(blh);
		recordOnOppRecorder(blh);
	}
	
	public void recordOnRecorder(ArrayList<ShipSaveHelper> own_ships, ArrayList<ShipSaveHelper> opp_ships, int own_size, int opp_size, Direction own_dir,
			Direction opp_dir) {
		BattleRecorderTE te = getRecorder();
		
		if(te != null) {
			te.setOwnShips(own_ships);
			te.setOppShips(opp_ships);
			te.setOwnSize(own_size);
			te.setOppSize(opp_size);
			te.setOwnDir(own_dir);
			te.setOppDir(opp_dir);
		}
	}
	
	private void saveRecorder() {
		BattleRecorderTE te = getRecorder();
		
		if(te != null) {
			Player oplayer = getOpponentPlayer();
			String opstring = oplayer == null ? "dummy" : oplayer.getName().getString();
			Player player = getPlayer();
			String pstring = player == null ? "dummy" : player.getName().getString();
			te.generateLog(getPlayer(), pstring, opstring);
			te.reset();
		}
	}
	
	private BattleRecorderTE getRecorder() {
		GameController controller = (GameController) getBlockState().getBlock();
		BlockEntity tile = level.getBlockEntity(worldPosition.offset(controller.getFacing(getBlockState()).getOpposite().getNormal()));
		
		if(tile instanceof BattleRecorderTE)
			return (BattleRecorderTE) tile;
		
		return null;
	}
	
	/** Notify the controller of the result obtained by executing the previous action
	 * 
	 * @param cont If the controller keeps its turn
	 * @param timeout If the controller timed out
	 * @param set_hit If the controller should set hit_once to true
	 */
	public void notifyResult(boolean cont, boolean timeout, boolean set_hit) {
		BlockState state = level.getBlockState(opponent);
		
		if(!(state.getBlock() instanceof GameController && state.hasBlockEntity()))
			return;
		
		BlockEntity otile = level.getBlockEntity(opponent);
			
		if(!(otile instanceof GameControllerTE))
			return;
		
		GameControllerTE ote = (GameControllerTE) otile;
		Player ownp = owner.equals("dummy")? null : level.getPlayerByUUID(UUID.fromString(owner));
		Player oppp = ote.getOwner().equals("dummy") ? null : level.getPlayerByUUID(UUID.fromString(ote.getOwner()));
				
		if(!cont) {
			if(timeout && !hit_once) {
				timeout_times++;
				cons_timeout_times++;
			}
			
			if(ownp != null) {
				if(timeout) {
					if(hit_once)
						cons_timeout_times = 0;
					
					NWBasicMethods.messagePlayerCustomRecord(this, ownp, NWBasicMethods.createRedText("message.naval_warfare.time_up_own").
							getString(), false);
				}
				else
					cons_timeout_times = 0;
				
				NWBasicMethods.messagePlayerCustomRecord(this, ownp, NWBasicMethods.createRedText("message.naval_warfare.opponent_turn").getString(), false);
				ownp.playNotifySound(NWSounds.OPPONENT_TURN.get(), SoundSource.MASTER, 1, 1.5f);
			}
			
			if(oppp != null) {
				if(timeout) 
					NWBasicMethods.messagePlayerCustomRecord(this, oppp, NWBasicMethods.createGreenText("message.naval_warfare.time_up_opponent").
							getString(), true);
				
				NWBasicMethods.messagePlayerCustomRecord(this, oppp, NWBasicMethods.createGreenText("message.naval_warfare.your_turn").getString(), true);
				oppp.playNotifySound(NWSounds.YOUR_TURN.get(), SoundSource.MASTER, 1, 0.5f);
			}
			
			if(cons_timeout_times >= NavalWarfareConfig.max_cons_timeouts.get() || timeout_times >= NavalWarfareConfig.max_timeouts.get()) {
				endGame(level, this, false, ownp, false);
				endGame(level, ote, true, oppp, true);
				
				NWBasicMethods.messagePlayerCustomRecord(this, ownp, NWBasicMethods.createRedText("message.naval_warfare.timed_out_own").
						getString(), false);
				
				NWBasicMethods.messagePlayerCustomRecord(this, oppp, NWBasicMethods.createGreenText("message.naval_warfare.timed_out_opponent").
						getString(), true);
			}
			else {
				hit_once = false;
				resetTurnTime();
				setTurn(false, false);
				ote.setActionNumber(action_number + 2);
				ote.setTurn(true, true);
			}
		}
		else {
			if(!ote.isAlive()) {
				endGame(level, this, true, ownp, true);
				endGame(level, ote, false, oppp, true);
			}
			else if(!isAlive()) {
				endGame(level, this, false, ownp, true);
				endGame(level, ote, true, oppp, true);
			}
			else if(set_hit)
				hit_once = true;
		}
	}
	
	public void endGame(Level level, GameControllerTE te, boolean won, @Nullable Player player, boolean loot) {
		endGame(level, te, won, player, loot, true);
	}

	public void endGame(Level level, GameControllerTE te, boolean won, @Nullable Player player, boolean loot, boolean reset) {
		if(reset) {
			te.setGame(false);
			te.setTurn(false, false);
			te.setBoardSize(0);
			te.setHP(0);
			te.action_number = 0;
			te.resetTurnTime();
			te.setClearBoard(true);
			te.resetActions();
			te.timeout_times = 0;
			te.cons_timeout_times = 0;
			hit_once = false;
			streak = 0;
			
			level.setBlockAndUpdate(te.getBlockPos(), te.getBlockState().setValue(GameController.STATE, ControllerState.INACTIVE));
		}
		
		if(player == null)
			return;
		
		if(loot)
			genFromLootTable(won, false, player);
		
		if(won) {
			NWBasicMethods.sendGameStatusToPlayer(level, te.getOwner(), "message.naval_warfare.game_won_main", "dark_green", 
					"message.naval_warfare.game_won_sub", "green");
			player.playNotifySound(NWSounds.VICTORY.get(), SoundSource.MASTER, 1, 0.8f);
			
			NWBasicMethods.messagePlayerCustomRecord(te, player, NWBasicMethods.createGreenText("message.naval_warfare.game_won_main").
					getString(), false);
			
			if(player instanceof ServerPlayer) {
				try {
					ServerPlayer sp = (ServerPlayer) player;
					sp.awardStat(NWStats.WINS);
					NWTriggers.WIN.trigger(sp);
					
					Stat<ResourceLocation> stat = Stats.CUSTOM.get(new ResourceLocation(NavalWarfare.MOD_ID, "wins"), StatFormatter.DEFAULT);
					int val = sp.getStats().getValue(stat);
					
					if(val >= 10)
						NWTriggers.WIN_10.trigger(sp);
					if(val >= 50)
						NWTriggers.WIN_50.trigger(sp);

				} catch(NullPointerException e) {
					NavalWarfare.LOGGER.error("Naval Warfare: Failed to fetch the wins statistic.");
				}
			}
		}
		else {
			NWBasicMethods.sendGameStatusToPlayer(level, te.getOwner(), "message.naval_warfare.game_lost_main", "dark_red", 
					"message.naval_warfare.game_lost_sub", "red");
			NWBasicMethods.messagePlayerCustomRecord(te, player, NWBasicMethods.createRedText("message.naval_warfare.game_lost_main").
					getString(), false);
			player.playNotifySound(NWSounds.DEFEAT.get(), SoundSource.MASTER, 1, 0.75f);
		}
		
		if(player instanceof ServerPlayer) {
			try {
				ServerPlayer sp = (ServerPlayer) player;
				sp.awardStat(NWStats.GAMES_PLAYED);
				
			} catch(NullPointerException e) {
				NavalWarfare.LOGGER.error("Naval Warfare: Failed to fetch the games played statistic.");
			}
		}
	}

	public boolean checkTurn() {	
		if(turn_time  >= MAX_TURN_TIME - 300) {
			if(turn_time == MAX_TURN_TIME - 300) {
				informSelfOfTimer(level, "message.naval_warfare.time_fifteen", SoundEvents.NOTE_BLOCK_DIDGERIDOO);
				informOpponentOfTimer(level, "message.naval_warfare.time_fifteen", SoundEvents.NOTE_BLOCK_DIDGERIDOO);
			} else if(turn_time == MAX_TURN_TIME - 100) {
				informSelfOfTimer(level, "message.naval_warfare.time_five", SoundEvents.NOTE_BLOCK_DIDGERIDOO);
				informOpponentOfTimer(level, "message.naval_warfare.time_five", SoundEvents.NOTE_BLOCK_DIDGERIDOO);
			} else if(turn_time == MAX_TURN_TIME - 80) {
				informSelfOfTimer(level, "message.naval_warfare.time_four", SoundEvents.NOTE_BLOCK_DIDGERIDOO);
				informOpponentOfTimer(level, "message.naval_warfare.time_four", SoundEvents.NOTE_BLOCK_DIDGERIDOO);
			} else if(turn_time == MAX_TURN_TIME - 60) {
				informSelfOfTimer(level, "message.naval_warfare.time_three", SoundEvents.NOTE_BLOCK_DIDGERIDOO);
				informOpponentOfTimer(level, "message.naval_warfare.time_three", SoundEvents.NOTE_BLOCK_DIDGERIDOO);
			} else if(turn_time == MAX_TURN_TIME - 40) {
				informSelfOfTimer(level, "message.naval_warfare.time_two", SoundEvents.NOTE_BLOCK_DIDGERIDOO);
				informOpponentOfTimer(level, "message.naval_warfare.time_two", SoundEvents.NOTE_BLOCK_DIDGERIDOO);
			} else if(turn_time == MAX_TURN_TIME - 20) {
				informSelfOfTimer(level, "message.naval_warfare.time_one", SoundEvents.NOTE_BLOCK_DIDGERIDOO);
				informOpponentOfTimer(level, "message.naval_warfare.time_one", SoundEvents.NOTE_BLOCK_DIDGERIDOO);
			}
		}
		
		if(turn_time >= MAX_TURN_TIME) {
			informSelfOfTimer(level, "message.naval_warfare.time_up_own", NWSounds.SELF_TIMEOUT.get());
			informOpponentOfTimer(level, "message.naval_warfare.time_up_opponent", NWSounds.OPPONENT_TIMEOUT.get());
			notifyResult(false, true, false);
			
			return false;
		}
		
		return true;
	}
	
	private Player getPlayer() {
		if(hasOwner() && !owner.equals("dummy")) 
			return level.getPlayerByUUID(UUID.fromString(owner));
			
		return null;
	}
	
	private Player getOpponentPlayer() {
		if(opponent != null) {
			BlockEntity tile = level.getBlockEntity(opponent);
			
			if(tile instanceof GameControllerTE) {
				GameControllerTE te = (GameControllerTE) tile;
				
				if(te.hasOwner() && !te.getOwner().equals("dummy"))
					return level.getPlayerByUUID(UUID.fromString(te.getOwner()));
			}
		}
		
		return null;
	}
	
	public void deployStartingPassiveAbilities(Level level, Player player) {
		BlockState state = level.getBlockState(zero);
		
		if(state.getBlock() instanceof Board) {
			Board board = (Board) state.getBlock();
			
			if(board.validateBoard(level, zero)) {
				BoardTE bte = (BoardTE) level.getBlockEntity(zero);
				bte.activateStartPassives(level, player, zero, board.getControllerFacing(level, zero));
			}
		}
	}
	
	public void genFromLootTable(boolean won, boolean ai, Player player) {
		if(player.isCreative())
			return;
		
		String loot_table = won ? "naval_warfare:game_end/game_won" : "naval_warfare:game_end/game_lost";
		
		if(ai)
			loot_table += "_ai";
		
		LootTable table = level.getServer().getLootTables().get(new ResourceLocation(loot_table));
		LootContext.Builder lootcontext$builder = new LootContext.Builder(level.getServer().overworld());
		LootContextParamSet.Builder lps$builder = new LootContextParamSet.Builder();
		LootContext context = lootcontext$builder.create(lps$builder.build());
		
		List<ItemStack> loot = table.getRandomItems(context);
		
		for(ItemStack stack : loot)
			NWBasicMethods.addOrSpawn(player, stack, level, player.blockPosition());
	}
	
	private boolean placeShipRandomly(ShipBlock ship, Direction facing) {
		BlockEntity tile = level.getBlockEntity(zero);
		
		if(!(tile instanceof BoardTE))
			return false;
		
		BoardTE te = (BoardTE) tile;
		
		ArrayList<Direction> dirs = new ArrayList<>(Arrays.asList(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST));
		ArrayList<Integer> tiles = new ArrayList<>();
		BlockState state = ship.defaultBlockState();
		
		for(int i = 0; i < board_size; i++)
			tiles.add(i);
		
		Collections.shuffle(tiles);
		
		for(int i : tiles) {
			Collections.shuffle(dirs);
			
			for(Direction dir : dirs) {
				BlockPos pos = te.locateId(te.getBlockPos(), i, facing);
				
				if(pos != null) {
					if(ship.summonShip(level, pos.above(), state.setValue(ShipBlock.FACING, dir), true, false))
						return true;
				}
			}
		}
		
		return false;
	}
	
	public void resetBoard(Player player) {
		GameController controller = (GameController) getBlockState().getBlock();
		controller.removeBoardAndShips(level, this, false);
		controller.spawnBoard(level, player, this, worldPosition, board_size, false);
	}
	
	@Override
	public void saveAdditional(CompoundTag compound) {
		compound.put(NavalWarfare.MOD_ID, NBTHelper.toNBT(this));
	}
	
	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		CompoundTag initvalues = compound.getCompound(NavalWarfare.MOD_ID);
		
		if(!initvalues.isEmpty()) {
			owner = initvalues.getString("owner");
			CompoundTag zero_board = initvalues.getCompound("zero");
			
			if(zero_board.contains("no_pos"))
				zero = null;
			else
				zero = NBTHelper.readBlockPos(zero_board);
			
			CompoundTag opponent_pos = initvalues.getCompound("opponent");
			
			if(opponent_pos.contains("no_pos"))
				opponent = null;
			else
				opponent = NBTHelper.readBlockPos(opponent_pos);
			
			CompoundTag opponent_zero_pos = initvalues.getCompound("opponent_zero");
			
			if(opponent_zero_pos.contains("no_pos"))
				opponent_zero = null;
			else
				opponent_zero = NBTHelper.readBlockPos(opponent_zero_pos);
			
			actions = new ArrayList<>();
			
			if(initvalues.contains("actions")) {
				ListTag list = initvalues.getList("actions", Tag.TAG_COMPOUND);
				
				for(Tag nbt : list) {
					CompoundTag cnbt = (CompoundTag) nbt;
					ControllerActionHelper cah = NBTHelper.readCAH(cnbt);
					addAction(cah);
				}
			}
			
			turn_actions = new ArrayList<>();
			
			if(initvalues.contains("turn_actions")) {
				ListTag list = initvalues.getList("actions", Tag.TAG_COMPOUND);
				
				for(Tag nbt : list) {
					CompoundTag cnbt = (CompoundTag) nbt;
					ControllerActionHelper cah = NBTHelper.readCAH(cnbt);
					addTurnAction(cah);
				}
			}
			
			results = new ArrayList<>();
			
			if(initvalues.contains("results")) {
				ListTag list = initvalues.getList("results", Tag.TAG_COMPOUND);
				
				for(Tag nbt : list) {
					CompoundTag cnbt = (CompoundTag) nbt;
					TargetResultHelper trh = NBTHelper.readTRH(cnbt);
					addResult(trh);
				}
			}
			
			registered = new ArrayList<>();
			
			if(initvalues.contains("registered")) {
				ListTag list = initvalues.getList("registered", Tag.TAG_COMPOUND);
				
				for(Tag nbt : list) {
					CompoundTag cnbt = (CompoundTag) nbt;
					register(cnbt.getString("ship"));
				}
			}
			
			register_buffer = new ArrayList<>();
			
			if(initvalues.contains("register_buffer")) {
				ListTag list = initvalues.getList("register_buffer", Tag.TAG_COMPOUND);
				
				for(Tag nbt : list) {
					CompoundTag cnbt = (CompoundTag) nbt;
					
					addRegisterToBuffer(cnbt.getString("ship"));
				}
			}
			
			playing_game = initvalues.getBoolean("playing_game");
			clear_board = initvalues.getBoolean("clear_board");
			has_turn = initvalues.getBoolean("has_turn");
			turn_time = initvalues.getInt("turn_time");
			board_size = initvalues.getInt("board_size");
			hp = initvalues.getInt("hp");
			config_name = initvalues.getString("name");
			action_time = initvalues.getInt("action_time");
			action_number = initvalues.getInt("action_number");
			energy = initvalues.getInt("energy");
			do_turn_actions = initvalues.getBoolean("do_turn_actions");	
			turn_action_amount = initvalues.getInt("turn_action_amount");
			has_spell = initvalues.getBoolean("has_spell");
			cons_timeout_times = initvalues.getInt("cons_timeout_times");
			timeout_times = initvalues.getInt("timeout_times");
			reg_buffer_time = initvalues.getInt("reg_buffer_time");
			hit_once = initvalues.getBoolean("hit_once");
			streak = initvalues.getInt("streak");
			setChanged();
		}
	}

	public void tick() {
		if(level.isClientSide())
			return;
		
		if(!playing_game) {
			if(clear_board){
				if(turn_time < CLEAR_BOARD_TIME)
					turn_time++;
				else {
					saveRecorder();
					clear_board = false;
					turn_time = -1;
					GameController controller = (GameController) getBlockState().getBlock();
					controller.removeBoardAndShips(level, this);
				}
				setChanged();
			}
			else if(hasRegInBuffer()) {
				if(reg_buffer_time >= 5) {
					reg_buffer_time = 0;
					ShipBlock ship = NWBasicMethods.getShipFromRegname(register_buffer.get(0));					
					
					if(ship != null) {
						GameController controller = (GameController) getBlockState().getBlock();
						boolean placed = placeShipRandomly(ship, controller.getFacing(getBlockState()));
						
						if(placed) {
							register(register_buffer.get(0));
							addHP(ship.getMaxHP());
						}
					}
					
					register_buffer.remove(0);
				}
				else
					reg_buffer_time++;
			}
		}
		
		else if(has_turn && checkTurn()) {			
			if(do_turn_actions) {
				if(hasTurnWork())
					turn_action_amount = turn_actions.size();
				
				do_turn_actions = false;
			}
			
			if(hasTurnWork()) {
				if(shouldDoTurnWork()) {
					doTurnWork();
					turn_action_amount--;
					action_time = 0;
				}
				else
					action_time++;
			}
			else if(hasWork()) {
				if(shouldDoWork()) {
					doWork();
					action_time = 0;
				}
				else
					action_time++;
			}
			else
				turn_time++;
			setChanged();
		}
	}
}