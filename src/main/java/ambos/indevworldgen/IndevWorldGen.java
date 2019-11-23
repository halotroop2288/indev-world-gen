package ambos.indevworldgen;

import ambos.indevworldgen.gen.AlphaChunkGeneratorConfig;
import ambos.indevworldgen.gen.AlphaChunkGeneratorType;
import ambos.indevworldgen.gen.IndevChunkGeneratorConfig;
import ambos.indevworldgen.gen.IndevChunkGeneratorType;
import ambos.indevworldgen.worldtype.OldWorldType;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class IndevWorldGen implements ModInitializer {
	private static final String MOD_ID = "indevworldgen";
	
	public static IndevChunkGeneratorType INDEV_CGT;
	public static AlphaChunkGeneratorType ALPHA_CGT;
	
	static OldWorldType<?> loadMeOnClientPls; // make sure world types are loaded on client by referencing a field in onInitialize()
	
	@Override
	public void onInitialize() {
		loadMeOnClientPls = OldWorldType.INDEV;
		
		INDEV_CGT = Registry.register(Registry.CHUNK_GENERATOR_TYPE, IndevWorldGen.id("old_indev"), new IndevChunkGeneratorType(false, () -> new IndevChunkGeneratorConfig()));
		ALPHA_CGT = Registry.register(Registry.CHUNK_GENERATOR_TYPE, IndevWorldGen.id("old_alpha"), new AlphaChunkGeneratorType(false, () -> new AlphaChunkGeneratorConfig()));
	}

	public static Identifier id(String id) {
		return new Identifier(MOD_ID, id);
	}
}
