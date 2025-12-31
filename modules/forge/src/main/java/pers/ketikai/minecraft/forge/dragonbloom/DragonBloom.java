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

package pers.ketikai.minecraft.forge.dragonbloom;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import pers.ketikai.minecraft.forge.dragonbloom.proxy.CommonProxy;
import pers.ketikai.minecraft.protocol.dragonbloom.config.CompiledConfiguration;
import pers.ketikai.minecraft.tags.dragonbloom.Tags;

import java.util.Objects;

@Mod(modid = Tags.ID, name = Tags.NAME, version = Tags.VERSION, dependencies = "required:dragoncore")
public class DragonBloom {

    @SidedProxy(
            clientSide = "pers.ketikai.minecraft.forge.dragonbloom.proxy.ClientProxy",
            serverSide = "pers.ketikai.minecraft.forge.dragonbloom.proxy.CommonProxy"
    )
    public static CommonProxy proxy;
    private static Logger logger;
    private static volatile CompiledConfiguration configuration;

    @NotNull
    public static Logger getLogger() {
        return Objects.requireNonNull(logger);
    }

    @NotNull
    public static CompiledConfiguration getConfiguration() {
        return Objects.requireNonNull(configuration);
    }

    public static void setConfiguration(CompiledConfiguration configuration) {
        DragonBloom.configuration = configuration;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        proxy.onServerStarting(event);
    }
}
