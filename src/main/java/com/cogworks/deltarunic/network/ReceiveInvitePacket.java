package com.cogworks.deltarunic.network;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record ReceiveInvitePacket(String hostName, UUID hostUuid) implements CustomPacketPayload {
    public static final Type<ReceiveInvitePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("deltarunic", "receive_invite"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ReceiveInvitePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ReceiveInvitePacket::hostName,
            UUIDUtil.STREAM_CODEC, ReceiveInvitePacket::hostUuid,
            ReceiveInvitePacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ReceiveInvitePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                Component acceptButton = Component.literal("[ACCEPT]")
                        .setStyle(Style.EMPTY
                                .withColor(ChatFormatting.GREEN)
                                .withBold(true)
                                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/deltarunic accept " + packet.hostUuid())));

                Component message = Component.literal("§e[Deltarunic] §f" + packet.hostName + " challenged you to a duel! (Expires in 30s) ")
                        .append(acceptButton);

                mc.player.sendSystemMessage(message);
            }
        });
    }
}