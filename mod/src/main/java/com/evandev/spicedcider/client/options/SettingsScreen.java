package com.evandev.spicedcider.client.options;

import com.evandev.spicedcider.client.options.entry.CategoryEntry;
import com.evandev.spicedcider.client.options.entry.Entry;
import com.evandev.spicedcider.client.options.entry.SearchEntry;
import com.evandev.spicedcider.client.options.entry.TextEntry;
import com.evandev.spicedcider.compat.controlify.ControlifySettings;
import com.evandev.spicedcider.compat.sodium.SodiumSetting;
import com.evandev.spicedcider.mixin.minecraft.client.PackAccessor;
import com.google.common.hash.Hashing;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.settings.KeyModifier;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class SettingsScreen extends Screen {

    private static final ResourceLocation DIRT = ResourceLocation.withDefaultNamespace("textures/block/dirt.png");
    private static final ResourceLocation UNKNOWN_PACK = ResourceLocation.withDefaultNamespace("textures/misc/unknown_pack.png");
    private static final ResourceLocation SCROLLER = ResourceLocation.withDefaultNamespace("widget/scroller");
    private static final ResourceLocation SCROLLER_BACKGROUND = ResourceLocation.withDefaultNamespace("widget/scroller_background");
    private static final int TOP = 24;
    private static final int BOTTOM_BAND = 28;
    private static final int SEPARATOR = 8;
    private static final int PAGE_ROW = 20;
    private static final int SCROLLBAR = 6;
    private static final Set<String> COLLAPSED = new HashSet<>();
    private static String lastPage = "general";

    private final Screen lastScreen;
    private final List<SettingsPage> pages;
    private final Map<String, ResourceLocation> packIcons = new HashMap<>();
    private SettingsPage page;
    private List<Entry> entries = List.of();
    private double scroll;
    private double sidebarScroll;
    private boolean draggingScroll;
    private boolean draggingSidebarScroll;
    @Nullable
    private Entry pressed;
    @Nullable
    private KeyMapping bindingKey;
    private InputConstants.Key lastPressedKey = InputConstants.UNKNOWN;
    private InputConstants.Key lastPressedModifier = InputConstants.UNKNOWN;
    private boolean keyHeld;
    private boolean modifierHeld;
    @Nullable
    private PackSelectionModel packModel;
    private boolean packsChanged;
    @Nullable
    private String pendingLanguage;
    @Nullable
    private Object captureToken;
    private Component captureName = Component.empty();
    private String searchQuery = "";
    @Nullable
    private SearchEntry searchEntry;
    private int guiScale;

    public SettingsScreen(Screen lastScreen) {
        super(Component.translatable("spicedcider.options.title"));
        this.lastScreen = lastScreen;
        this.pages = SettingsPages.create();
        this.page = pages.stream().filter(p -> p.id().equals(lastPage)).findFirst().orElse(pages.getFirst());
        if (ModList.get().isLoaded("sodium")) SodiumSetting.reload();
    }

    public static boolean useSodium() {
        return ModList.get().isLoaded("sodium");
    }

    public Set<String> collapsed() {
        return COLLAPSED;
    }

    @Nullable
    public KeyMapping bindingKey() {
        return bindingKey;
    }

    public void startBinding(KeyMapping mapping) {
        bindingKey = mapping;
        lastPressedKey = InputConstants.UNKNOWN;
        lastPressedModifier = InputConstants.UNKNOWN;
        keyHeld = false;
        modifierHeld = false;
    }

    public void beginCapture(Component name, Object token) {
        captureName = name;
        captureToken = token;
    }

    @Nullable
    public Object captureToken() {
        return captureToken;
    }

    public void endCapture() {
        captureToken = null;
    }

    private boolean capturing() {
        return bindingKey != null || captureToken != null;
    }

    public void collectSnapPoints(SnapSink sink) {
        layoutEntries();
        SnapSink clipped = (x, y, range) -> {
            if (y >= top() && y < bottom()) sink.accept(x, y, range);
        };
        for (Entry entry : entries) {
            if (entry.isVisibleBetween(top(), bottom())) entry.collectSnapPoints(clipped);
        }
        int y = sidebarTop();
        for (SettingsPage entry : visiblePages()) {
            clipped.accept(14, y + PAGE_ROW / 2, PAGE_ROW / 2);
            y += PAGE_ROW;
        }
        sink.accept(width / 2, height - 14, 10);
    }

    private int top() {
        return TOP;
    }

    private int bottom() {
        return height - BOTTOM_BAND;
    }

    private int sidebarWidth() {
        return Math.max(100, (int) (width * 0.25F));
    }

    private int pageX() {
        return sidebarWidth() + SEPARATOR + 8;
    }

    private int pageWidth() {
        return width - pageX() - SCROLLBAR - 10;
    }

    @Override
    protected void init() {
        guiScale = minecraft.options.guiScale().get();
        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> onClose()).bounds(width / 2 - 100, height - 24, 200, 20).build());
        rebuildEntries();
    }

    public void rebuildEntries() {
        entries = page.id().equals("search") ? buildSearch() : page.builder().apply(this);
        clampScroll();
    }

    private List<Entry> buildSearch() {
        if (searchEntry == null) {
            searchEntry = new SearchEntry(searchQuery, value -> {
                searchQuery = value;
                rebuildEntries();
            });
        }
        List<Entry> result = new ArrayList<>();
        result.add(searchEntry);
        String query = searchQuery.trim().toLowerCase(Locale.ROOT);
        if (query.isEmpty()) return result;

        Set<String> seen = new HashSet<>();
        for (SettingsPage other : pages) {
            if (!other.searchable() || !other.visible().getAsBoolean()) continue;
            for (Entry entry : other.builder().apply(this)) {
                if (!(entry instanceof CategoryEntry category)) continue;
                Component title = Component.translatable("spicedcider.options.search.path", other.title(), category.title());
                CategoryEntry filtered = category.filter(query, title);
                if (filtered == null) continue;
                filtered.children().removeIf(child -> child instanceof com.evandev.spicedcider.client.options.entry.SettingEntry setting && !seen.add(setting.setting().name().getString()));
                if (!filtered.children().isEmpty()) result.add(filtered);
            }
        }
        if (result.size() == 1) result.add(new TextEntry(Component.translatable("spicedcider.options.search.none")));
        return result;
    }

    private void selectPage(SettingsPage next) {
        if (next == page) return;
        applyPending();
        page = next;
        lastPage = next.id();
        scroll = 0;
        pressed = null;
        rebuildEntries();
        minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    private List<SettingsPage> visiblePages() {
        return pages.stream().filter(p -> p.visible().getAsBoolean()).toList();
    }

    private int contentHeight() {
        int total = 0;
        for (Entry entry : entries) total += entry.height();
        return total;
    }

    private int maxScroll() {
        return Math.max(0, contentHeight() - (bottom() - top()) + 4);
    }

    private int sidebarHeight() {
        return visiblePages().size() * PAGE_ROW;
    }

    private int maxSidebarScroll() {
        return Math.max(0, sidebarHeight() - (bottom() - top()));
    }

    private void clampScroll() {
        scroll = Mth.clamp(scroll, 0, maxScroll());
        sidebarScroll = Mth.clamp(sidebarScroll, 0, maxSidebarScroll());
    }

    private void layoutEntries() {
        clampScroll();
        int y = top() + 2 - (int) scroll;
        for (Entry entry : entries) {
            entry.setBounds(pageX(), y, pageWidth());
            y += entry.height();
        }
    }

    private int sidebarTop() {
        int height = sidebarHeight();
        int region = bottom() - top();
        return height < region ? top() + (region - height) / 2 : top() - (int) sidebarScroll;
    }

    private boolean inContent(double mouseX, double mouseY) {
        return mouseY >= top() && mouseY < bottom() && mouseX >= sidebarWidth() + SEPARATOR;
    }

    private boolean inSidebar(double mouseX, double mouseY) {
        return mouseY >= top() && mouseY < bottom() && mouseX >= 0 && mouseX < sidebarWidth();
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (minecraft.level == null) {
            tiledDirt(graphics, 0, 0, width, height, 0.125F, 0, 0);
            tiledDirt(graphics, 0, top(), sidebarWidth(), bottom(), 0.125F, 0, (float) sidebarScroll);
            tiledDirt(graphics, sidebarWidth() + SEPARATOR, top(), width, bottom(), 0.125F, 0, (float) scroll);
            tiledDirt(graphics, 0, 0, width, top(), 0.25F, 0, 0);
            tiledDirt(graphics, 0, bottom(), width, height, 0.25F, 0, 0);
            tiledDirt(graphics, sidebarWidth(), top(), sidebarWidth() + SEPARATOR, bottom(), 0.25F, 0, 0);
            graphics.fillGradient(0, top(), width, top() + 4, 0xFF000000, 0x00000000);
            graphics.fillGradient(0, bottom() - 4, width, bottom(), 0x00000000, 0xFF000000);
        } else {
            renderTransparentBackground(graphics);
            graphics.fill(0, 0, width, top(), 0x5F000000);
            graphics.fill(0, bottom(), width, height, 0x5F000000);
            graphics.fill(sidebarWidth(), top(), sidebarWidth() + SEPARATOR, bottom(), 0x5F000000);
        }
    }

    private static void tiledDirt(GuiGraphics graphics, int x0, int y0, int x1, int y1, float shade, float u, float v) {
        if (x1 <= x0 || y1 <= y0) return;
        graphics.setColor(shade, shade, shade, 1.0F);
        graphics.blit(DIRT, x0, y0, 0, x0 + u, y0 + v, x1 - x0, y1 - y0, 32, 32);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        boolean blocked = capturing();
        int mx = blocked ? -1 : mouseX;
        int my = blocked ? -1 : mouseY;
        super.render(graphics, mx, my, partialTick);

        layoutEntries();
        int contentMouseX = inContent(mx, my) ? mx : -1;
        int contentMouseY = inContent(mx, my) ? my : -1;
        graphics.enableScissor(sidebarWidth() + SEPARATOR, top(), width, bottom());
        for (Entry entry : entries) {
            if (entry.isVisibleBetween(top(), bottom())) {
                entry.render(graphics, contentMouseX, contentMouseY, partialTick);
            }
        }
        graphics.disableScissor();

        renderSidebar(graphics, inSidebar(mx, my) ? mx : -1, inSidebar(mx, my) ? my : -1);
        renderScrollbar(graphics, width - SCROLLBAR - 2, scroll, maxScroll(), contentHeight());
        renderScrollbar(graphics, sidebarWidth() - SCROLLBAR, sidebarScroll, maxSidebarScroll(), sidebarHeight());
        graphics.drawCenteredString(font, title, width / 2, 8, 0xFFFFFF);

        if (blocked) {
            graphics.fill(0, 0, width, height, 0xA0000000);
            Component prompt = bindingKey != null
                    ? Component.translatable("spicedcider.options.key.picking", bindingKey.getDisplayName())
                    : Component.translatable("spicedcider.options.controller.picking", captureName);
            graphics.drawCenteredString(font, prompt, width / 2, height / 2 - 4, 0xFFFFFF);
            return;
        }

        if (inContent(mx, my)) {
            for (Entry entry : entries) {
                if (entry.isMouseOver(mx, my)) {
                    List<FormattedCharSequence> tooltip = entry.tooltip(mx, my);
                    if (!tooltip.isEmpty()) graphics.renderTooltip(font, tooltip, mx, my);
                    break;
                }
            }
        }
    }

    private void renderSidebar(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.enableScissor(0, top(), sidebarWidth(), bottom());
        int y = sidebarTop();
        for (SettingsPage entry : visiblePages()) {
            boolean hovered = mouseX >= 0 && mouseY >= y && mouseY < y + PAGE_ROW;
            int color = hovered ? 0xFFFFFFA0 : entry == page ? 0xFFFFFFFF : 0xFF7F7F7F;
            graphics.renderItem(entry.icon(), 6, y + 2);
            graphics.drawString(font, entry.title(), 26, y + 6, color);
            y += PAGE_ROW;
        }
        graphics.disableScissor();
    }

    private void renderScrollbar(GuiGraphics graphics, int x, double amount, int max, int content) {
        if (max <= 0) return;
        int region = bottom() - top();
        int thumb = Mth.clamp((int) ((float) region * region / content), 32, region - 8);
        int thumbY = top() + (int) (amount * (region - thumb) / max);
        graphics.blitSprite(SCROLLER_BACKGROUND, x, top(), SCROLLBAR, region);
        graphics.blitSprite(SCROLLER, x, thumbY, SCROLLBAR, thumb);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (captureToken != null) {
            endCapture();
            return true;
        }
        if (bindingKey != null) {
            InputConstants.Key key = InputConstants.Type.MOUSE.getOrCreate(button);
            bindingKey.setKeyModifierAndCode(KeyModifier.NONE, key);
            minecraft.options.setKey(bindingKey, key);
            finishBinding();
            return true;
        }
        if (super.mouseClicked(mouseX, mouseY, button)) return true;

        if (maxScroll() > 0 && mouseX >= width - SCROLLBAR - 2 && mouseX < width - 2 && mouseY >= top() && mouseY < bottom()) {
            draggingScroll = true;
            return true;
        }
        if (maxSidebarScroll() > 0 && mouseX >= sidebarWidth() - SCROLLBAR && mouseX < sidebarWidth() && mouseY >= top() && mouseY < bottom()) {
            draggingSidebarScroll = true;
            return true;
        }

        if (inSidebar(mouseX, mouseY)) {
            int y = sidebarTop();
            for (SettingsPage entry : visiblePages()) {
                if (mouseY >= y && mouseY < y + PAGE_ROW) {
                    selectPage(entry);
                    return true;
                }
                y += PAGE_ROW;
            }
            return false;
        }

        if (inContent(mouseX, mouseY)) {
            layoutEntries();
            for (Entry entry : List.copyOf(entries)) {
                if (entry.isMouseOver(mouseX, mouseY) && entry.mouseClicked(mouseX, mouseY, button)) {
                    pressed = entry;
                    checkGuiScale();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggingScroll = false;
        draggingSidebarScroll = false;
        if (pressed != null) {
            Entry entry = pressed;
            pressed = null;
            entry.mouseReleased(mouseX, mouseY, button);
            checkGuiScale();
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        int region = bottom() - top();
        if (draggingScroll) {
            int thumb = Mth.clamp((int) ((float) region * region / contentHeight()), 32, region - 8);
            scroll += dragY * maxScroll() / Math.max(1, region - thumb);
            clampScroll();
            return true;
        }
        if (draggingSidebarScroll) {
            int thumb = Mth.clamp((int) ((float) region * region / sidebarHeight()), 32, region - 8);
            sidebarScroll += dragY * maxSidebarScroll() / Math.max(1, region - thumb);
            clampScroll();
            return true;
        }
        if (pressed != null) return pressed.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (capturing()) return true;
        double amount = scrollY * (hasControlDown() ? 96 : 24);
        if (inSidebar(mouseX, mouseY)) {
            sidebarScroll -= amount;
        } else if (inContent(mouseX, mouseY)) {
            scroll -= amount;
        }
        clampScroll();
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (captureToken != null) {
            if (keyCode == InputConstants.KEY_ESCAPE) endCapture();
            return true;
        }
        if (bindingKey != null) {
            InputConstants.Key key = InputConstants.getKey(keyCode, scanCode);
            if (lastPressedModifier == InputConstants.UNKNOWN && KeyModifier.isKeyCodeModifier(key)) {
                lastPressedModifier = key;
                modifierHeld = true;
            } else {
                lastPressedKey = key;
                keyHeld = true;
            }
            return true;
        }
        for (Entry entry : List.copyOf(entries)) {
            if (entry.keyPressed(keyCode, scanCode, modifiers)) return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (bindingKey == null || (net.minecraft.client.Minecraft.ON_OSX && scanCode == 63)) {
            return super.keyReleased(keyCode, scanCode, modifiers);
        }
        if (keyCode == InputConstants.KEY_ESCAPE) {
            bindingKey.setKeyModifierAndCode(KeyModifier.NONE, InputConstants.UNKNOWN);
            minecraft.options.setKey(bindingKey, InputConstants.UNKNOWN);
            finishBinding();
            return true;
        }
        InputConstants.Key key = InputConstants.getKey(keyCode, scanCode);
        if (lastPressedKey.equals(key)) keyHeld = false;
        else if (lastPressedModifier.equals(key)) modifierHeld = false;
        if (keyHeld || modifierHeld) return true;

        if (!lastPressedKey.equals(InputConstants.UNKNOWN)) {
            bindingKey.setKeyModifierAndCode(KeyModifier.getKeyModifier(lastPressedModifier), lastPressedKey);
            minecraft.options.setKey(bindingKey, lastPressedKey);
        } else {
            bindingKey.setKeyModifierAndCode(KeyModifier.NONE, lastPressedModifier);
            minecraft.options.setKey(bindingKey, lastPressedModifier);
        }
        finishBinding();
        return true;
    }

    private void finishBinding() {
        bindingKey = null;
        lastPressedKey = InputConstants.UNKNOWN;
        lastPressedModifier = InputConstants.UNKNOWN;
        keyHeld = false;
        modifierHeld = false;
        KeyMapping.resetMapping();
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        for (Entry entry : List.copyOf(entries)) {
            if (entry.charTyped(codePoint, modifiers)) return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public void tick() {
        for (Entry entry : entries) entry.tick();
    }

    private void checkGuiScale() {
        int current = minecraft.options.guiScale().get();
        if (current != guiScale) {
            guiScale = current;
            minecraft.resizeDisplay();
        }
    }

    public PackSelectionModel packModel() {
        if (packModel == null) {
            packsChanged = false;
            packModel = new PackSelectionModel(() -> {
                packsChanged = true;
                rebuildEntries();
            }, this::packIcon, minecraft.getResourcePackRepository(), repository -> minecraft.options.updateResourcePacks(repository));
            packModel.findNewPacks();
        }
        return packModel;
    }

    public boolean isPackVisible(String id) {
        Pack pack = minecraft.getResourcePackRepository().getPack(id);
        return pack != null && !pack.isHidden() && !((PackAccessor) pack).spicedcider$metadata().isHidden();
    }

    private ResourceLocation packIcon(Pack pack) {
        return packIcons.computeIfAbsent(pack.getId(), id -> loadPackIcon(pack));
    }

    private ResourceLocation loadPackIcon(Pack pack) {
        try (PackResources resources = pack.open()) {
            IoSupplier<InputStream> supplier = resources.getRootResource("pack.png");
            if (supplier == null) return UNKNOWN_PACK;
            String id = pack.getId();
            ResourceLocation location = ResourceLocation.withDefaultNamespace("pack/" + Util.sanitizeName(id, ResourceLocation::validPathChar) + "/" + Hashing.sha1().hashUnencodedChars(id) + "/icon");
            try (InputStream stream = supplier.get()) {
                minecraft.getTextureManager().register(location, new DynamicTexture(NativeImage.read(stream)));
            }
            return location;
        } catch (Exception e) {
            return UNKNOWN_PACK;
        }
    }

    public String selectedLanguage() {
        return pendingLanguage != null ? pendingLanguage : minecraft.getLanguageManager().getSelected();
    }

    public void selectLanguage(String code) {
        pendingLanguage = code;
    }

    private void applyPending() {
        if (packModel != null) {
            PackSelectionModel model = packModel;
            packModel = null;
            if (packsChanged) model.commit();
        }
        if (pendingLanguage != null) {
            String code = pendingLanguage;
            pendingLanguage = null;
            if (!code.equals(minecraft.getLanguageManager().getSelected())) {
                minecraft.getLanguageManager().setSelected(code);
                minecraft.options.languageCode = code;
                minecraft.reloadResourcePacks();
                minecraft.options.save();
            }
        }
    }

    @Override
    public void onClose() {
        applyPending();
        if (useSodium()) SodiumSetting.apply();
        if (ModList.get().isLoaded("controlify")) ControlifySettings.syncMovement(minecraft.options);
        minecraft.setScreen(lastScreen);
    }

    @Override
    public void removed() {
        minecraft.options.save();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return !capturing();
    }
}
