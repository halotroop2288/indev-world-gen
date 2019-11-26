package ambos.indevworldgen.gen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ambos.indevworldgen.IndevWorldGen;
import ambos.indevworldgen.gen.biomesource.HeightRetriever;
import ambos.indevworldgen.gen.biomesource.OldBiomeSource;
import ambos.indevworldgen.util.noise.IndevNoiseSampler;
import ambos.indevworldgen.util.noise.OctaveAlpha11NoiseSampler;
import ambos.indevworldgen.util.noise.OctaveIndevNoiseSampler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityCategory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.ZombieSiegeManager;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.IWorld;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.CatSpawner;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.GenerationStep.Carver;
import net.minecraft.world.gen.PhantomSpawner;
import net.minecraft.world.gen.PillagerSpawner;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;
import net.minecraft.world.gen.decorator.CountExtraChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.RandomFeatureConfig;

public class IndevChunkGenerator extends SurfaceChunkGenerator<IndevChunkGeneratorConfig> implements HeightRetriever {

	public static enum Type {
		ISLAND,
		WOODS,
		FLOATING,
		INLAND,
		PARADISE,
		HELL
	}

	public final Type type;
	private final int size; // the original mod changed this based on type
	private final int layers = 2;
	private final double width = 2D;

	// I'm porting code from another mod back in 1.7.10
	private OctaveIndevNoiseSampler noiseGen1;
	private OctaveIndevNoiseSampler noiseGen2;
	private OctaveIndevNoiseSampler noiseGen3;
	private OctaveIndevNoiseSampler noiseGen4;
	public OctaveIndevNoiseSampler mainNoiseSampler;
	public OctaveIndevNoiseSampler noiseGen6;
	public OctaveIndevNoiseSampler noiseGen10;
	public OctaveIndevNoiseSampler noiseGen11;
	public IndevNoiseSampler perlin;
	
	public OctaveAlpha11NoiseSampler alphaTreeNoise;

	public IndevChunkGenerator(IWorld world, BiomeSource biomeSource, IndevChunkGeneratorConfig config) {
		super(world, biomeSource, 4, 8, 256, config, true);
		
		Random rand = new Random(world.getSeed());
		
		this.noiseGen1 = new OctaveIndevNoiseSampler(rand, 16);
		this.noiseGen2 = new OctaveIndevNoiseSampler(rand, 16);
		this.noiseGen3 = new OctaveIndevNoiseSampler(rand, 8);
		this.noiseGen4 = new OctaveIndevNoiseSampler(rand, 4);
		this.mainNoiseSampler = new OctaveIndevNoiseSampler(rand, 4);
		this.noiseGen6 = new OctaveIndevNoiseSampler(rand, 5);
		this.noiseGen10 = new OctaveIndevNoiseSampler(rand, 6);
		this.noiseGen11 = new OctaveIndevNoiseSampler(rand, 8);
		this.perlin = new IndevNoiseSampler(this.random);
		
		this.alphaTreeNoise = new OctaveAlpha11NoiseSampler(rand, 8);

		type = config.getType();

		this.size = type == Type.FLOATING ? 12 : 7;
		
		if (biomeSource instanceof OldBiomeSource) {
			((OldBiomeSource) biomeSource).setHeightRetriever(this);
		}

		this.random.consume(2620);
	}

	@Override
	public void populateEntities(ChunkRegion region) {
		int centreX = region.getCenterChunkX();
		int centreZ = region.getCenterChunkZ();
		Biome biome = region.getChunk(centreX, centreZ).getBiomeArray()[0];
		ChunkRandom rand = new ChunkRandom();
		rand.setSeed(region.getSeed(), centreX << 4, centreZ << 4);
		SpawnHelper.populateEntities(region, biome, centreX, centreZ, rand);
	}

	private static final BlockState AIR = Blocks.AIR.getDefaultState();
	private static final BlockState STONE = Blocks.STONE.getDefaultState();
	private static final BlockState GRASS = Blocks.GRASS_BLOCK.getDefaultState();
	private static final BlockState DIRT = Blocks.DIRT.getDefaultState();
	private static final BlockState GRAVEL = Blocks.GRAVEL.getDefaultState();
	private static final BlockState SAND = Blocks.SAND.getDefaultState();

	@Override
	public void populateNoise(IWorld world, Chunk chunk) {
	}

	@Override
	public void buildSurface(Chunk chunk) {
		ChunkPos chunkPos = chunk.getPos();
		if (this.type == Type.FLOATING) {
			this.generateSkylands(chunk, chunkPos.x, chunkPos.z);
			this.replaceSurfaceBlocks(chunk);
		} else {
			this.generateTerrain(chunk, chunkPos.x, chunkPos.z);
		}
	}

	public void replaceSurfaceBlocks(Chunk chunk) {
		BlockPos.Mutable pos = new BlockPos.Mutable();

		for (int x = 0; x < 16; ++x) {
			pos.setX(x);
			for (int z = 0; z < 16; ++z) {
				pos.setZ(z);
				int run = -1;
				boolean air = true;

				for (int y = 255; y >= 0; --y) {
					pos.setY(y);
					BlockState b = AIR;

					BlockState currentState = chunk.getBlockState(pos);
					if(currentState == AIR) {
						run = -1;
					} else if(currentState == STONE) {
						run++;
						if(run == 0 && air) {
							b = GRASS;
						} else if(run < 3) {
							b = DIRT;
						} else {
							b = STONE;
						}
						air = false;
					} else {
						run++;
						b = currentState;
					}

					chunk.setBlockState(pos, b, false);
				}
			}
		}
	}

	@Override
	public void carve(Chunk chunk, Carver carverStep) {
		if (this.type != Type.FLOATING) {
			super.carve(chunk, carverStep);
		}
	} // proudly stealing 1.7.10 code from https://github.com/Ted80-Minecraft-Mods/Old-World-Gen

	public void generateSkylands(Chunk chunk, int chunkX, int chunkZ) {
		int seaLevel = 64;
		int startX = chunkX << 4;
		int startZ = chunkZ << 4;

		BlockPos.Mutable posMutable = new BlockPos.Mutable();

		if(chunkX > -size && chunkX < size && chunkZ > -size && chunkZ < size) // I should remove this :P infinite floating islands!
		{
			for(int layer = 0; layer < layers; ++layer)
			{
				for (int x = startX; x < startX + 16; ++x)
				{
					posMutable.setX(x & 15);
					for (int z = startZ; z < startZ + 16; ++z)
					{
						posMutable.setZ(z & 15);

						float sampledValue = (float)this.mainNoiseSampler.sample((x + (layer * 2000F)) / 4.0F, (z + (layer * 2000F)) / 4.0F);
						int upperBound = 35 + (layer * 45) + ((int) sampledValue);

						if(upperBound < 1) 
						{ 
							upperBound = 1; 
						}

						if ((float)this.mainNoiseSampler.sample(x, z) < 0.0F)
						{
							upperBound = upperBound / 2 << 1;
							if ((float)this.mainNoiseSampler.sample(x / 5, z / 5) < 0.0F)
							{
								upperBound++;
							}	
						}

						int thickness = -25;
						int less = (int) Math.floor(Math.sqrt((x-0)*(x-0) + (z-0)*(z-0)) / width);
						if(less > 150) { less = 150; }
						thickness += less;

						double ovar32 = clamp(getNoise(8, x + (layer * 2000), z + (layer * 2000), 50, 50, 0));
						int lowerBound = (int) (ovar32 * (seaLevel / 2)) + 20 + (layer * 45) + thickness;

						boolean flagSand = noiseGen3.sample(x + (layer * 2000F), z + (layer * 2000F)) > 52D + (less / 3D); 
						boolean flagGravel = noiseGen11.sample(x + (layer * 2000F), z + (layer * 2000F)) > 62D + (less / 3D); 

						for (int y = 0; y < 256; y++) {
							posMutable.setY(y);

							BlockState toSet = AIR;
							if(y == upperBound) {
								if(flagGravel) {
									toSet = GRAVEL;
								} else if(flagSand) {
									toSet = SAND;
								} else if(y > lowerBound) {
									toSet = STONE;
								}
							}
							else if (y > lowerBound && y < upperBound)
							{
								toSet = STONE;
							}
							chunk.setBlockState(posMutable, toSet, false);
						}
					}	
				}
			}
		}
	}

	private double clamp(double input) {
		if (input > 1.0D) {
			return 1.0D;
		} if (input < -1.0D) {
			return -1.0D;
		}
		return input;
	}

	private double getNoise(int level, int x, int y, double xfact, double yfact, double zstart) {
		double result = 0;
		for (double l = 1; l <= level*level; l *= 2) {
			result += perlin.sample((x / xfact) * l, (y / yfact) * l) / l;
		}
		return result; 
	}

	// Code to port to 1.14.4 from 1.7.10
	// Do I even want to stream this lol
	public void generateTerrain(Chunk chunk, int chunkX, int chunkZ)
	{		
		final int seaLevel = 64;

		int startX = chunkX << 4;
		int startZ = chunkZ << 4;

		BlockPos.Mutable mutablePos = new BlockPos.Mutable();

		for (int x = startX; x < startX + 16; ++x) {
			mutablePos.setX(x & 15);
			for (int z = startZ; z < startZ + 16; ++z) {
				mutablePos.setZ(z & 15);
				int n = x / 1024;
				int i1 = z / 1024;

				int beachYValue = 64;
				if(this.type == Type.ISLAND) {
					float f2 = (float)this.mainNoiseSampler.sample(x / 4.0F, z / 4.0F);
					beachYValue = 74 - ((int) Math.floor(Math.sqrt((0D-x)*(0D-x) + (0D-z)*(0D-z)) / (double) size));
					if(beachYValue < 50) { beachYValue = 50; }
					beachYValue += ((int) f2);
				} else {
					float f1 = (float)(this.noiseGen1.sample(x / 0.03125F, 0.0D, z / 0.03125F) - this.noiseGen2.sample(x / 0.015625F, 0.0D, z / 0.015625F)) / 512.0F / 4.0F;
					float f2 = (float)this.mainNoiseSampler.sample(x / 4.0F, z / 4.0F);
					float f3 = (float)this.noiseGen6.sample(x / 8.0F, z / 8.0F) / 8.0F;
					f2 = f2 > 0.0F ? (float)(this.noiseGen3.sample(x * 0.2571428F * 2.0F, z * 0.2571428F * 2.0F) * f3 / 4.0D) : (float)(this.noiseGen4.sample(x * 0.2571428F, z * 0.2571428F) * f3);
					beachYValue = (int)(f1 + 64.0F + f2);
				}

				if ((float)this.mainNoiseSampler.sample(x, z) < 0.0F) {
					beachYValue = beachYValue / 2 << 1;
					if ((float)this.mainNoiseSampler.sample(x / 5, z / 5) < 0.0F) {
						beachYValue++;
					}	
				}

				boolean flagSand = noiseGen3.sample(x, z) > 8D;
				boolean flagGravel = noiseGen11.sample(x, z) > 18D;
				if(this.type == Type.PARADISE) { 
					flagSand = noiseGen3.sample(x, z) > -32D; 
				} else if(this.type == Type.HELL || this.type == Type.WOODS) { 
					flagSand = noiseGen3.sample(x, z) > -8D; 
				}

				if(this.type == Type.ISLAND) {
					flagSand = true;
				}

				// oh no time to port old 1.7 world gen
				for (int y = 0; y < 256; y++) {
					mutablePos.setY(y);
					BlockState toSet = AIR;

					int beachHeight = seaLevel + 1;
					if (this.type == Type.PARADISE) {
						beachHeight = seaLevel + 3;
					}

					if (y == 0) {
						toSet = Blocks.BEDROCK.getDefaultState();
					} else if ((y == beachYValue) && beachYValue >= beachHeight)  {
						if(this.type == Type.HELL)
						{
							toSet = Blocks.COARSE_DIRT.getDefaultState();
						}
						else
						{
							toSet = GRASS;
						}	
					} else if (y == beachYValue) { // beach
						if(flagGravel) {
							toSet = GRAVEL;
							if(this.type == Type.HELL) {
								toSet = GRASS;
							}
						} else if(flagSand) {
							toSet = SAND;
							if(this.type == Type.HELL) {
								toSet = GRASS;
							}
						} else if (beachYValue > seaLevel - 1) {
							toSet = GRASS;
						} else {
							toSet = DIRT;
						}
					} else if (y <= beachYValue - 2) {
						toSet = STONE;
					} else if (y < beachYValue) {
						toSet = DIRT;
					} else if (y <= 64 && !(this.type == Type.FLOATING)) {
						if(this.type == Type.HELL) {
							if (y == 64) {
								toSet = Blocks.LAVA.getDefaultState(); // hopefully this is fine
								// should be flowing
							} else {
								toSet = Blocks.LAVA.getDefaultState();
							}
						} else {
							toSet = Blocks.WATER.getDefaultState();
						}	
					}	

					random.setSeed(n + i1 * 13871);
					int i5 = (n << 10) + 128 + random.nextInt(512);
					int i6 = (i1 << 10) + 128 + random.nextInt(512);
					i5 = x - i5;
					int i7 = z - i6;
					if (i5 < 0) {
						i5 = -i5;
					} if (i7 < 0) {
						i7 = -i7;
					} if (i7 > i5) {
						i5 = i7;
					} if ((i5 = 127 - i5) == 255) {
						i5 = 1;
					} if (i5 < beachYValue) {
						i5 = beachYValue;
					}

					Block toSetBlock = toSet.getBlock();
					if ((y <= i5) && ((toSetBlock == Blocks.AIR) || (toSetBlock == Blocks.WATER) || (toSetBlock == Blocks.LAVA)))
					{
						toSet = Blocks.BRICKS.getDefaultState(); // nvm it's just a pyramid :P
					}

					chunk.setBlockState(mutablePos, toSet, false);
				}
			}	
		}
	}
	//*/

	@Override
	public int getSeaLevel() {
		return 64;
	}
	
	@Override
	public void generateFeatures(ChunkRegion region) {
		if (IndevWorldGen.config.alphaTreePlacementStyle) {
			generateFeaturesAlpha(region);
		} else {
			super.generateFeatures(region);
		}
	}

	private void generateFeaturesAlpha(ChunkRegion region) {
		int chunkX = region.getCenterChunkX();
		int chunkZ = region.getCenterChunkZ();
		int x = chunkX * 16;
		int z = chunkZ * 16;
		BlockPos pos = new BlockPos(x, 0, z);
		Biome biome = this.getDecorationBiome(region, pos.add(8, 8, 8));
		ChunkRandom random = new ChunkRandom();
		long seed = random.setSeed(region.getSeed(), x, z);
		GenerationStep.Feature[] featureSteps = GenerationStep.Feature.values();
		int stageCount = featureSteps.length;

		for(int i = 0; i < stageCount; ++i) {
			GenerationStep.Feature step = featureSteps[i];

			try {
				if (step == GenerationStep.Feature.VEGETAL_DECORATION) {
					this.alphaDecorateTrees(chunkX, chunkZ, x, z, pos, region);
				} else {
					biome.generateFeatureStep(step, this, region, seed, random, pos);
				}
			} catch (Exception e) {
				CrashReport crashReport = CrashReport.create(e, "Biome decoration");
				crashReport.addElement("Generation").add("CenterX", (Object)chunkX).add("CenterZ", (Object)chunkZ).add("Step", (Object)step).add("Seed", (Object)seed).add("Biome", (Object)Registry.BIOME.getId(biome));
				throw new CrashException(crashReport);
			}
		}
	}

	private final ChunkRandom alphaTreeRand = new ChunkRandom();

	// I copy pasted this from the alpha 1.1.2_01 jar file lol
	// I knew one day my alpha mapping files would be useful :P (not really, but it's nice that they were)
	private void alphaDecorateTrees(int chunkX, int chunkZ, int x, int z, BlockPos pos, IWorld region) {
		this.alphaTreeRand.setSeed(this.seed);
		this.alphaTreeRand.setSeed(chunkX * (this.alphaTreeRand.nextLong() / 2L * 2L + 1L) + chunkZ * (this.alphaTreeRand.nextLong() / 2L * 2L + 1L) ^ this.seed);

		double scale = 0.5;
		int count = (int)((this.alphaTreeNoise.sample(x * scale, z * scale) / 8.0 + this.alphaTreeRand.nextDouble() * 4.0 + 4.0) / 3.0);
		if (count < 0) {
			count = 0;
		}

		if (this.alphaTreeRand.nextInt(10) == 0) {
			++count;
		}

		ConfiguredFeature<?> randomTreeProvider = getConfiguredTreeFeature(count);
		
		// the tree provider is sometimes null so this check is neccessary
		if (randomTreeProvider != null && count > 0) {
			try {
				randomTreeProvider.generate(region, this, alphaTreeRand, pos);
			} catch (Exception e) {
				CrashReport crashReport = CrashReport.create(e, "Feature placement");
				CrashReportSection reportSection = crashReport.addElement("Feature").add("Id", (Object)Registry.FEATURE.getId(randomTreeProvider.feature));
				Feature<?> reportFeature = randomTreeProvider.feature;
				reportSection.add("Description", reportFeature::toString);
				throw new CrashException(crashReport);
			}
		}
	}

	private static ConfiguredFeature<?> getConfiguredTreeFeature(int count) {
		return configuredTreeCache.computeIfAbsent(count, e -> Biome.configureFeature(Feature.RANDOM_SELECTOR, new RandomFeatureConfig(new Feature[]{Feature.FANCY_TREE}, new FeatureConfig[]{FeatureConfig.DEFAULT}, new float[]{0.1f}, Feature.NORMAL_TREE, FeatureConfig.DEFAULT), Decorator.COUNT_EXTRA_HEIGHTMAP, new CountExtraChanceDecoratorConfig(count, 0.0f, 0)));
	}

	private static final Map<Integer, ConfiguredFeature<?>> configuredTreeCache = new HashMap<>();

	// overworld behaviour
	private final PhantomSpawner phantomSpawner = new PhantomSpawner();
	private final PillagerSpawner pillagerSpawner = new PillagerSpawner();
	private final CatSpawner catSpawner = new CatSpawner();
	private final ZombieSiegeManager zombieSiegeManager = new ZombieSiegeManager();

	@Override
	public int getSpawnHeight() {
		return this.world.getSeaLevel() + 1;
	}

	public List<Biome.SpawnEntry> getEntitySpawnList(EntityCategory category, BlockPos pos) {
		if (Feature.SWAMP_HUT.method_14029(this.world, pos)) {
			if (category == EntityCategory.MONSTER) {
				return Feature.SWAMP_HUT.getMonsterSpawns();
			}

			if (category == EntityCategory.CREATURE) {
				return Feature.SWAMP_HUT.getCreatureSpawns();
			}
		} else if (category == EntityCategory.MONSTER) {
			if (Feature.PILLAGER_OUTPOST.isApproximatelyInsideStructure(this.world, pos)) {
				return Feature.PILLAGER_OUTPOST.getMonsterSpawns();
			}

			if (Feature.OCEAN_MONUMENT.isApproximatelyInsideStructure(this.world, pos)) {
				return Feature.OCEAN_MONUMENT.getMonsterSpawns();
			}
		}

		return super.getEntitySpawnList(category, pos);
	}

	public void spawnEntities(ServerWorld serverWorld, boolean spawnMonsters, boolean spawnAnimals) {
		this.phantomSpawner.spawn(serverWorld, spawnMonsters, spawnAnimals);
		this.pillagerSpawner.spawn(serverWorld, spawnMonsters, spawnAnimals);
		this.catSpawner.spawn(serverWorld, spawnMonsters, spawnAnimals);
		this.zombieSiegeManager.tick(serverWorld, spawnMonsters, spawnAnimals);
	}

	// We don't need these
	@Override
	protected void sampleNoiseColumn(double[] array, int x, int z) {
		this.sampleNoiseColumn(array, x, z, 684.4119873046875D, 684.4119873046875D, 8.555149841308594D, 4.277574920654297D, 3, -10);
	}
	@Override
	protected double computeNoiseFalloff(double depth, double scale, int y) {
		return 0.1f;
	}
	@Override
	protected double[] computeNoiseRange(int x, int z) {
		return new double[] {0.1f, 0.1f}; 
	}

	@Override
	public int getHeight(int x, int z) {
		int maxGroundY = 0;

		final int seaLevel = 64;

		int n = x / 1024;
		int i1 = z / 1024;

		int beachYValue = 64;
		if(this.type == Type.ISLAND) {
			float f2 = (float)this.mainNoiseSampler.sample(x / 4.0F, z / 4.0F);
			beachYValue = 74 - ((int) Math.floor(Math.sqrt((0D-x)*(0D-x) + (0D-z)*(0D-z)) / (double) size));
			if(beachYValue < 50) { beachYValue = 50; }
			beachYValue += ((int) f2);
		} else {
			float f1 = (float)(this.noiseGen1.sample(x / 0.03125F, 0.0D, z / 0.03125F) - this.noiseGen2.sample(x / 0.015625F, 0.0D, z / 0.015625F)) / 512.0F / 4.0F;
			float f2 = (float)this.mainNoiseSampler.sample(x / 4.0F, z / 4.0F);
			float f3 = (float)this.noiseGen6.sample(x / 8.0F, z / 8.0F) / 8.0F;
			f2 = f2 > 0.0F ? (float)(this.noiseGen3.sample(x * 0.2571428F * 2.0F, z * 0.2571428F * 2.0F) * f3 / 4.0D) : (float)(this.noiseGen4.sample(x * 0.2571428F, z * 0.2571428F) * f3);
			beachYValue = (int)(f1 + 64.0F + f2);
		}

		if ((float)this.mainNoiseSampler.sample(x, z) < 0.0F) {
			beachYValue = beachYValue / 2 << 1;
			if ((float)this.mainNoiseSampler.sample(x / 5, z / 5) < 0.0F) {
				beachYValue++;
			}	
		}

		for (int y = 0; y < 128; y++) {
			boolean setSolidBlock = false;

			int beachHeight = seaLevel + 1;
			if (this.type == Type.PARADISE) {
				beachHeight = seaLevel + 3;
			}

			if (y == 0) {
				setSolidBlock = true;
			} else if ((y == beachYValue) && beachYValue >= beachHeight)  {
				setSolidBlock = true;
			} else if (y == beachYValue) { // beach
				setSolidBlock = true;
			} else if (y <= beachYValue - 2) {
				setSolidBlock = true;
			} else if (y < beachYValue) {
				setSolidBlock = true;
			} else if (y <= 64 && !(this.type == Type.FLOATING)) {
				setSolidBlock = false;
			}	

			random.setSeed(n + i1 * 13871);
			int i5 = (n << 10) + 128 + random.nextInt(512);
			int i6 = (i1 << 10) + 128 + random.nextInt(512);
			i5 = x - i5;
			int i7 = z - i6;
			if (i5 < 0) {
				i5 = -i5;
			} if (i7 < 0) {
				i7 = -i7;
			} if (i7 > i5) {
				i5 = i7;
			} if ((i5 = 127 - i5) == 255) {
				i5 = 1;
			} if (i5 < beachYValue) {
				i5 = beachYValue;
			}

			if ((y <= i5) && (!setSolidBlock)) {
				setSolidBlock = true;
			}

			if (setSolidBlock) {
				maxGroundY = y;
			}
		}

		return maxGroundY;
	}

	@Override
	public int getSeaLevelForBiomeGen() {
		return 64;
	}
}