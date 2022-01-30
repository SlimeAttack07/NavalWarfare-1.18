package slimeattack07.naval_warfare.objects.items;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import slimeattack07.naval_warfare.NavalWarfare;
import slimeattack07.naval_warfare.config.NavalWarfareConfig;
import slimeattack07.naval_warfare.objects.blocks.GameController;
import slimeattack07.naval_warfare.util.NWBasicMethods;
import slimeattack07.naval_warfare.util.helpers.ShipSaveHelper;

public class ShipConfiguration extends Item {
	public static int MAX_HEALTH_ALLOWED = NavalWarfareConfig.max_fleet_hp.get(); // Default: 24

	public ShipConfiguration() {
		super(new Item.Properties().tab(NavalWarfare.NAVAL_WARFARE).stacksTo(1));
	}
	
	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		
		if(level.isClientSide())
			return InteractionResult.SUCCESS;
		
		BlockPos pos = context.getClickedPos();
		BlockState state = level.getBlockState(pos);
		
		if(state.getBlock() instanceof GameController) {
			GameController controller = (GameController) state.getBlock();
			controller.handleShipConfig(state, level, pos, context.getPlayer(), context.getItemInHand());
		}
		
		return InteractionResult.SUCCESS;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flagIn) {
		prepare(stack);

		CompoundTag nbt = stack.getOrCreateTag();
		CompoundTag nw = nbt.getCompound(NavalWarfare.MOD_ID);
		tooltip.add(NWBasicMethods.createGrayText("descriptions.naval_warfare.ship_config_name", ": " + stack.getDisplayName().getString().
				replace("[", "").replace("]", "")));
		tooltip.add(NWBasicMethods.createGrayText("descriptions.naval_warfare.ship_config_board_size", ": " + nw.getInt("board_size")));
		tooltip.add(NWBasicMethods.createGrayText("descriptions.naval_warfare.ship_config_hp", ": " + nw.getInt("hp")));
		tooltip.add(NWBasicMethods.createGrayText("descriptions.naval_warfare.ship_config_ships", ": "));
		
		for(String name : getShipNames(readShips(nw)))
			tooltip.add(NWBasicMethods.createGreenText(name));
			
		super.appendHoverText(stack, level, tooltip, flagIn);
	}
	
	public boolean prepare(ItemStack stack) {
		if(stack.getItem() instanceof ShipConfiguration) {
			CompoundTag nbt = stack.getOrCreateTag();

			if(!nbt.contains(NavalWarfare.MOD_ID)) {		
				nbt.put(NavalWarfare.MOD_ID, saveData(-1, new ArrayList<>(), Direction.NORTH));
				stack.setTag(nbt);
			}
			
			return true;
		}
		
		return false;
	}
	
	public ArrayList<String> getShipNames(ArrayList<ShipSaveHelper> sshl) {
		ArrayList<String> names = new ArrayList<>();
		
		for(ShipSaveHelper ssh : sshl) {
			Block block = ForgeRegistries.BLOCKS.getValue(ssh.getShip());
			
			if(block != null)
				names.add(NWBasicMethods.getTranslation(block));
		}
		
		return names;
	}
	
	public ArrayList<ShipSaveHelper> readShips(CompoundTag nbt){
		ListTag ships = nbt.getList("ships", Tag.TAG_COMPOUND);
		ArrayList<ShipSaveHelper> ssh = new ArrayList<>();
		
		for(Tag inbt : ships) {
			CompoundTag ship = (CompoundTag) inbt;
			ssh.add(new ShipSaveHelper(ship.getString("ship"), ship.getInt("pos"), ship.getString("facing"), ship.getInt("hp")));
		}
		
		return ssh;
	}
	
	public boolean isValid(ItemStack stack) {
		if(!prepare(stack))
			return false;
		CompoundTag nbt = stack.getOrCreateTag();
		CompoundTag nw = nbt.getCompound(NavalWarfare.MOD_ID);
		
		return nw.getInt("board_size") >= 0;
	}
	
	public void saveShipConfiguration(ItemStack stack, int board_size, ArrayList<ShipSaveHelper> ships, Direction dir) {
		if(prepare(stack)) {
			stack.setTag(saveData(board_size, ships, dir));
		}
	}
	
	private CompoundTag saveData(int board_size, ArrayList<ShipSaveHelper> ships, Direction facing) {
		CompoundTag nbt = new CompoundTag();
		CompoundTag nw = new CompoundTag();
		ListTag ship_list = new ListTag();
		
		nw.putInt("board_size", board_size);
		int hp;
		
		if(ships.isEmpty())
			hp = -1;
		else
			hp = 0;
		
		for(ShipSaveHelper ssh : ships) {
			CompoundTag ship = new CompoundTag();
			ship.putString("ship", ssh.getShip().toString());
			ship.putInt("pos", ssh.getPos());
			ship.putString("facing", ssh.getDir().toString().toUpperCase());
			ship.putInt("hp", ssh.getHP());
			ship_list.add(ship);
			hp += ssh.getHP();
		}
		
		nw.putInt("hp", hp);
		nw.put("ships", ship_list);
		nw.putString("facing", facing.name().toUpperCase());
		nbt.put(NavalWarfare.MOD_ID, nw);

		return nbt;
	}
}
