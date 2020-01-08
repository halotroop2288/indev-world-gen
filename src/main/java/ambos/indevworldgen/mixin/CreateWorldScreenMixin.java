package ambos.indevworldgen.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ambos.indevworldgen.gui.CustomizeIndevLevelScreen;
import net.minecraft.client.gui.screen.CustomizeBuffetLevelScreen;
import net.minecraft.client.gui.screen.CustomizeFlatLevelScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.world.level.LevelGeneratorType;

@Mixin(CreateWorldScreen.class)
public class CreateWorldScreenMixin extends Screen {

	protected CreateWorldScreenMixin(Text title) {
		super(title);
	}
	
	@Shadow	private ButtonWidget customizeTypeButton;
	@Shadow private int generatorType;
	@Shadow private boolean isGeneratorTypeValid(){return true;}
	@Shadow private CompoundTag generatorOptionsTag;

	@Inject(at = @At("TAIL"), method = "init()V")
	public void init(CallbackInfo info)
	{
		System.out.println("Successfully injected to CreateWorldScreen");
		this.customizeTypeButton = (ButtonWidget)this.addButton(new ButtonWidget(this.width / 2 + 5, 120, 150, 20,
				I18n.translate("selectWorld.customizeType"), (buttonWidget) -> {
		    System.out.println("This should be printed, but it won't be.");
			if (LevelGeneratorType.TYPES[this.generatorType] == LevelGeneratorType.FLAT)
		    {
		    	this.minecraft.openScreen(new CustomizeFlatLevelScreen(((CreateWorldScreen)(Object)this), this.generatorOptionsTag));
		    }

		    if (LevelGeneratorType.TYPES[this.generatorType] == LevelGeneratorType.BUFFET)
		    {
				this.minecraft.openScreen(new CustomizeBuffetLevelScreen(((CreateWorldScreen)(Object)this), this.generatorOptionsTag));
		    }
		    
		    System.out.println("Trying...");
		    if (LevelGeneratorType.TYPES[this.generatorType].getName().contains("indev"))
		    {
				this.minecraft.openScreen(new CustomizeIndevLevelScreen(((CreateWorldScreen)(Object)this), this.generatorOptionsTag));
			}
		}));
		this.customizeTypeButton.visible = false;
	}
}
