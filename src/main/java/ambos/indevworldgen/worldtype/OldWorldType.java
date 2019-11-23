package ambos.indevworldgen.worldtype;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import ambos.indevworldgen.IndevWorldGen;
import ambos.indevworldgen.gen.IndevChunkGenerator;
import ambos.indevworldgen.gen.IndevChunkGeneratorConfig;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeSourceType;
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
	
	// Instances
	public static final OldWorldType<IndevChunkGenerator> INDEV = new OldWorldType<>("old_indev", (world) -> {
		IndevChunkGeneratorConfig chunkGenConfig = new IndevChunkGeneratorConfig();
		VanillaLayeredBiomeSourceConfig biomeSourceConfig = BiomeSourceType.VANILLA_LAYERED.getConfig().setLevelProperties(world.getLevelProperties()).setGeneratorSettings(chunkGenConfig);
		
		return IndevWorldGen.INSTANCE.create(world, BiomeSourceType.VANILLA_LAYERED.applyConfig(biomeSourceConfig), chunkGenConfig);
	});
	
	public static interface WorldTypeChunkGeneratorFactory<T extends ChunkGenerator<?>> {
		T create(World world);
	}
}
