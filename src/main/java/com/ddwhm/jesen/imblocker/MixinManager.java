package com.ddwhm.jesen.imblocker;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MixinManager implements IMixinConfigPlugin {

    private static final Map<String, String> mixinDeps = new HashMap<>();

    static {
        mixinDeps.put("com.ddwhm.jesen.imblocker.mixin.rei", "roughlyenoughitems");
        mixinDeps.put("com.ddwhm.jesen.imblocker.mixin.libgui", "libgui");
        mixinDeps.put("com.ddwhm.jesen.imblocker.mixin.replay", "replaymod");
    }
    public static int protocolVersion;

    // rewrite the getversion function to fix the issue #12
    private static int getGameVersion() {
        try (
                final InputStream stream = IMixinConfigPlugin.class.getResourceAsStream("/version.json");
                final Reader reader = new InputStreamReader(stream)
        ) {

            final JsonObject versions = new JsonParser().parse(reader).getAsJsonObject();
            return versions.get("protocol_version").getAsInt();
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException("[IMBLOCKER] Couldn't get the game protocol_version", e);
        }
    }

    @Override
    public void onLoad(String mixinPackage) {
        protocolVersion = getGameVersion();
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.endsWith("mixin.MixinAbstractButtonWidget") && protocolVersion < 705) {
            return false;
        }
        if (mixinClassName.endsWith("mixin.compat115.MixinAbstractButtonWidget") && protocolVersion >= 705) {
            return false;
        }
        for (Map.Entry<String, String> entry: mixinDeps.entrySet()) {
            if (mixinClassName.startsWith(entry.getKey())) {
                return FabricLoader.getInstance().isModLoaded(entry.getValue());
            }
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
