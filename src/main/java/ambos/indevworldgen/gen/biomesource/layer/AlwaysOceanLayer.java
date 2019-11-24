package ambos.indevworldgen.gen.biomesource.layer;

import net.minecraft.world.biome.layer.InitLayer;
import net.minecraft.world.biome.layer.LayerRandomnessSource;

public enum AlwaysOceanLayer implements InitLayer {
	INSTANCE;

	@Override
	public int sample(LayerRandomnessSource context, int x, int y) {
		return 0;
	}
	
	
}
