package ambos.indevworldgen.gen.biomesource;

import java.util.List;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.Sets;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class OldBiomeSource extends BiomeSource {
	private static final Biome[] biomes = new Biome[]{Biomes.OCEAN, Biomes.PLAINS, Biomes.DESERT, Biomes.MOUNTAINS, Biomes.FOREST, Biomes.TAIGA, Biomes.SWAMP, Biomes.RIVER, Biomes.FROZEN_OCEAN, Biomes.FROZEN_RIVER, Biomes.SNOWY_TUNDRA, Biomes.SNOWY_MOUNTAINS, Biomes.MUSHROOM_FIELDS, Biomes.MUSHROOM_FIELD_SHORE, Biomes.BEACH, Biomes.DESERT_HILLS, Biomes.WOODED_HILLS, Biomes.TAIGA_HILLS, Biomes.MOUNTAIN_EDGE, Biomes.JUNGLE, Biomes.JUNGLE_HILLS, Biomes.JUNGLE_EDGE, Biomes.DEEP_OCEAN, Biomes.STONE_SHORE, Biomes.SNOWY_BEACH, Biomes.BIRCH_FOREST, Biomes.BIRCH_FOREST_HILLS, Biomes.DARK_FOREST, Biomes.SNOWY_TAIGA, Biomes.SNOWY_TAIGA_HILLS, Biomes.GIANT_TREE_TAIGA, Biomes.GIANT_TREE_TAIGA_HILLS, Biomes.WOODED_MOUNTAINS, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.BADLANDS, Biomes.WOODED_BADLANDS_PLATEAU, Biomes.BADLANDS_PLATEAU, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_WARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.SUNFLOWER_PLAINS, Biomes.DESERT_LAKES, Biomes.GRAVELLY_MOUNTAINS, Biomes.FLOWER_FOREST, Biomes.TAIGA_MOUNTAINS, Biomes.SWAMP_HILLS, Biomes.ICE_SPIKES, Biomes.MODIFIED_JUNGLE, Biomes.MODIFIED_JUNGLE_EDGE, Biomes.TALL_BIRCH_FOREST, Biomes.TALL_BIRCH_HILLS, Biomes.DARK_FOREST_HILLS, Biomes.SNOWY_TAIGA_MOUNTAINS, Biomes.GIANT_SPRUCE_TAIGA, Biomes.GIANT_SPRUCE_TAIGA_HILLS, Biomes.MODIFIED_GRAVELLY_MOUNTAINS, Biomes.SHATTERED_SAVANNA, Biomes.SHATTERED_SAVANNA_PLATEAU, Biomes.ERODED_BADLANDS, Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU, Biomes.MODIFIED_BADLANDS_PLATEAU};
	
	private final BiomeSamplerPicker samplerPicker;

	private HeightRetriever heightRetriever = HeightRetriever.NONE;

	public OldBiomeSource(long seed, OverworldChunkGeneratorConfig config) {
		samplerPicker = OldBiomeLayers.build(seed, config);
	}
	
	public void setHeightRetriever(HeightRetriever retriever) {
		this.heightRetriever = retriever;
	}

	@Override
	public Biome getBiome(int x, int z) {
		return this.samplerPicker.getSampler(heightRetriever, x, z).sample(x, z);
	}

	@Override
	public Biome[] sampleBiomes(int x, int z, int width, int height, boolean flag) {
		return this.samplerPicker.getSampler(heightRetriever, x, z).sample(x, z, width, height);
	}

	@Override
	public Set<Biome> getBiomesInArea(int x, int z, int radius) {
		int xLow = x - radius >> 2;;
		int zLow = z - radius >> 2;
		int xHigh = x + radius >> 2;
		int zHigh = z + radius >> 2;
		int xRange = xHigh - xLow + 1;
		int zRange = zHigh - zLow + 1;

		return Sets.newHashSet(this.sampleBiomes(xLow, zLow, xRange, zRange));
	}

	@Override
	public BlockPos locateBiome(int x, int z, int radius, List<Biome> biomes, Random random) {
		int xLow = x - radius >> 2;
		int zLow = z - radius >> 2;
		int xHigh = x + radius >> 2;
		int zHigh = z + radius >> 2;
		int xRange = xHigh - xLow + 1;
		int zRange = zHigh - zLow + 1;

		Biome[] sampledBiomes = this.sampleBiomes(xLow, zLow, xRange, zRange);
		BlockPos pos = null;
		int randBound = 0;

		for(int index = 0; index < xRange * zRange; ++index) {
			int posX = xLow + index % xRange << 2;
			int posZ = zLow + index / xRange << 2;
			if (biomes.contains(sampledBiomes[index])) {
				if (pos == null || random.nextInt(randBound + 1) == 0) {
					pos = new BlockPos(posX, 0, posZ);
				}

				++randBound;
			}
		}

		return pos;
	}

	@Override
	public boolean hasStructureFeature(StructureFeature<?> feature) {
		return (Boolean)this.structureFeatures.computeIfAbsent(feature, (structureFeature) -> {
			Biome[] var2 = biomes;
			int var3 = var2.length;

			for(int var4 = 0; var4 < var3; ++var4) {
				Biome biome = var2[var4];
				if (biome.hasStructureFeature(structureFeature)) {
					return true;
				}
			}

			return false;
		});
	}

	@Override
	public Set<BlockState> getTopMaterials() {
		if (this.topMaterials.isEmpty()) {
			Biome[] var1 = biomes;
			int var2 = var1.length;

			for(int var3 = 0; var3 < var2; ++var3) {
				Biome biome = var1[var3];
				this.topMaterials.add(biome.getSurfaceConfig().getTopMaterial());
			}
		}

		return this.topMaterials;
	}

}
