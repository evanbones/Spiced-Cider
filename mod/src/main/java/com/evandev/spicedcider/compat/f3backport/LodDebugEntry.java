package com.evandev.spicedcider.compat.f3backport;

import club.iananderson.f3backport.client.gui.components.debug.DebugScreenDisplayer;
import club.iananderson.f3backport.client.gui.components.debug.entries.DebugGroup;
import club.iananderson.f3backport.client.gui.components.debug.entries.DebugScreenEntries;
import club.iananderson.f3backport.client.gui.components.debug.entries.DebugScreenEntry;
import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.lodgen.LodGen;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

import java.awt.Color;

public final class LodDebugEntry implements DebugScreenEntry {
    private static final DebugGroup GROUP = DebugGroup.Builder.titled("Distant Terrain").withAccentColor(new Color(120, 200, 255)).build();

    public static void register() {
        DebugScreenEntries.register(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "lod_generation"), new LodDebugEntry());
    }

    @Override
    public void display(DebugScreenDisplayer displayer, Level level, LevelChunk clientChunk, LevelChunk serverChunk) {
        if (Minecraft.getInstance().getSingleplayerServer() == null) return;
        String line = LodGen.debugLine();
        if (line != null) displayer.addToGroup(GROUP, line);
    }

    @Override
    public boolean enabled() {
        return true;
    }
}
