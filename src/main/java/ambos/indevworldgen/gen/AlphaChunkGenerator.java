package ambos.indevworldgen.gen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ambos.indevworldgen.IndevWorldGen;
import ambos.indevworldgen.gen.biomesource.HeightRetriever;
import ambos.indevworldgen.gen.biomesource.OldBiomeSource;
import ambos.indevworldgen.util.noise.OctaveAlpha11NoiseSampler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityCategory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
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
import net.minecraft.world.gen.PhantomSpawner;
import net.minecraft.world.gen.PillagerSpawner;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;
import net.minecraft.world.gen.decorator.CountExtraChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.RandomFeatureConfig;

public class AlphaChunkGenerator extends SurfaceChunkGenerator<AlphaChunkGeneratorConfig> implements HeightRetriever {
	private OctaveAlpha11NoiseSampler noise1;
	private OctaveAlpha11NoiseSampler noise2;
	private OctaveAlpha11NoiseSampler noise3;
	private OctaveAlpha11NoiseSampler beachNoise;
	private OctaveAlpha11NoiseSampler surfaceNoise;
	public OctaveAlpha11NoiseSampler noise6;
	public OctaveAlpha11NoiseSampler noise7;
	public OctaveAlpha11NoiseSampler treeNoise;

	private double[] heightNoise;
	private double[] noiseArray1, noiseArray2, noiseArray3, noiseArray4, noiseArray5;
	private double[] sandSample = new double[256];
	private double[] gravelSample = new double[256];
	private double[] stoneNoise = new double[256];

	public AlphaChunkGenerator(IWorld world, BiomeSource biomeSource, AlphaChunkGeneratorConfig config) {
		super(world, biomeSource, 4, 8, 256, config, true);

		Random rand = new Random(world.getSeed());

		noise1 = new OctaveAlpha11NoiseSampler(rand, 16);
		noise2 = new OctaveAlpha11NoiseSampler(rand, 16);
		noise3 = new OctaveAlpha11NoiseSampler(rand, 8);
		beachNoise = new OctaveAlpha11NoiseSampler(rand, 4);
		surfaceNoise = new OctaveAlpha11NoiseSampler(rand, 4);
		noise6 = new OctaveAlpha11NoiseSampler(rand, 10);
		noise7 = new OctaveAlpha11NoiseSampler(rand, 16);
		treeNoise = new OctaveAlpha11NoiseSampler(rand, 8);

		this.random.consume(2620);

		if (biomeSource instanceof OldBiomeSource) {
			((OldBiomeSource) biomeSource).setHeightRetriever(this);
		}
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
		int chunkX = chunk.getPos().x;
		int chunkZ = chunk.getPos().z;
		final double oneEighth = 0.125D;
		final double oneQuarter = 0.25D;
		this.heightNoise = this.generateOctaves(this.heightNoise, chunkX * 4, 0, chunkZ * 4, 5, 17, 5);

		BlockPos.Mutable posMutable = new BlockPos.Mutable();

		for (int xSubChunk = 0; xSubChunk < 4; ++xSubChunk) {
			for (int zSubChunk = 0; zSubChunk < 4; ++zSubChunk) {
				for (int ySubChunk = 0; ySubChunk < 16; ++ySubChunk) {

					double sampleNWLow = this.heightNoise[(xSubChunk * 5 + zSubChunk) * 17 + ySubChunk];
					double sampleSWLow = this.heightNoise[((xSubChunk) * 5 + zSubChunk + 1) * 17 + ySubChunk];
					double sampleNELow = this.heightNoise[((xSubChunk + 1) * 5 + zSubChunk) * 17 + ySubChunk];
					double sampleSELow = this.heightNoise[((xSubChunk + 1) * 5 + zSubChunk + 1) * 17 + ySubChunk];

					double sampleNWHigh = (this.heightNoise[((xSubChunk) * 5 + zSubChunk) * 17 + ySubChunk + 1] - sampleNWLow) * oneEighth;
					double sampleSWHigh = (this.heightNoise[((xSubChunk) * 5 + zSubChunk + 1) * 17 + ySubChunk + 1] - sampleSWLow) * oneEighth;
					double sampleNEHigh = (this.heightNoise[((xSubChunk + 1) * 5 + zSubChunk) * 17 + ySubChunk + 1] - sampleNELow) * oneEighth;
					double sampleSEHigh = (this.heightNoise[((xSubChunk + 1) * 5 + zSubChunk + 1) * 17 + ySubChunk + 1] - sampleSELow) * oneEighth;

					for (int localY = 0; localY < 8; ++localY) {
						int y = ySubChunk * 8 + localY;
						posMutable.setY(y);

						double sampleNWInitial = sampleNWLow;
						double sampleSWInitial = sampleSWLow;
						double sampleNAverage = (sampleNELow - sampleNWLow) * oneQuarter;
						double sampleSAverage = (sampleSELow - sampleSWLow) * oneQuarter;

						for (int localX = 0; localX < 4; ++localX) {
							posMutable.setX(localX + xSubChunk * 4);

							double someValueToDoWithSettingStone = sampleNWInitial;
							double someOffsetThing = (sampleSWInitial - sampleNWInitial) * oneQuarter;

							for (int localZ = 0; localZ < 4; ++localZ) {
								posMutable.setZ(zSubChunk * 4 + localZ);

								BlockState toSet = AIR;

								if (y < this.getSeaLevel()) {
									toSet = Blocks.WATER.getDefaultState();
								}
								if (someValueToDoWithSettingStone > 0.0D) {
									toSet = STONE;
								}

								chunk.setBlockState(posMutable, toSet, false);
								someValueToDoWithSettingStone += someOffsetThing;
							}

							sampleNWInitial += sampleNAverage;
							sampleSWInitial += sampleSAverage;
						}

						sampleNWLow += sampleNWHigh;
						sampleSWLow += sampleSWHigh;
						sampleNELow += sampleNEHigh;
						sampleSELow += sampleSEHigh;
					}
				}
			}
		}
	}

	private double[] generateOctaves(double[] oldArray, int x, int y, int z, int xSize, int ySize, int zSize) {
		if (oldArray == null) {
			oldArray = new double[xSize * ySize * zSize];
		}

		final double const1 = 684.412D;
		final double const2 = 684.412D;
		this.noiseArray4 =
				this.noise6.sample(this.noiseArray4, (double) x, (double) y, (double) z, xSize, 1, zSize, 1.0D, 0.0D, 1.0D);
		this.noiseArray5 = this.noise7.sample(this.noiseArray5, (double) x, (double) y, (double) z, xSize, 1, zSize, 100.0D, 0.0D, 100.0D);
		this.noiseArray3 = this.noise3.sample(this.noiseArray3, (double) x, (double) y, (double) z, xSize, ySize, zSize, const1 / 80.0D, const2 / 160.0D, const1 / 80.0D);
		this.noiseArray1 = this.noise1.sample(this.noiseArray1, (double) x, (double) y, (double) z, xSize, ySize, zSize, const1, const2, const1);
		this.noiseArray2 = this.noise2.sample(this.noiseArray2, (double) x, (double) y, (double) z, xSize, ySize, zSize, const1, const2, const1);

		int index0 = 0;
		int index1 = 0;

		for (int localX = 0; localX < xSize; ++localX) {
			for (int localZ = 0; localZ < zSize; ++localZ) {
				double double0 = (this.noiseArray4[index1] + 256.0D) / 512.0D;
				if (double0 > 1.0D) {
					double0 = 1.0D;
				}

				double double2 = 0.0D;
				double double3 = this.noiseArray5[index1] / 8000.0D;
				if (double3 < 0.0D) {
					double3 = -double3;
				}

				double3 = double3 * 3.0D - 3.0D;
				if (double3 < 0.0D) {
					double3 = double3 / 2.0D;
					if (double3 < -1.0D) {
						double3 = -1.0D;
					}

					double3 = double3 / 1.4D;
					double3 = double3 / 2.0D;
					double0 = 0.0D;
				} else {
					if (double3 > 1.0D) {
						double3 = 1.0D;
					}

					double3 = double3 / 6.0D;
				}

				double0 = double0 + 0.5D;
				double3 = double3 * (double) ySize / 16.0D;
				double double4 = (double) ySize / 2.0D + double3 * 4.0D;
				++index1;

				for (int localY = 0; localY < ySize; ++localY) {
					double double1 = 0.0D;
					double double5 = ((double) localY - double4) * 12.0D / double0;
					if (double5 < 0.0D) {
						double5 *= 4.0D;
					}

					double sample0 = this.noiseArray1[index0] / 512.0D;
					double sample1 = this.noiseArray2[index0] / 512.0D;
					double sample2 = (this.noiseArray3[index0] / 10.0D + 1.0D) / 2.0D;
					if (sample2 < 0.0D) {
						double1 = sample0;
					} else if (sample2 > 1.0D) {
						double1 = sample1;
					} else {
						double1 = sample0 + (sample1 - sample0) * sample2;
					}

					double1 = double1 - double5;
					if (localY > ySize - 4) {
						double double6 = (double) ((float) (localY - (ySize - 4)) / 3.0F);
						double1 = double1 * (1.0D - double6) + -10.0D * double6;
					}

					if ((double) localY < double2) {
						double double7 = (double2 - (double) localY) / 4.0D;
						if (double7 < 0.0D) {
							double7 = 0.0D;
						}

						if (double7 > 1.0D) {
							double7 = 1.0D;
						}

						double1 = double1 * (1.0D - double7) + -10.0D * double7;
					}

					oldArray[index0] = double1;
					++index0;
				}
			}
		}

		return oldArray;
	}

	@Override
	public void buildSurface(Chunk chunk) {
		this.replaceSurfaceBlocks(chunk);
	}

	@Override
	protected void buildBedrock(Chunk chunk, Random random) {
	}

	private void replaceSurfaceBlocks(Chunk chunk) {
		BlockPos.Mutable pos = new BlockPos.Mutable();

		int chunkX = chunk.getPos().x;
		int chunkZ = chunk.getPos().z;

		final int seaLevel = this.getSeaLevel();
		final double oneSixteenth = 0.03125D;

		this.sandSample = this.beachNoise.sample(this.sandSample, chunkX * 16, chunkZ * 16, 0.0D, 16, 16, 1, oneSixteenth, oneSixteenth, 1.0D);
		this.gravelSample = this.beachNoise.sample(this.gravelSample, chunkZ * 16, 109.0134D, chunkX * 16, 16, 1, 16, oneSixteenth, 1.0D, oneSixteenth);
		this.stoneNoise = this.surfaceNoise.sample(this.stoneNoise, chunkX * 16, chunkZ * 16, 0.0D, 16, 16, 1, oneSixteenth * 2.0D, oneSixteenth * 2.0D, oneSixteenth * 2.0D);

		for (int x = 0; x < 16; x++) {
			pos.setX(x);
			for (int z = 0; z < 16; z++) {
				pos.setZ(z);
				boolean sandSampleAtPos = this.sandSample[(x + z * 16)] + random.nextDouble() * 0.2D > 0.0D;
				boolean gravelSampleAtPos = this.gravelSample[(x + z * 16)] + random.nextDouble() * 0.2D > 3.0D;
				int stoneSampleAtPos = (int) (this.stoneNoise[(x + z * 16)] / 3.0D + 3.0D + random.nextDouble() * 0.25D);
				int run = -1;
				BlockState topState = GRASS;
				BlockState underState = DIRT;

				for (int y = 256; y >= 128; --y) {
					pos.setY(y);
					chunk.setBlockState(pos, AIR, false);
				}
				for (int y = 127; y >= 0; --y) {
					pos.setY(y);
					if (y <= random.nextInt(6) - 1) {
						chunk.setBlockState(new BlockPos(x, y, z), Blocks.BEDROCK.getDefaultState(), false);
					} else {
						Block currentBlock = chunk.getBlockState(pos).getBlock();

						if (currentBlock == Blocks.AIR) {
							run = -1;
						} else if (currentBlock == Blocks.STONE) {
							if (run == -1) {
								if (stoneSampleAtPos <= 0) {
									topState = AIR;
									underState = STONE;
								} else if ((y >= seaLevel - 4) && (y <= seaLevel + 1)) {
									topState = GRASS;
									underState = DIRT;

									if (gravelSampleAtPos) {
										topState = AIR;
										underState = GRAVEL;
									}

									if (sandSampleAtPos) {
										topState = SAND;
										underState = SAND;
									}
								}

								if ((y < seaLevel) && topState.isAir()) {
									topState = Blocks.WATER.getDefaultState();
								}

								run = stoneSampleAtPos;
								if (y >= seaLevel - 1) {
									chunk.setBlockState(new BlockPos(x, y, z), topState, false);
								} else {
									chunk.setBlockState(new BlockPos(x, y, z), underState, false);
								}
							} else if (run > 0) {
								run--;
								chunk.setBlockState(new BlockPos(x, y, z), underState, false);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public int getSeaLevel() {
		return 64;
	}

	@Override
	public void generateFeatures(ChunkRegion region) {
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
		int count = (int)((this.treeNoise.sample(x * scale, z * scale) / 8.0 + this.alphaTreeRand.nextDouble() * 4.0 + 4.0) / 3.0);
		if (count < 0) {
			count = 0;
		}

		if (this.alphaTreeRand.nextInt(10) == 0) {
			++count;
		}

		ConfiguredFeature<?> randomTreeProvider = getConfiguredTreeFeature(count);
		
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
		} else {
			IndevWorldGen.log.info("wtf null");
		}
		System.out.println("finished tree gen");
	}

	private static ConfiguredFeature<?> getConfiguredTreeFeature(int count) {
		return configuredTreeCache.computeIfAbsent(count, e -> Biome.configureFeature(Feature.RANDOM_SELECTOR, new RandomFeatureConfig(new Feature[]{Feature.FANCY_TREE}, new FeatureConfig[]{FeatureConfig.DEFAULT}, new float[]{0.1f}, Feature.NORMAL_TREE, FeatureConfig.DEFAULT), Decorator.COUNT_EXTRA_HEIGHTMAP, new CountExtraChanceDecoratorConfig(count, 0.0f, 0)));
	}

	private static final Map<Integer, ConfiguredFeature<?>> configuredTreeCache = new HashMap<>();

	// */
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

	// We don't actually need these
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
	public int getHeight(int x, int z) { // lol I should probably make this caching
		int chunkX = (x >> 4);
		int chunkZ = (z >> 4);
		final double oneEighth = 0.125D;
		final double oneQuarter = 0.25D;

		this.heightNoise = this.generateOctaves(this.heightNoise, chunkX * 4, 0, chunkZ * 4, 5, 17, 5);

		int xSubChunk = (x >> 2) & 0b11;
		int zSubChunk = (z >> 2) & 0b11;

		int maxGroundY = 0;

		final int actualLocalX = x - chunkX - xSubChunk;
		final int actualLocalZ = x - chunkZ - zSubChunk;

		for (int ySubChunk = 0; ySubChunk < 16; ++ySubChunk) {

			double sampleNWLow = this.heightNoise[(xSubChunk * 5 + zSubChunk) * 17 + ySubChunk];
			double sampleSWLow = this.heightNoise[((xSubChunk) * 5 + zSubChunk + 1) * 17 + ySubChunk];
			double sampleNELow = this.heightNoise[((xSubChunk + 1) * 5 + zSubChunk) * 17 + ySubChunk];
			double sampleSELow = this.heightNoise[((xSubChunk + 1) * 5 + zSubChunk + 1) * 17 + ySubChunk];

			double sampleNWHigh = (this.heightNoise[((xSubChunk) * 5 + zSubChunk) * 17 + ySubChunk + 1] - sampleNWLow) * oneEighth;
			double sampleSWHigh = (this.heightNoise[((xSubChunk) * 5 + zSubChunk + 1) * 17 + ySubChunk + 1] - sampleSWLow) * oneEighth;
			double sampleNEHigh = (this.heightNoise[((xSubChunk + 1) * 5 + zSubChunk) * 17 + ySubChunk + 1] - sampleNELow) * oneEighth;
			double sampleSEHigh = (this.heightNoise[((xSubChunk + 1) * 5 + zSubChunk + 1) * 17 + ySubChunk + 1] - sampleSELow) * oneEighth;

			for (int localY = 0; localY < 8; ++localY) {
				int y = ySubChunk * 8 + localY;;

				double sampleNWInitial = sampleNWLow;
				double sampleSWInitial = sampleSWLow;
				double sampleNAverage = (sampleNELow - sampleNWLow) * oneQuarter;
				double sampleSAverage = (sampleSELow - sampleSWLow) * oneQuarter;

				xloop: for (int localX = 0; localX < 4; ++localX) {

					double someValueToDoWithSettingStone = sampleNWInitial;
					double someOffsetThing = (sampleSWInitial - sampleNWInitial) * oneQuarter;

					for (int localZ = 0; localZ < 4; ++localZ) {

						if (someValueToDoWithSettingStone > 0.0D) {
							maxGroundY = y;
						}

						someValueToDoWithSettingStone += someOffsetThing;

						if (actualLocalZ == localZ && actualLocalX == localX) {
							break xloop;
						}
					}

					sampleNWInitial += sampleNAverage;
					sampleSWInitial += sampleSAverage;
				}

				sampleNWLow += sampleNWHigh;
				sampleSWLow += sampleSWHigh;
				sampleNELow += sampleNEHigh;
				sampleSELow += sampleSEHigh;
			}
		}

		return maxGroundY;
	}

	@Override
	public int getSeaLevelForBiomeGen() {
		return 64;
	}
}