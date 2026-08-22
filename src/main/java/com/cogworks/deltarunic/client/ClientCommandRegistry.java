package com.cogworks.deltarunic.client;

import com.cogworks.deltarunic.battle.BattleAttackData;
import com.cogworks.deltarunic.client.gui.DeltaruneBattleGui;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

import java.util.Comparator;

@EventBusSubscriber(modid = "deltarunic", value = Dist.CLIENT)
public class ClientCommandRegistry {

    @SubscribeEvent
    public static void registerCommands(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(
                Commands.literal("testdeltarunicgui")
                        .executes(context -> {
                            Minecraft mc = Minecraft.getInstance();
                            if (mc.player == null || mc.level == null) return 0;


                            LivingEntity opponent = null;
                            if (mc.crosshairPickEntity instanceof LivingEntity living) {
                                opponent = living;
                            }


                            if (opponent == null) {
                                opponent = mc.level.getEntitiesOfClass(LivingEntity.class, mc.player.getBoundingBox().inflate(10.0))
                                        .stream()
                                        .filter(e -> e != mc.player)
                                        .min(Comparator.comparingDouble(e -> e.distanceToSqr(mc.player)))
                                        .orElse(null);
                            }


                            if (opponent == null) {
                                opponent = mc.player;
                            }

                            LivingEntity targetOpponent = opponent;
                            BattleAttackData attackData = resolveAttackDataForEntity(targetOpponent);

                            mc.execute(() -> mc.setScreen(new DeltaruneBattleGui(
                                    mc.player,
                                    targetOpponent,
                                    attackData
                            )));
                            return 1;
                        })
        );
    }

    private static BattleAttackData resolveAttackDataForEntity(LivingEntity entity) {
        String entityTypeId = entity.getType().getDescriptionId();

        if (entityTypeId.contains("zombie") || entityTypeId.contains("skeleton") || entityTypeId.contains("blaze")) {
            return new BattleAttackData(100, 100, 200, 150, 1.0f, 1.0f, 0.0f, 0.0f, false, "");
        }

        return null;
    }
}