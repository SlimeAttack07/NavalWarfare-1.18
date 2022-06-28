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
import net.minecraftforge.registries.RegistryObject;
import slimeattack07.naval_warfare.objects.blocks.Board;
import slimeattack07.naval_warfare.objects.blocks.ShipBlock;
import slimeattack07.naval_warfare.tileentity.BoardTE;
import slimeattack07.naval_warfare.tileentity.GameControllerTE;
import slimeattack07.naval_warfare.util.BoardState;
import slimeattack07.naval_warfare.util.NWBasicMethods;
import slimeattack07.naval_warfare.util.helpers.BattleLogHelper;

public class Deployable implements Ability {
	private final int AMOUNT;
	private final int COST;
	private final String NAME;
	private final RegistryObject<Item> ITEM;
	protected final ShipBlock SHIP;
	
	public Deployable(int amount, int cost, String name, RegistryObject<Item> item, ShipBlock ship) {
		NAME = name;
		AMOUNT = amount;
		COST = cost;
		ITEM = item;
		SHIP = ship;
	}
	
	@Override
	public void activate(Level level, Player player, BoardTE board) {
		ArrayList<BoardTE> tiles = getTiles(level, board);
		
		if(tiles.isEmpty())
			return;
		
		BoardTE te = tiles.get(0);
		BlockPos pos = te.getBlockPos().above();
		
		ArrayList<Direction> dirs = Lists.newArrayList(new Direction[] {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST});
		Collections.shuffle(dirs);
		
		boolean success = false;
		
		for(Direction dir : dirs) {
			success = SHIP.summonShip(level, pos, SHIP.defaultBlockState().setValue(ShipBlock.FACING, dir), true, false);
			
			if(success) {
				if(SHIP.hasPassiveAbility() && SHIP.PASSIVE_ABILITY.getPassiveType().equals(PassiveType.DEPLOYED))
					SHIP.PASSIVE_ABILITY.activate(level, player, board);
				
				Board b = (Board) board.getBlockState().getBlock();
				GameControllerTE controller = b.getController(level, board.getBlockPos());
				
				if(controller != null)
					controller.recordOnRecorders(BattleLogHelper.createDeployable(board.getId(), SHIP.getRegistryName(), dir));
				
				break;
			}
		}
		
		if(!success)
			NWBasicMethods.messagePlayer(player, "message.naval_warfare.failed_deploy");
		
		Board b = (Board) te.getBlockState().getBlock();
		BoardState bstate = b.getBoardState(te.getBlockState());
		level.setBlockAndUpdate(te.getBlockPos(), te.getBlockState().setValue(Board.STATE, bstate.deselect()));		
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
	
	@Override
	public MutableComponent hoverableInfo() {
		String name = ChatFormatting.GREEN + NWBasicMethods.getTranslation(getTranslation()) + ChatFormatting.WHITE;
		String hover = NWBasicMethods.getTranslation("misc.naval_warfare.cost") + ": " + COST + ", ";
		
		return NWBasicMethods.hoverableText(name, "gray", hover);
	}
	
	@Override
	public boolean targetDefensive() {
		return true;
	}
	
	@Override
	public Item getAnimationItem() {
		return ITEM.get();
	}
	
	@Override
	public int energyCost() {
		return COST;
	}
}
