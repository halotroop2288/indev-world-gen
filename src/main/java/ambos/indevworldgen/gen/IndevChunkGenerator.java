package ambos.indevworldgen.gen;

import java.util.List;

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
		ChunkPos chunkPos = chunk.getPos();
		final int startX = chunkPos.getStartX();
		final int startZ = chunkPos.getStartZ();
		BlockPos.Mutable pos = new BlockPos.Mutable();
		for (int localX = 0; localX < 16; ++localX) {
			pos.setX(startX + localX);
			for (int localZ = 0; localZ < 16; ++localZ) {
				pos.setZ(startZ + localZ);
				for (int y = 255; y >= 0; y--) {
					pos.setY(y);
					chunk.setBlockState(pos, y < 66 ? Blocks.STONE.getDefaultState() : Blocks.AIR.getDefaultState(), false);
				}
			}
		}
	}

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