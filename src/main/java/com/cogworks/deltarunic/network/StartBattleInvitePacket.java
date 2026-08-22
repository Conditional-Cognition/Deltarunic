package com.cogworks.deltarunic.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record StartBattleInvitePacket(int targetEntityId) implements CustomPacketPayload {
    public static final Type<StartBattleInvitePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("deltarunic", "start_battle_invite"));

    public static final StreamCodec<RegistryFriendlyByteBuf, StartBattleInvitePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, StartBattleInvitePacket::targetEntityId,
            StartBattleInvitePacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(StartBattleInvitePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer sender = (ServerPlayer) context.player();
            if (sender != null && sender.level().getEntity(packet.targetEntityId()) instanceof ServerPlayer target) {
                BattleInvitationManager.createInvite(sender, target);
            } else if (sender != null) {
                sender.sendSystemMessage(net.minecraft.network.chat.Component.literal("Invalid target for a PvP duel."));
            }
        });
    }
}