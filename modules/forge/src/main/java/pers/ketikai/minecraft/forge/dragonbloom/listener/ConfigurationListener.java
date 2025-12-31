package pers.ketikai.minecraft.forge.dragonbloom.listener;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;
import pers.ketikai.minecraft.forge.dragonbloom.DragonBloom;
import pers.ketikai.minecraft.forge.dragonbloom.packet.event.ForgePacketReceivedEvent;
import pers.ketikai.minecraft.protocol.dragonbloom.config.Configuration;

public class ConfigurationListener {

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void on(ForgePacketReceivedEvent event) {
        Object payload = event.getPayload();
        Logger logger = DragonBloom.getLogger();
        logger.info("接收到远程数据");
        if (payload instanceof Configuration) {
            DragonBloom.setConfiguration(((Configuration) payload).compile());
            logger.info("已加载远程配置");
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void on(FMLNetworkEvent.ServerDisconnectionFromClientEvent event) {
        DragonBloom.setConfiguration(null);
        DragonBloom.getLogger().info("已清除远程配置");
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void on(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        DragonBloom.setConfiguration(null);
        DragonBloom.getLogger().info("已清除远程配置");
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void on(PlayerEvent.PlayerLoggedOutEvent event) {
        DragonBloom.setConfiguration(null);
        DragonBloom.getLogger().info("已清除远程配置");
    }
}
