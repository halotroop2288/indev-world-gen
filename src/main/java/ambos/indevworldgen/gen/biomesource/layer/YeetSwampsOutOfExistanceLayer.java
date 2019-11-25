package ambos.indevworldgen.gen.biomesource.layer;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.LayerRandomnessSource;
import net.minecraft.world.biome.layer.SouthEastSamplingLayer;

public enum YeetSwampsOutOfExistanceLayer implements SouthEastSamplingLayer {
	INSTANCE;
	
	private static final int SWAMP_ID = Registry.BIOME.getRawId(Biomes.SWAMP);
	
	private static final int PLAINS_ID = Registry.BIOME.getRawId(Biomes.PLAINS);
	private static final int FOREST_ID = Registry.BIOME.getRawId(Biomes.FOREST);
	
	@Override
	public int sample(LayerRandomnessSource context, int se) {
		return se == SWAMP_ID ? (context.nextInt(2) == 0 ? PLAINS_ID : FOREST_ID) : se;
	}

}
