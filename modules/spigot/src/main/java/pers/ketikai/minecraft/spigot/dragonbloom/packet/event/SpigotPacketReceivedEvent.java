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

package pers.ketikai.minecraft.spigot.dragonbloom.packet.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SpigotPacketReceivedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Object payload;

    public SpigotPacketReceivedEvent(Player player, @NotNull Object payload) {
        Objects.requireNonNull(payload);
        this.player = player;
        this.payload = payload;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    @NotNull
    public Object getPayload() {
        return payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpigotPacketReceivedEvent that = (SpigotPacketReceivedEvent) o;
        return Objects.equals(player, that.player) && Objects.equals(payload, that.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, payload);
    }

    @Override
    public String toString() {
        return "SpigotPacketReceivedEvent{" +
                "player=" + player +
                ", payload=" + payload +
                '}';
    }
}
