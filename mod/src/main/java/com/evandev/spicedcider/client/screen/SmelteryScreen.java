package com.evandev.spicedcider.client.screen;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.content.casting.smeltery.SmelteryBlockEntity;
import com.evandev.spicedcider.content.casting.smeltery.SmelteryMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class SmelteryScreen extends AbstractContainerScreen<SmelteryMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "textures/gui/container/smeltery.png");
    public static final int FUEL_X = 8;
    public static final int FUEL_Y = 18;
    public static final int FUEL_W = 14;
    public static final int FUEL_H = 48;
    public static final int TANK_X = 124;
    public static final int TANK_Y = 18;
    public static final int TANK_W = 44;
    public static final int TANK_H = 48;
    public static final int BAR_W = 3;
    public static final int BAR_COLOR = 0xFFE8752A;

    public SmelteryScreen(SmelteryMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    private SmelteryBlockEntity smeltery() {
        SmelteryBlockEntity smeltery = this.menu.smeltery();
        if (smeltery == null && Minecraft.getInstance().level != null
                && Minecraft.getInstance().level.getBlockEntity(this.menu.pos()) instanceof SmelteryBlockEntity be) {
            smeltery = be;
        }
        return smeltery;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        graphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
        for (int slot = 0; slot < SmelteryBlockEntity.INPUTS; slot++) {
            int height = Math.round(this.menu.progress(slot) * 16.0F);
            if (height > 0) {
                int bx = x + SmelteryMenu.slotX(slot) + 18;
                int by = y + SmelteryMenu.slotY(slot) + 16;
                graphics.fill(bx, by - height, bx + BAR_W, by, BAR_COLOR);
            }
        }
        SmelteryBlockEntity smeltery = smeltery();
        if (smeltery == null) {
            return;
        }
        FluidStack fuel = smeltery.getFuel();
        if (!fuel.isEmpty()) {
            int height = Math.max(1, Math.round((float) FUEL_H * fuel.getAmount() / SmelteryBlockEntity.FUEL_CAPACITY));
            fluid(graphics, fuel, x + FUEL_X, y + FUEL_Y + FUEL_H - height, FUEL_W, height);
        }
        int bottom = y + TANK_Y + TANK_H;
        for (Segment segment : segments(smeltery)) {
            fluid(graphics, segment.fluid(), x + TANK_X, bottom - segment.top(), TANK_W, segment.top() - segment.bottom());
        }
    }

    private record Segment(FluidStack fluid, int index, int bottom, int top) {
    }

    private static List<Segment> segments(SmelteryBlockEntity smeltery) {
        List<Segment> list = new ArrayList<>();
        int level = 0;
        int amount = 0;
        List<FluidStack> metals = smeltery.getMetals();
        for (int i = 0; i < metals.size(); i++) {
            amount += metals.get(i).getAmount();
            int top = Math.max(level + 1, Math.round((float) TANK_H * amount / SmelteryBlockEntity.METAL_CAPACITY));
            top = Math.min(TANK_H, top);
            if (top > level) {
                list.add(new Segment(metals.get(i), i, level, top));
                level = top;
            }
        }
        return list;
    }

    private static void fluid(GuiGraphics graphics, FluidStack fluid, int x, int y, int width, int height) {
        if (height <= 0) {
            return;
        }
        IClientFluidTypeExtensions extensions = IClientFluidTypeExtensions.of(fluid.getFluid());
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(extensions.getStillTexture(fluid));
        int tint = extensions.getTintColor(fluid);
        float r = FastColor.ARGB32.red(tint) / 255.0F;
        float g = FastColor.ARGB32.green(tint) / 255.0F;
        float b = FastColor.ARGB32.blue(tint) / 255.0F;
        graphics.enableScissor(x, y, x + width, y + height);
        for (int ty = y + height - 16; ty > y - 16; ty -= 16) {
            for (int tx = x; tx < x + width; tx += 16) {
                graphics.blit(tx, ty, 0, 16, 16, sprite, r, g, b, 1.0F);
            }
        }
        graphics.disableScissor();
    }

    private boolean inTank(double mouseX, double mouseY) {
        double x = mouseX - this.leftPos;
        double y = mouseY - this.topPos;
        return x >= TANK_X && x < TANK_X + TANK_W && y >= TANK_Y && y < TANK_Y + TANK_H;
    }

    private Segment segmentAt(double mouseY) {
        SmelteryBlockEntity smeltery = smeltery();
        if (smeltery == null) {
            return null;
        }
        double fromBottom = this.topPos + TANK_Y + TANK_H - mouseY;
        for (Segment segment : segments(smeltery)) {
            if (fromBottom >= segment.bottom() && fromBottom < segment.top()) {
                return segment;
            }
        }
        return null;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (inTank(mouseX, mouseY) && this.minecraft != null && this.minecraft.gameMode != null) {
            Segment segment = segmentAt(mouseY);
            if (segment != null && button == 0 && hasShiftDown()) {
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, SmelteryMenu.DUMP_OFFSET + segment.index());
                return true;
            }
            if (segment != null && button == 0 && segment.index() > 0) {
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, segment.index());
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderTooltip(graphics, mouseX, mouseY);
        if (!this.menu.getCarried().isEmpty()) {
            return;
        }
        SmelteryBlockEntity smeltery = smeltery();
        int x = mouseX - this.leftPos;
        int y = mouseY - this.topPos;
        if (inTank(mouseX, mouseY)) {
            List<Component> lines = new ArrayList<>();
            Segment segment = segmentAt(mouseY);
            if (segment == null) {
                lines.add(Component.translatable("spicedcider.smeltery.metal", smeltery == null ? 0 : smeltery.metalAmount(), SmelteryBlockEntity.METAL_CAPACITY));
            } else {
                lines.add(Component.translatable("spicedcider.crucible.fluid", segment.fluid().getAmount(), segment.fluid().getHoverName()));
                lines.add(Component.translatable(segment.index() == 0 ? "spicedcider.smeltery.draining" : "spicedcider.smeltery.select").withStyle(net.minecraft.ChatFormatting.GRAY));
                lines.add(Component.translatable("spicedcider.smeltery.dump").withStyle(net.minecraft.ChatFormatting.DARK_GRAY));
            }
            graphics.renderComponentTooltip(this.font, lines, mouseX, mouseY);
        } else if (x >= FUEL_X && x < FUEL_X + FUEL_W && y >= FUEL_Y && y < FUEL_Y + FUEL_H) {
            FluidStack fuel = smeltery == null ? FluidStack.EMPTY : smeltery.getFuel();
            Component line = fuel.isEmpty()
                    ? Component.translatable("spicedcider.smeltery.no_fuel")
                    : Component.translatable("spicedcider.crucible.fluid", fuel.getAmount(), fuel.getHoverName());
            graphics.renderComponentTooltip(this.font, List.of(line), mouseX, mouseY);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
