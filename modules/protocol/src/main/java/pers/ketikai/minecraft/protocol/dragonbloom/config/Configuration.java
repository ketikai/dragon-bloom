package pers.ketikai.minecraft.protocol.dragonbloom.config;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

@Data
public class Configuration {

    public static final short ID = 0x10;

    @NotNull
    private Boolean enabled;
    @NotNull
    private Set<String> includes;
    @NotNull
    private Set<String> excludes;
    @NotNull
    private List<String> patterns;

    @NotNull
    public CompiledConfiguration compile() {
        return new CompiledConfiguration(enabled, includes, excludes, patterns);
    }
}
