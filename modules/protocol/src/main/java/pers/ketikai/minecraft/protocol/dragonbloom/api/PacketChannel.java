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

package pers.ketikai.minecraft.protocol.dragonbloom.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import pers.ketikai.minecraft.protocol.dragonbloom.api.exception.PacketException;
import pers.ketikai.minecraft.protocol.dragonbloom.util.Entry;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public abstract class PacketChannel implements PacketChannelAdapter {

    private static final Map<String, PacketChannel> CHANNELS = new ConcurrentHashMap<>(16, 0.75F);
    protected final String name;
    protected final Map<Short, PacketCodec> codecs = new ConcurrentHashMap<>(16, 0.75F);
    protected volatile PacketSender sender;
    protected volatile PacketReceiver receiver;

    protected PacketChannel(@NotNull String name, PacketSender sender, PacketReceiver receiver) {
        this.name = Objects.requireNonNull(name);
        this.sender = sender;
        this.receiver = receiver;
    }

    @NotNull
    public static PacketChannel of(@NotNull String name) {
        return CHANNELS.computeIfAbsent(name, k -> new SimplePacketChannel(k, null, null));
    }

    @SafeVarargs
    protected static Map<String, Object> newContext(Entry<String, Object>... context) {
        Map<String, Object> ret = new HashMap<>(context.length);
        for (Entry<String, Object> entry : context) {
            if (entry == null) {
                continue;
            }
            String key = entry.getKey();
            if (key == null) {
                continue;
            }
            Object value = entry.getValue();
            ret.put(key, value);
        }
        return ret;
    }

    public final void register(short packetId, @NotNull PacketCodec codec) {
        Objects.requireNonNull(codec);
        codecs.put(packetId, codec);
    }

    public final void unregister(short packetId) {
        codecs.remove(packetId);
    }

    public final @NotNull String getName() {
        return name;
    }

    public final void setSender(PacketSender sender) {
        this.sender = sender;
    }

    public final void setReceiver(PacketReceiver receiver) {
        this.receiver = receiver;
    }

    @SuppressWarnings("LoggingSimilarMessage")
    private static final class SimplePacketChannel extends PacketChannel {

        private static final Logger log = LogManager.getLogger(SimplePacketChannel.class);

        private SimplePacketChannel(@NotNull String name, PacketSender sender, PacketReceiver receiver) {
            super(name, sender, receiver);
        }

        @Override
        @SafeVarargs
        public final void send(short id, @NotNull Object payload, @NotNull Entry<String, Object>... context) throws PacketException {
            Objects.requireNonNull(payload);
            PacketSender sender = this.sender;
            if (sender == null) {
                return;
            }
            PacketCodec codec = codecs.get(id);
            if (codec == null) {
                log.debug("No codec found for id {}", id);
                return;
            }
            log.debug("Sending packet {} with id {}", payload, id);
            byte[] encoded = codec.encode(payload);
            ByteBuffer packet = ByteBuffer.allocate(encoded.length + 2);
            packet.putShort(id);
            packet.put(encoded);
            sender.send(packet.array(), newContext(context));
        }


        @Override
        @SafeVarargs
        public final void receive(byte @NotNull [] packet, @NotNull Entry<String, Object>... context) throws PacketException {
            Objects.requireNonNull(packet);
            PacketReceiver receiver = this.receiver;
            if (receiver == null) {
                return;
            }
            ByteBuffer wrapped = ByteBuffer.wrap(packet);
            short id = wrapped.getShort();
            PacketCodec codec = codecs.get(id);
            if (codec == null) {
                log.debug("No codec found for id {}", id);
                return;
            }
            byte[] payload = new byte[wrapped.remaining()];
            wrapped.get(payload);
            log.debug("Receiving packet {} with id {}", payload, id);
            Object decoded = codec.decode(payload);
            if (decoded == null) {
                return;
            }
            receiver.receive(decoded, newContext(context));
        }
    }
}
