package slimeattack07.naval_warfare.objects.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import slimeattack07.naval_warfare.init.NWBlocks;
import slimeattack07.naval_warfare.objects.blocks.GameController;
import slimeattack07.naval_warfare.objects.blocks.ShipBlock;
import slimeattack07.naval_warfare.tileentity.GameControllerTE;
import slimeattack07.naval_warfare.util.ControllerState;
import slimeattack07.naval_warfare.util.NWBasicMethods;

public class ShipItem extends BlockItem{
	public final String TIER;
	public String name = null;
	public final boolean PLACEABLE;

	public ShipItem(Block blockIn, CreativeModeTab group, boolean placeable) {
		super(blockIn, new Item.Properties().tab(group));
		
		if(blockIn instanceof ShipBlock) {
			ShipBlock ship = (ShipBlock) blockIn;
			TIER = tierToColor(ship.getTier());
		}
		else
			TIER = "white";
		
		PLACEABLE = placeable;
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {
		return 1;
	}
	
	@Override
	public Component getName(ItemStack stack) {
		if(name == null)
			name = getRegistryName().toString().replace(":", ".");
		
		return Component.Serializer.fromJson("{\"translate\":\"block." + name +  "\",\"color\":\"" + TIER + "\"}");
	}
	
	private String tierToColor(int tier) {
		switch(tier) {
		case 1: return "gold";
		case 2: return "dark_gray";
		case 3: return "yellow";
		case 4: return "gray";
		case 5: return "aqua";
		case 6: return "red";
		default: return "black";
		}
	}
	
	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		
		if(level.isClientSide())
			return InteractionResult.SUCCESS;
		
		Player player = context.getPlayer();
		BlockPos pos = context.getClickedPos();
		BlockState state =  level.getBlockState(pos);
		String ship = getRegistryName().toString();
		
		Block block = getBlock();
		
		if(!(block instanceof ShipBlock)) 
			return InteractionResult.SUCCESS;
		
		ShipBlock shipblock = (ShipBlock) block;
		
		if(state.getBlock() instanceof GameController) {
			GameController controller = (GameController) state.getBlock();
			
			if(controller.validateController(level, pos)) {
				GameControllerTE te = (GameControllerTE) level.getBlockEntity(pos);
				
				if(!controller.getState(state).equals(ControllerState.EDIT_CONFIG) || te.board_size <= 0) {
					NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.cannot_register");
					
					return InteractionResult.SUCCESS;
				}
				
				if(!player.isCrouching()) {
					if(!PLACEABLE) {
						NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.not_usable");
						return InteractionResult.SUCCESS;
					}
					
					if(te.register(ship, player, shipblock)) 
						NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.ship_registered");
					else
						NWBasicMethods.messagePlayerActionbarBack(player, "message.naval_warfare.already_registered", ": " +
							ShipConfiguration.MAX_HEALTH_ALLOWED);
				}
				else {
					if(!PLACEABLE) {
						NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.not_usable");
						return InteractionResult.SUCCESS;
					}
					
					if(te.deregister(ship, player, shipblock)) 
						NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.ship_deregistered");
					else
						NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.not_registered");
				}
			}
			else
				NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.controller_corrupt");
			
			return InteractionResult.SUCCESS;
		}
		
		block = state.getBlock();
		
		if(context.getClickedFace().equals(Direction.UP) && (block.equals(NWBlocks.BOARD.get()) || block.equals(NWBlocks.SHIP_DISPLAY.get())))
			return super.useOn(context);
		
		NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.only_place_on_board");
		return InteractionResult.SUCCESS;
	}
}
