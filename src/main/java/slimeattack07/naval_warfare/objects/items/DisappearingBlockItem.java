package slimeattack07.naval_warfare.objects.items;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import slimeattack07.naval_warfare.NavalWarfare;

public class DisappearingBlockItem extends BlockItem{

	public DisappearingBlockItem(Block blockIn) {
		super(blockIn, new Item.Properties().tab(NavalWarfare.NW_ANIMATIONS));
	}

	@Override
	public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
		if(entity.level.isClientSide())
			return false;
		
		entity.kill();
		return true;
	}
}
