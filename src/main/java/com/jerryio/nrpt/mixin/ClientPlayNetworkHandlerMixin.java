package com.jerryio.nrpt.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.jerryio.nrpt.NoResourcePackThxMod;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;
//#if MC_VERSION >= 1.17.0
import net.minecraft.client.option.ServerList;
//#else
/// import net.minecraft.client.options.ServerList;
//#endif
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

        //#if MC_VERSION >= 1.17.0
        if (serverInfo != null && serverInfo.getResourcePackPolicy() == ServerInfo.ResourcePackPolicy.ENABLED) {
            me.invokeSendResourcePackStatus(ResourcePackStatusC2SPacket.Status.ACCEPTED);
            me.invokeFeedbackAfterDownload(me.getClient().getResourcePackProvider().download(url, sha1, true));

            NoResourcePackThxMod.LOGGER.info("Resource pack has been accepted");
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

            NoResourcePackThxMod.LOGGER.info("Asking the player if they want to accept the resource pack");
        //#else
        /// if (serverInfo != null && serverInfo.getResourcePack() == ServerInfo.ResourcePackState.ENABLED) {
            /// me.invokeSendResourcePackStatus(ResourcePackStatusC2SPacket.Status.ACCEPTED);
            /// me.invokeFeedbackAfterDownload(me.getClient().getResourcePackDownloader().download(url, sha1));

            /// NoResourcePackThxMod.LOGGER.info("Resource pack has been accepted");
        /// } else if (serverInfo == null ||
                /// serverInfo.getResourcePack() == ServerInfo.ResourcePackState.PROMPT) {
            /// // serverInfo.getResourcePackPolicy() == ServerInfo.ResourcePackPolicy.DISABLED
            /// // BEHAIVOR: do not ask the player again if the player said no

            /// me.invokeSendResourcePackStatus(ResourcePackStatusC2SPacket.Status.ACCEPTED);

            /// me.getClient().execute(
                    /// () -> me.getClient().openScreen(new ConfirmScreen(
                            /// result -> {
                                /// me.getClient().openScreen(null);
                                /// ServerInfo serverInfo2 = me.getClient().getCurrentServerEntry();

                                /// me.invokeSendResourcePackStatus(ResourcePackStatusC2SPacket.Status.ACCEPTED);

                                /// if (result) {
                                    /// me.invokeFeedbackAfterDownload(
                                            /// me.getClient().getResourcePackDownloader().download(url, sha1));
                                /// }

                                /// if (serverInfo2 != null) {
                                    /// serverInfo2.setResourcePackState(
                                            /// result ? ServerInfo.ResourcePackState.ENABLED
                                                    /// : ServerInfo.ResourcePackState.DISABLED);

                                    /// ServerList.updateServerListEntry(serverInfo2);
                                /// }
                             /// },
                            /// new TranslatableText("multiplayer.texturePrompt.line1"),
                            /// new TranslatableText("multiplayer.texturePrompt.line2"))));

            /// NoResourcePackThxMod.LOGGER.info("Asking the player if they want to accept the resource pack");
        //#endif
        } else {
            me.invokeSendResourcePackStatus(ResourcePackStatusC2SPacket.Status.ACCEPTED);

            NoResourcePackThxMod.LOGGER.info("Resource pack has been ignored");
        }

        ci.cancel();
    }

}
