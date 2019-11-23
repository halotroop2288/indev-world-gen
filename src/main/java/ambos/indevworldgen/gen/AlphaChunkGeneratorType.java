package ambos.indevworldgen.gen;

import java.util.function.Supplier;

import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;

public class AlphaChunkGeneratorType extends ChunkGeneratorType<AlphaChunkGeneratorConfig, AlphaChunkGenerator> {
	
	public AlphaChunkGeneratorType(boolean buffetScreen, Supplier<AlphaChunkGeneratorConfig> configSupplier) {
		super(null, buffetScreen, configSupplier);
	}
	
	public static void init() {
		// NO-OP
	}
	
	@Override
	public AlphaChunkGenerator create(World world, BiomeSource biomeSource, AlphaChunkGeneratorConfig config) {
		return new AlphaChunkGenerator(world, biomeSource, config);
	}
}