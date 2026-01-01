package pers.ketikai.minecraft.forge.dragonbloom;

import blockbuster.BedrockScheme;
import blockbuster.emitter.BedrockEmitter;
import blockbuster.render.BloomHelper;
import eos.moe.dragoncore.eea;
import eos.moe.dragoncore.kea;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.util.Map;

public abstract class DragonBloomHook {

    public static void hookBedrockEmitterSetScheme(@NotNull BedrockEmitter emitter, @Nullable BedrockScheme scheme, @Nullable Map<String, String> variables) {
        String schemeKey = scheme == null ? null : scheme.identifier;
        Logger logger = DragonBloom.getLogger();
        logger.debug(() -> {
            String variablesContent = variables == null ? null : variables.toString();
            return "hookBedrockEmitterSetScheme: { effect: '" + emitter.effect + "', bloom: '" + emitter.bloom + "', scheme: '" + schemeKey + "', variables: '" + variablesContent + "' }";
        });
        if (schemeKey != null) {
            try {
                if (!DragonBloom.getConfiguration().isMatched(schemeKey)) {
                    return;
                }
            } catch (Exception e) {
                logger.error(e);
                return;
            }
            String effect = emitter.effect;
            if (effect == null) {
                emitter.effect = "@";
            } else if (!effect.contains("@")) {
                emitter.effect = "@" + effect;
            }
            emitter.bloom = true;
        }
    }

    private static final Field kea_f;
    static  {
        try {
            kea_f = kea.class.getDeclaredField("f");
            kea_f.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private static ResourceLocation getGlowTexture(@NotNull kea that) {
        try {
            return (ResourceLocation) kea_f.get(that);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static kea hookEeaFunc_77036_a0(kea kea, @NotNull eea eea, @NotNull EntityLivingBase entity, float a, float b, float c, float d, float e, float f) {
        if (getGlowTexture(kea) == null) {
            return kea;
        }
        BloomHelper.start();
        return kea;
    }

    public static kea hookEeaFunc_77036_a1(kea kea, @NotNull eea eea, @NotNull EntityLivingBase entity, float a, float b, float c, float d, float e, float f) {
        if (getGlowTexture(kea) == null) {
            return kea;
        }
        BloomHelper.end();
        return kea;
    }
}
