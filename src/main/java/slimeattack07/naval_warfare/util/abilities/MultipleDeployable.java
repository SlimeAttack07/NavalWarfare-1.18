package slimeattack07.naval_warfare.util.abilities;

import java.util.ArrayList;
import java.util.Collections;

import com.google.common.collect.Lists;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import slimeattack07.naval_warfare.objects.blocks.Board;
import slimeattack07.naval_warfare.objects.blocks.ShipBlock;
import slimeattack07.naval_warfare.tileentity.BoardTE;
import slimeattack07.naval_warfare.tileentity.GameControllerTE;
import slimeattack07.naval_warfare.util.BoardState;
import slimeattack07.naval_warfare.util.NWBasicMethods;
import slimeattack07.naval_warfare.util.helpers.BattleLogHelper;
import slimeattack07.naval_warfare.util.helpers.ControllerActionHelper;

public class MultipleDeployable extends Deployable {
	private final int TIMES;
	private PassiveType TYPE;
	
	public MultipleDeployable(int amount, int cost, String name, Block ship, boolean passive, int times) {
		super(amount, cost, name, ship, passive);
		
		TYPE = passive ? PassiveType.DESTROYED : PassiveType.NOT;
		TIMES = times;
	}
	
	@Override
	public void activate(Level level, Player player, BoardTE board) {
		ArrayList<BoardTE> tiles = getTilesReal(level, board);
		
		if(tiles.isEmpty())
			return;
		
		int spawns = 0;
		
		ArrayList<Direction> dirs = Lists.newArrayList(new Direction[] {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST});
		Collections.shuffle(dirs);
		
		Board b = (Board) board.getBlockState().getBlock();
		GameControllerTE controller = b.getController(level, board.getBlockPos());
		
		for(BoardTE te : tiles) {
			BlockPos pos = te.getBlockPos().above();
			
			for(Direction dir : dirs) {
				boolean success = SHIP.summonShip(level, pos, SHIP.defaultBlockState().setValue(ShipBlock.FACING, dir), true, false);
				
				if(success) {
					if(SHIP.hasPassiveAbility() && SHIP.PASSIVE_ABILITY.getPassiveType().equals(PassiveType.DEPLOYED))
						SHIP.PASSIVE_ABILITY.activate(level, player, board);
					
					if(controller != null)
						controller.recordOnRecorders(BattleLogHelper.createDeployable(te.getId(), SHIP.getRegistryName(), dir));
					
					spawns++;
					
					break;
				}
			}
			
			if(spawns >= TIMES)
				break;		
		}
		
		if(spawns < TIMES && !PASSIVE) {
			NWBasicMethods.messagePlayer(player, "message.naval_warfare.failed_deploy_all");
			
			if(controller != null) {
				int energy = (int) Math.floor((COST / TIMES) * (TIMES - spawns));
				controller.addAction(ControllerActionHelper.createEnergyGain(energy, true));
			}
		}
				
		BoardState bstate = b.getBoardState(board.getBlockState());
		level.setBlockAndUpdate(board.getBlockPos(), board.getBlockState().setValue(Board.STATE, bstate.deselect()));
	}
	
	@Override
	public MutableComponent hoverableInfo() {
		String name = ChatFormatting.GREEN + NWBasicMethods.getTranslation(getTranslation()) + ChatFormatting.WHITE;
		String hover = PASSIVE ? "" : NWBasicMethods.getTranslation("misc.naval_warfare.cost") + ": " + COST + ", ";
		hover += NWBasicMethods.getTranslation("misc.naval_warfare.amount_deployed") + ": " + TIMES;
		
		return NWBasicMethods.hoverableText(name, "gray", hover);
	}
	
	@Override
	public PassiveType getPassiveType() {
		return TYPE;
	}
}
