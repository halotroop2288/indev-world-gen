package ambos.indevworldgen.gen;

import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;

public class IndevChunkGeneratorConfig extends OverworldChunkGeneratorConfig {
	private IndevChunkGenerator.Type type = IndevChunkGenerator.Type.INLAND;
	
	@Override
	public int getRiverSize() { // actually alters the spread, not the width
		return 9; // not that river biomes will do anything much if our chunk generator ignores them
	}
	
	public IndevChunkGenerator.Type getType() {
		return type;
	}
	
	public void setType(IndevChunkGenerator.Type type) {
		this.type = type;
	}
}
