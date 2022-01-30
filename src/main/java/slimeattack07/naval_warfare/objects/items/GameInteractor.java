package slimeattack07.naval_warfare.objects.items;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import slimeattack07.naval_warfare.NavalWarfare;
import slimeattack07.naval_warfare.objects.blocks.BoardRedirect;
import slimeattack07.naval_warfare.objects.blocks.GameController;
import slimeattack07.naval_warfare.objects.blocks.ShipBlock;
import slimeattack07.naval_warfare.util.InteractorMode;
import slimeattack07.naval_warfare.util.InteractorType;
import slimeattack07.naval_warfare.util.NWBasicMethods;

public class GameInteractor extends Item{
	public final InteractorType type;

	public GameInteractor(InteractorType type) {
		super(new Item.Properties().tab(NavalWarfare.NAVAL_WARFARE).stacksTo(1));
		this.type = type;
	}
	
	public InteractorType getType() {
		return type;
	}
	
	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		
		if(level.isClientSide())
			return InteractionResult.SUCCESS;
		
		BlockPos pos = context.getClickedPos();
		BlockState state = level.getBlockState(pos);
		ItemStack stack = context.getItemInHand();
		Player player = context.getPlayer();		
		
		if(((GameInteractor) stack.getItem()).getType().equals(InteractorType.DUMMY))
			player = null;
		
		if(state.getBlock() instanceof GameController) {
			GameController controller = (GameController) state.getBlock();
			controller.handleInteraction(state, level, pos, player, stack);
			return InteractionResult.SUCCESS;
		}
		
		if(state.getBlock() instanceof ShipBlock) {
			pos = pos.below();
			state = level.getBlockState(pos);
		}
		
		if(state.getBlock() instanceof BoardRedirect) {
			BoardRedirect board = (BoardRedirect) state.getBlock();
			board.handleInteraction(state, level, pos, player, stack, pos);
			return InteractionResult.SUCCESS;
		}
		
		return InteractionResult.SUCCESS;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player playerIn, InteractionHand handIn) {
		ItemStack stack = playerIn.getItemInHand(handIn);
		
		if(level.isClientSide())
			return InteractionResultHolder.success(stack);
		
		cycleMode(stack);
		NWBasicMethods.messagePlayerActionbarBack(playerIn, "descriptions.naval_warfare.tool_mode", ": " + getMode(stack).getName());
		
		return InteractionResultHolder.success(stack);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flagIn) {
		prepare(stack);
		CompoundTag nbt = stack.getOrCreateTag();
		CompoundTag nw = nbt.getCompound(NavalWarfare.MOD_ID);
		tooltip.add(NWBasicMethods.createGrayText("descriptions.naval_warfare.tool_mode", ": " +
				InteractorMode.valueOf(nw.getString("mode")).getName()));
		
		super.appendHoverText(stack, level, tooltip, flagIn);
	}
	
	private void prepare(ItemStack stack) {
		CompoundTag nbt = stack.getOrCreateTag();
		
		if(!nbt.contains(NavalWarfare.MOD_ID)) {
			CompoundTag nw = new CompoundTag();
			nw.putString("mode", InteractorMode.NEW_SHIP_CONFIG.toString());
			nbt.put(NavalWarfare.MOD_ID, nw);
			stack.setTag(nbt);
		}
	}
	
	public InteractorMode getMode(ItemStack stack) {
		if(stack.getItem() instanceof GameInteractor) {
			prepare(stack);
			CompoundTag nbt = stack.getOrCreateTag();
			CompoundTag nw = nbt.getCompound(NavalWarfare.MOD_ID);
			return InteractorMode.valueOf(nw.getString("mode"));
		}
		
		return InteractorMode.NEW_SHIP_CONFIG;
	}
	
	private void cycleMode(ItemStack stack) {
		if(stack.getItem() instanceof GameInteractor) {
			prepare(stack);
			CompoundTag nbt = stack.getOrCreateTag();
			CompoundTag nw = nbt.getCompound(NavalWarfare.MOD_ID);
			String old_mode = nw.getString("mode");
			InteractorMode mode = InteractorMode.valueOf(old_mode);
			
			if(mode != null)
				mode = mode.cycle();
			nw.remove("mode");
			nw.putString("mode", mode.toString());
		}
	}
	
	public void setMode(ItemStack stack, InteractorMode mode) {
		if(stack.getItem() instanceof GameInteractor) {
			prepare(stack);
			CompoundTag nbt = stack.getOrCreateTag();
			CompoundTag nw = nbt.getCompound(NavalWarfare.MOD_ID);
			nw.remove("mode");
			nw.putString("mode", mode.toString());
		}
	}
}
