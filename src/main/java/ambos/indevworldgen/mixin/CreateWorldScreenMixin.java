package ambos.indevworldgen.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.world.level.LevelGeneratorType;

@Mixin(CreateWorldScreen.class)
public class CreateWorldScreenMixin extends Screen
{
	private ButtonWidget customizeIndevTypeButton;
	private String[] WORLD_TYPE_SUFFIXES;
	private int typeListLength;

	@Shadow	private CompoundTag generatorOptionsTag;
	@Shadow private int generatorType;
	@Shadow private boolean moreOptionsOpen;
	
	private CreateWorldScreenMixin(Text title)
	{	super(title);	}
	
	@Inject(at = @At("HEAD"), method = "init()V")
	private void initFirst(CallbackInfo info)
	{
		this.WORLD_TYPE_SUFFIXES = new String[] // FIXME: This probably shouldn't be hardcoded.
			{
				"inland", // Inland / Normal
				"hell", // Hell
				"floating", // Floating
				"woods", // Woods
				"island", // Island
				"paradise" // Paradise
			};
		this.typeListLength = 0;
		this.generatorType = 0;
		this.moreOptionsOpen = false;
		this.customizeIndevTypeButton = (ButtonWidget) this.addButton(new ButtonWidget(this.width / 2 + 5, 120, 150, 20,
			I18n.translate("generator.pick_indev_map_type") + I18n.translate("indev_maptype." + WORLD_TYPE_SUFFIXES[this.typeListLength]), (buttonWidget) ->
			{
				++this.typeListLength;
				if (this.typeListLength >= WORLD_TYPE_SUFFIXES.length) this.typeListLength = 0;
				
				buttonWidget.setMessage(I18n.translate("generator.pick_indev_map_type") +
					I18n.translate("indev_maptype." + WORLD_TYPE_SUFFIXES[typeListLength]));
			}));
		this.customizeIndevTypeButton.visible = false;
	}
	
	@Inject(at = @At("TAIL"), method = "setMoreOptionsOpen()V")
	private void showCustomizeIndevTypeButton(CallbackInfo info)
	{	try {
		this.customizeIndevTypeButton.visible = (this.moreOptionsOpen && LevelGeneratorType.TYPES[this.generatorType].getName().contains("indev")); }
		finally {}
	}
}
