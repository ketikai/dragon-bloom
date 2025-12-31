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

package pers.ketikai.minecraft.spigot.dragonbloom.packet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import pers.ketikai.minecraft.protocol.dragonbloom.api.PacketChannel;
import pers.ketikai.minecraft.protocol.dragonbloom.api.exception.PacketException;
import pers.ketikai.minecraft.protocol.dragonbloom.util.Entry;

enum SpigotPacketListener implements PluginMessageListener {

    INSTANCE;

    private static final Logger log = LogManager.getLogger(SpigotPacketListener.class);

    @SuppressWarnings("unchecked")
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        try {
            PacketChannel.of(channel).receive(message, Entry.of("player", player));
        } catch (PacketException e) {
            log.throwing(e);
        }
    }
}
