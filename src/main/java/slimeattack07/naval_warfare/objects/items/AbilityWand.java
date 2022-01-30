package slimeattack07.naval_warfare.objects.items;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import slimeattack07.naval_warfare.NavalWarfare;
import slimeattack07.naval_warfare.objects.blocks.Board;
import slimeattack07.naval_warfare.objects.blocks.BoardRedirect;
import slimeattack07.naval_warfare.objects.blocks.GameController;
import slimeattack07.naval_warfare.objects.blocks.ShipBlock;
import slimeattack07.naval_warfare.tileentity.BoardTE;
import slimeattack07.naval_warfare.tileentity.GameControllerTE;
import slimeattack07.naval_warfare.tileentity.ShipTE;
import slimeattack07.naval_warfare.util.BoardState;
import slimeattack07.naval_warfare.util.NWBasicMethods;
import slimeattack07.naval_warfare.util.abilities.Ability;

public class AbilityWand extends Item{

	public AbilityWand() {
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
		tooltip.add(NWBasicMethods.createGrayText(nw.getString("ability")));
		
		super.appendHoverText(stack, level, tooltip, flagIn);
	}
	
	private void prepare(ItemStack stack) {
		CompoundTag nbt = stack.getOrCreateTag();
		if(!nbt.contains(NavalWarfare.MOD_ID)) {
			CompoundTag nw = new CompoundTag();
			CompoundTag ship = new CompoundTag();
			ListTag highlighted = new ListTag();
			
			nw.putString("ability", NWBasicMethods.getTranslation("abilities.naval_warfare.none_selected"));
			nw.put("ship", ship);
			nw.put("highlighted", highlighted);
			nbt.put(NavalWarfare.MOD_ID, nw);
			stack.setTag(nbt);
		}
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
		
		if(state.getBlock() instanceof ShipBlock) {
			ShipBlock ship = (ShipBlock) state.getBlock();
			
			if(ship.validateShip(level, pos) && ship.hasActiveAbility()) {
				Ability ability = ship.ACTIVE_ABILITY;
				ShipTE te = (ShipTE) level.getBlockEntity(pos);
				
				if(!ShipBlock.getState(state).isAlive())
					NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.no_ability_allowed");
				else if(te.getActiveAmount() > 0) {
					deselect(level, stack);
					setNBT(stack, ability.getTranslation(), pos);
					NWBasicMethods.messagePlayerActionbarBack(player, "message.naval_warfare.selected", ": "
							+ NWBasicMethods.getTranslation(ability.getTranslation()));
					
					if(!ability.needsTarget()) {
						ArrayList<BlockPos> list = new ArrayList<>();
						list.add(te.getBlockPos().below());
						setHighlighted(stack, te.getBlockPos().below(), list);
					}
				}
				else
					NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.no_active_uses");
			}
			else
				NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.no_active_ability");
		}
		
		else if(state.getBlock() instanceof Board) {
			deselect(level, stack);
			Board board = (Board) state.getBlock();
			
			if(board.validateBoard(level, pos)) {
				BoardTE te = (BoardTE) level.getBlockEntity(pos);
				ArrayList<BlockPos> positions = new ArrayList<>();
				Ability ability = getAbility(level, stack);				
				
				if(ability != null) {
					if(ability.targetDefensive() && state.getBlock() instanceof BoardRedirect)
						NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.target_defensive");
					else if(!ability.targetDefensive() && !(state.getBlock() instanceof BoardRedirect)) 
						NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.target_offensive");
					else if(!board.ownsBlock(player, level, te.getController()))
						NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.not_owner_b");
					else {
						for(BoardTE bte: ability.getTiles(level, te)) {
							positions.add(bte.getBlockPos());
						}
						
						setHighlighted(stack, pos, positions);
						select(level, stack);
					}
				}
				else {
					NWBasicMethods.messagePlayerActionbar(player, "abilities.naval_warfare.none_selected");
				}
			} else
				NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.board_corrupt");
		}
		
		else if(state.getBlock() instanceof GameController) {
			if(!verifyAbilityAllowed(level, player, stack, pos))
				return InteractionResult.SUCCESS;
			
			GameController controller = (GameController) state.getBlock();
			Ability ability = getAbility(level, stack);
			
			GameControllerTE cte = (GameControllerTE) level.getBlockEntity(pos);
			
			if(cte == null) {
				NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.controller_corrupt");
				return InteractionResult.SUCCESS;
			}
			
			BlockPos origin = getOrigin(stack);
			Block block = level.getBlockState(origin).getBlock();
			
			if(block instanceof Board) {
				Board board = (Board) block;
				
				if(board.validateBoard(level, origin)) {
					BoardTE te = (BoardTE) level.getBlockEntity(origin);
					
					if(ability != null) {
						if(cte.getEnergy() >= ability.energyCost()) {
							
							if(!controller.activateActiveAbility(level, player, pos, te, ability, stack, false))
								NWBasicMethods.messagePlayerActionbar(player, "ability.naval_warfare.error");
						}
						else
							NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.not_enough_energy");
					}
					else
						NWBasicMethods.messagePlayerActionbar(player, "abilities.naval_warfare.none_selected");
					
					deselect(level, stack);
					setNBT(stack, "abilities.naval_warfare.none_selected", pos);
				}
				else
					NWBasicMethods.messagePlayerActionbar(player, "abilities.naval_warfare.none_selected");
				
			}
		}
			
		return InteractionResult.SUCCESS;
	}
	
	public boolean verifyAbilityAllowed(Level level, Player player, ItemStack stack, BlockPos controller) {
		BlockPos pos = getShip(stack);
		
		if(pos != null) {
			BlockState state = level.getBlockState(pos);
			
			if(state.getBlock() instanceof ShipBlock) {
				if(!ShipBlock.getState(state).isAlive()) {
					NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.no_ability_allowed");
					deselect(level, stack);
					setNBT(stack, "abilities.naval_warfare.none_selected", pos);
					return false;
				}
				
				BlockEntity tile = level.getBlockEntity(pos.below());
				
				if(tile instanceof BoardTE) {
					BoardTE bte = (BoardTE) tile;
					
					if(!bte.getController().equals(controller)) {
						NWBasicMethods.messagePlayer(player, "message.naval_warfare.different_controller");
						deselect(level, stack);
						setNBT(stack, "abilities.naval_warfare.none_selected", pos);
						return false;
					}
					
					tile = level.getBlockEntity(bte.getController());
					
					if(tile instanceof GameControllerTE) {
						GameControllerTE gte = (GameControllerTE) tile;
						
						if(!gte.getOwner().equals("dummy") && !gte.getOwner().equals(player.getStringUUID())) {
							NWBasicMethods.messagePlayer(player, "message.naval_warfare.not_owner_s");
							deselect(level, stack);
							setNBT(stack, "abilities.naval_warfare.none_selected", pos);
							return false;
						}
						
						return true;
					}
				}
				
				
			}
		}
		
		return false;
	}
		
	public Ability getAbility(Level level, ItemStack stack) {
		if(stack.getItem() instanceof AbilityWand) {
			prepare(stack);
			CompoundTag nbt = stack.getOrCreateTag();
			CompoundTag nw = nbt.getCompound(NavalWarfare.MOD_ID);
			CompoundTag ship = nw.getCompound("ship");
			BlockPos pos = new BlockPos(ship.getInt("x"), ship.getInt("y"), ship.getInt("z"));
			BlockState state = level.getBlockState(pos);
			
			if(state.getBlock() instanceof ShipBlock) {
				ShipBlock block = (ShipBlock) state.getBlock();
				
				if(block.validateShip(level, pos)) {
					ShipTE te = (ShipTE) level.getBlockEntity(pos);
					
					if(te.getActiveAmount() > 0)
						return block.ACTIVE_ABILITY;
				}
			}
		}
		
		return null;
	}
	
	public void decreaseActiveAmount(Level level, ItemStack stack){
		if(stack.getItem() instanceof AbilityWand) {
			prepare(stack);
			CompoundTag nbt = stack.getOrCreateTag();
			CompoundTag nw = nbt.getCompound(NavalWarfare.MOD_ID);
			CompoundTag ship = nw.getCompound("ship");
			BlockPos pos = new BlockPos(ship.getInt("x"), ship.getInt("y"), ship.getInt("z"));
			BlockState state = level.getBlockState(pos);
			
			if(state.getBlock() instanceof ShipBlock) {
				ShipBlock block = (ShipBlock) state.getBlock();
				
				if(block.validateShip(level, pos)) {
					ShipTE te = (ShipTE) level.getBlockEntity(pos);
					block.propagateAbilityAmount(level, pos, te.getActiveAmount() - 1);
				}
			}
		}
		
		return;
	}
	
	public BlockPos getOrigin(ItemStack stack) {
		if(stack.getItem() instanceof AbilityWand) {
			prepare(stack);
			CompoundTag nbt = stack.getOrCreateTag();
			CompoundTag nw = nbt.getCompound(NavalWarfare.MOD_ID);
			CompoundTag origin = nw.getCompound("origin");
			return new BlockPos(origin.getInt("x"), origin.getInt("y"), origin.getInt("z"));
		}
		
		return null;
	}
	
	public BlockPos getShip(ItemStack stack) {
		if(stack.getItem() instanceof AbilityWand) {
			prepare(stack);
			CompoundTag nbt = stack.getOrCreateTag();
			CompoundTag nw = nbt.getCompound(NavalWarfare.MOD_ID);
			CompoundTag ship = nw.getCompound("ship");
			return new BlockPos(ship.getInt("x"), ship.getInt("y"), ship.getInt("z"));
		}
		
		return null;
	}
	
	public void select(Level level, ItemStack stack) {
		if(stack.getItem() instanceof AbilityWand) {
			prepare(stack);
			CompoundTag nbt = stack.getOrCreateTag();
			CompoundTag nw = nbt.getCompound(NavalWarfare.MOD_ID);
			ListTag list = nw.getList("highlighted", Tag.TAG_COMPOUND);
			
			for(Tag inbt : list) {
				CompoundTag cnbt = (CompoundTag) inbt;
				BlockPos pos = new BlockPos(cnbt.getInt("x"), cnbt.getInt("y"), cnbt.getInt("z"));
				BlockState state = level.getBlockState(pos);
				
				if(state.getBlock() instanceof Board) {
					Board board = (Board) state.getBlock();
					BoardState bstate = board.getBoardState(state);
					level.setBlockAndUpdate(pos, state.setValue(Board.STATE, bstate.select()));
				}
			}
		}
	}
	
	public void deselect(Level level, ItemStack stack) {
		if(stack.getItem() instanceof AbilityWand) {
			prepare(stack);
			CompoundTag nbt = stack.getOrCreateTag();
			CompoundTag nw = nbt.getCompound(NavalWarfare.MOD_ID);
			ListTag list = nw.getList("highlighted", Tag.TAG_COMPOUND);
			
			for(Tag inbt : list) {
				CompoundTag cnbt = (CompoundTag) inbt;
				BlockPos pos = new BlockPos(cnbt.getInt("x"), cnbt.getInt("y"), cnbt.getInt("z"));
				BlockState state = level.getBlockState(pos);
				
				if(state.getBlock() instanceof Board) {
					Board board = (Board) state.getBlock();
					BoardState bstate = board.getBoardState(state);
					level.setBlockAndUpdate(pos, state.setValue(Board.STATE, bstate.deselect()));
				}
			}
			
			setHighlighted(stack, null, new ArrayList<>());
		}
	}
	
	public void setHighlighted(ItemStack stack, BlockPos origin, ArrayList<BlockPos> highlighted) {
		if(stack.getItem() instanceof AbilityWand) {
			prepare(stack);
			CompoundTag nbt = stack.getOrCreateTag();
			CompoundTag nw = nbt.getCompound(NavalWarfare.MOD_ID);
			nw.remove("origin");
			nw.remove("highlighted");
			
			if(origin != null) {
				CompoundTag originnbt = new CompoundTag();
				originnbt.putInt("x", origin.getX());
				originnbt.putInt("y", origin.getY());
				originnbt.putInt("z", origin.getZ());
				nw.put("origin", originnbt);
			}
			
			ListTag ListTag = new ListTag();
			
			for(BlockPos pos : highlighted) {
				CompoundTag pnbt = new CompoundTag();
				pnbt.putInt("x", pos.getX());
				pnbt.putInt("y", pos.getY());
				pnbt.putInt("z", pos.getZ());
				ListTag.add(pnbt);
			}
			
			nw.put("highlighted", ListTag);
		}
	}
	
	public void setNBT(ItemStack stack, String ability, BlockPos ship) {
		if(stack.getItem() instanceof AbilityWand) {
			prepare(stack);
			CompoundTag nbt = stack.getOrCreateTag();
			CompoundTag nw = nbt.getCompound(NavalWarfare.MOD_ID);
			nw.remove("ability");
			nw.remove("ship"); // remember to un-highlight?
			nw.putString("ability", NWBasicMethods.getTranslation(ability));
			
			CompoundTag shipnbt = new CompoundTag();
			shipnbt.putInt("x", ship.getX());
			shipnbt.putInt("y", ship.getY());
			shipnbt.putInt("z", ship.getZ());
			nw.put("ship", shipnbt);
		}
	}
}
