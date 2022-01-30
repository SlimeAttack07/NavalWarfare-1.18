package slimeattack07.naval_warfare.objects.items;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import slimeattack07.naval_warfare.NavalWarfare;
import slimeattack07.naval_warfare.init.NWBlocks;
import slimeattack07.naval_warfare.objects.blocks.Board;
import slimeattack07.naval_warfare.objects.blocks.GameController;
import slimeattack07.naval_warfare.objects.blocks.PassiveAbilityBlock;
import slimeattack07.naval_warfare.objects.blocks.ShipBlock;
import slimeattack07.naval_warfare.tileentity.BoardTE;
import slimeattack07.naval_warfare.tileentity.EnergyShieldTE;
import slimeattack07.naval_warfare.tileentity.GameControllerTE;
import slimeattack07.naval_warfare.tileentity.PassiveAbilityTE;
import slimeattack07.naval_warfare.tileentity.ShipTE;
import slimeattack07.naval_warfare.util.ControllerState;
import slimeattack07.naval_warfare.util.NWBasicMethods;
import slimeattack07.naval_warfare.util.abilities.Ability;
import slimeattack07.naval_warfare.util.helpers.ShipInfoHelper;

public class InfoTool extends Item{

	public InfoTool() {
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
		
		String infotool = ChatFormatting.BLUE + "(" + NWBasicMethods.getTranslation("item.naval_warfare.info_tool") + ") " +
				ChatFormatting.WHITE;
		
		if(state.getBlock() instanceof ShipBlock) {
			ShipBlock ship = (ShipBlock) state.getBlock();
			
			if(ship.validateShip(level, pos)) {
				NWBasicMethods.messagePlayerCustom(player, infotool + ChatFormatting.GREEN + 
						NWBasicMethods.getTranslation(ship));
				
				NWBasicMethods.messagePlayerCustom(player, infotool +
						NWBasicMethods.getTranslation("message.naval_warfare.info_tool.ship_state") + " " +
						ChatFormatting.GREEN + NWBasicMethods.getTranslation("misc.naval_warfare." + ShipBlock.getState(state).getSerializedName()));
				
				ShipTE te = (ShipTE) level.getBlockEntity(pos);
				
				if(ship.hasActiveAbility()) {
					Ability ability = ship.ACTIVE_ABILITY;
					NWBasicMethods.messagePlayerCustom(player, infotool +
							NWBasicMethods.getTranslation("message.naval_warfare.info_tool.active_ability") + ": " +
							ChatFormatting.GREEN + NWBasicMethods.getTranslation(ability.getTranslation()));
					
					NWBasicMethods.messagePlayerCustom(player, infotool +
							NWBasicMethods.getTranslation("message.naval_warfare.info_tool.ability_uses")
							.replace("MARKER1", "" + ChatFormatting.GREEN + te.getActiveAmount())
							);
					
					NWBasicMethods.messagePlayerCustom(player, infotool +
							NWBasicMethods.getTranslation("message.naval_warfare.info_tool.ability_cost")
							.replace("MARKER1", "" + ChatFormatting.GREEN + ability.energyCost())
							);
				}
				else {
					NWBasicMethods.messagePlayerCustom(player, infotool +
							NWBasicMethods.getTranslation("message.naval_warfare.info_tool.no_active_ability"));
				}
				if(ship.hasPassiveAbility()) {
					Ability ability = ship.PASSIVE_ABILITY;
					NWBasicMethods.messagePlayerCustom(player, infotool +
							NWBasicMethods.getTranslation("message.naval_warfare.info_tool.passive_ability") + ": " +
							ChatFormatting.GREEN + NWBasicMethods.getTranslation(ability.getTranslation()));
					
					NWBasicMethods.messagePlayerCustom(player, infotool +
							NWBasicMethods.getTranslation("message.naval_warfare.info_tool.ability_uses")
							.replace("MARKER1", "" + ChatFormatting.GREEN + te.getPassiveAmount())
							);
				}
				else {
					NWBasicMethods.messagePlayerCustom(player, infotool +
							NWBasicMethods.getTranslation("message.naval_warfare.info_tool.no_passive_ability"));
				}
			}
				else
					NWBasicMethods.messagePlayerActionbar(player, "message.naval_warfare.info_tool.error");
		}
		
		else if(state.getBlock() instanceof Board) {
			Board board = (Board) state.getBlock();
			
			NWBasicMethods.messagePlayerCustom(player, infotool +
					NWBasicMethods.getTranslation("message.naval_warfare.info_tool.board_state") + " " +
					ChatFormatting.GREEN + NWBasicMethods.getTranslation("misc.naval_warfare." + board.getBoardState(state).getSerializedName()));
			
			if(board.validateBoard(level, pos)) {
				BoardTE te = (BoardTE) level.getBlockEntity(pos);
				NWBasicMethods.messagePlayerCustom(player, infotool +
						NWBasicMethods.getTranslation("message.naval_warfare.info_tool.board_id") + ": " +
						ChatFormatting.GREEN + te.getId());
			}
			else {
				NWBasicMethods.messagePlayerActionbar(player, "abilities.naval_warfare.none");
			}
		}	
		
		else if(state.getBlock() instanceof GameController) {
			GameController controller = (GameController) state.getBlock();
			
			if(controller.validateController(level, pos) && controller.getState(state).equals(ControllerState.PLAYING_GAME)) {
				GameControllerTE te = (GameControllerTE) level.getBlockEntity(pos);
				BlockPos opp = te.getOpponent();
				
				if(controller.validateController(level, opp)) {
					BoardTE opp_boardte = controller.getOpponentBoardTile(level, te, 0, false);
					Board opp_board = (Board) opp_boardte.getBlockState().getBlock();
					Direction dir = opp_board.getControllerFacing(level, opp_boardte.getBlockPos());
					
					ArrayList<ShipInfoHelper> sih_list = opp_boardte.collectShipsInfo(level, opp_boardte.getBlockPos(), dir);
					boolean full = player.isCrouching();
					String opponent = full ? "message.naval_warfare.info_tool.opponent" : "message.naval_warfare.info_tool.opponent_remaining";
					
					NWBasicMethods.messagePlayerCustom(player, infotool +
							NWBasicMethods.getTranslation(opponent) + ": ");
					
					String big_divider = "====================";
					String divider = "--------------------";
					NWBasicMethods.messagePlayerCustom(player, infotool + big_divider);
					
					
					for(int i = 0; i < sih_list.size(); i++) {
						ShipInfoHelper sih = sih_list.get(i);
						
					
						if(!full && !sih.getState().contains("alive"))
							continue;
						
						NWBasicMethods.messagePlayerCustom(player, infotool +
								NWBasicMethods.getTranslation("descriptions.naval_warfare.ship_config_name") + ": " +
								ChatFormatting.GREEN + sih.getName() + ChatFormatting.WHITE + " | " + 
								NWBasicMethods.getTranslation("descriptions.naval_warfare.ship_shape") + ": " +
								ChatFormatting.GREEN + sih.getShape() + ChatFormatting.WHITE + " | " + 
								NWBasicMethods.getTranslation("misc.naval_warfare.status") + ": " +
								ChatFormatting.GREEN + NWBasicMethods.getTranslation(sih.getState()));
						
						NWBasicMethods.messagePlayerCustom(player, infotool +
								NWBasicMethods.getTranslation("ability.naval_warfare.active_ability") + ": " +
								ChatFormatting.GREEN + sih.getActiveAbility() + ChatFormatting.WHITE + " | " +
								NWBasicMethods.getTranslation("ability.naval_warfare.passive_ability") + ": " +
								ChatFormatting.GREEN + sih.getPassiveAbility());
						
						if(i != sih_list.size() - 1)
							NWBasicMethods.messagePlayerCustom(player, infotool + divider);
					}
					
					NWBasicMethods.messagePlayerCustom(player, infotool + big_divider);
				}
			}
		}
		else if(state.getBlock() instanceof PassiveAbilityBlock) {
			BlockEntity tile = level.getBlockEntity(pos);
			
			if(tile instanceof PassiveAbilityTE) {
				PassiveAbilityTE te = (PassiveAbilityTE) tile;
				NWBasicMethods.messagePlayerCustom(player, infotool +
						NWBasicMethods.getTranslation("message.naval_warfare.info_tool.passive_block_owner") + ": " + 
						StringUtils.join(te.ownersToString(), ", "));
			}
			
			if(tile instanceof EnergyShieldTE) {
				EnergyShieldTE te = (EnergyShieldTE) tile;
				NWBasicMethods.messagePlayerCustom(player, infotool +
						NWBasicMethods.getTranslation("message.naval_warfare.info_tool.energy_shield_health").replace("MARKER1", "" + te.getHP()));
			}
		}
		else if(state.getBlock().equals(NWBlocks.SHIP_HERE.get())) {
			NWBasicMethods.messagePlayerCustom(player, infotool +
					NWBasicMethods.getTranslation("message.naval_warfare.info_tool.ship_here"));
		}
		else if(state.getBlock().equals(NWBlocks.SHIP_CLOSE.get())) {
			NWBasicMethods.messagePlayerCustom(player, infotool +
					NWBasicMethods.getTranslation("message.naval_warfare.info_tool.ship_close"));
		}
		
		return InteractionResult.SUCCESS;
	}
}
