package com.evandev.spicedcider.client.page;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.content.item.RuinedPageItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

public class RuinedPageScreen extends Screen {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "textures/gui/ruined_page.png");
    private static final String LOST = "§f§k§a§b";
    private static final int PAGE_WIDTH = 176;
    private static final int PAGE_HEIGHT = 224;
    private static final int TEXT_X = 22;
    private static final int TEXT_Y = 22;
    private static final int TEXT_WIDTH = 132;
    private static final int TEXT_HEIGHT = 172;
    private static final int PARAGRAPH_GAP = 4;
    private static final float[] SCALES = {1.0F, 0.875F, 0.75F, 0.625F};
    private static final int TEAL_INK = 0x1F5752;
    private static final int GREEN_INK = 0x3D5E22;
    private static final int LOST_INK = 0x8B5736;
    private static final int NUMBER_INK = 0x968B6C;

    private final int page;
    private final List<Component> paragraphs = new ArrayList<>();
    private final List<List<FormattedCharSequence>> lines = new ArrayList<>();
    private float scale = 1.0F;

    public RuinedPageScreen(int page) {
        super(Component.translatable("item.spicedcider.ruined_page"));
        this.page = page;
    }

    public static void open(int page) {
        Minecraft.getInstance().setScreen(new RuinedPageScreen(page));
    }

    @Override
    protected void init() {
        if (paragraphs.isEmpty()) parse();
        for (float candidate : SCALES) {
            scale = candidate;
            wrap((int) (TEXT_WIDTH / candidate));
            if (textHeight() * candidate <= TEXT_HEIGHT) break;
        }
    }

    private void parse() {
        RandomSource random = RandomSource.create();
        String name = minecraft != null ? minecraft.getUser().getName() : "";
        String text = I18n.get("item.spicedcider.ruined_page.text." + page).replace("PLAYERNAME", name);
        for (String paragraph : text.split("\n")) {
            int ink = paragraph.startsWith("§3") ? TEAL_INK : GREEN_INK;
            String body = paragraph.startsWith("§") ? paragraph.substring(2) : paragraph;
            MutableComponent component = Component.empty();
            int lost;
            while ((lost = body.indexOf(LOST)) >= 0) {
                component.append(Component.literal(body.substring(0, lost)).withColor(ink));
                component.append(Component.literal("XXXXXX".substring(0, 3 + random.nextInt(4)))
                        .withStyle(style -> style.withObfuscated(true).withColor(LOST_INK)));
                body = body.substring(lost + LOST.length());
                if (body.startsWith("§")) body = body.substring(2);
            }
            component.append(Component.literal(body).withColor(ink));
            paragraphs.add(component);
        }
    }

    private void wrap(int width) {
        lines.clear();
        for (Component paragraph : paragraphs) lines.add(font.split(paragraph, width));
    }

    private int textHeight() {
        int height = -PARAGRAPH_GAP;
        for (List<FormattedCharSequence> paragraph : lines) height += paragraph.size() * font.lineHeight + PARAGRAPH_GAP;
        return height;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        int left = (width - PAGE_WIDTH) / 2;
        int top = (height - PAGE_HEIGHT) / 2;
        graphics.blit(TEXTURE, left, top, 0, 0, PAGE_WIDTH, PAGE_HEIGHT);

        graphics.pose().pushPose();
        graphics.pose().translate(left + TEXT_X, top + TEXT_Y, 0);
        graphics.pose().scale(scale, scale, 1.0F);
        int y = 0;
        for (List<FormattedCharSequence> paragraph : lines) {
            for (FormattedCharSequence line : paragraph) {
                graphics.drawString(font, line, 0, y, GREEN_INK, false);
                y += font.lineHeight;
            }
            y += PARAGRAPH_GAP;
        }
        graphics.pose().popPose();

        String number = page + " / " + RuinedPageItem.PAGES;
        graphics.drawString(font, number, left + (PAGE_WIDTH - font.width(number)) / 2, top + PAGE_HEIGHT - 22, NUMBER_INK, false);
    }
}
