package slimeattack07.naval_warfare.util.helpers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import slimeattack07.naval_warfare.NavalWarfare;
import slimeattack07.naval_warfare.objects.blocks.Board;
import slimeattack07.naval_warfare.tileentity.BoardTE;
import slimeattack07.naval_warfare.util.BoardState;

public class BattleLogHelper {
	public int id;
	public boolean opponent;
	public BoardState board_state;
	
	public BattleLogHelper() {
	}
	
	public BattleLogHelper copy() {
		BattleLogHelper blh = new BattleLogHelper();
		
		blh.id = id;
		blh.opponent = opponent;
		blh.board_state = board_state;
		
		return blh;
	}
	
	public static BattleLogHelper createBoardState(int id, boolean opponent, BoardState state) {
		BattleLogHelper b = new BattleLogHelper();
		
		b.id = id;
		b.opponent = opponent;
		b.board_state = state;
		
		return b;
	}
	
	
	public static BattleLogHelper fromCAH(Level level, ControllerActionHelper cah) {		
		if(cah == null || cah.action == null) {
			NavalWarfare.LOGGER.warn("Can't convert corrupt CAH to BLH: CAH = " + cah);
			return new BattleLogHelper();
		}
		
		switch(cah.action) {
		case ACTIVE_ABILITY:
			break;
		case ANNOUNCE:
			break;
		case BOMBER:
			break;
		case END_TURN:
			break;
		case FLARE:
			break;
		case FRAGBOMB:
			break;
		case GAIN_ENERGY:
			break;
		case MULTI_TARGET:
			break;
		case NAPALM:
			break;
		case PASSIVE_ABILITY:
			break;
		case RAFT:
			break;
		case SPELL:
			break;
		case SPYGLASS:
			break;
		case TARGET:
			return extractBoard(level, cah.pos, cah.multi_ability);
		case TORPEDO:
			break;
		case TURN_DAMAGE:
			break;
		case VALIDATE:
			break;
		default:
			break;
		}
		
		return new BattleLogHelper();
	}
	
	private static BattleLogHelper extractBoard(Level level, BlockPos pos, boolean opponent) {
		BattleLogHelper blh = new BattleLogHelper();
		BlockEntity tile = level.getBlockEntity(pos);
		
		if(tile instanceof BoardTE) {
			BoardTE te = (BoardTE) tile;
			Board board = (Board) te.getBlockState().getBlock();
			blh.id = te.getId();
			blh.opponent = opponent;
			blh.board_state = board.getBoardState(te.getBlockState());
			
			return blh;
		}
		
		NavalWarfare.LOGGER.warn("Can't convert pos to BLH: pos = " + pos);
		return blh;
	}
}
