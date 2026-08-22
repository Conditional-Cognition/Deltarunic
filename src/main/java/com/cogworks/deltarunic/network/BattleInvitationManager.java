package com.cogworks.deltarunic.network;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BattleInvitationManager {
    private static final long EXPIRY_DURATION_MS = 30 * 1000;
    private static final double MAX_DISTANCE = 30.0;

    private record InviteData(UUID hostUuid, long expirationTime) {}
    private static final Map<UUID, InviteData> pendingInvites = new HashMap<>();

    public static void createInvite(ServerPlayer host, ServerPlayer target) {
        if (host.equals(target)) {
            host.sendSystemMessage(Component.literal("You cannot challenge yourself!"));
            return;
        }

        if (host.distanceToSqr(target) > MAX_DISTANCE * MAX_DISTANCE) {
            host.sendSystemMessage(Component.literal("Target player is too far away! (Max 30 blocks)"));
            return;
        }

        if (!hasLineOfSight(host, target)) {
            host.sendSystemMessage(Component.literal("Cannot challenge: No clear line of sight between you!"));
            return;
        }

        long expiresAt = System.currentTimeMillis() + EXPIRY_DURATION_MS;
        pendingInvites.put(target.getUUID(), new InviteData(host.getUUID(), expiresAt));

        target.connection.send(new ReceiveInvitePacket(host.getName().getString(), host.getUUID()));
        host.sendSystemMessage(Component.literal("Battle challenge sent to " + target.getName().getString() + "! Expires in 30 seconds."));
    }

    public static UUID getValidInvite(UUID targetUuid) {
        InviteData invite = pendingInvites.get(targetUuid);
        if (invite == null) return null;

        if (System.currentTimeMillis() > invite.expirationTime()) {
            pendingInvites.remove(targetUuid);
            return null;
        }

        pendingInvites.remove(targetUuid);
        return invite.hostUuid();
    }

    private static boolean hasLineOfSight(ServerPlayer host, ServerPlayer target) {
        Vec3 hostEyes = host.getEyePosition();
        Vec3 targetEyes = target.getEyePosition();

        BlockHitResult hitResult = host.level().clip(new ClipContext(
                hostEyes,
                targetEyes,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                host
        ));

        return hitResult.getType() == HitResult.Type.MISS;
    }
}