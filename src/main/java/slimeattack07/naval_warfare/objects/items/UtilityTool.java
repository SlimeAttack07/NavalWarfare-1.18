package slimeattack07.naval_warfare.objects.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import slimeattack07.naval_warfare.NavalWarfare;
import slimeattack07.naval_warfare.init.NWBlocks;
import slimeattack07.naval_warfare.objects.blocks.Board;
import slimeattack07.naval_warfare.objects.blocks.GameController;
import slimeattack07.naval_warfare.objects.blocks.ShipBlock;
import slimeattack07.naval_warfare.tileentity.BoardTE;
import slimeattack07.naval_warfare.tileentity.GameControllerTE;
import slimeattack07.naval_warfare.util.ControllerState;
import slimeattack07.naval_warfare.util.NWBasicMethods;
import slimeattack07.naval_warfare.util.UtilityMode;
import slimeattack07.naval_warfare.util.abilities.Seaworthy;
import slimeattack07.naval_warfare.util.helpers.ShipSaveHelper;

public class UtilityTool extends Item{
	public UtilityTool() {
		super(new Item.Properties().tab(NavalWarfare.NAVAL_WARFARE).stacksTo(1));
	}
	
	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		
		if(level.isClientSide())
			return InteractionResult.SUCCESS;
		
		BlockPos pos = context.getClickedPos();
		BlockState state = level.getBlockState(pos);
		Player player = context.getPlayer();		
		UtilityMode mode = getMode(context.getItemInHand());
		
		if(state.getBlock() instanceof GameController) {
			if(!validateInteraction(level, pos)) {
				NWBasicMethods.messagePlayer(player, "message.naval_warfare.utility_tool.only_in_edit");
				return InteractionResult.SUCCESS;
			}
			
			BlockEntity tile = level.getBlockEntity(pos);
			
			if(tile instanceof GameControllerTE) {
				GameControllerTE gte = (GameControllerTE) tile;
				
				if(gte.hasRegInBuffer()) {
					NWBasicMethods.messagePlayer(player, "message.naval_warfare.still_randomizing");
					return InteractionResult.SUCCESS;
				}

				tile = level.getBlockEntity(gte.getZero());
				
				if(tile instanceof BoardTE) {
					BoardTE bte = (BoardTE) tile;
					handleInteractionController(level, mode, gte, bte, player);
				}
			}
			
			return InteractionResult.SUCCESS;
		}
		
		if(state.getBlock() instanceof ShipBlock) {
			if(!validateInteraction(level, pos)) {
				NWBasicMethods.messagePlayer(player, "message.naval_warfare.utility_tool.only_in_edit");
				return InteractionResult.SUCCESS;
			}
			
			if(!mode.equals(UtilityMode.RANDOMIZE_PLACEMENT_ONE))
				return InteractionResult.SUCCESS;
			
			BlockEntity tile = level.getBlockEntity(pos.below());
			
			if(tile instanceof BoardTE) {
				BoardTE bte = (BoardTE) tile;
				tile = level.getBlockEntity(bte.getController());
				
				if(tile instanceof GameControllerTE) {
					GameControllerTE gte = (GameControllerTE) tile;
					level.removeBlock(pos, true);
					gte.addRegisterToBuffer(state.getBlock().getRegistryName().toString());		
				}
			}
		}
		
		return InteractionResult.SUCCESS;
	}
	
	private boolean validateInteraction(Level level, BlockPos pos) {
		BlockPos c_pos = pos;
		BlockState state = level.getBlockState(c_pos);
		
		if(state.getBlock() instanceof ShipBlock) {
			c_pos = pos.below();
			state = level.getBlockState(c_pos);
		}
		
		if(state.getBlock().equals(NWBlocks.BOARD.get())) {
			BlockEntity tile = level.getBlockEntity(c_pos);
			
			if(tile instanceof BoardTE) {
				BoardTE te = (BoardTE) tile;
				c_pos = te.getController();
				state = level.getBlockState(c_pos);
			}
		}
		
		if(state.getBlock() instanceof GameController) {
			GameController c = (GameController) state.getBlock();
			
			return c.getState(state).equals(ControllerState.EDIT_CONFIG);
		}
		
		return false;
	}
	
	private void handleInteractionController(Level level, UtilityMode mode, GameControllerTE gte, BoardTE bte, Player player) {
		Board board = (Board) bte.getBlockState().getBlock();
		Direction facing = board.getControllerFacing(level, bte.getBlockPos());
		
		switch(mode) {
		case RANDOMIZE_PLACEMENT_ALL: {
			ArrayList<ShipSaveHelper> ships = bte.collectShips(level, bte.getBlockPos(), board.getControllerFacing(level, bte.getBlockPos()), player);
			
			if(ships == null)
				return;
			
			for(ShipSaveHelper ssh : ships) {
				BlockPos ssh_pos = bte.locateId(bte.getBlockPos(), ssh.getPos(), facing);
				
				if(ssh_pos != null) {
					ssh_pos = ssh_pos.above();					
					level.removeBlock(ssh_pos, true);
				}
			}
			
			for(ShipSaveHelper ssh : ships)
				gte.addRegisterToBuffer(ssh.getShip().toString());
		}
			break;
		case PLACE_RANDOM_SHIP:{ 
			String ship = returnRandomShip(gte.getHP(), gte.getRegistered(), player, true);
			
			if(ship != null) {
				int extra_tiles = regnameToExtraTiles(ship);
				
				if(extra_tiles > 0) {
					gte.setBoardSize(gte.getBoardSize() + extra_tiles);
					gte.resetBoard(player);
				}
				
				gte.addRegisterToBuffer(ship);
			}
		}
			break;
		case FILL_WITH_RANDOM_SHIPS: {
			int hp = gte.getHP();
			int extra_tiles = 0;
			ArrayList<String> registered = new ArrayList<>(gte.getRegistered());
			ArrayList<String> ships = new ArrayList<>();
			
			for (int i = 0; i < 1000; i++) {
				String ship = returnRandomShip(hp, registered, player, false);
				
				if(ship != null) {
					ships.add(ship);
					registered.add(ship);
					hp += regnameToHP(ship);
					extra_tiles += regnameToExtraTiles(ship);
				}else {
					if (ships.isEmpty()){
						if(player.isCreative() && player.isCrouching())
							NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.utility_tool.failed_ship_gen");
						else
							NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.utility_tool.no_valid_ships");
					}else {
						if(extra_tiles > 0) {
							gte.setBoardSize(gte.getBoardSize() + extra_tiles);
							gte.resetBoard(player);
						}
						
						for(String s : ships)
							gte.addRegisterToBuffer(s);
					}
					
					break;
				}
			}
		}
			break;
		default:
			break;
		}
	}
	
	@Nullable
	private String returnRandomShip(int hp, ArrayList<String> registered, Player player, boolean message) {
		boolean creative = player.isCreative() && player.isCrouching();
		
		if(creative) {
			ArrayList<String> valid = fetchValidShips(hp, registered);
			
			if(!valid.isEmpty()) {
				ThreadLocalRandom rand = ThreadLocalRandom.current();
				int i = rand.nextInt(valid.size());
				
				return valid.get(i);
			}	
			
			if(message)
				NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.utility_tool.failed_ship_gen");
		}
		else {
			ArrayList<ItemStack> ships = new ArrayList<>();
			
			for(ItemStack stack : player.getInventory().items)
				if(stack.getItem().getTags().contains(new ResourceLocation(NavalWarfare.MOD_ID, "ships"))) {
					ships.add(stack);
			}
			
			ships = filterInvalidShips(hp, ships, registered);
			
			if(!ships.isEmpty()) {
				ThreadLocalRandom rand = ThreadLocalRandom.current();
				int i = rand.nextInt(ships.size());
				return ships.get(i).getItem().getRegistryName().toString();
			}
			
			if(message)
				NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.utility_tool.no_valid_ships");
		}
		
		return null;
	}
	
	private int regnameToHP(String name) {
		Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(name));
		
		if(block != null && block instanceof ShipBlock) {
			ShipBlock ship = (ShipBlock) block;
			
			return ship.getMaxHP();
		}
		
		return 0;
	}
	
	private int regnameToExtraTiles(String name) {
		Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(name));
		
		if(block != null && block instanceof ShipBlock) {
			ShipBlock ship = (ShipBlock) block;
			
			return ship.hasPassiveAbility() && ship.PASSIVE_ABILITY instanceof Seaworthy ? ship.PASSIVE_ABILITY.getAmount() : 0;
		}
		
		return 0;
	}
	
	private ArrayList<ItemStack> filterInvalidShips(int hp, ArrayList<ItemStack> ships, ArrayList<String> registered){
		ArrayList<ItemStack> valid = new ArrayList<>();	
		int max_hp_allowed = ShipConfiguration.MAX_HEALTH_ALLOWED - hp;
		max_hp_allowed = Math.min(max_hp_allowed, 5);
		String tag = NavalWarfare.MOD_ID + ":ships_hp";
		ResourceLocation t1 = new ResourceLocation(NavalWarfare.MOD_ID, "ships_t1");
		
		for(ItemStack stack : ships) {
			if(registered.contains(stack.getItem().getRegistryName().toString()))
				continue;
			
			Set<ResourceLocation> tags = stack.getItem().getTags();
			
			for(int i = 2; i <= max_hp_allowed; i++) {
				if(tags.contains(new ResourceLocation(tag + i)) && !tags.contains(t1)) {
					valid.add(stack);
					break;
				}
			}
		}
		
		return valid;
	}
	
	private ArrayList<String> fetchValidShips(int hp, ArrayList<String> registered){
		ArrayList<String> valid = new ArrayList<>();
		int max_hp_allowed = ShipConfiguration.MAX_HEALTH_ALLOWED - hp;
		max_hp_allowed = Math.min(max_hp_allowed, 5);
		String tag = NavalWarfare.MOD_ID + ":ships_hp";
		ResourceLocation t1 = new ResourceLocation(NavalWarfare.MOD_ID, "ships_t1");
		
		for(RegistryObject<Block> regob : NWBlocks.NW_SHIPS.getEntries()) {
			String name = regob.get().getRegistryName().toString();
			
			if(registered.contains(name))
				continue;
			
			Set<ResourceLocation> tags = regob.get().getTags();
			
			for(int i = 2; i <= max_hp_allowed; i++) {
				if(tags.contains(new ResourceLocation(tag + i)) && !tags.contains(t1)) {
					valid.add(name);
					break;
				}
			}
		}
		
		return valid;
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
				UtilityMode.valueOf(nw.getString("mode")).getName()));
		
		super.appendHoverText(stack, level, tooltip, flagIn);
	}
	
	private void prepare(ItemStack stack) {
		CompoundTag nbt = stack.getOrCreateTag();
		
		if(!nbt.contains(NavalWarfare.MOD_ID)) {
			CompoundTag nw = new CompoundTag();
			nw.putString("mode", UtilityMode.RANDOMIZE_PLACEMENT_ONE.toString());
			nbt.put(NavalWarfare.MOD_ID, nw);
			stack.setTag(nbt);
		}
	}
	
	public UtilityMode getMode(ItemStack stack) {
		if(stack.getItem() instanceof UtilityTool) {
			prepare(stack);
			CompoundTag nbt = stack.getOrCreateTag();
			CompoundTag nw = nbt.getCompound(NavalWarfare.MOD_ID);
			return UtilityMode.valueOf(nw.getString("mode"));
		}
		
		return UtilityMode.RANDOMIZE_PLACEMENT_ONE;
	}
	
	private void cycleMode(ItemStack stack) {
		if(stack.getItem() instanceof UtilityTool) {
			prepare(stack);
			CompoundTag nbt = stack.getOrCreateTag();
			CompoundTag nw = nbt.getCompound(NavalWarfare.MOD_ID);
			String old_mode = nw.getString("mode");
			UtilityMode mode = UtilityMode.valueOf(old_mode);
			
			if(mode != null)
				mode = mode.cycle();
			nw.remove("mode");
			nw.putString("mode", mode.toString());
		}
	}
	
	public void setMode(ItemStack stack, UtilityMode mode) {
		if(stack.getItem() instanceof UtilityTool) {
			prepare(stack);
			CompoundTag nbt = stack.getOrCreateTag();
			CompoundTag nw = nbt.getCompound(NavalWarfare.MOD_ID);
			nw.remove("mode");
			nw.putString("mode", mode.toString());
		}
	}
}
