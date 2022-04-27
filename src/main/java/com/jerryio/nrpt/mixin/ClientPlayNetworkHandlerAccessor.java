package com.jerryio.nrpt.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.ResourcePackStatusC2SPacket;

@Mixin(ClientPlayNetworkHandler.class)
public interface ClientPlayNetworkHandlerAccessor {

    @Accessor
    MinecraftClient getClient();

    @Invoker
    void invokeSendPacket(Packet<?> packet);

    @Invoker
    void invokeSendResourcePackStatus(ResourcePackStatusC2SPacket.Status packStatus);

    @Invoker
    void invokeFeedbackAfterDownload(java.util.concurrent.CompletableFuture<?> downloadFuture);
}
