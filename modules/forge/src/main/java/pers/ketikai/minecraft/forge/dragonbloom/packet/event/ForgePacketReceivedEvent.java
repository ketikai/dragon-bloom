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

package pers.ketikai.minecraft.forge.dragonbloom.packet.event;

import net.minecraftforge.fml.common.eventhandler.Event;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ForgePacketReceivedEvent extends Event {

    private final Object payload;

    public ForgePacketReceivedEvent(@NotNull Object payload) {
        Objects.requireNonNull(payload);
        this.payload = payload;
    }

    @NotNull
    public Object getPayload() {
        return payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ForgePacketReceivedEvent that = (ForgePacketReceivedEvent) o;
        return Objects.equals(payload, that.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(payload);
    }

    @Override
    public String toString() {
        return "ForgePacketReceivedEvent{" +
                "payload=" + payload +
                '}';
    }
}
