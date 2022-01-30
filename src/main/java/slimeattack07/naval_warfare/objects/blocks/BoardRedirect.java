package slimeattack07.naval_warfare.objects.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import slimeattack07.naval_warfare.tileentity.BoardTE;
import slimeattack07.naval_warfare.tileentity.GameControllerTE;
import slimeattack07.naval_warfare.util.NWBasicMethods;

public class BoardRedirect extends Board{
	
	@Override
	public void handleInteraction(BlockState state, Level level, BlockPos pos, Player player,
			ItemStack stack, BlockPos matching) {
		
		BlockEntity tile = level.getBlockEntity(pos);
		
		if(!(tile instanceof BoardTE))
			return;
		
		BoardTE te = (BoardTE) tile;
		
		if(!ownsBlock(player, level, te.getController())) {
			NWBasicMethods.messagePlayer(player, "message.naval_warfare.not_owner_b");
			return;
		}
		
		if(!hasTurn(player, level, te.getController())) {
			NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.not_turn");
			return;
		}
		
		if(!canAccept(player, level, te.getController())) {
			NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.busy");
			return;
		}
		
		BlockPos cpos = te.getController();
		BlockState cstate = level.getBlockState(cpos);
		
		if(cstate.getBlock() instanceof GameController && cstate.hasBlockEntity()) {
			GameControllerTE cte = (GameControllerTE) level.getBlockEntity(cpos);
			
			if(cte.hasGame()) {
				GameController controller = (GameController) cstate.getBlock();
				BoardTE obte = controller.getOpponentBoardTile(level, cte, te.getId(), false);
				
				if(obte != null) {
					Board ob = (Board) obte.getBlockState().getBlock();
					ob.handleInteraction(obte.getBlockState(), level, obte.getBlockPos(), player, stack, pos);
				}
			}
		}
	}
}
