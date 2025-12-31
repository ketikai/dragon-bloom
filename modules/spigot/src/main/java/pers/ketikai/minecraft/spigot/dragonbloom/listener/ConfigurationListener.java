package pers.ketikai.minecraft.spigot.dragonbloom.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import pers.ketikai.minecraft.protocol.dragonbloom.api.exception.PacketException;
import pers.ketikai.minecraft.protocol.dragonbloom.config.Configuration;
import pers.ketikai.minecraft.protocol.dragonbloom.util.Entry;
import pers.ketikai.minecraft.spigot.dragonbloom.DragonBloom;
import pers.ketikai.minecraft.tags.dragonbloom.Tags;

public class ConfigurationListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on(PlayerRegisterChannelEvent event) {
        if (!Tags.ID.equals(event.getChannel())) {
            return;
        }
        Player player = event.getPlayer();
        try {
            DragonBloom.channel().send(
                    Configuration.ID,
                    DragonBloom.getConfiguration(),
                    Entry.of("sender", player)
            );
            player.sendMessage("§2成功同步配置");
        } catch (PacketException e) {
            e.printStackTrace();
        }
    }
}
