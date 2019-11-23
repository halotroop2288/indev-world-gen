package ambos.indevworldgen.gen;

import java.util.List;
import java.util.Random;

import ambos.indevworldgen.util.noise.OctaveAlphaNoiseSampler;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityCategory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.village.ZombieSiegeManager;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.IWorld;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.CatSpawner;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.PhantomSpawner;
import net.minecraft.world.gen.PillagerSpawner;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

public class AlphaChunkGenerator extends SurfaceChunkGenerator<AlphaChunkGeneratorConfig> {

	private OctaveAlphaNoiseSampler noise1;
	private OctaveAlphaNoiseSampler noise2;
	private OctaveAlphaNoiseSampler noise3;
	private OctaveAlphaNoiseSampler noise4;
	private OctaveAlphaNoiseSampler noise5;
	public OctaveAlphaNoiseSampler noise6;
	public OctaveAlphaNoiseSampler noise7;
	public OctaveAlphaNoiseSampler noise8;

	private Random rand2;

	private double field_4180_q[];
	private double field_905_r[];
    private double field_904_s[];
    private double field_903_t[];

	public AlphaChunkGenerator(IWorld world, BiomeSource biomeSource, AlphaChunkGeneratorConfig config) {
		super(world, biomeSource, 4, 8, 256, config, true);

		noise1 = new OctaveAlphaNoiseSampler(this.random, 16);
		noise2 = new OctaveAlphaNoiseSampler(this.random, 16);
		noise3 = new OctaveAlphaNoiseSampler(this.random, 8);
		noise4 = new OctaveAlphaNoiseSampler(this.random, 4);
		noise5 = new OctaveAlphaNoiseSampler(this.random, 4);
		noise6 = new OctaveAlphaNoiseSampler(this.random, 10);
		noise7 = new OctaveAlphaNoiseSampler(this.random, 16);
		noise8 = new OctaveAlphaNoiseSampler(this.random, 8);

		rand2 = new Random(world.getSeed());

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
/*
	private double[] initializeNoiseField(double ad[], int i, int j, int k, int l, int i1, int j1) {
		if(ad == null) {
			ad = new double[l * i1 * j1];
		}
		double d = 684.41200000000003D;
		double d1 = 684.41200000000003D;
		double ad1[] = ManagerOWG.temperature;
		double ad2[] = ManagerOWG.humidity;

		field_4182_g = field_922_a.func_4109_a(field_4182_g, i, k, l, j1, 1.121D, 1.121D, 0.5D);
		field_4181_h = field_921_b.func_4109_a(field_4181_h, i, k, l, j1, 200D, 200D, 0.5D);
		field_4185_d = field_910_m.func_807_a(field_4185_d, i, j, k, l, i1, j1, d / 80D, d1 / 160D, d / 80D);
		field_4184_e = field_912_k.func_807_a(field_4184_e, i, j, k, l, i1, j1, d, d1, d);
		field_4183_f = field_911_l.func_807_a(field_4183_f, i, j, k, l, i1, j1, d, d1, d);
		int k1 = 0;
		int l1 = 0;
		int i2 = 16 / l;
		for(int j2 = 0; j2 < l; j2++)
		{
			int k2 = j2 * i2 + i2 / 2;
			for(int l2 = 0; l2 < j1; l2++)
			{
				int i3 = l2 * i2 + i2 / 2;
				double d2 = ad1[k2 * 16 + i3];
				double d3 = ad2[k2 * 16 + i3] * d2;
				double d4 = 1.0D - d3;
				d4 *= d4;
				d4 *= d4;
				d4 = 1.0D - d4;
				double d5 = (field_4182_g[l1] + 256D) / 512D;
				d5 *= d4;
				if(d5 > 1.0D)
				{
					d5 = 1.0D;
				}
				double d6 = field_4181_h[l1] / 8000D;
				if(d6 < 0.0D)
				{
					d6 = -d6 * 0.29999999999999999D;
				}
				d6 = d6 * 3D - 2D;
				if(d6 < 0.0D)
				{
					d6 /= 2D;
					if(d6 < -1D)
					{
						d6 = -1D;
					}
					d6 /= 1.3999999999999999D;
					d6 /= 2D;
					d5 = 0.0D;
				} else
				{
					if(d6 > 1.0D)
					{
						d6 = 1.0D;
					}
					d6 /= 8D;
				}
				if(d5 < 0.0D)
				{
					d5 = 0.0D;
				}
				d5 += 0.5D;
				d6 = (d6 * (double)i1) / 16D;
				double d7 = (double)i1 / 2D + d6 * 4D;
				l1++;
				for(int j3 = 0; j3 < i1; j3++)
				{
					double d8 = 0.0D;
					double d9 = (((double)j3 - d7) * 12D) / d5;
					if(d9 < 0.0D)
					{
						d9 *= 4D;
					}
					double d10 = field_4184_e[k1] / 512D;
					double d11 = field_4183_f[k1] / 512D;
					double d12 = (field_4185_d[k1] / 10D + 1.0D) / 2D;
					if(d12 < 0.0D)
					{
						d8 = d10;
					} else
						if(d12 > 1.0D)
						{
							d8 = d11;
						} else
						{
							d8 = d10 + (d11 - d10) * d12;
						}
					d8 -= d9;
					if(j3 > i1 - 4)
					{
						double d13 = (float)(j3 - (i1 - 4)) / 3F;
						d8 = d8 * (1.0D - d13) + -10D * d13;
					}
					ad[k1] = d8;
					k1++;
				}

			}

		}

		return ad;
	}
*/ /*
	public void generateTerrain(int i, int j) {
		byte byte0 = 4;
		byte byte1 = 64;
		int k = byte0 + 1;
		byte byte2 = 17;
		int l = byte0 + 1;
		field_4180_q = initializeNoiseField(field_4180_q, i * byte0, 0, j * byte0, k, byte2, l);
		for(int i1 = 0; i1 < byte0; i1++)
		{
			for(int j1 = 0; j1 < byte0; j1++)
			{
				for(int k1 = 0; k1 < 16; k1++)
				{
					double d = 0.125D;
					double d1 = field_4180_q[((i1 + 0) * l + (j1 + 0)) * byte2 + (k1 + 0)];
					double d2 = field_4180_q[((i1 + 0) * l + (j1 + 1)) * byte2 + (k1 + 0)];
					double d3 = field_4180_q[((i1 + 1) * l + (j1 + 0)) * byte2 + (k1 + 0)];
					double d4 = field_4180_q[((i1 + 1) * l + (j1 + 1)) * byte2 + (k1 + 0)];
					double d5 = (field_4180_q[((i1 + 0) * l + (j1 + 0)) * byte2 + (k1 + 1)] - d1) * d;
					double d6 = (field_4180_q[((i1 + 0) * l + (j1 + 1)) * byte2 + (k1 + 1)] - d2) * d;
					double d7 = (field_4180_q[((i1 + 1) * l + (j1 + 0)) * byte2 + (k1 + 1)] - d3) * d;
					double d8 = (field_4180_q[((i1 + 1) * l + (j1 + 1)) * byte2 + (k1 + 1)] - d4) * d;
					for(int l1 = 0; l1 < 8; l1++)
					{
						double d9 = 0.25D;
						double d10 = d1;
						double d11 = d2;
						double d12 = (d3 - d1) * d9;
						double d13 = (d4 - d2) * d9;
						for(int i2 = 0; i2 < 4; i2++)
						{
							int j2 = i2 + i1 * 4 << 11 | 0 + j1 * 4 << 7 | k1 * 8 + l1;
							char c = '\200';
							double d14 = 0.25D;
							double d15 = d10;
							double d16 = (d11 - d10) * d14;
							for(int k2 = 0; k2 < 4; k2++)
							{
								double d17 = ad[(i1 * 4 + i2) * 16 + (j1 * 4 + k2)];
								Block l2 = Blocks.air;
								if(k1 * 8 + l1 < byte1)
								{
									if(d17 < 0.5D && k1 * 8 + l1 >= byte1 - 1)
									{
										l2 = Blocks.ice;
									} 
									else
									{
										l2 = Blocks.water;
									}
								}
								if(d15 > 0.0D)
								{
									l2 = Blocks.stone;
								}
								blocks[j2] = l2;
								j2 += c;
								d15 += d16;
							}

							d10 += d12;
							d11 += d13;
						}

						d1 += d5;
						d2 += d6;
						d3 += d7;
						d4 += d8;
					}

				}

			}

		}
	}
*/
	@Override
	public void buildSurface(Chunk chunk) {
		ChunkPos chunkPos = chunk.getPos();
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
	public int getSeaLevel() {
		return 64;
	}

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
}