package ambos.indevworldgen.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.text.Text;

@Mixin(CreateWorldScreen.class)
public class CreateWorldScreenMixin extends Screen {

	protected CreateWorldScreenMixin(Text title) {
		super(title);
	}

	@Inject(at = @At("HEAD"), method = "init()V")
	public void init() {
		
	}
}
