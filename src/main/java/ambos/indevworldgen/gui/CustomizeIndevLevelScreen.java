package ambos.indevworldgen.gui;

import java.util.List;
import java.util.stream.Collectors;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

@Environment(EnvType.CLIENT)
public class CustomizeIndevLevelScreen extends Screen {
	private static final List<Identifier> CHUNK_GENERATOR_TYPES;
	private final CreateWorldScreen parent;
	private final CompoundTag generatorOptionsTag;
	private int biomeListLength;
	private ButtonWidget confirmButton;

	public CustomizeIndevLevelScreen(CreateWorldScreen parent, CompoundTag generatorOptionsTag) {
		super(new TranslatableText("createWorld.customize.indev.title", new Object[0]));
		this.parent = parent;
		this.generatorOptionsTag = generatorOptionsTag;
	}

	protected void init() {
		this.minecraft.keyboard.enableRepeatEvents(true);
		this.addButton(new ButtonWidget((this.width - 200) / 2, 40, 200, 20,
				I18n.translate("createWorld.customize.buffet.generatortype") + " "
						+ I18n.translate(
								Util.createTranslationKey("generator", (Identifier) CHUNK_GENERATOR_TYPES.get(0))),
				(buttonWidget) -> {
					buttonWidget.setMessage(
							I18n.translate("createWorld.customize.buffet.generatortype") + " " + I18n.translate(
									Util.createTranslationKey("generator", (Identifier) CHUNK_GENERATOR_TYPES.get(0))));
				}));
		this.confirmButton = (ButtonWidget) this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150,
				20, I18n.translate("gui.done"), (buttonWidget) -> {
					this.parent.generatorOptionsTag = this.getGeneratorTag();
					this.minecraft.openScreen(this.parent);
				}));
		this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel"),
				(buttonWidget) -> {
					this.minecraft.openScreen(this.parent);
				}));
		this.initListSelectLogic();
		this.refreshConfirmButton();
	}

	private void initListSelectLogic() {
		int j;
		if (this.generatorOptionsTag.contains("chunk_generator", 10)
				&& this.generatorOptionsTag.getCompound("chunk_generator").contains("type", 8)) {
			Identifier identifier = new Identifier(
					this.generatorOptionsTag.getCompound("chunk_generator").getString("type"));
			for (j = 0; j < CHUNK_GENERATOR_TYPES.size(); ++j) {
				if (((Identifier) CHUNK_GENERATOR_TYPES.get(j)).equals(identifier)) {
					this.biomeListLength = j;
					break;
				}
			}
		}
		this.generatorOptionsTag.remove("chunk_generator");
	}

	private CompoundTag getGeneratorTag() {
		CompoundTag compoundTag = new CompoundTag();
		CompoundTag compoundTag2 = new CompoundTag();
		CompoundTag compoundTag3 = new CompoundTag();
		compoundTag2.put("options", compoundTag3);
		CompoundTag compoundTag4 = new CompoundTag();
		CompoundTag compoundTag5 = new CompoundTag();
		compoundTag4.putString("type", ((Identifier) CHUNK_GENERATOR_TYPES.get(this.biomeListLength)).toString());
		compoundTag5.putString("default_block", "minecraft:stone");
		compoundTag5.putString("default_fluid", "minecraft:water");
		compoundTag4.put("options", compoundTag5);
		compoundTag.put("chunk_generator", compoundTag4);
		return compoundTag;
	}

	public void refreshConfirmButton() {
		this.confirmButton.active = true;
	}

	public void render(int mouseX, int mouseY, float delta) {
		this.renderDirtBackground(0);
		this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 8, 16777215);
		this.drawCenteredString(this.font, I18n.translate("createWorld.customize.indev.generator"), this.width / 2, 30,
				10526880);
		super.render(mouseX, mouseY, delta);
	}

	static {
		// FIXME: This should only equal types that are identified as "old_indev", if
		// this doesn't work, or lists more than just indev, fix it
		CHUNK_GENERATOR_TYPES = (List<Identifier>) Registry.CHUNK_GENERATOR_TYPE.getIds().stream()
				.filter((identifier) -> {
					return Registry.CHUNK_GENERATOR_TYPE.containsId(new Identifier("old_indev"));
				}).collect(Collectors.toList());
	}
}
