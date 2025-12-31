/*
 *     Copyright (C) 2024 ideal-state
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package pers.ketikai.minecraft.spigot.dragonbloom;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import pers.ketikai.minecraft.protocol.dragonbloom.codec.GsonPacketCodec;
import pers.ketikai.minecraft.protocol.dragonbloom.config.Configuration;
import pers.ketikai.minecraft.protocol.dragonbloom.util.Entry;
import pers.ketikai.minecraft.spigot.dragonbloom.command.DragonBloomCommand;
import pers.ketikai.minecraft.spigot.dragonbloom.listener.ConfigurationListener;
import pers.ketikai.minecraft.spigot.dragonbloom.packet.SpigotPacketChannel;
import pers.ketikai.minecraft.tags.dragonbloom.Tags;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

public class DragonBloom extends JavaPlugin {

    private static volatile Configuration configuration;
    private static volatile SpigotPacketChannel channel;

    @NotNull
    public static Configuration getConfiguration() {
        return Objects.requireNonNull(configuration);
    }

    public static void setConfiguration(@NotNull Configuration configuration) {
        DragonBloom.configuration = Objects.requireNonNull(configuration);
    }

    @NotNull
    public static Configuration loadConfiguration(@NotNull File dataFolder) {
        File configFile = new File(dataFolder, "config.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Configuration configuration;
        boolean exists = configFile.exists();
        Path configFilePath = configFile.toPath();
        try (BufferedReader reader = exists ? Files.newBufferedReader(configFilePath, StandardCharsets.UTF_8) : new BufferedReader(new InputStreamReader(Objects.requireNonNull(DragonBloom.class.getClassLoader().getResourceAsStream("config.json")), StandardCharsets.UTF_8))) {
            configuration = gson.fromJson(reader, Configuration.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!exists) {
            if (!dataFolder.exists() && !dataFolder.mkdirs()) {
                throw new RuntimeException("无法创建配置目录");
            }
            try {
                Files.write(configFilePath, gson.toJson(configuration).getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE_NEW);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return configuration;
    }

    @NotNull
    public static void dumpReadme(@NotNull File dataFolder) {
        File configFile = new File(dataFolder, "README.md");
        if (!configFile.exists()) {
            if (!dataFolder.exists() && !dataFolder.mkdirs()) {
                throw new RuntimeException("无法创建配置目录");
            }
            Path configFilePath = configFile.toPath();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(DragonBloom.class.getClassLoader().getResourceAsStream("config.json")), StandardCharsets.UTF_8))) {
                try (BufferedWriter writer = Files.newBufferedWriter(configFilePath, StandardOpenOption.CREATE_NEW)) {
                    while (reader.ready()) {
                        writer.write(reader.readLine());
                        writer.newLine();
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @NotNull
    public static SpigotPacketChannel channel() {
        return Objects.requireNonNull(channel);
    }

    @Override
    public void onLoad() {
        File dataFolder = getDataFolder();
        dumpReadme(dataFolder);
        setConfiguration(loadConfiguration(dataFolder));
    }

    @Override
    public void onEnable() {
        channel = new SpigotPacketChannel(this, Tags.ID,
                Entry.of(Configuration.ID, new GsonPacketCodec<>(Configuration.class))
        );
        getCommand(Tags.NAME).setExecutor(new DragonBloomCommand(getDataFolder()));
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new ConfigurationListener(), this);
    }
}
