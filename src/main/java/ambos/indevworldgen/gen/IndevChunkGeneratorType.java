package ambos.indevworldgen.gen;

import java.util.function.Supplier;

import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;

public class IndevChunkGeneratorType extends ChunkGeneratorType<IndevChunkGeneratorConfig, IndevChunkGenerator> {
	
	public IndevChunkGeneratorType(boolean buffetScreen, Supplier<IndevChunkGeneratorConfig> configSupplier) {
		super(null, buffetScreen, configSupplier);
	}
	
	public static void init() {
		// NO-OP
	}
	
	@Override
	public IndevChunkGenerator create(World world, BiomeSource biomeSource, IndevChunkGeneratorConfig config) {
		return new IndevChunkGenerator(world, biomeSource, config);
	}
}