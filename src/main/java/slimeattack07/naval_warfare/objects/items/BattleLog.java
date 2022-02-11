package slimeattack07.naval_warfare.objects.items;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
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
			stack.setTag(nbt);
		}
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player playerIn, InteractionHand handIn) {
		ItemStack stack = playerIn.getItemInHand(handIn);
		
		if(level.isClientSide() || !(playerIn instanceof ServerPlayer))
			return InteractionResultHolder.success(stack);
		
		boolean load = playerIn.isCrouching();
		
		NavalNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) playerIn), new BattleLogMessage(
				getLog(stack), load, stack));
		
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
				// TODO: Add ship spawning here
				te.setPlaying(true);
				NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.battle_log.load_success");
			}
		}
			
		return InteractionResult.SUCCESS;
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
			CompoundTag nbt = stack.getOrCreateTag();
			CompoundTag nw = nbt.getCompound(NavalWarfare.MOD_ID);
			nw.remove("log");
			nw.put("log", log);
		}
	}
}
