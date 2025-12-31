package pers.ketikai.minecraft.protocol.dragonbloom.config;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Data
public class CompiledConfiguration {

    private final boolean enabled;
    @NotNull
    private final Set<String> includes;
    @NotNull
    private final Set<String> excludes;
    @NotNull
    private final List<Pattern> patterns;

    CompiledConfiguration(Boolean enabled, @NotNull Set<String> includes, @NotNull Set<String> excludes, @NotNull List<String> patterns) {
        this.enabled = enabled == null || enabled;
        this.includes = Collections.unmodifiableSet(includes);
        this.excludes = Collections.unmodifiableSet(excludes);
        this.patterns = Collections.unmodifiableList(patterns.stream().map(Pattern::compile).collect(Collectors.toList()));
    }

    public boolean isMatched(@NotNull String name) {
        if (!enabled) {
            return false;
        }
        if (!includes.isEmpty() && includes.contains(name)) {
            return true;
        }
        if (!excludes.isEmpty() && excludes.contains(name)) {
            return false;
        }
        for (Pattern pattern : patterns) {
            if (pattern.matcher(name).find()) {
                return true;
            }
        }
        return false;
    }
}
