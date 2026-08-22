package com.cogworks.deltarunic.registry;

import com.cogworks.deltarunic.items.DesignatedSoulItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = "deltarunic")
public class BattleEventBusSubscriber {

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (event.getSource().getEntity() instanceof LivingEntity attacker) {
                if (DesignatedSoulItem.tryTriggerCounter(player, attacker)) {
                    event.setCanceled(true);
                }
            }
        }
    }
}