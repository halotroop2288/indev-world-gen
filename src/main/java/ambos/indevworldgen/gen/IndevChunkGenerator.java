package ambos.indevworldgen.gen;

import java.util.List;

import net.minecraft.block.Block;
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

public class IndevChunkGenerator extends SurfaceChunkGenerator<IndevChunkGeneratorConfig> {
	
	public static enum Type {
		ISLAND,
		WOODS,
		FLOATING,
		INLAND
	}
	
	public final Type type = Type.INLAND;
	
	public IndevChunkGenerator(IWorld world, BiomeSource biomeSource, IndevChunkGeneratorConfig chunkGeneratorConfig) {
		super(world, biomeSource, 4, 8, 256, chunkGeneratorConfig, true);
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

	public void populateNoise(IWorld world, Chunk chunk) {
		BlockPos.Mutable pos = new BlockPos.Mutable();
		for (int localX = 0; localX < 16; ++localX) {
			pos.setX(localX);
			for (int localZ = 0; localZ < 16; ++localZ) {
				pos.setZ(localZ);
				for (int y = 255; y >= 0; y--) {
					pos.setY(y);
					chunk.setBlockState(pos, y < 66 ? Blocks.STONE.getDefaultState() : Blocks.AIR.getDefaultState(), false);
				}
			}
		}
	}

	private static final BlockState AIR = Blocks.AIR.getDefaultState();
	private static final BlockState STONE = Blocks.STONE.getDefaultState();
	private static final BlockState GRASS = Blocks.STONE.getDefaultState();
	private static final BlockState DIRT = Blocks.STONE.getDefaultState();

	public void generateSurfaceBlocks(int startX, int startZ, Chunk chunk) {
		BlockPos.Mutable pos = new BlockPos.Mutable();

		for (int x = 0; x < 16; ++x) {
			for (int z = 0; z < 16; ++z) {
				int run = -1;
				boolean air = true;

				for (int y = 255; y >= 0; --y) {
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

	/*
	public void generateTerrain(int par1, int par2, Block[] ba, byte[] bm)
	{		
		int height = 128;
		int seaLevel = 64;
		int i = par1 << 4;
		int j = par2 << 4;
		int jj = 0;
		int lx = 0; int lz = 0;

		for (int k = i; k < i + 16; k++)
		{
			for (int m = j; m < j + 16; m++)
			{
				int n = k / 1024;
				int i1 = m / 1024;

				int i2 = 64;
				if(this.type == Type.ISLAND)
				{
					float f2 = (float)this.noiseGen5.a(k / 4.0F, m / 4.0F);
					i2 = 74 - ((int) Math.floor(Math.sqrt((0D-k)*(0D-k) + (0D-m)*(0D-m)) / (double) size));
					if(i2 < 50) { i2 = 50; }
					i2 += ((int) f2);
				}
				else
				{
					float f1 = (float)(this.noiseGen1.a(k / 0.03125F, 0.0D, m / 0.03125F) - this.noiseGen2.a(k / 0.015625F, 0.0D, m / 0.015625F)) / 512.0F / 4.0F;
					float f2 = (float)this.noiseGen5.a(k / 4.0F, m / 4.0F);
					float f3 = (float)this.noiseGen6.a(k / 8.0F, m / 8.0F) / 8.0F;
					f2 = f2 > 0.0F ? (float)(this.noiseGen3.a(k * 0.2571428F * 2.0F, m * 0.2571428F * 2.0F) * f3 / 4.0D) : (float)(this.noiseGen4.a(k * 0.2571428F, m * 0.2571428F) * f3);
					i2 = (int)(f1 + 64.0F + f2);
				}

				if ((float)this.noiseGen5.a(k, m) < 0.0F)
				{
					i2 = i2 / 2 << 1;
					if ((float)this.noiseGen5.a(k / 5, m / 5) < 0.0F)
					{
						i2++;
					}	
				}

				//BEACH SETTINGS
				boolean flagSand = noiseGen3.a(k, m) > 8D;
				boolean flagGravel = noiseGen11.a(k, m) > 18D;
				if(themePARADISE)
				{ 
					flagSand = noiseGen3.a(k, m) > -32D; 
				}
				else if(themeHELL || themeWOODS)
				{ 
					flagSand = noiseGen3.a(k, m) > -8D; 
				}

				if(typeIsland)
				{
					flagSand = true;
				}

				//CREATE WORLD
				for (int i3 = 0; i3 < 256; i3++)
				{
					Block i4 = Blocks.air;
					int i4m = 0;
					int beachHeight = seaLevel + 1;
					if(themePARADISE){ beachHeight = seaLevel + 3; }

					//GENERATE BEDROCK
					if(i3 == 0)
					{
						i4 = Blocks.bedrock;
					}

					//GENERATE GRASS
					else if ((i3 == i2) && i2 >= beachHeight) 
					{
						if(themeHELL)
						{
							i4 = Blocks.dirt;
							i4m = 1;
						}
						else
						{
							i4 = Blocks.grass;
						}	
					}

					//BEACH GEN
					else if (i3 == i2)
					{
						if(flagGravel)
						{
							i4 = Blocks.gravel;
							if(themeHELL)
							{
								i4 = Blocks.grass;
							}
						}
						else if(flagSand)
						{
							i4 = Blocks.sand;
							if(themeHELL)
							{
								i4 = Blocks.grass;
							}
						}
						else if (i2 > seaLevel - 1)
						{
							i4 = Blocks.grass;
						}
						else
						{
							i4 = Blocks.dirt;
						}
					}

					//GENERATE STONE
					else if (i3 <= i2 - 2)
					{
						i4 = Blocks.stone;
					}

					//GENERATE DIRT
					else if (i3 < i2)
					{
						i4 = Blocks.dirt;
					}

					//GENERATE LIQUIDS
					else if (i3 <= 64 && !typeFloating)
					{
						if(themeHELL)
						{
							if (i3 == 64)
							{
								i4 = Blocks.flowing_lava;
							}
							else
							{
								i4 = Blocks.lava;
							}
						}
						else
						{
							i4 = Blocks.water;
						}	
					}	

					rand.setSeed(n + i1 * 13871);
					int i5 = (n << 10) + 128 + rand.nextInt(512);
					int i6 = (i1 << 10) + 128 + rand.nextInt(512);
					i5 = k - i5;
					int i7 = m - i6;
					if (i5 < 0)
					{
						i5 = -i5;
					}	
					if (i7 < 0)
					{
						i7 = -i7;
					}
					if (i7 > i5)
					{
						i5 = i7;
					}	
					if ((i5 = 127 - i5) == 255)
					{
						i5 = 1;
					}	
					if (i5 < i2)
					{
						i5 = i2;
					}	
					if ((i3 <= i5) && ((i4 == Blocks.air) || (i4 == Blocks.water) || (i4 == Blocks.lava)))
					{
						i4 = Blocks.brick_block;
					} 

					ba[jj] = i4;
					bm[jj] = (byte)i4m;
					jj++;
				}
			}	
		}
	}
	*/
	
	@Override
	public int getSeaLevel() {
		return 62;
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