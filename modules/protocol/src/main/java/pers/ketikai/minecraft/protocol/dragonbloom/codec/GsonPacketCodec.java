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

package pers.ketikai.minecraft.protocol.dragonbloom.codec;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.ketikai.minecraft.protocol.dragonbloom.api.PacketCodec;
import pers.ketikai.minecraft.protocol.dragonbloom.api.exception.PacketException;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class GsonPacketCodec<T> implements PacketCodec {

    protected final Class<T> packetType;
    protected final Gson mapper;

    public GsonPacketCodec(@NotNull Class<T> packetType) {
        this.packetType = Objects.requireNonNull(packetType);
        this.mapper = new Gson();
    }

    @Override
    public byte @NotNull [] encode(@NotNull Object payload) throws PacketException {
        try {
            return mapper.toJson(Objects.requireNonNull(payload)).getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new PacketException(e);
        }
    }

    @Override
    public @Nullable Object decode(byte @NotNull [] payload) throws PacketException {
        try {
            return mapper.fromJson(new String(Objects.requireNonNull(payload), StandardCharsets.UTF_8), packetType);
        } catch (Exception e) {
            throw new PacketException(e);
        }
    }
}
