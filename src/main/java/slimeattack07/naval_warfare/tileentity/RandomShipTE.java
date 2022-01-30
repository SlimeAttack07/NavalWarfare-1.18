package slimeattack07.naval_warfare.tileentity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import slimeattack07.naval_warfare.NavalWarfare;
import slimeattack07.naval_warfare.config.NavalWarfareConfig;
import slimeattack07.naval_warfare.init.NWBlocks;
import slimeattack07.naval_warfare.init.NWTileEntityTypes;
import slimeattack07.naval_warfare.objects.blocks.ShipBlock;
import slimeattack07.naval_warfare.util.NWBasicMethods;
import slimeattack07.naval_warfare.util.helpers.NBTHelper;

public class RandomShipTE extends BlockEntity{
	public boolean can_spawn = true;

	public RandomShipTE(BlockPos pos, BlockState state) {
		super(NWTileEntityTypes.RANDOM_SHIP.get(), pos, state);
	}
	
	public void init() {
		can_spawn = true;
	}
	
	private ArrayList<BlockPos> spawnDisplays(Level level, BlockPos pos) {
		ArrayList<BlockPos> locations = new ArrayList<>();
		
		for(int i = -2; i < 3; i++) {
			for(int k = -2; k < 3; k++) {
				BlockPos loc = pos.offset(i, -2, k);
				locations.add(loc.above());
				level.setBlockAndUpdate(loc, NWBlocks.SHIP_DISPLAY.get().defaultBlockState());
			}
		}
		
		Collections.shuffle(locations);
		return locations;
	}
	
	@Nullable
	private ShipBlock generateRandomShip() {
		ThreadLocalRandom rand = ThreadLocalRandom.current();
		
		int total_weight = NavalWarfareConfig.cc_ship_t1_weight.get() + NavalWarfareConfig.cc_ship_t2_weight.get() + 
				NavalWarfareConfig.cc_ship_t3_weight.get()+ NavalWarfareConfig.cc_ship_t4_weight.get() + NavalWarfareConfig.cc_ship_t5_weight.get();
		
		int result = rand.nextInt(total_weight);
		
		if(result <= NavalWarfareConfig.cc_ship_t1_weight.get() )
			return tagToShip("ships_t1", rand);
		
		result -= NavalWarfareConfig.cc_ship_t1_weight.get();
		
		if(result <= NavalWarfareConfig.cc_ship_t2_weight.get() )
			return tagToShip("ships_t2", rand);
		
		result -= NavalWarfareConfig.cc_ship_t2_weight.get();
		
		if(result <= NavalWarfareConfig.cc_ship_t3_weight.get() )
			return tagToShip("ships_t3", rand);
		
		result -= NavalWarfareConfig.cc_ship_t3_weight.get();
		
		if(result <= NavalWarfareConfig.cc_ship_t4_weight.get() )
			return tagToShip("ships_t4", rand);
		
		return tagToShip("ships_t5", rand);
	}
	
	@Nullable
	private ShipBlock tagToShip(String tag, Random rand) {
		Block block = NWBasicMethods.getRandomTaggedBlock(tag, rand, NWBlocks.NW_SHIPS);
		
		if(block != null && block instanceof ShipBlock)
			return (ShipBlock) block;
		
		return null;
	}
	
	private ArrayList<Direction> randomDirections(){
		ArrayList<Direction> dirs = new ArrayList<>(Arrays.asList(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST));
		Collections.shuffle(dirs);
		
		return dirs;
	}
	
	private boolean spawnShip(ShipBlock ship, Level level, BlockPos pos, BlockState state) {
		return ship.summonShip(level, pos, state, true, true);
	}
	
	private void summonShipRandomly(ShipBlock ship, Level level, ArrayList<BlockPos> locations) {		
		for(BlockPos pos : locations) {
			ArrayList<Direction> dirs = randomDirections();
			
			for(Direction dir : dirs) {
				BlockState state = ship.defaultBlockState().setValue(ShipBlock.FACING, dir);
				
				if(spawnShip(ship, level, pos, state)) {
					ship.propagateAbilityAmount(level, pos, 0);
					return;
				}
			}
		}
	}
	
	public void tick() {
		// Uncomment this when modifying/creating structures that use this block.
//		if(true)
//			return; 
		
		if(level.isClientSide() || !can_spawn) 
			return;
		
		can_spawn = false;
		ArrayList<BlockPos> locations = spawnDisplays(level, worldPosition);
		ShipBlock ship = generateRandomShip();
		
		if(ship == null)
			return;
		
		summonShipRandomly(ship, level, locations);
		level.setBlockAndUpdate(worldPosition, Blocks.AIR.defaultBlockState());
	}
	
	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		
		CompoundTag initvalues = compound.getCompound(NavalWarfare.MOD_ID);
		
		if(initvalues != null) {
			can_spawn = initvalues.getBoolean("can_spawn");
		} else
			init();
	}
	
	@Override
	public void saveAdditional(CompoundTag compound) {
		compound.put(NavalWarfare.MOD_ID, NBTHelper.toNBT(this));
	}
}
