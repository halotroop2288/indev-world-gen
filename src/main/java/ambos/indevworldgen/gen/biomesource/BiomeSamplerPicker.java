package ambos.indevworldgen.gen.biomesource;

import net.minecraft.world.biome.source.BiomeLayerSampler;

public interface BiomeSamplerPicker {
	BiomeLayerSampler getSampler(HeightRetriever heightRetriever, int x, int z);
}
