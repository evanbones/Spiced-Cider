package com.evandev.spicedcider.datagen.providers;

import com.evandev.spicedcider.SpicedCider;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import net.minecraft.core.BlockPos;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ModPonderSchematicProvider implements DataProvider {

    private final PackOutput.PathProvider pathProvider;

    public ModPonderSchematicProvider(PackOutput output) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "ponder");
    }

    private static Schematic ironGolem() {
        return new Schematic(5)
                .set(2, 1, 2, "minecraft:iron_block")
                .fill(1, 2, 2, 3, 2, 2, "minecraft:iron_block")
                .set(2, 3, 2, "minecraft:carved_pumpkin", "facing", "north");
    }

    private static Schematic snowGolem() {
        return new Schematic(5)
                .fill(1, 1, 1, 3, 1, 3, "minecraft:snow", "layers", "1")
                .fill(2, 1, 2, 2, 2, 2, "minecraft:snow_block")
                .set(2, 3, 2, "minecraft:carved_pumpkin", "facing", "north");
    }

    private static Schematic copperGolem() {
        return new Schematic(5)
                .set(2, 1, 2, "minecraft:copper_block")
                .set(2, 2, 2, "minecraft:carved_pumpkin", "facing", "north");
    }

    private static Schematic copperGolemSorting() {
        return new Schematic(5)
                .set(0, 1, 2, "minecraft:copper_chest", "facing", "north")
                .set(4, 1, 1, "quark:oak_chest", "facing", "west")
                .set(4, 1, 3, "quark:oak_chest", "facing", "west");
    }

    private static Schematic sputter() {
        return new Schematic(5)
                .set(2, 1, 2, "minecraft:barrel", "facing", "up")
                .set(2, 2, 2, "minecraft:carved_pumpkin", "facing", "north");
    }

    private static Schematic wither() {
        return new Schematic(5)
                .set(2, 1, 2, "minecraft:soul_sand")
                .set(1, 2, 2, "minecraft:soul_soil")
                .set(2, 2, 2, "minecraft:soul_sand")
                .set(3, 2, 2, "minecraft:soul_sand")
                .fill(1, 3, 2, 3, 3, 2, "minecraft:wither_skeleton_skull", "rotation", "8");
    }

    private static Schematic tuningFork() {
        return new Schematic(5)
                .set(3, 0, 2, "minecraft:copper_block");
    }

    private static Schematic netherPortal() {
        return new Schematic(6)
                .fill(1, 1, 2, 4, 1, 2, "minecraft:obsidian")
                .fill(1, 5, 2, 4, 5, 2, "minecraft:obsidian")
                .fill(1, 2, 2, 1, 4, 2, "minecraft:obsidian")
                .fill(4, 2, 2, 4, 4, 2, "minecraft:obsidian");
    }

    private static Schematic beacon() {
        Schematic schematic = new Schematic(5);
        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                schematic.set(x, 1, z, (x + z) % 3 == 0 ? "minecraft:gold_block" : "minecraft:iron_block");
            }
        }
        return schematic
                .fill(1, 2, 1, 3, 2, 3, "minecraft:iron_block")
                .set(2, 2, 2, "minecraft:diamond_block")
                .set(2, 3, 2, "minecraft:beacon");
    }

    private static Schematic conduit() {
        return new Schematic(5)
                .fill(1, 2, 1, 3, 4, 3, "minecraft:water", "level", "0")
                .set(2, 3, 2, "minecraft:conduit", "waterlogged", "true")
                .fill(0, 1, 2, 4, 1, 2, "minecraft:prismarine")
                .fill(0, 5, 2, 4, 5, 2, "minecraft:prismarine_bricks")
                .fill(0, 2, 2, 0, 4, 2, "minecraft:dark_prismarine")
                .fill(4, 2, 2, 4, 4, 2, "minecraft:dark_prismarine")
                .set(0, 3, 2, "minecraft:sea_lantern")
                .set(4, 3, 2, "minecraft:sea_lantern");
    }

    private static Schematic comparatorModes() {
        return new Schematic(5)
                .set(4, 1, 2, "minecraft:redstone_block")
                .set(3, 1, 2, "minecraft:comparator", "facing", "east", "mode", "compare", "powered", "true")
                .set(2, 1, 2, "minecraft:redstone_wire", "power", "15", "east", "side", "west", "side")
                .set(1, 1, 2, "minecraft:redstone_wire", "power", "14", "east", "side", "west", "side")
                .set(0, 1, 2, "minecraft:redstone_lamp", "lit", "true")
                .set(3, 1, 1, "minecraft:redstone_wire", "north", "side", "south", "side")
                .set(3, 1, 0, "minecraft:redstone_wire", "south", "side", "west", "side")
                .set(2, 1, 0, "minecraft:lever", "face", "floor", "facing", "east");
    }

    private static Schematic comparatorContainers() {
        return new Schematic(5)
                .set(4, 1, 2, "minecraft:barrel", "facing", "up")
                .set(3, 1, 2, "minecraft:comparator", "facing", "east")
                .fill(0, 1, 2, 2, 1, 2, "minecraft:redstone_wire", "east", "side", "west", "side");
    }

    private static Schematic repeater() {
        return new Schematic(5)
                .set(4, 1, 2, "minecraft:lever", "face", "floor", "facing", "north")
                .set(3, 1, 2, "minecraft:repeater", "facing", "east")
                .fill(1, 1, 2, 2, 1, 2, "minecraft:redstone_wire", "east", "side", "west", "side")
                .set(0, 1, 2, "minecraft:redstone_lamp")
                .set(3, 1, 1, "minecraft:repeater", "facing", "north")
                .set(3, 1, 0, "minecraft:lever", "face", "floor", "facing", "east");
    }

    private static Schematic observer() {
        return new Schematic(5)
                .set(3, 0, 2, "minecraft:farmland", "moisture", "7")
                .set(3, 1, 2, "minecraft:wheat", "age", "0")
                .set(2, 1, 2, "minecraft:observer", "facing", "east")
                .set(1, 1, 2, "minecraft:redstone_wire", "east", "side", "west", "side")
                .set(0, 1, 2, "minecraft:redstone_lamp");
    }

    private static Schematic daylightDetector() {
        return new Schematic(5)
                .set(2, 1, 2, "minecraft:daylight_detector")
                .set(1, 1, 2, "minecraft:redstone_wire", "east", "side", "west", "side")
                .set(0, 1, 2, "minecraft:redstone_lamp");
    }

    private static Schematic hopper() {
        return new Schematic(5)
                .set(2, 1, 2, "minecraft:barrel", "facing", "up")
                .set(2, 2, 2, "minecraft:hopper", "facing", "down")
                .set(2, 3, 2, "minecraft:barrel", "facing", "up")
                .set(1, 2, 2, "minecraft:redstone_block");
    }

    private static Schematic crafter() {
        return new Schematic(5)
                .set(2, 1, 2, "minecraft:crafter", "orientation", "west_up")
                .set(2, 2, 2, "minecraft:hopper", "facing", "down")
                .set(3, 1, 2, "minecraft:stone_button", "face", "wall", "facing", "east");
    }

    private static Schematic noteBlock() {
        return new Schematic(5)
                .set(2, 1, 2, "minecraft:oak_planks")
                .set(2, 2, 2, "minecraft:note_block", "instrument", "bass")
                .set(2, 3, 2, "minecraft:zombie_head", "rotation", "8");
    }

    private static Schematic tapping() {
        Schematic schematic = new Schematic(5)
                .fill(2, 1, 2, 2, 4, 2, "nomansland:maple_log", "axis", "y")
                .set(2, 2, 1, "nomansland:tap", "facing", "north", "closed", "false")
                .set(2, 1, 1, "minecraft:cauldron");
        for (int y = 4; y <= 5; y++) {
            int radius = y == 4 ? 2 : 1;
            for (int x = 2 - radius; x <= 2 + radius; x++) {
                for (int z = 2 - radius; z <= 2 + radius; z++) {
                    if (x == 2 && z == 2 && y < 5) continue;
                    schematic.set(x, y, z, "nomansland:maple_leaves", "persistent", "false", "distance", "1");
                }
            }
        }
        return schematic;
    }

    private static Schematic cookingPot() {
        return new Schematic(5)
                .set(0, 1, 2, "farmersdelight:stove", "facing", "north", "lit", "true")
                .set(0, 2, 2, "farmersdelight:cooking_pot", "facing", "north", "support", "none")
                .set(2, 1, 2, "minecraft:campfire", "facing", "north", "lit", "true")
                .set(2, 2, 2, "farmersdelight:cooking_pot", "facing", "north", "support", "tray")
                .set(4, 1, 2, "minecraft:magma_block")
                .set(4, 2, 2, "minecraft:hopper", "facing", "down")
                .set(4, 3, 2, "farmersdelight:cooking_pot", "facing", "north", "support", "none");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        Map<String, Schematic> schematics = new LinkedHashMap<>();
        schematics.put("golems/iron_golem", ironGolem());
        schematics.put("golems/snow_golem", snowGolem());
        schematics.put("golems/copper_golem", copperGolem());
        schematics.put("golems/copper_golem_sorting", copperGolemSorting());
        schematics.put("golems/sputter", sputter());
        schematics.put("golems/wither", wither());
        schematics.put("golems/tuning_fork", tuningFork());
        schematics.put("multiblocks/nether_portal", netherPortal());
        schematics.put("multiblocks/beacon", beacon());
        schematics.put("multiblocks/conduit", conduit());
        schematics.put("redstone/comparator_modes", comparatorModes());
        schematics.put("redstone/comparator_containers", comparatorContainers());
        schematics.put("redstone/repeater", repeater());
        schematics.put("redstone/observer", observer());
        schematics.put("redstone/daylight_detector", daylightDetector());
        schematics.put("redstone/hopper", hopper());
        schematics.put("redstone/crafter", crafter());
        schematics.put("redstone/note_block", noteBlock());
        schematics.put("misc/tapping", tapping());
        schematics.put("misc/cooking_pot", cookingPot());
        return CompletableFuture.allOf(schematics.entrySet().stream()
                .map(entry -> write(output, entry.getKey(), entry.getValue()))
                .toArray(CompletableFuture[]::new));
    }

    private CompletableFuture<?> write(CachedOutput output, String name, Schematic schematic) {
        return CompletableFuture.runAsync(() -> {
            try {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                HashingOutputStream hashing = new HashingOutputStream(Hashing.sha1(), bytes);
                NbtIo.writeCompressed(schematic.toTag(), hashing);
                Path path = pathProvider.file(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, name), "nbt");
                output.writeIfNeeded(path, bytes.toByteArray(), hashing.hash());
            } catch (IOException e) {
                SpicedCider.LOGGER.error("Failed to write ponder schematic {}", name, e);
            }
        });
    }

    @Override
    public String getName() {
        return "Ponder Schematics";
    }

    private static final class Schematic {
        private final Map<BlockPos, CompoundTag> blocks = new LinkedHashMap<>();

        private Schematic(int floorSize) {
            for (int x = 0; x < floorSize; x++) {
                for (int z = 0; z < floorSize; z++) {
                    set(x, 0, z, (x + z) % 2 == 0 ? "minecraft:white_concrete" : "minecraft:snow_block");
                }
            }
        }

        private Schematic set(int x, int y, int z, String id, String... properties) {
            CompoundTag state = new CompoundTag();
            state.putString("Name", id);
            if (properties.length > 0) {
                CompoundTag props = new CompoundTag();
                for (int i = 0; i + 1 < properties.length; i += 2) {
                    props.putString(properties[i], properties[i + 1]);
                }
                state.put("Properties", props);
            }
            blocks.put(new BlockPos(x, y, z), state);
            return this;
        }

        private Schematic fill(int x1, int y1, int z1, int x2, int y2, int z2, String id, String... properties) {
            for (int x = x1; x <= x2; x++) {
                for (int y = y1; y <= y2; y++) {
                    for (int z = z1; z <= z2; z++) {
                        set(x, y, z, id, properties);
                    }
                }
            }
            return this;
        }

        private CompoundTag toTag() {
            List<CompoundTag> palette = new ArrayList<>();
            ListTag blockList = new ListTag();
            int maxX = 0;
            int maxY = 0;
            int maxZ = 0;
            for (Map.Entry<BlockPos, CompoundTag> entry : blocks.entrySet()) {
                BlockPos pos = entry.getKey();
                int index = palette.indexOf(entry.getValue());
                if (index < 0) {
                    index = palette.size();
                    palette.add(entry.getValue());
                }
                CompoundTag block = new CompoundTag();
                ListTag posTag = new ListTag();
                posTag.add(IntTag.valueOf(pos.getX()));
                posTag.add(IntTag.valueOf(pos.getY()));
                posTag.add(IntTag.valueOf(pos.getZ()));
                block.put("pos", posTag);
                block.putInt("state", index);
                blockList.add(block);
                maxX = Math.max(maxX, pos.getX());
                maxY = Math.max(maxY, pos.getY());
                maxZ = Math.max(maxZ, pos.getZ());
            }
            ListTag paletteTag = new ListTag();
            paletteTag.addAll(palette);
            ListTag size = new ListTag();
            size.add(IntTag.valueOf(maxX + 1));
            size.add(IntTag.valueOf(maxY + 1));
            size.add(IntTag.valueOf(maxZ + 1));
            CompoundTag tag = new CompoundTag();
            tag.put("size", size);
            tag.put("entities", new ListTag());
            tag.put("blocks", blockList);
            tag.put("palette", paletteTag);
            return NbtUtils.addCurrentDataVersion(tag);
        }
    }
}
