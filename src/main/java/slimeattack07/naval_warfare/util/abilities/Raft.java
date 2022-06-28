package slimeattack07.naval_warfare.util.abilities;

import java.util.ArrayList;
import java.util.Collections;

import com.google.common.collect.Lists;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import slimeattack07.naval_warfare.config.NavalWarfareConfig;
import slimeattack07.naval_warfare.init.NWItems;
import slimeattack07.naval_warfare.objects.blocks.Board;
import slimeattack07.naval_warfare.objects.blocks.GameController;
import slimeattack07.naval_warfare.objects.blocks.ShipBlock;
import slimeattack07.naval_warfare.tileentity.BoardTE;
import slimeattack07.naval_warfare.tileentity.GameControllerTE;
import slimeattack07.naval_warfare.util.BoardState;
import slimeattack07.naval_warfare.util.NWBasicMethods;
import slimeattack07.naval_warfare.util.helpers.BattleLogHelper;
import slimeattack07.naval_warfare.util.helpers.ControllerActionHelper;

public class Raft extends Deployable{
	public Raft(ShipBlock ship) {
		super(1, 0, "crude_raft", NWItems.RAFT, ship);
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
				Board b = (Board) te.getBlockState().getBlock();
				GameControllerTE gc = b.getController(level, te.getBlockPos());
				
				if(gc != null) {
					String p = player == null ? "dummy" : player.getStringUUID();
					GameController controller = (GameController) gc.getBlockState().getBlock();
					BoardTE ote = controller.getOpponentBoardTile(level, gc, te.getId(), true);
					gc.recordOnRecorders(BattleLogHelper.createDeployable(board.getId(), SHIP.getRegistryName(), dir));
					
					if(ote != null) {
						gc.addHP(1);
						gc.addTurnAction(ControllerActionHelper.createRaft(te.getBlockPos(), p, te.getBlockPos(), ote.getBlockPos(), 
								NavalWarfareConfig.raft_timeout.get() - 1));
					}
					else
						NWBasicMethods.messagePlayer(player, "message.naval_warfare.failed_deploy_raft");
				}
				
				break;
			}
		}
		
		if(!success)
			NWBasicMethods.messagePlayer(player, "message.naval_warfare.failed_deploy_spell");
		
		Board b = (Board) te.getBlockState().getBlock();
		BoardState bstate = b.getBoardState(te.getBlockState());
		level.setBlockAndUpdate(te.getBlockPos(), te.getBlockState().setValue(Board.STATE, bstate.deselect()));		
	}
}
