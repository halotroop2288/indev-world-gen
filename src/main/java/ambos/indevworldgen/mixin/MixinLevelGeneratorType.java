package ambos.indevworldgen.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import ambos.indevworldgen.worldtype.OldWorldType;
import net.minecraft.world.level.LevelGeneratorType;

@Mixin(LevelGeneratorType.class)
public class MixinLevelGeneratorType {
	
	@Inject(at = @At("HEAD"), method = "getTypeFromName", cancellable = true)
	private static void getTypeFromName(String name, CallbackInfoReturnable<LevelGeneratorType> info) {
		if (OldWorldType.STR_TO_WT_MAP.containsKey(name)) {
			info.setReturnValue(OldWorldType.STR_TO_WT_MAP.get(name).generatorType);
		}
	}
}