package net.fabricmc.example.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.example.ExampleMod;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.c2s.play.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.s2c.play.ResourcePackSendS2CPacket;
import net.minecraft.text.TranslatableText;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin
        implements ClientPlayPacketListener {

    @Inject(at = { @At("HEAD") }, method = {
            "onResourcePackSend(Lnet/minecraft/network/packet/s2c/play/ResourcePackSendS2CPacket;)V" }, cancellable = true)
    private void onResourcePackSend(ResourcePackSendS2CPacket packet, CallbackInfo ci) {
        String url = packet.getURL();
        String sha1 = packet.getSHA1();

        if (url.startsWith("level://"))
            return;

        ClientPlayNetworkHandlerAccessor me = (ClientPlayNetworkHandlerAccessor) this;
        ServerInfo serverInfo = me.getClient().getCurrentServerEntry();

        if (serverInfo != null && serverInfo.getResourcePackPolicy() == ServerInfo.ResourcePackPolicy.ENABLED) {
            me.invokeSendResourcePackStatus(ResourcePackStatusC2SPacket.Status.ACCEPTED);
            me.invokeFeedbackAfterDownload(me.getClient().getResourcePackProvider().download(url, sha1, true));

            ExampleMod.LOGGER.info("Resource pack has been enabled");
        } else if (serverInfo == null ||
                serverInfo.getResourcePackPolicy() == ServerInfo.ResourcePackPolicy.PROMPT) {
            // serverInfo.getResourcePackPolicy() == ServerInfo.ResourcePackPolicy.DISABLED
            // BEHAIVOR: do not ask the player again if the player said no

            me.invokeSendResourcePackStatus(ResourcePackStatusC2SPacket.Status.ACCEPTED);

            me.getClient().execute(
                    () -> me.getClient().setScreen(new ConfirmScreen(
                            result -> {
                                me.getClient().setScreen(null);
                                ServerInfo serverInfo2 = me.getClient().getCurrentServerEntry();

                                me.invokeSendResourcePackStatus(ResourcePackStatusC2SPacket.Status.ACCEPTED);

                                if (result) {
                                    me.invokeFeedbackAfterDownload(
                                            me.getClient().getResourcePackProvider().download(url, sha1, true));
                                }

                                if (serverInfo2 != null) {
                                    serverInfo2.setResourcePackPolicy(
                                            result ? ServerInfo.ResourcePackPolicy.ENABLED
                                                    : ServerInfo.ResourcePackPolicy.DISABLED);

                                    ServerList.updateServerListEntry(serverInfo2);
                                }
                            },
                            new TranslatableText("multiplayer.texturePrompt.line1"),
                            ClientPlayNetworkHandlerAccessor.invokeGetServerResourcePackPrompt(
                                    new TranslatableText("multiplayer.texturePrompt.line2"),
                                    packet.getPrompt()))));

            ExampleMod.LOGGER.info("Asking the player if they want to accept the resource pack");
        } else {
            me.invokeSendResourcePackStatus(ResourcePackStatusC2SPacket.Status.ACCEPTED);

            ExampleMod.LOGGER.info("Resource pack has been disabled");
        }

        ci.cancel();
    }

}