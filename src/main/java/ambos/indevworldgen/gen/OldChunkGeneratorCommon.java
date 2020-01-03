package ambos.indevworldgen.gen;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Lists;

import ambos.indevworldgen.util.noise.OctaveAlpha11NoiseSampler;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.IWorld;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.decorator.CountExtraChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.RandomFeatureConfig;
import net.minecraft.world.gen.feature.RandomFeatureEntry;

public abstract class OldChunkGeneratorCommon<T extends ChunkGeneratorConfig> extends ChunkGenerator<T> {
	public OldChunkGeneratorCommon(IWorld world, BiomeSource biomeSource, T config) {
		super(world, biomeSource, config);
	}

	@Override
	public void populateEntities(ChunkRegion region) {
		int centreX = region.getCenterChunkX();
		int centreZ = region.getCenterChunkZ();
		Biome biome = region.getBiome((new ChunkPos(centreX, centreZ)).getCenterBlockPos());
		ChunkRandom rand = new ChunkRandom();
		rand.setSeed(region.getSeed(), centreX << 4, centreZ << 4);
		SpawnHelper.populateEntities(region, biome, centreX, centreZ, rand);
	}

	private final ChunkRandom alphaTreeRand = new ChunkRandom();

	// I copy pasted this from the alpha 1.1.2_01 jar file lol
	// I knew one day my alpha mapping files would be useful :P (not really, but it's nice that they were)
	protected void alphaDecorateTrees(int chunkX, int chunkZ, int x, int z, BlockPos pos, IWorld region, OctaveAlpha11NoiseSampler treeNoise) {
		this.alphaTreeRand.setSeed(this.seed);
		this.alphaTreeRand.setSeed(chunkX * (this.alphaTreeRand.nextLong() / 2L * 2L + 1L) + chunkZ * (this.alphaTreeRand.nextLong() / 2L * 2L + 1L) ^ this.seed);

		double scale = 0.5;
		int count = (int)((treeNoise.sample(x * scale, z * scale) / 8.0 + this.alphaTreeRand.nextDouble() * 4.0 + 4.0) / 3.0);
		if (count < 0) {
			count = 0;
		}

		if (this.alphaTreeRand.nextInt(10) == 0) {
			++count;
		}

		ConfiguredFeature<?, ?> randomTreeProvider = getConfiguredTreeFeature(count);

		// the tree provider is sometimes null so this check is neccessary
		if (randomTreeProvider != null && count > 0) {
			try {
				randomTreeProvider.generate(region, this, alphaTreeRand, pos);
			} catch (Exception e) {
				CrashReport crashReport = CrashReport.create(e, "Feature placement");
				CrashReportSection reportSection = crashReport.addElement("Feature").add("Id", (Object)Registry.FEATURE.getId(randomTreeProvider.feature));
				Feature<?> reportFeature = randomTreeProvider.feature;
				reportSection.add("Description", reportFeature::toString);
				throw new CrashException(crashReport);
			}
		}
	}

	private static ConfiguredFeature<?, ?> getConfiguredTreeFeature(int count) {
		return configuredTreeCache.computeIfAbsent(count, e -> Feature.RANDOM_SELECTOR.configure(new RandomFeatureConfig(Lists.newArrayList(new RandomFeatureEntry<>(Feature.FANCY_TREE.configure(DefaultBiomeFeatures.FANCY_TREE_CONFIG), 0.1f)), Feature.NORMAL_TREE.configure(DefaultBiomeFeatures.OAK_TREE_CONFIG))).createDecoratedFeature(Decorator.COUNT_EXTRA_HEIGHTMAP.configure(new CountExtraChanceDecoratorConfig(count, 0.0f, 0))));
	}

	private static final Map<Integer, ConfiguredFeature<?, ?>> configuredTreeCache = new HashMap<>();
}
