package slimeattack07.naval_warfare.util.abilities;

import java.util.ArrayList;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import slimeattack07.naval_warfare.init.NWBlocks;
import slimeattack07.naval_warfare.objects.blocks.Board;
import slimeattack07.naval_warfare.objects.blocks.ShipBlock;
import slimeattack07.naval_warfare.tileentity.BoardTE;
import slimeattack07.naval_warfare.tileentity.GameControllerTE;
import slimeattack07.naval_warfare.tileentity.PassiveAbilityTE;
import slimeattack07.naval_warfare.util.NWBasicMethods;
import slimeattack07.naval_warfare.util.helpers.BattleLogHelper;

public class AntiAir implements Ability {
	private final String NAME;
	private final String OWNER;
	private final int UP;
	private final int DOWN;
	private final int LEFT;
	private final int RIGHT; // Note: affected area is (LEFT + RIGHT - 1) * (UP + DOWN - 1)
	private final PassiveType TYPE;
	
	public AntiAir(String name, String owner, int up, int down, int left, int right, boolean deployable) {
		NAME = name;
		OWNER = owner;
		UP = up;
		DOWN = down;
		LEFT = left;
		RIGHT = right;
		TYPE = deployable ? PassiveType.DEPLOYED : PassiveType.START_GAME;
	}
	
	@Override
	public void activate(Level level, Player player, BoardTE board) {
		ArrayList<BoardTE> tiles = getTiles(level, board);
		ArrayList<Integer> ids = new ArrayList<>();
		
		for(BoardTE te : tiles) {
			BlockPos pos = te.getBlockPos().above(5);
			level.setBlockAndUpdate(pos, NWBlocks.ANTI_AIR.get().defaultBlockState());
			BlockEntity ptile = level.getBlockEntity(pos);
			
			if(ptile instanceof PassiveAbilityTE) {
				PassiveAbilityTE pte = (PassiveAbilityTE) ptile;
				pte.addOwner(OWNER);
			}
			
			ids.add(te.getId());
		}
		
		if(!ids.isEmpty()) {
			Board b = (Board) board.getBlockState().getBlock();
			GameControllerTE controller = b.getController(level, board.getBlockPos());
			
			if(controller != null)
				controller.recordOnRecorders(BattleLogHelper.createSetBlocks(ids, NWBlocks.ANTI_AIR.get().getRegistryName(), 5, false));
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

		BlockPos up = te.getBlockPos().above();
		BlockState state = level.getBlockState(up);
		Direction dir = ShipBlock.getFacing(state);
		
		return te.collectTileArea(UP, DOWN, LEFT, RIGHT, dir);
	}
	
	@Override
	public PassiveType getPassiveType() {
		return TYPE;
	}
	
	@Override
	public void detachPassive(Level level, BoardTE board) {	
		ArrayList<BoardTE> tiles = getTiles(level, board);
		
		if(!tiles.isEmpty()) {
			BlockPos pos = tiles.get(0).getBlockPos().above(5);
			BlockEntity tile = level.getBlockEntity(pos);
			
			if(tile instanceof PassiveAbilityTE) {
				PassiveAbilityTE pte = (PassiveAbilityTE) tile;
				Board b = (Board) board.getBlockState().getBlock();
				pte.destroy(level, pos, OWNER, b.getController(level, board.getBlockPos()), 5);
			}
		}
	}
	
	@Override
	public MutableComponent hoverableInfo() {
		String name = ChatFormatting.GREEN + NWBasicMethods.getTranslation(getTranslation()) + ChatFormatting.WHITE;
		String hover = NWBasicMethods.getTranslation("misc.naval_warfare.length") + ": " + (LEFT + RIGHT - 1) + ", ";
		hover += NWBasicMethods.getTranslation("misc.naval_warfare.width") + ": " + (UP + DOWN - 1);
		
		return NWBasicMethods.hoverableText(name, "gray", hover);
	}
	
	@Override
	public boolean canBeDisabled() {
		return true;
	}
}
