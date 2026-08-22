package com.cogworks.deltarunic.items;

import com.cogworks.deltarunic.network.ServerBattleManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class DesignatedSoulItem extends Item {
    public DesignatedSoulItem(Properties properties) {
        super(properties);
    }
    public static boolean tryTriggerCounter(ServerPlayer player, LivingEntity attacker) {

        ItemStack offhandItem = player.getOffhandItem();
        if (offhandItem.getItem() instanceof DesignatedSoulItem) {

            Vec3 knockbackDir = player.getLookAngle().normalize().scale(7.0);
            attacker.setDeltaMovement(knockbackDir.x, 0.4, knockbackDir.z);
            attacker.hurtMarked = true;

            ServerBattleManager.startBattleSession(player, attacker);

            return true;
        }
        return false;
    }
}