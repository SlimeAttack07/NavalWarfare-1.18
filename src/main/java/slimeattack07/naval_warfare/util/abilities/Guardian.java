package slimeattack07.naval_warfare.util.abilities;

import java.util.ArrayList;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import slimeattack07.naval_warfare.init.NWBlocks;
import slimeattack07.naval_warfare.init.NWItems;
import slimeattack07.naval_warfare.objects.blocks.Board;
import slimeattack07.naval_warfare.objects.blocks.ShipBlock;
import slimeattack07.naval_warfare.tileentity.BoardTE;
import slimeattack07.naval_warfare.tileentity.EnergyShieldTE;
import slimeattack07.naval_warfare.tileentity.GameControllerTE;
import slimeattack07.naval_warfare.util.NWBasicMethods;
import slimeattack07.naval_warfare.util.helpers.BattleLogHelper;

public class Guardian implements Ability {
	private final String NAME;
	private final String OWNER;
	private final int AMOUNT;
	private final int HP;
	
	public Guardian(String name, String owner, int amount, int hp) {
		NAME = name;
		OWNER = owner;
		HP = hp;
		AMOUNT = amount;
	}
	
	@Override
	public void activate(Level level, Player player, BoardTE board) {
		ArrayList<BoardTE> tiles = getTiles(level, board);
		ArrayList<Integer> ids = new ArrayList<>();
		
		for(BoardTE te : tiles) {
			ids.add(te.getId());
			BlockPos pos = te.getBlockPos().above(4);
			
			if(!level.getBlockState(pos).getBlock().equals(NWBlocks.ENERGY_SHIELD.get()))
				level.setBlockAndUpdate(pos, NWBlocks.ENERGY_SHIELD.get().defaultBlockState());
			
			BlockEntity etile = level.getBlockEntity(pos);
			
			if(etile instanceof EnergyShieldTE) {
				EnergyShieldTE ete = (EnergyShieldTE) etile;
				ete.addOwner(OWNER);
				ete.initHP(HP);
			}
		}
		
		if(!ids.isEmpty()) {
			Board b = (Board) board.getBlockState().getBlock();
			GameControllerTE controller = b.getController(level, board.getBlockPos());
			
			if(controller != null)
				controller.recordOnRecorders(BattleLogHelper.createSetBlocks(ids, NWBlocks.ENERGY_SHIELD.get().getRegistryName(), 4, true));
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
	public ArrayList<BoardTE> getTiles(Level level, BoardTE te){
		if(te == null)
			return new ArrayList<>();

		ArrayList<BoardTE> tiles = te.selectRandomShip(AMOUNT, false, true, true, 2000, 2000);
		ArrayList<BoardTE> shield = new ArrayList<>();
		Board board = (Board) te.getBlockState().getBlock();
		
		for(BoardTE bte : tiles) {
			Block block = level.getBlockState(bte.getBlockPos().above()).getBlock();
			
			if(!(block instanceof ShipBlock))
				continue;
			
			ShipBlock ship = (ShipBlock) block;
			ArrayList<BlockPos> undamaged = ship.collectUndamagedParts(level, bte.getBlockPos().above());
			
			for(BlockPos bp : undamaged) {
				if(board.validateBoard(level, bp.below()))
					shield.add((BoardTE) level.getBlockEntity(bp.below()));
			}
		}

		return shield;
	}
	
	@Override
	public Item getAnimationItem() {
		return NWItems.GUARDIAN.get();
	}
	
	@Override
	public PassiveType getPassiveType() {
		return PassiveType.DESTROYED;
	}
	
	@Override
	public MutableComponent hoverableInfo() {
		String name = ChatFormatting.GREEN + NWBasicMethods.getTranslation(getTranslation()) + ChatFormatting.WHITE;
		String hover = NWBasicMethods.getTranslation("misc.naval_warfare.amount") + ": " + AMOUNT;
		hover += NWBasicMethods.getTranslation("misc.naval_warfare.health") + ": " + HP;
		
		return NWBasicMethods.hoverableText(name, "gray", hover);
	}
}
