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

package pers.ketikai.minecraft.forge.dragonbloom.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import pers.ketikai.minecraft.forge.dragonbloom.packet.event.ForgePacketReceivedEvent;
import pers.ketikai.minecraft.protocol.dragonbloom.api.PacketChannel;
import pers.ketikai.minecraft.protocol.dragonbloom.api.PacketChannelAdapter;
import pers.ketikai.minecraft.protocol.dragonbloom.api.PacketCodec;
import pers.ketikai.minecraft.protocol.dragonbloom.api.exception.PacketException;
import pers.ketikai.minecraft.protocol.dragonbloom.util.Entry;

import java.util.Objects;

public final class ForgePacketChannel implements PacketChannelAdapter {

    private static final Logger log = LogManager.getLogger(ForgePacketChannel.class);

    private final String name;
    private final FMLEventChannel fmlEventChannel;

    @SafeVarargs
    public ForgePacketChannel(@NotNull String name, Entry<Short, PacketCodec>... codecs) {
        Objects.requireNonNull(name);
        this.name = name;
        this.fmlEventChannel = NetworkRegistry.INSTANCE.newEventDrivenChannel(name);
        PacketChannel channel = PacketChannel.of(name);
        channel.setSender((packet, context) -> {
            log.debug("[{}] Sending packet {}", name, packet);
            ByteBuf byteBuf = Unpooled.wrappedBuffer(packet);
            PacketBuffer payload = new PacketBuffer(byteBuf);
            fmlEventChannel.sendToServer(new FMLProxyPacket(payload, name));
        });
        fmlEventChannel.register(ForgePacketListener.INSTANCE);
        channel.setReceiver((payload, context) -> {
            log.debug("[{}] Receiving packet {}", name, payload);
            MinecraftForge.EVENT_BUS.post(new ForgePacketReceivedEvent(payload));
        });
        for (Entry<Short, PacketCodec> codec : codecs) {
            if (codec == null) {
                continue;
            }
            Short key = codec.getKey();
            if (key == null) {
                continue;
            }
            PacketCodec value = codec.getValue();
            if (value == null) {
                continue;
            }
            channel.register(key, value);
        }
    }

    @SafeVarargs
    @Override
    public final void send(short id, @NotNull Object payload, @NotNull Entry<String, Object>... context) throws PacketException {
        PacketChannelAdapter channel = PacketChannel.of(name);
        channel.send(id, payload, context);
    }

    @SafeVarargs
    @Override
    public final void receive(byte @NotNull [] packet, @NotNull Entry<String, Object>... context) throws PacketException {
        PacketChannelAdapter channel = PacketChannel.of(name);
        channel.receive(packet, context);
    }

    @Override
    public @NotNull String getName() {
        PacketChannelAdapter channel = PacketChannel.of(name);
        return channel.getName();
    }
}
