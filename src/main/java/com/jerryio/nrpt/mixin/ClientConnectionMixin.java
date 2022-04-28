package com.jerryio.nrpt.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.ResourcePackStatusC2SPacket.Status;
import net.minecraft.network.ClientConnection;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {

    @ModifyVariable(
        method = { "send(Lnet/minecraft/network/Packet;Lio/netty/util/concurrent/GenericFutureListener;)V" },
        at = @At("HEAD"),
        index = 1
    )
    private Packet<?> mixin(Packet<?> packet) {
        if (packet instanceof ResourcePackStatusC2SPacket) {
            packet = new ResourcePackStatusC2SPacket(Status.ACCEPTED);
        }
        return packet;
    }
}
