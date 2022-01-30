package slimeattack07.naval_warfare.objects.items;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import slimeattack07.naval_warfare.NavalWarfare;
import slimeattack07.naval_warfare.init.NWBlocks;
import slimeattack07.naval_warfare.util.NWBasicMethods;

public class StarterKit extends Item{
	
	public StarterKit() {
		super(new Item.Properties().tab(NavalWarfare.NAVAL_WARFARE).stacksTo(1));
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player playerIn, InteractionHand handIn) {
		BlockPos pos = playerIn.blockPosition();
		ItemStack stack = playerIn.getItemInHand(handIn);
		
		if(level.isClientSide())
			return InteractionResultHolder.success(stack);
		
		NWBasicMethods.addOrSpawn(playerIn, new ItemStack(NWBlocks.KILGOBNET.get()), level, pos);
		NWBasicMethods.addOrSpawn(playerIn, new ItemStack(NWBlocks.MAORI.get()), level, pos);
		NWBasicMethods.addOrSpawn(playerIn, new ItemStack(NWBlocks.NIGHTHAWK.get()), level, pos);
		NWBasicMethods.addOrSpawn(playerIn, new ItemStack(NWBlocks.CYBELE.get()), level, pos);
		NWBasicMethods.addOrSpawn(playerIn, new ItemStack(NWBlocks.RAIDER.get()), level, pos);
		
		NWBasicMethods.destroyStack(stack);
		
		return InteractionResultHolder.success(ItemStack.EMPTY);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flagIn) {
		tooltip.add(NWBasicMethods.createBlueText("descriptions.naval_warfare.starter_kit"));
		tooltip.add(NWBasicMethods.createBlueText("block.naval_warfare.kilgobnet"));
		tooltip.add(NWBasicMethods.createBlueText("block.naval_warfare.maori"));
		tooltip.add(NWBasicMethods.createBlueText("block.naval_warfare.nighthawk"));
		tooltip.add(NWBasicMethods.createBlueText("block.naval_warfare.cybele"));
		tooltip.add(NWBasicMethods.createBlueText("block.naval_warfare.raider"));
		
		super.appendHoverText(stack, level, tooltip, flagIn);
	}
}
