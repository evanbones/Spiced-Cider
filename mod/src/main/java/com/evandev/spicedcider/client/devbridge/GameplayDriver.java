package com.evandev.spicedcider.client.devbridge;

import com.google.gson.JsonObject;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;

public final class GameplayDriver {
    private volatile Plan plan;
    private long ticksLeft;
    private long ticksRun;

    public record Plan(boolean forward, boolean back, boolean left, boolean right, boolean sprint, boolean jump,
                       boolean sneak, boolean attack, boolean use, boolean fly, float yawPerTick, float pitch, int swayTicks,
                       float swayDegrees, long ticks) {
    }

    public void start(Plan plan) {
        this.plan = plan;
        this.ticksLeft = plan.ticks();
        this.ticksRun = 0;
    }

    public void stop() {
        Plan current = plan;
        plan = null;
        if (current != null) Minecraft.getInstance().execute(this::release);
    }

    public boolean active() {
        return plan != null;
    }

    public void onClientTick() {
        Plan current = plan;
        if (current == null) return;
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || ticksLeft <= 0) {
            plan = null;
            release();
            return;
        }
        ticksLeft--;
        ticksRun++;
        Options options = mc.options;
        set(options.keyUp, current.forward());
        set(options.keyDown, current.back());
        set(options.keyLeft, current.left());
        set(options.keyRight, current.right());
        set(options.keySprint, current.sprint());
        set(options.keyJump, current.jump());
        set(options.keyShift, current.sneak());
        set(options.keyAttack, current.attack());
        set(options.keyUse, current.use());
        float yaw = current.yawPerTick();
        if (current.swayTicks() > 0) {
            double phase = (ticksRun % current.swayTicks()) / (double) current.swayTicks() * Math.PI * 2;
            yaw += (float) (Math.cos(phase) * current.swayDegrees() * Math.PI / current.swayTicks());
        }
        if (current.fly() && player.getAbilities().mayfly && !player.getAbilities().flying) {
            player.getAbilities().flying = true;
            player.onUpdateAbilities();
        }
        player.setYRot(player.getYRot() + yaw);
        player.setXRot(current.pitch());
    }

    public JsonObject describe() {
        JsonObject out = new JsonObject();
        out.addProperty("active", plan != null);
        out.addProperty("ticksLeft", plan == null ? 0 : ticksLeft);
        out.addProperty("ticksRun", ticksRun);
        return out;
    }

    private void release() {
        Options options = Minecraft.getInstance().options;
        for (KeyMapping key : new KeyMapping[]{options.keyUp, options.keyDown, options.keyLeft, options.keyRight,
                options.keySprint, options.keyJump, options.keyShift, options.keyAttack, options.keyUse}) {
            key.setDown(false);
        }
    }

    private static void set(KeyMapping key, boolean down) {
        key.setDown(down);
    }
}
