package dev.emi.emi.jemi;

import java.util.List;

import com.google.common.collect.Lists;

import dev.emi.emi.EmiPort;
import dev.emi.emi.EmiUtil;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.config.EmiConfig;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class JemiStack<T> extends EmiStack {
	private final IIngredientType<T> type;
	private final IIngredientHelper<T> helper;
	private final IIngredientRenderer<T> renderer;
	public final T ingredient;

	public JemiStack(IIngredientType<T> type, IIngredientHelper<T> helper, IIngredientRenderer<T> renderer, T ingredient) {
		this.type = type;
		this.helper = helper;
		this.renderer = renderer;
		this.ingredient = ingredient;
	}

	@Override
	public void render(MatrixStack matrices, int x, int y, float delta, int flags) {
		int xOff = (16 - renderer.getWidth()) / 2;
		int yOff = (16 - renderer.getHeight()) / 2;
		matrices.push();
		matrices.translate(x + xOff, y + yOff, 0);
		renderer.render(matrices, ingredient);
		matrices.pop();
	}

	@Override
	public EmiStack copy() {
		return new JemiStack<T>(type, helper, renderer, helper.copyIngredient(ingredient));
	}

	@Override
	public boolean isEmpty() {
		return !helper.isValidIngredient(ingredient);
	}

	@Override
	public NbtCompound getNbt() {
		return null;
	}

	@Override
	public Object getKey() {
		return helper.getUniqueId(ingredient, UidContext.Ingredient);
	}

	@Override
	public Identifier getId() {
		return helper.getResourceLocation(ingredient);
	}

	@Override
	public List<Text> getTooltipText() {
		return renderer.getTooltip(ingredient, TooltipContext.BASIC);
	}

	@Override
	public List<TooltipComponent> getTooltip() {
		List<TooltipComponent> list = Lists.newArrayList();
		MinecraftClient client = MinecraftClient.getInstance();
		list.addAll(renderer.getTooltip(ingredient, client.options.advancedItemTooltips ? TooltipContext.Default.ADVANCED : TooltipContext.Default.BASIC)
			.stream().map(EmiPort::ordered).map(TooltipComponent::of).toList());

		Identifier id = getId();
		if (EmiConfig.appendModId && id != null) {
			String mod = EmiUtil.getModName(id.getNamespace());
			list.add(TooltipComponent.of(EmiPort.ordered(EmiPort.literal(mod, Formatting.BLUE, Formatting.ITALIC))));
		}

		list.addAll(super.getTooltip());
		return list;
	}

	@Override
	public Text getName() {
		return EmiPort.literal(helper.getDisplayName(ingredient));
	}
}
