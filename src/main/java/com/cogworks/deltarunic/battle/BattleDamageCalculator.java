package com.cogworks.deltarunic.battle;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;

public class BattleDamageCalculator {
    public static float calculateDamage(LivingEntity entity, DamageSource source, float amount) {
        ItemStack itemStack = entity.getUseItem();
        boolean hasShieldEquipped = !itemStack.isEmpty() && itemStack.getItem() instanceof ShieldItem;
        
        if (entity.isUsingItem() && hasShieldEquipped) {
            applyShieldStun(entity);
            return 0.0f;
        } else {
            return amount * 0.5f;
        }
    }

    private static void applyShieldStun(LivingEntity entity) {
        entity.setDeltaMovement(0.0, entity.getDeltaMovement().y, 0.0);
    }
}