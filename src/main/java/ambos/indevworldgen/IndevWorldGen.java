package ambos.indevworldgen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import ambos.indevworldgen.config.IndevWorldGenConfig;
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

	public static IndevWorldGenConfig config = new IndevWorldGenConfig();

	static OldWorldType<?> loadMeOnClientPls; // make sure world types are loaded on client by referencing a field in onInitialize()
	public static Logger log;
	
	@Override
	public void onInitialize() {
		log = LogManager.getLogger("Indev World Gen");
		
		loadConfig();

		loadMeOnClientPls = OldWorldType.INDEV;

		INDEV_CGT = Registry.register(Registry.CHUNK_GENERATOR_TYPE, IndevWorldGen.id("old_indev"), new IndevChunkGeneratorType(false, () -> new IndevChunkGeneratorConfig()));
		ALPHA_CGT = Registry.register(Registry.CHUNK_GENERATOR_TYPE, IndevWorldGen.id("old_alpha"), new AlphaChunkGeneratorType(false, () -> new AlphaChunkGeneratorConfig()));
	}

	private static void loadConfig() {
		Gson gson = new Gson();

		// first make sure config folder exists
		new File("./config/").mkdir();

		File loc = new File("./config/indevworldgen.json");
		try {
			if (loc.createNewFile()) {
				log.info("Creating config for indev world gen");

				try (FileWriter writer = new FileWriter(loc)) {
					writer.write("{\n" + 
							"  \"generateSwamps\": true\n" +
							"}");
				} catch (FileNotFoundException e) {
					throw new RuntimeException("Unhandled FileNotFoundException in generating config!");
				}
			}

			log.info("Loading indev world type config file");
			try (FileReader reader = new FileReader(loc)) {
				config = gson.fromJson(reader, IndevWorldGenConfig.class);
			} catch (FileNotFoundException e) {
				throw new RuntimeException("Unhandled FileNotFoundException in reading config!");
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Unhandled IOException in config handling!");
		}
	}

	public static Identifier id(String id) {
		return new Identifier(MOD_ID, id);
	}
}
