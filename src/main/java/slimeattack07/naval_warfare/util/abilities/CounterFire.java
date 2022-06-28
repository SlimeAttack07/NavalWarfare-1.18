package slimeattack07.naval_warfare.util.abilities;

import java.util.ArrayList;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import slimeattack07.naval_warfare.config.NavalWarfareConfig;
import slimeattack07.naval_warfare.init.NWBlocks;
import slimeattack07.naval_warfare.init.NWItems;
import slimeattack07.naval_warfare.init.NWSounds;
import slimeattack07.naval_warfare.objects.blocks.Board;
import slimeattack07.naval_warfare.objects.blocks.GameController;
import slimeattack07.naval_warfare.tileentity.BoardTE;
import slimeattack07.naval_warfare.tileentity.GameControllerTE;
import slimeattack07.naval_warfare.util.NWBasicMethods;
import slimeattack07.naval_warfare.util.TargetType;
import slimeattack07.naval_warfare.util.helpers.BattleLogHelper;
import slimeattack07.naval_warfare.util.helpers.ControllerActionHelper;

public class CounterFire implements Ability {
	private final String NAME;
	private final int AMOUNT;
	private final Block BLOCK;
	private final boolean SUPPRESSION;
	
	public CounterFire(String name, int amount, Block block, boolean suppression) {
		NAME = name;
		AMOUNT = amount;
		BLOCK = NavalWarfareConfig.show_on_hit_passives.get() ? block : NWBlocks.SHELL.get();
		SUPPRESSION = suppression;
	}
	
	@Override
	public void activate(Level level, Player player, BoardTE board) {
		GameControllerTE controller = null;
		GameController control = null;
		ArrayList<BoardTE> tiles = getTiles(level, board);
		int delay = 20;
		String playername = "dummy";
		ArrayList<Integer> ids = new ArrayList<>();
		
		if(!tiles.isEmpty()) {
			Board b = (Board) tiles.get(0).getBlockState().getBlock();
			b.selectTiles(level, tiles);
		}
		
		for(BoardTE te : tiles) {
			if(controller == null) {
				Board b = (Board) te.getBlockState().getBlock();
				controller = b.getController(level, te.getBlockPos());
				
				if(controller == null)
					return;
				
				playername = controller.getOwner();
				control = (GameController) controller.getBlockState().getBlock();
				BlockPos opp = controller.getOpponent();
				
				if(!control.validateController(level, opp))
					return;
				
				controller = (GameControllerTE) level.getBlockEntity(opp);
				control = (GameController) controller.getBlockState().getBlock();
			}

			BoardTE matching = control.getBoardTile(level, controller, te.getId());
			
			if(matching != null) {			
				ControllerActionHelper cah = ControllerActionHelper.createMultiTarget(delay, matching.getBlockPos(), playername, 
						matching.getBlockPos(), te.getBlockPos(), 1, TargetType.NORMAL, false, true);
				
				ids.add(te.getId());
				controller.addAction(cah);
				delay = 0;
								
				NWBasicMethods.dropBlock(level, te.getBlockPos(), BLOCK);
				NWBasicMethods.dropBlock(level, matching.getBlockPos(), BLOCK);
			}
		}
		
		controller.addAction(ControllerActionHelper.createValidate());
		
		if(!ids.isEmpty()) {
			controller.recordOnRecorders(BattleLogHelper.createSounds(ids, false, NWSounds.SHOT.get(), 1f, 1.25f));
			controller.recordOnRecorders(BattleLogHelper.createDropBlocks(ids, false, BLOCK.getRegistryName()));
		}
	}

	@Override
	public int getAmount() {
		return 1;
	}

	@Override
	public String getTranslation() {
		return "abilities.naval_warfare." + NAME;
	}
	
	@Override
	public String getPassiveCategory() {
		return "abilities.naval_warfare.counter_fire";
	}

	@Override
	public ArrayList<BoardTE> getTiles(Level level, BoardTE te){
		BoardTE bte = te.getOpponentBoardZero();
		
		if(bte == null)
			return new ArrayList<>();
		
		return bte.selectRandomTiles(AMOUNT, true, false);
	}
	
	@Override
	public PassiveType getPassiveType() {
		return SUPPRESSION ? PassiveType.DESTROYED : PassiveType.HIT;
	}
	
	@Override
	public MutableComponent hoverableInfo() {
		String translation = SUPPRESSION || NavalWarfareConfig.reveal_on_hit_passives.get() ? getTranslation() : getPassiveCategory();
		String name = ChatFormatting.GREEN + NWBasicMethods.getTranslation(translation) + ChatFormatting.WHITE;
		String hover = NWBasicMethods.getTranslation("misc.naval_warfare.amount") + ": " + AMOUNT;
		
		return NWBasicMethods.hoverableText(name, "gray", hover);
	}
	
	@Override
	public Item getAnimationItem() {
		return SUPPRESSION ? NWItems.SUPPRESSION.get() : NWItems.COUNTERFIRE.get();
	}
}
