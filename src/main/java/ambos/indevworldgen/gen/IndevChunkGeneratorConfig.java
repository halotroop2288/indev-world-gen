package ambos.indevworldgen.gen;

import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;

public class IndevChunkGeneratorConfig extends OverworldChunkGeneratorConfig {
	@Override
	public int getBiomeSize() {
		return 3;
	}
	
	@Override
	public int getRiverSize() { // actually alters the spread, not the width
		return 9; // not that river biomes will do anything much if our chunk generator ignores them
	}
}
