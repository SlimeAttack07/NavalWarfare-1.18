package slimeattack07.naval_warfare.util.abilities;

import java.util.ArrayList;
import java.util.Collections;

import com.google.common.collect.Lists;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import slimeattack07.naval_warfare.objects.blocks.Board;
import slimeattack07.naval_warfare.objects.blocks.ShipBlock;
import slimeattack07.naval_warfare.tileentity.BoardTE;
import slimeattack07.naval_warfare.tileentity.GameControllerTE;
import slimeattack07.naval_warfare.util.BoardState;
import slimeattack07.naval_warfare.util.NWBasicMethods;
import slimeattack07.naval_warfare.util.helpers.BattleLogHelper;
import slimeattack07.naval_warfare.util.helpers.ControllerActionHelper;

public class Deployable implements Ability {
	private final int AMOUNT;
	protected final int COST;
	private final String NAME;
	protected final ShipBlock SHIP;
	protected final boolean PASSIVE;
	
	public Deployable(int amount, int cost, String name, Block ship, boolean passive) {
		NAME = name;
		AMOUNT = amount;
		COST = cost;
		SHIP = ship instanceof ShipBlock ? (ShipBlock) ship : null;
		PASSIVE = passive;
	}
	
	@Override
	public void activate(Level level, Player player, BoardTE board) {
		if(SHIP == null)
			return;
		
		ArrayList<BoardTE> tiles = getTilesReal(level, board);
		
		if(tiles.isEmpty())
			return;
		
		boolean success = false;
		boolean shifted = false;
		
		ArrayList<Direction> dirs = Lists.newArrayList(new Direction[] {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST});
		Collections.shuffle(dirs);
		
		Board b = (Board) board.getBlockState().getBlock();
		GameControllerTE controller = b.getController(level, board.getBlockPos());
		
		for(BoardTE te : tiles) {
			BlockPos pos = te.getBlockPos().above();
			
			for(Direction dir : dirs) {
				success = SHIP.summonShip(level, pos, SHIP.defaultBlockState().setValue(ShipBlock.FACING, dir), true, false);
				
				if(success) {
					if(SHIP.hasPassiveAbility() && SHIP.PASSIVE_ABILITY.getPassiveType().equals(PassiveType.DEPLOYED))
						SHIP.PASSIVE_ABILITY.activate(level, player, te);
					
					if(controller != null)
						controller.recordOnRecorders(BattleLogHelper.createDeployable(te.getId(), SHIP.getRegistryName(), dir));
					
					break;
				}
			}
			
			if(success)
				break;
			
			shifted = true;			
		}
		
		if(!success && !PASSIVE) {
			NWBasicMethods.messagePlayer(player, "message.naval_warfare.failed_deploy");
			
			if(controller != null)
				controller.addAction(ControllerActionHelper.createEnergyGain(COST, true));
		}
		
		if(shifted && !PASSIVE)
			NWBasicMethods.messagePlayer(player, "message.naval_warfare.shifted");		
				
		BoardState bstate = b.getBoardState(board.getBlockState());
		level.setBlockAndUpdate(board.getBlockPos(), board.getBlockState().setValue(Board.STATE, bstate.deselect()));
	}

	@Override
	public int getAmount() {
		return AMOUNT;
	}

	@Override
	public String getTranslation() {
		return "abilities.naval_warfare." + NAME;
	}

	@Override
	public ArrayList<BoardTE> getTiles(Level level, BoardTE te){
		if(te == null)
			return new ArrayList<>();

		ArrayList<BoardTE> tiles = new ArrayList<>();
		
		tiles.add(te);
		
		return tiles;
	}
	
	public ArrayList<BoardTE> getTilesReal(Level level, BoardTE te){
		if(te == null)
			return new ArrayList<>();

		ArrayList<BoardTE> tiles = new ArrayList<>();
		
		tiles = te.collectUnknownEmptyTilesNoMatch(false);
		tiles.remove(te);
		Collections.shuffle(tiles);
		
		if(!PASSIVE)
			tiles.add(0, te);
		
		return tiles;
	}
	
	@Override
	public MutableComponent hoverableInfo() {
		String name = ChatFormatting.GREEN + NWBasicMethods.getTranslation(getTranslation()) + ChatFormatting.WHITE;
		String hover = PASSIVE ? "" : NWBasicMethods.getTranslation("misc.naval_warfare.cost") + ": " + COST;
		
		return NWBasicMethods.hoverableText(name, "gray", hover);
	}
	
	@Override
	public boolean targetDefensive() {
		return true;
	}
	
	@Override
	public Item getAnimationItem() {
		return SHIP == null ? Blocks.BARRIER.asItem() : SHIP.asItem();
	}
	
	@Override
	public int energyCost() {
		return COST;
	}
}
