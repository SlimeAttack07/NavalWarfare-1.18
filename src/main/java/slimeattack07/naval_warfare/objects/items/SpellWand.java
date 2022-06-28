package slimeattack07.naval_warfare.objects.items;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import slimeattack07.naval_warfare.NavalWarfare;
import slimeattack07.naval_warfare.config.NavalWarfareConfig;
import slimeattack07.naval_warfare.init.NWBlocks;
import slimeattack07.naval_warfare.objects.blocks.Board;
import slimeattack07.naval_warfare.objects.blocks.BoardRedirect;
import slimeattack07.naval_warfare.objects.blocks.GameController;
import slimeattack07.naval_warfare.objects.blocks.ShipBlock;
import slimeattack07.naval_warfare.tileentity.BoardTE;
import slimeattack07.naval_warfare.tileentity.GameControllerTE;
import slimeattack07.naval_warfare.util.BoardState;
import slimeattack07.naval_warfare.util.NWBasicMethods;
import slimeattack07.naval_warfare.util.Spell;
import slimeattack07.naval_warfare.util.abilities.Ability;
import slimeattack07.naval_warfare.util.abilities.EnergyShield;
import slimeattack07.naval_warfare.util.abilities.Heatseaker;
import slimeattack07.naval_warfare.util.abilities.Raft;
import slimeattack07.naval_warfare.util.abilities.Sonar;

public class SpellWand extends Item{

	public SpellWand() {
		super(new Item.Properties().tab(NavalWarfare.NAVAL_WARFARE).stacksTo(1));
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player playerIn, InteractionHand handIn) {
		ItemStack stack = playerIn.getItemInHand(handIn);
		if(level.isClientSide())
			return InteractionResultHolder.success(stack);
		
		boolean backwards = playerIn == null ? false : playerIn.isCrouching();
		
		cycleMode(stack, backwards);
		NWBasicMethods.messagePlayerActionbarBack(playerIn, "descriptions.naval_warfare.spell", ": " + getSpell(stack).getName());
		return InteractionResultHolder.success(stack);
	}
	
	private void cycleMode(ItemStack stack, boolean backwards) {
		if(stack.getItem() instanceof SpellWand) {
			prepare(stack);
			CompoundTag nbt = stack.getOrCreateTag();
			CompoundTag nw = nbt.getCompound(NavalWarfare.MOD_ID);
			
			try {
				Spell spell = Spell.valueOf(nw.getString("spell").toUpperCase());
				
				if(spell != null) {
					spell = spell.cycle(backwards);
					nw.remove("spell");
					nw.putString("spell", spell.toString());
				}
			} catch(IllegalArgumentException e) {
				return;
			}	
		}
	}
	
	public Spell getSpell(ItemStack stack) {
		if(stack.getItem() instanceof SpellWand) {
			try {
			prepare(stack);
			CompoundTag nbt = stack.getOrCreateTag();
			CompoundTag nw = nbt.getCompound(NavalWarfare.MOD_ID);
			
			return Spell.valueOf(nw.getString("spell"));
			} catch(IllegalArgumentException e) {
				return null;
			}
		}
		
		return null;
	}
	
	
	@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flagIn) {
		prepare(stack);
		CompoundTag nbt = stack.getOrCreateTag();
		CompoundTag nw = nbt.getCompound(NavalWarfare.MOD_ID);
		String s = nw.getString("spell");
		try {
			Spell spell = Spell.valueOf(s.toUpperCase());
			tooltip.add(NWBasicMethods.createGrayText(spell.getName()));
		} catch(IllegalArgumentException e) {
			tooltip.add(NWBasicMethods.createGrayText(s));
		}
		
		super.appendHoverText(stack, level, tooltip, flagIn);
	}
	
	private void prepare(ItemStack stack) {
		CompoundTag nbt = stack.getOrCreateTag();
		if(!nbt.contains(NavalWarfare.MOD_ID)) {
			CompoundTag nw = new CompoundTag();
			ListTag highlighted = new ListTag();
			
			nw.putString("spell", Spell.RAFT.toString());
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
		
		if(state.getBlock() instanceof Board) {
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
					NWBasicMethods.messagePlayerActionbar(player, "spell.naval_warfare.corrupt");
				}
			}	
		}
		
		else if(state.getBlock() instanceof GameController) {			
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
						if(cte.hasSpell()) {
							if(!controller.activateActiveAbility(level, player, pos, te, ability, stack, true))
								NWBasicMethods.messagePlayerActionbar(player, "ability.naval_warfare.error");
						}
						else
							NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.no_spell");
					}
					else
						NWBasicMethods.messagePlayerActionbar(player, "spell.naval_warfare.corrupt");
					
					deselect(level, stack);
				}
				else
					NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.board_corrupt");
				
			}
		}
			
		return InteractionResult.SUCCESS;
	}
	
	@Nullable
	public static Ability spellToAbility(Spell spell) {
		if(spell == null)
			return null;
		
		switch(spell) {
		case SHIELD:
			double width = (NavalWarfareConfig.magic_shield_width.get() + 1) / 2d;
			double length = (NavalWarfareConfig.magic_shield_length.get() + 1) / 2d;
			NavalWarfare.LOGGER.info("cwidth is " + NavalWarfareConfig.magic_shield_width.get());
			NavalWarfare.LOGGER.info("width, length is " + width + ", " + length);
			return new EnergyShield(1, 0, "magic_shield", "spell_shield", NavalWarfareConfig.magic_shield_health.get(), (int) Math.ceil(length), 
					(int) Math.floor(length), (int) Math.ceil(width), (int) Math.floor(width));
		case HEATSEAKER:
			return new Heatseaker();
		case RAFT:
			return new Raft((ShipBlock) NWBlocks.RAFT.get());
		case SONAR:
			return new Sonar();
			default: 
				return null;
		}
	}
		
	public Ability getAbility(Level level, ItemStack stack) {
		if(stack.getItem() instanceof SpellWand) {
			prepare(stack);
			CompoundTag nbt = stack.getOrCreateTag();
			CompoundTag nw = nbt.getCompound(NavalWarfare.MOD_ID);
			try {
				Spell spell = Spell.valueOf(nw.getString("spell").toUpperCase());
				return spellToAbility(spell);
			} catch(IllegalArgumentException e) {
				return null;
			}
		}
		
		return null;
	}
	
	public BlockPos getOrigin(ItemStack stack) {
		if(stack.getItem() instanceof SpellWand) {
			prepare(stack);
			CompoundTag nbt = stack.getOrCreateTag();
			CompoundTag nw = nbt.getCompound(NavalWarfare.MOD_ID);
			CompoundTag origin = nw.getCompound("origin");
			return new BlockPos(origin.getInt("x"), origin.getInt("y"), origin.getInt("z"));
		}
		
		return null;
	}
	
	public void select(Level level, ItemStack stack) {
		if(stack.getItem() instanceof SpellWand) {
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
		if(stack.getItem() instanceof SpellWand) {
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
		if(stack.getItem() instanceof SpellWand) {
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
	
	public void setNBT(ItemStack stack, String ability) {
		if(stack.getItem() instanceof SpellWand) {
			prepare(stack);
			CompoundTag nbt = stack.getOrCreateTag();
			CompoundTag nw = nbt.getCompound(NavalWarfare.MOD_ID);
			nw.remove("spell");
			nw.putString("spell", NWBasicMethods.getTranslation(ability));
		}
	}
}
