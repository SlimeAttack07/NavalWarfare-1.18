package slimeattack07.naval_warfare.util.abilities;

import java.util.ArrayList;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import slimeattack07.naval_warfare.init.NWBlocks;
import slimeattack07.naval_warfare.init.NWItems;
import slimeattack07.naval_warfare.objects.blocks.Board;
import slimeattack07.naval_warfare.objects.blocks.ShipBlock;
import slimeattack07.naval_warfare.tileentity.BoardTE;
import slimeattack07.naval_warfare.tileentity.EnergyShieldTE;
import slimeattack07.naval_warfare.tileentity.GameControllerTE;
import slimeattack07.naval_warfare.tileentity.PassiveAbilityTE;
import slimeattack07.naval_warfare.util.BoardState;
import slimeattack07.naval_warfare.util.NWBasicMethods;
import slimeattack07.naval_warfare.util.helpers.BattleLogHelper;

public class EnergyShield implements Ability {
	private final int AMOUNT;
	private final int COST;
	private final String NAME;
	private final String OWNER;
	private final int HP;
	private final int UP;
	private final int DOWN;
	private final int LEFT;
	private final int RIGHT; // Note: affected area is (LEFT + RIGHT - 1) * (UP + DOWN - 1)
	private final boolean ACTIVE;
	
	public EnergyShield(int amount, int cost, String name, String owner, int hp, int up, int down, int left, int right) {
		NAME = name;
		OWNER = owner;
		HP = hp;
		UP = up;
		DOWN = down;
		LEFT = left;
		RIGHT = right;
		AMOUNT = amount;
		COST = cost;
		ACTIVE = true;
	}
	
	public EnergyShield(String name, String owner, int hp, int up, int down, int left, int right) {
		NAME = name;
		OWNER = owner;
		HP = hp;
		UP = up;
		DOWN = down;
		LEFT = left;
		RIGHT = right;
		AMOUNT = 1;
		COST = 0;
		ACTIVE = false;
	}
	
	@Override
	public void activate(Level level, Player player, BoardTE board) {
		ArrayList<BoardTE> tiles = getTiles(level, board);
		ArrayList<Integer> ids = new ArrayList<>();
		
		for(BoardTE te : tiles) {
			BlockPos pos = te.getBlockPos().above(4);
			
			if(!level.getBlockState(pos).getBlock().equals(NWBlocks.ENERGY_SHIELD.get()))
				level.setBlockAndUpdate(pos, NWBlocks.ENERGY_SHIELD.get().defaultBlockState());
			
			BlockEntity etile = level.getBlockEntity(pos);
			
			if(etile instanceof EnergyShieldTE) {
				EnergyShieldTE ete = (EnergyShieldTE) etile;
				ete.addOwner(OWNER);
				ete.initHP(HP);
			}
			
			if(ACTIVE) {
				Board b = (Board) te.getBlockState().getBlock();
				BoardState bstate = b.getBoardState(te.getBlockState());
				level.setBlockAndUpdate(te.getBlockPos(), te.getBlockState().setValue(Board.STATE, bstate.deselect()));
			}
			
			ids.add(te.getId());
		}
		
		if(!ids.isEmpty()) {
			Board b = (Board) board.getBlockState().getBlock();
			GameControllerTE controller = b.getController(level, board.getBlockPos());
			
			if(controller != null)
				controller.recordOnRecorders(BattleLogHelper.createSetBlocks(ids, NWBlocks.ENERGY_SHIELD.get().getRegistryName(), 4, false));
		}
	}

	@Override
	public int getAmount() {
		return AMOUNT;
	}
	
	@Override
	public int energyCost() {
		return COST;
	}

	@Override
	public String getTranslation() {
		return "abilities.naval_warfare." + NAME;
	}

	@Override
	public ArrayList<BoardTE> getTiles(Level level, BoardTE te){
		if(te == null)
			return new ArrayList<>();

		BlockPos up = te.getBlockPos().above();
		BlockState state = level.getBlockState(up);
		Direction dir = ShipBlock.getFacing(state);
		
		return te.collectTileArea(UP, DOWN, LEFT, RIGHT, dir);
	}
	
	@Override
	public PassiveType getPassiveType() {
		return ACTIVE ? PassiveType.NOT : PassiveType.START_GAME;
	}
	
	@Override
	public void detachPassive(Level level, BoardTE board) {
		ArrayList<BoardTE> tiles = getTiles(level, board);
		
		if(!tiles.isEmpty()) {
			BlockPos pos = tiles.get(0).getBlockPos().above(4);
			BlockEntity tile = level.getBlockEntity(pos);
			
			if(tile instanceof PassiveAbilityTE) {
				PassiveAbilityTE pte = (PassiveAbilityTE) tile;
				Board b = (Board) board.getBlockState().getBlock();
				pte.destroy(level, pos, OWNER, b.getController(level, board.getBlockPos()), 4);
			}
		}
	}
	
	@Override
	public MutableComponent hoverableInfo() {
		String name = ChatFormatting.GREEN + NWBasicMethods.getTranslation(getTranslation()) + ChatFormatting.WHITE;
		String hover = ACTIVE ?  NWBasicMethods.getTranslation("ability.naval_warfare.energy_cost") + ": " + COST + ", " : "";
		hover += NWBasicMethods.getTranslation("misc.naval_warfare.length") + ": " + (LEFT + RIGHT - 1) + ", ";
		hover += NWBasicMethods.getTranslation("misc.naval_warfare.width") + ": " + (UP + DOWN - 1) + ", ";
		hover += NWBasicMethods.getTranslation("misc.naval_warfare.health") + ": " + HP;
		
		return NWBasicMethods.hoverableText(name, "gray", hover);
	}
	
	@Override
	public boolean canBeDisabled() {
		return !ACTIVE;
	}
	
	@Override
	public boolean targetDefensive() {
		return true;
	}
	
	@Override
	public Item getAnimationItem() {
		return ACTIVE ? NWItems.SHIELD_PLACER.get() : NWBlocks.ENERGY_SHIELD.get().asItem();
	}
}
