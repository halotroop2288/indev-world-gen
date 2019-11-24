package ambos.indevworldgen.worldtype;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import ambos.indevworldgen.IndevWorldGen;
import ambos.indevworldgen.gen.AlphaChunkGenerator;
import ambos.indevworldgen.gen.AlphaChunkGeneratorConfig;
import ambos.indevworldgen.gen.IndevChunkGenerator;
import ambos.indevworldgen.gen.IndevChunkGeneratorConfig;
import ambos.indevworldgen.gen.biomesource.OldBiomeSource;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSourceType;
import net.minecraft.world.biome.source.FixedBiomeSourceConfig;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSourceConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelGeneratorType;

public class OldWorldType<T extends ChunkGenerator<?>> {
	public static final List<OldWorldType<?>> TYPES = Lists.newArrayList();
	
	public static final Map<LevelGeneratorType, OldWorldType<?>> LGT_TO_WT_MAP = Maps.newHashMap();
	public static final Map<String, OldWorldType<?>> STR_TO_WT_MAP = Maps.newHashMap();
	
	public OldWorldType(String name, WorldTypeChunkGeneratorFactory<T> chunkGenSupplier) {
		this.generatorType = LevelGeneratorTypeFactory.createWorldType(name);
		this.chunkGenSupplier = chunkGenSupplier;
		
		if (this.generatorType == null) {
			throw new NullPointerException("An old world type has a null generator type: " + name + "!");
		}
		
		LGT_TO_WT_MAP.put(generatorType, this);
		STR_TO_WT_MAP.put(name, this);
	}
	
	public final LevelGeneratorType generatorType;
	public final WorldTypeChunkGeneratorFactory<T> chunkGenSupplier;
	
	// ===================== Instances ============================
	public static final OldWorldType<IndevChunkGenerator> INDEV = new OldWorldType<>("old_indev", (world) -> {
		IndevChunkGeneratorConfig chunkGenConfig = new IndevChunkGeneratorConfig();
		VanillaLayeredBiomeSourceConfig biomeSourceConfig = BiomeSourceType.VANILLA_LAYERED.getConfig().setLevelProperties(world.getLevelProperties()).setGeneratorSettings(chunkGenConfig);
		
		return IndevWorldGen.INDEV_CGT.create(world, BiomeSourceType.VANILLA_LAYERED.applyConfig(biomeSourceConfig), chunkGenConfig);
	});
	
	public static final OldWorldType<IndevChunkGenerator> INDEV_HELL = new OldWorldType<>("old_indev_hell", (world) -> {
		IndevChunkGeneratorConfig chunkGenConfig = new IndevChunkGeneratorConfig();
		VanillaLayeredBiomeSourceConfig biomeSourceConfig = BiomeSourceType.VANILLA_LAYERED.getConfig().setLevelProperties(world.getLevelProperties()).setGeneratorSettings(chunkGenConfig);
		
		chunkGenConfig.setType(IndevChunkGenerator.Type.HELL);
		return IndevWorldGen.INDEV_CGT.create(world, BiomeSourceType.VANILLA_LAYERED.applyConfig(biomeSourceConfig), chunkGenConfig);
	});
	
	public static final OldWorldType<IndevChunkGenerator> INDEV_FLOATING = new OldWorldType<>("old_indev_floating", (world) -> {
		IndevChunkGeneratorConfig chunkGenConfig = new IndevChunkGeneratorConfig();
		VanillaLayeredBiomeSourceConfig biomeSourceConfig = BiomeSourceType.VANILLA_LAYERED.getConfig().setLevelProperties(world.getLevelProperties()).setGeneratorSettings(chunkGenConfig);
		
		chunkGenConfig.setType(IndevChunkGenerator.Type.FLOATING);
		return IndevWorldGen.INDEV_CGT.create(world, BiomeSourceType.VANILLA_LAYERED.applyConfig(biomeSourceConfig), chunkGenConfig);
	});
	
	public static final OldWorldType<IndevChunkGenerator> INDEV_WOODS = new OldWorldType<>("old_indev_woods", (world) -> {
		IndevChunkGeneratorConfig chunkGenConfig = new IndevChunkGeneratorConfig();
		FixedBiomeSourceConfig biomeSourceConfig = BiomeSourceType.FIXED.getConfig().setBiome(Biomes.FOREST);
		
		chunkGenConfig.setType(IndevChunkGenerator.Type.WOODS);
		return IndevWorldGen.INDEV_CGT.create(world, BiomeSourceType.FIXED.applyConfig(biomeSourceConfig), chunkGenConfig);
	});
	
	public static final OldWorldType<IndevChunkGenerator> INDEV_ISLAND = new OldWorldType<>("old_indev_island", (world) -> {
		IndevChunkGeneratorConfig chunkGenConfig = new IndevChunkGeneratorConfig();
		VanillaLayeredBiomeSourceConfig biomeSourceConfig = BiomeSourceType.VANILLA_LAYERED.getConfig().setLevelProperties(world.getLevelProperties()).setGeneratorSettings(chunkGenConfig);
		
		chunkGenConfig.setType(IndevChunkGenerator.Type.ISLAND);
		return IndevWorldGen.INDEV_CGT.create(world, BiomeSourceType.VANILLA_LAYERED.applyConfig(biomeSourceConfig), chunkGenConfig);
	});
	
	public static final OldWorldType<IndevChunkGenerator> INDEV_PARADISE = new OldWorldType<>("old_indev_paradise", (world) -> {
		IndevChunkGeneratorConfig chunkGenConfig = new IndevChunkGeneratorConfig();
		VanillaLayeredBiomeSourceConfig biomeSourceConfig = BiomeSourceType.VANILLA_LAYERED.getConfig().setLevelProperties(world.getLevelProperties()).setGeneratorSettings(chunkGenConfig);
		
		chunkGenConfig.setType(IndevChunkGenerator.Type.PARADISE);
		return IndevWorldGen.INDEV_CGT.create(world, BiomeSourceType.VANILLA_LAYERED.applyConfig(biomeSourceConfig), chunkGenConfig);
	});
	
	public static final OldWorldType<AlphaChunkGenerator> ALPHA = new OldWorldType<>("old_alpha", (world) -> {
		AlphaChunkGeneratorConfig chunkGenConfig = new AlphaChunkGeneratorConfig();
		
		return IndevWorldGen.ALPHA_CGT.create(world, new OldBiomeSource(world.getSeed(), chunkGenConfig), chunkGenConfig);
	});
	// ===========================================================
	// ideally they would be the settings of the same world type
	// but idk how to do world type settings screens
	
	public static interface WorldTypeChunkGeneratorFactory<T extends ChunkGenerator<?>> {
		T create(World world);
	}
	
	/*
	 * AlphaChunkGeneratorConfig chunkGenConfig = new AlphaChunkGeneratorConfig();
		VanillaLayeredBiomeSourceConfig biomeSourceConfig = BiomeSourceType.VANILLA_LAYERED.getConfig().setLevelProperties(world.getLevelProperties()).setGeneratorSettings(chunkGenConfig);
		
		return IndevWorldGen.ALPHA_CGT.create(world, BiomeSourceType.VANILLA_LAYERED.applyConfig(biomeSourceConfig), chunkGenConfig);
	 */
}
