package ambos.indevworldgen.gen.biomesource;

import java.util.function.LongFunction;

import com.google.common.collect.ImmutableList;

import ambos.indevworldgen.IndevWorldGen;
import ambos.indevworldgen.gen.biomesource.layer.AlwaysContinentLayer;
import ambos.indevworldgen.gen.biomesource.layer.AlwaysOceanLayer;
import ambos.indevworldgen.gen.biomesource.layer.YeetSwampsOutOfExistanceLayer;
import net.minecraft.world.biome.layer.AddBambooJungleLayer;
import net.minecraft.world.biome.layer.AddClimateLayers;
import net.minecraft.world.biome.layer.AddColdClimatesLayer;
import net.minecraft.world.biome.layer.AddDeepOceanLayer;
import net.minecraft.world.biome.layer.AddEdgeBiomesLayer;
import net.minecraft.world.biome.layer.AddHillsLayer;
import net.minecraft.world.biome.layer.AddMushroomIslandLayer;
import net.minecraft.world.biome.layer.AddRiversLayer;
import net.minecraft.world.biome.layer.AddSunflowerPlainsLayer;
import net.minecraft.world.biome.layer.ApplyOceanTemperatureLayer;
import net.minecraft.world.biome.layer.EaseBiomeEdgeLayer;
import net.minecraft.world.biome.layer.IncreaseEdgeCurvatureLayer;
import net.minecraft.world.biome.layer.OceanTemperatureLayer;
import net.minecraft.world.biome.layer.ScaleLayer;
import net.minecraft.world.biome.layer.SetBaseBiomesLayer;
import net.minecraft.world.biome.layer.SimpleLandNoiseLayer;
import net.minecraft.world.biome.layer.SmoothenShorelineLayer;
import net.minecraft.world.biome.layer.type.ParentedLayer;
import net.minecraft.world.biome.layer.util.CachingLayerContext;
import net.minecraft.world.biome.layer.util.CachingLayerSampler;
import net.minecraft.world.biome.layer.util.LayerFactory;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;
import net.minecraft.world.biome.source.BiomeLayerSampler;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;
import net.minecraft.world.level.LevelGeneratorType;

public final class OldBiomeLayers {

	private static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> stackRepeat(long seed, ParentedLayer layer, LayerFactory<T> parent, int count, LongFunction<C> contextProvider) {
		LayerFactory<T> layerFactory = parent;

		for(int i = 0; i < count; ++i) {
			layerFactory = layer.create(contextProvider.apply(seed + (long)i), layerFactory);
		}

		return layerFactory;
	}

	public static <T extends LayerSampler, C extends LayerSampleContext<T>> ImmutableList<LayerFactory<T>> buildFactories(OverworldChunkGeneratorConfig settings, LongFunction<C> contextProvider) {
		LayerFactory<T> continent = AlwaysContinentLayer.INSTANCE.create(contextProvider.apply(1L));
		continent = AddColdClimatesLayer.INSTANCE.create(contextProvider.apply(2L), continent);
		continent = IncreaseEdgeCurvatureLayer.INSTANCE.create(contextProvider.apply(3L), continent);
		continent = AddClimateLayers.AddTemperateBiomesLayer.INSTANCE.create(contextProvider.apply(2L), continent);
		continent = AddClimateLayers.AddCoolBiomesLayer.INSTANCE.create(contextProvider.apply(2L), continent);
		continent = AddClimateLayers.AddSpecialBiomesLayer.INSTANCE.create(contextProvider.apply(3L), continent);
		continent = ScaleLayer.NORMAL.create(contextProvider.apply(2002L), continent);
		continent = ScaleLayer.NORMAL.create(contextProvider.apply(2003L), continent);
		continent = IncreaseEdgeCurvatureLayer.INSTANCE.create(contextProvider.apply(4L), continent);
		continent = AddMushroomIslandLayer.INSTANCE.create(contextProvider.apply(5L), continent);
		continent = AddDeepOceanLayer.INSTANCE.create(contextProvider.apply(4L), continent);
		continent = stackRepeat(1000L, ScaleLayer.NORMAL, continent, 0, contextProvider);

		int biomeSize = 4;
		if (settings != null) {
			biomeSize = settings.getBiomeSize();
		}

		LayerFactory<T> noise = stackRepeat(1000L, ScaleLayer.NORMAL, continent, 0, contextProvider);
		noise = SimpleLandNoiseLayer.INSTANCE.create(contextProvider.apply(100L), noise);
		LayerFactory<T> biomes = (new SetBaseBiomesLayer(LevelGeneratorType.DEFAULT, -1)).create(contextProvider.apply(200L), continent);
		biomes = AddBambooJungleLayer.INSTANCE.create(contextProvider.apply(1001L), biomes);
		biomes = stackRepeat(1000L, ScaleLayer.NORMAL, biomes, 1, contextProvider);
		if (!IndevWorldGen.config.generateSwamps) {
			biomes = YeetSwampsOutOfExistanceLayer.INSTANCE.create(contextProvider.apply(200L), biomes);
		}
		biomes = stackRepeat(1001L, ScaleLayer.NORMAL, biomes, 1, contextProvider);
		biomes = EaseBiomeEdgeLayer.INSTANCE.create(contextProvider.apply(1000L), biomes);
		LayerFactory<T> noise2 = stackRepeat(1000L, ScaleLayer.NORMAL, noise, 2, contextProvider);
		biomes = AddHillsLayer.INSTANCE.create(contextProvider.apply(1000L), biomes, noise2);
		biomes = AddSunflowerPlainsLayer.INSTANCE.create(contextProvider.apply(1001L), biomes);

		for(int k = 0; k < biomeSize; ++k) {
			biomes = ScaleLayer.NORMAL.create(contextProvider.apply((long)(1000 + k)), biomes);
			if (k == 0) {
				biomes = IncreaseEdgeCurvatureLayer.INSTANCE.create(contextProvider.apply(3L), biomes);
			}

			if (k == 1 || biomeSize == 1) {
				biomes = AddEdgeBiomesLayer.INSTANCE.create(contextProvider.apply(1000L), biomes);
			}
		}

		biomes = SmoothenShorelineLayer.INSTANCE.create(contextProvider.apply(1000L), biomes);
		biomes = AddRiversLayer.INSTANCE.create(contextProvider.apply(100L), biomes, noise);

		// ocean
		LayerFactory<T> oceanTemperature = OceanTemperatureLayer.INSTANCE.create(contextProvider.apply(2L));
		oceanTemperature = stackRepeat(2001L, ScaleLayer.NORMAL, oceanTemperature, 6, contextProvider);

		LayerFactory<T> ocean = AlwaysOceanLayer.INSTANCE.create(contextProvider.apply(1L));
		ocean = ApplyOceanTemperatureLayer.INSTANCE.create(contextProvider.apply(100L), ocean, oceanTemperature);

		return ImmutableList.of(ocean, biomes);
	}

	public static BiomeSamplerPicker build(long seed, OverworldChunkGeneratorConfig settings) {
		final int cacheSize = 25;
		ImmutableList<LayerFactory<CachingLayerSampler>> resultList = buildFactories(settings, (salt) -> {
			return new CachingLayerContext(cacheSize, seed, salt);
		});

		BiomeLayerSampler oceanSampler = new BiomeLayerSampler(resultList.get(0));
		BiomeLayerSampler landSampler = new BiomeLayerSampler(resultList.get(1));

		return new DefaultBiomeSamplerPicker(oceanSampler, landSampler);
	}

	static class DefaultBiomeSamplerPicker implements BiomeSamplerPicker {
		private final BiomeLayerSampler oceanSampler, landSampler;

		public DefaultBiomeSamplerPicker(BiomeLayerSampler ocean, BiomeLayerSampler land) {
			oceanSampler = ocean;
			landSampler = land;
		}

		@Override
		public BiomeLayerSampler getSampler(HeightRetriever heightRetriever, int genX, int genZ) {
			if (heightRetriever.getHeight(genX << 2, genZ << 2) < heightRetriever.getSeaLevelForBiomeGen()) {
				return this.oceanSampler;
			} else {
				return this.landSampler;
			}
		}
	}
}
