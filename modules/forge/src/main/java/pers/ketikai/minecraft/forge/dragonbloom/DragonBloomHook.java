package pers.ketikai.minecraft.forge.dragonbloom;

import blockbuster.BedrockScheme;
import blockbuster.emitter.BedrockEmitter;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public abstract class DragonBloomHook {

    public static BedrockScheme hookBedrockEmitterSetScheme(@NotNull BedrockEmitter emitter, @Nullable BedrockScheme scheme, @Nullable Map<String, String> variables) {
        String schemeKey = scheme == null ? null : scheme.identifier;
        Logger logger = DragonBloom.getLogger();
        logger.debug(() -> {
            String variablesContent = variables == null ? null : variables.toString();
            return "hookBedrockEmitterSetScheme: { effect: '" + emitter.effect + "', bloom: '" + emitter.bloom + "', scheme: '" + schemeKey + "', variables: '" + variablesContent + "' }";
        });
        if (schemeKey != null) {
            try {
                if (!DragonBloom.getConfiguration().isMatched(schemeKey)) {
                    return scheme;
                }
            } catch (Exception e) {
                logger.error(e);
                return scheme;
            }
            String effect = emitter.effect;
            if (effect == null) {
                emitter.effect = "@";
            } else if (!effect.contains("@")) {
                emitter.effect = "@" + effect;
            }
            emitter.bloom = true;
        }
        return scheme;
    }
}
