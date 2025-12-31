package pers.ketikai.minecraft.spigot.dragonbloom.command;

import eos.moe.dragoncore.network.PacketSender;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pers.ketikai.minecraft.protocol.dragonbloom.api.exception.PacketException;
import pers.ketikai.minecraft.protocol.dragonbloom.config.Configuration;
import pers.ketikai.minecraft.spigot.dragonbloom.DragonBloom;
import pers.ketikai.minecraft.tags.dragonbloom.Tags;

import java.io.File;
import java.util.Locale;
import java.util.Objects;

public class DragonBloomCommand implements CommandExecutor {

    private final File dataFolder;

    public DragonBloomCommand(@NotNull File dataFolder) {
        this.dataFolder = Objects.requireNonNull(dataFolder);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!(commandSender instanceof Player) || !commandSender.isOp()) {
            return false;
        }
        Player player = (Player) commandSender;
        if (args.length == 1 && "reload".equals(args[0])) {
            try {
                Configuration configuration = DragonBloom.loadConfiguration(dataFolder);
                DragonBloom.setConfiguration(configuration);
                DragonBloom.channel().send(
                        Configuration.ID,
                        configuration
                );
            } catch (PacketException e) {
                player.sendMessage("§c" + e.getLocalizedMessage());
                return false;
            }
            player.sendMessage("§2成功重载配置");
            return true;
        }
        if (args.length < 2 || args.length > 5) {
            return false;
        }
        Type type;
        try {
            type = Type.valueOf(args[0].toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException e) {
            player.sendMessage("§c" + e.getLocalizedMessage());
            return false;
        }
        String particle = args[1];
        int life;
        if (args.length == 2) {
            life = 1000;
        } else {
            try {
                life = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                player.sendMessage("§c" + e.getLocalizedMessage());
                return false;
            }
        }
        Location location = player.getLocation();
        try {
            PacketSender.addParticle(
                    player,
                    particle,
                    Tags.ID + ":" + particle,
                    Type.LOCATION.equals(type) ? String.format("%f,%f,%f", location.getX(), location.getY(), location.getZ()) : player.getUniqueId().toString(),
                    args.length != 4 ? "0,0,0" : args[3],
                    args.length != 5 ? "0,0,0" : args[4],
                    life
            );
        } catch (Exception e) {
            player.sendMessage("§c" + e.getLocalizedMessage());
            return false;
        }
        player.sendMessage("§2成功生成粒子");
        return true;
    }

    private enum Type {
        LOCATION,
        FOLLOW
    }
}
