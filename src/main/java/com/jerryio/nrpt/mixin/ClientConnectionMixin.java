package com.jerryio.nrpt.mixin;

import com.jerryio.nrpt.NoResourcePackThxMod;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.MessageType;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.c2s.play.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.ResourcePackStatusC2SPacket.Status;
import net.minecraft.network.packet.s2c.play.ResourcePackSendS2CPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.ClickEvent.Action;
import net.minecraft.text.ClickEvent;
import net.minecraft.util.Util;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {

    @ModifyVariable(
        method = { "send(Lnet/minecraft/network/Packet;Lio/netty/util/concurrent/GenericFutureListener;)V" },
        at = @At("HEAD"),
        index = 1
    )
    private Packet<?> send(Packet<?> packet) {
        if (packet instanceof ResourcePackStatusC2SPacket) {
            packet = new ResourcePackStatusC2SPacket(Status.ACCEPTED);
        }
        return packet;
    }

    @Inject(
        method = { "handlePacket(Lnet/minecraft/network/Packet;Lnet/minecraft/network/listener/PacketListener;)V" },
        at = { @At("HEAD") },
        cancellable = true
    )
    private static <T extends PacketListener> void handlePacket(Packet<T> packet, PacketListener listener, CallbackInfo ci) {
        if (packet instanceof ResourcePackSendS2CPacket) {
            ResourcePackSendS2CPacket rps = (ResourcePackSendS2CPacket) packet;
            MinecraftClient mc = MinecraftClient.getInstance();

            ClickEvent ce = new ClickEvent(Action.OPEN_URL, rps.getURL());
            Text link = new LiteralText("[link]").setStyle(
                    new LiteralText("").getStyle()
                            .withColor(TextColor.parse("blue"))
                            .withUnderline(true)
                            .withClickEvent(ce));
            Text sentence = new LiteralText("The server recommends the use of a custom resource pack. ").append(link);

            mc.inGameHud.addChatMessage(MessageType.SYSTEM, sentence, Util.NIL_UUID);
            NoResourcePackThxMod.LOGGER.info("The resource pack is available at " + rps.getURL());
        }
    }
}
