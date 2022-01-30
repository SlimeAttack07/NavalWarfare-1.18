package slimeattack07.naval_warfare.objects.structures;

import java.util.Optional;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.structures.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.PostPlacementProcessor;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import slimeattack07.naval_warfare.NavalWarfare;

public class CaptainsCabin extends StructureFeature<JigsawConfiguration>{
	
	public CaptainsCabin(Codec<JigsawConfiguration> codec) {
        super(codec, CaptainsCabin::createPiecesGenerator, PostPlacementProcessor.NONE);
    }
	
	@Override
    public GenerationStep.Decoration step() {
        return GenerationStep.Decoration.SURFACE_STRUCTURES;
    }
	
	private static boolean isFeatureChunk(PieceGeneratorSupplier.Context<JigsawConfiguration> context) {
        BlockPos pos = context.chunkPos().getWorldPosition();

        int landHeight = context.chunkGenerator().getFirstOccupiedHeight(pos.getX(), pos.getZ(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor());
        NoiseColumn columnOfBlocks = context.chunkGenerator().getBaseColumn(pos.getX(), pos.getZ(), context.heightAccessor());

        BlockState topBlock = columnOfBlocks.getBlock(landHeight);

        return topBlock.getFluidState().isEmpty() && !isNearVillage(context.chunkGenerator(), context.seed(), context.chunkPos(), 5);
    }
	
	private static boolean isNearVillage(ChunkGenerator chunkGenerator, long seed, ChunkPos chunkPos, int range) {
	      StructureFeatureConfiguration structurefeatureconfiguration = chunkGenerator.getSettings().getConfig(StructureFeature.VILLAGE);
	      if (structurefeatureconfiguration == null) {
	         return false;
	      } else {
	         int chunkX = chunkPos.x;
	         int chunkZ = chunkPos.z;

	         for(int i = chunkX - range; i <= chunkX + range; ++i) {
	            for(int j = chunkZ - range; j <= chunkZ + range; ++j) {
	               ChunkPos chunkpos = StructureFeature.VILLAGE.getPotentialFeatureChunk(structurefeatureconfiguration, seed, i, j);
	               
	               if (i == chunkpos.x && j == chunkpos.z) {
	                  return true;
	               }
	            }
	         }

	         return false;
	      }
	   }
	
	public static Optional<PieceGenerator<JigsawConfiguration>> createPiecesGenerator(PieceGeneratorSupplier.Context<JigsawConfiguration> context){
		if(!CaptainsCabin.isFeatureChunk(context)) {
			return Optional.empty();
		}
		
		JigsawConfiguration config = new JigsawConfiguration(() -> context.registryAccess().ownedRegistryOrThrow(Registry.TEMPLATE_POOL_REGISTRY).
				get(new ResourceLocation(NavalWarfare.MOD_ID, "captains_cabin/start_pool")), 10);
		
		PieceGeneratorSupplier.Context<JigsawConfiguration> new_context = new PieceGeneratorSupplier.Context<>(context.chunkGenerator(), 
				context.biomeSource(), context.seed(), context.chunkPos(), config, context.heightAccessor(), context.validBiome(), 
				context.structureManager(), context.registryAccess());
		
		BlockPos pos = context.chunkPos().getMiddleBlockPosition(0);
		
		Optional<PieceGenerator<JigsawConfiguration>> structurePiecesGenerator = JigsawPlacement.addPieces(new_context, PoolElementStructurePiece::new, 
				pos, false, true);
		
		return structurePiecesGenerator;
	}
}
