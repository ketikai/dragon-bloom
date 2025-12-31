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
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pers.ketikai.minecraft.protocol.dragonbloom.api.PacketChannel;
import pers.ketikai.minecraft.protocol.dragonbloom.api.exception.PacketException;

enum ForgePacketListener {

    INSTANCE;

    private static final Logger log = LogManager.getLogger(ForgePacketListener.class);

    @SuppressWarnings("unchecked")
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onClientPacketReceived(FMLNetworkEvent.ClientCustomPacketEvent event) {
        FMLProxyPacket proxyPacket = event.getPacket();
        if (proxyPacket == null) {
            return;
        }
        ByteBuf payload = proxyPacket.payload();
        if (payload == null) {
            return;
        }
        byte[] packet = new byte[payload.readableBytes()];
        payload.readBytes(packet);
        try {
            PacketChannel.of(proxyPacket.channel()).receive(packet);
        } catch (PacketException e) {
            log.throwing(e);
        }
    }
}
