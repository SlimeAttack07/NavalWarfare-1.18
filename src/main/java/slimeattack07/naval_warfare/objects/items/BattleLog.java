package slimeattack07.naval_warfare.objects.items;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.PacketDistributor;
import slimeattack07.naval_warfare.NavalWarfare;
import slimeattack07.naval_warfare.network.NavalNetwork;
import slimeattack07.naval_warfare.network.message.BattleLogMessage;
import slimeattack07.naval_warfare.objects.blocks.BattleViewer;
import slimeattack07.naval_warfare.tileentity.BattleViewerTE;
import slimeattack07.naval_warfare.util.NWBasicMethods;
import slimeattack07.naval_warfare.util.helpers.NBTHelper;
import slimeattack07.naval_warfare.util.helpers.ShipSaveHelper;

public class BattleLog extends Item{

	public BattleLog() {
		super(new Item.Properties().tab(NavalWarfare.NAVAL_WARFARE).stacksTo(1));
	}
	
	@Override
	public int getItemStackLimit(ItemStack stack) {
		return 1;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flagIn) {
		prepare(stack);
		CompoundTag nbt = stack.getOrCreateTag();
		CompoundTag nw = nbt.getCompound(NavalWarfare.MOD_ID);
		
		if(nw.isEmpty())
			tooltip.add(NWBasicMethods.createGrayText("message.naval_warfare.battle_log.no_log"));
		else
			tooltip.add(NWBasicMethods.createGrayText("message.naval_warfare.battle_log.right_click"));
		
		tooltip.add(NWBasicMethods.createGrayText("message.naval_warfare.battle_log.shift_right_click"));
		
		super.appendHoverText(stack, level, tooltip, flagIn);
	}
	
	private void prepare(ItemStack stack) {
		CompoundTag nbt = stack.getOrCreateTag();
		
		if(!nbt.contains(NavalWarfare.MOD_ID)) {
			CompoundTag nw = new CompoundTag();
			nbt.put(NavalWarfare.MOD_ID, nw);
		}
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player playerIn, InteractionHand handIn) {
		ItemStack stack = playerIn.getItemInHand(handIn);
		
		if(level.isClientSide() || !(playerIn instanceof ServerPlayer))
			return InteractionResultHolder.success(stack);
		
		boolean load = playerIn.isCrouching();
		
		CompoundTag nbt = getLog(stack);
		nbt.putString("info", stack.getHoverName().getString());
		
		NavalNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) playerIn), new BattleLogMessage(nbt, load));
		
		return InteractionResultHolder.success(stack);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		
		if(level.isClientSide())
			return InteractionResult.SUCCESS;

		BlockPos pos = context.getClickedPos();
		BlockEntity tile = level.getBlockEntity(pos);
		ItemStack stack = context.getItemInHand();
		Player player = context.getPlayer();		
		
		if(tile instanceof BattleViewerTE) {
			BattleViewerTE te = (BattleViewerTE) tile;
			CompoundTag log = getLog(stack);
			
			if(!validateLog(log)) {
				NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.battle_log.load_fail");
				return InteractionResult.SUCCESS;
			}
			
			ListTag actions = log.getList("actions", Tag.TAG_COMPOUND);
			te.setActions(actions);
			BattleViewer viewer = (BattleViewer) te.getBlockState().getBlock();
			boolean spawned = viewer.spawnBoards(level, player, te, log.getInt("own_size"), log.getInt("opp_size"));
			
			if(spawned) {
				viewer.spawnShips(level, getDir(log, false), pos, getShips(log, false), viewer.getFacing(level.getBlockState(pos)), false);
				viewer.spawnShips(level, getDir(log, true), pos, getShips(log, true), viewer.getFacing(level.getBlockState(pos)), true);
				te.uuid = player == null ? null : player.getUUID();
				te.playing = true;
				NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.battle_log.load_success");
			}
		}
			
		return InteractionResult.SUCCESS;
	}
	
	private Direction getDir(CompoundTag log, boolean opponent) {
		return opponent ? Direction.valueOf(log.getString("opp_dir").toUpperCase()) : Direction.valueOf(log.getString("own_dir").toUpperCase());
	}
	
	private ArrayList<ShipSaveHelper> getShips(CompoundTag log, boolean opponent){
		ArrayList<ShipSaveHelper> ships = new ArrayList<>();
		ListTag list = opponent ? log.getList("opp_ships", Tag.TAG_COMPOUND) : log.getList("own_ships", Tag.TAG_COMPOUND);
		
		for(Tag tag : list) {
			CompoundTag ctag = (CompoundTag) tag;
			ShipSaveHelper ssh = NBTHelper.readSSH(ctag);
			
			if(ssh != null)
				ships.add(ssh);
		}
		
		return ships;
	}
	
	private boolean validateLog(CompoundTag log) {
		if(log != null && !log.isEmpty())
			return log.contains("own_size") && log.contains("opp_size") && log.contains("own_ships") && log.contains("opp_ships") && 
					log.contains("actions");
		
		return false;
	}
	
	public CompoundTag getLog(ItemStack stack) {
		if(stack.getItem() instanceof BattleLog) {
			prepare(stack);
			CompoundTag nbt = stack.getOrCreateTag();
			CompoundTag nw = nbt.getCompound(NavalWarfare.MOD_ID);
			return nw.getCompound("log");
		}
		
		return null;
	}
	
	public void setLog(ItemStack stack, CompoundTag log) {
		if(stack.getItem() instanceof BattleLog) {
			prepare(stack);
			setName(stack, log.getString("info"));
			log.remove("info");
			CompoundTag nbt = stack.getOrCreateTag();
			CompoundTag nw = nbt.getCompound(NavalWarfare.MOD_ID);
			nw.remove("log");
			nw.put("log", log);
		}
	}
	
	public void setName(ItemStack stack, String name) {
		stack.setHoverName(new TextComponent(name));
	}
}
