package ambos.indevworldgen.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import ambos.indevworldgen.worldtype.OldWorldType;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.OverworldDimension;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.level.LevelGeneratorType;

@Mixin(OverworldDimension.class)
public abstract class MixinOverworldDimension extends Dimension {
	public MixinOverworldDimension(World world, DimensionType type, float f) {
		super(world, type, f);
	}

	@Inject(method = "createChunkGenerator", at = @At("RETURN"), cancellable = true)
	public void createChunkGenerator(CallbackInfoReturnable<ChunkGenerator<? extends ChunkGeneratorConfig>> info) {
		LevelGeneratorType type = this.world.getLevelProperties().getGeneratorType();

		if(OldWorldType.LGT_TO_WT_MAP.containsKey(type)) {
			OldWorldType<?> worldType = OldWorldType.LGT_TO_WT_MAP.get(type);
			info.setReturnValue(
				worldType.chunkGenSupplier.create(this.world)
			);
		}
	}

}