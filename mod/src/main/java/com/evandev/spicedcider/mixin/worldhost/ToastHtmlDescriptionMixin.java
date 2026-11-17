package com.evandev.spicedcider.mixin.worldhost;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@IfModLoaded("world_host")
@Mixin(targets = "io.github.gaming32.worldhost.toast.ToastBuilder", remap = false)
public class ToastHtmlDescriptionMixin {

    @Unique
    private static final Pattern spicedcider$HEADING = Pattern.compile("<(title|h1)[^>]*>(.*?)</\1>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    @Unique
    private static final int spicedcider$MAX_LENGTH = 120;

    @ModifyVariable(method = "description", at = @At("HEAD"), argsOnly = true)
    private Component spicedcider$stripHtml(Component description) {
        if (description == null) return null;
        String text = description.getString();
        String lower = text.toLowerCase(Locale.ROOT);
        if (!lower.contains("<html") && !lower.contains("<!doctype") && !lower.contains("<body") && !lower.contains("<head")) return description;
        Matcher heading = spicedcider$HEADING.matcher(text);
        String cleaned = heading.find() ? heading.group(2) : text;
        cleaned = cleaned.replaceAll("(?is)<(script|style)[^>]*>.*?</\1>", " ")
                .replaceAll("<[^>]*>", " ")
                .replace("&nbsp;", " ").replace("&amp;", "&").replace("&lt;", "<").replace("&gt;", ">")
                .replaceAll("\s+", " ")
                .trim();
        int start = text.indexOf('<');
        String prefix = start > 0 ? text.substring(0, start).replaceAll("\s+", " ").trim() : "";
        if (!prefix.isEmpty() && !cleaned.isEmpty()) cleaned = prefix + " " + cleaned;
        else if (cleaned.isEmpty()) cleaned = prefix;
        if (cleaned.length() > spicedcider$MAX_LENGTH) cleaned = cleaned.substring(0, spicedcider$MAX_LENGTH - 3).trim() + "...";
        return Component.literal(cleaned);
    }
}
