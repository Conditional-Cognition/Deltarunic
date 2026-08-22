package com.cogworks.deltarunic.network;

import com.cogworks.deltarunic.client.gui.DeltaruneBattleGui;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ServerBattleManager {

    private static final Map<UUID, LivingEntity> activeOpponents = new HashMap<>();

    public static void startBattleSession(ServerPlayer host, LivingEntity opposition) {

        if (opposition instanceof Mob mob) {
            mob.setNoAi(true);
        }
        activeOpponents.put(host.getUUID(), opposition);

        host.sendSystemMessage(Component.literal("§a[Deltarunic] Battle session initialized against " + opposition.getName().getString() + "!"));

        host.connection.send(new OpenBattleGuiPacket(opposition.getId(), DeltaruneBattleGui.SessionRole.HOST_CONTROLLER.ordinal()));

        if (opposition instanceof ServerPlayer targetPlayer) {
            targetPlayer.sendSystemMessage(Component.literal("§a[Deltarunic] Battle initiated against " + host.getName().getString() + "!"));
            targetPlayer.connection.send(new OpenBattleGuiPacket(host.getId(), DeltaruneBattleGui.SessionRole.OPPOSITION_ATTACKER.ordinal()));
        }
    }

    public static void endSession(ServerPlayer host) {
        LivingEntity opposition = activeOpponents.remove(host.getUUID());
        if (opposition instanceof Mob mob) {
            mob.setNoAi(false);
        }
    }
}