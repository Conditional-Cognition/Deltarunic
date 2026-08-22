package com.cogworks.deltarunic.network;

import com.cogworks.deltarunic.battle.BattleAttackData;
import com.cogworks.deltarunic.client.gui.DeltaruneBattleGui;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record OpenBattleGuiPacket(int opponentEntityId, int roleOrdinal) implements CustomPacketPayload {
    public static final Type<OpenBattleGuiPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("deltarunic", "open_battle_gui"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenBattleGuiPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, OpenBattleGuiPacket::opponentEntityId,
            ByteBufCodecs.VAR_INT, OpenBattleGuiPacket::roleOrdinal,
            OpenBattleGuiPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenBattleGuiPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && mc.level != null) {
                if (mc.level.getEntity(packet.opponentEntityId()) instanceof LivingEntity opponent) {
                    DeltaruneBattleGui.SessionRole role = DeltaruneBattleGui.SessionRole.values()[packet.roleOrdinal()];

                    BattleAttackData fallbackData = new BattleAttackData(200, 150, "split_horizontal", 5.0f, 10.0f);

                    mc.setScreen(new DeltaruneBattleGui(
                            mc.player,
                            opponent,
                            role,
                            fallbackData
                    ));
                }
            }
        });
    }
}