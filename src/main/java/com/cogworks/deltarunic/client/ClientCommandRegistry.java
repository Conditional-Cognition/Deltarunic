package com.cogworks.deltarunic.client;

import com.cogworks.deltarunic.battle.BattleAttackData;
import com.cogworks.deltarunic.client.gui.DeltaruneBattleGui;
import com.cogworks.deltarunic.network.BattleInvitationManager;
import com.cogworks.deltarunic.network.ServerBattleManager;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

import java.util.Comparator;
import java.util.UUID;

@EventBusSubscriber(modid = "deltarunic", value = Dist.CLIENT)
public class ClientCommandRegistry {

    @SubscribeEvent
    public static void registerCommands(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(
                Commands.literal("deltarunic")

                        .then(Commands.literal("testbbox")
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
                                    BattleAttackData attackData = new BattleAttackData(200, 150, "split_horizontal", 5.0f, 10.0f);

                                    mc.execute(() -> mc.setScreen(new DeltaruneBattleGui(
                                            mc.player,
                                            targetOpponent,
                                            DeltaruneBattleGui.SessionRole.HOST_CONTROLLER,
                                            attackData
                                    )));
                                    return 1;
                                })
                        )

                        .then(Commands.literal("accept")
                                .then(Commands.argument("hostUuid", UuidArgument.uuid())
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            UUID hostUuid = UuidArgument.getUuid(context, "hostUuid");

                                            UUID validatedHostUuid = BattleInvitationManager.getValidInvite(player.getUUID());
                                            if (validatedHostUuid == null || !validatedHostUuid.equals(hostUuid)) {
                                                player.sendSystemMessage(Component.literal("§c[Deltarunic] This battle invitation has expired or is invalid."));
                                                return 0;
                                            }

                                            ServerPlayer host = context.getSource().getServer().getPlayerList().getPlayer(hostUuid);
                                            if (host == null) {
                                                player.sendSystemMessage(Component.literal("§c[Deltarunic] The host is no longer online."));
                                                return 0;
                                            }

                                            ServerBattleManager.startBattleSession(host, player);
                                            return 1;
                                        })
                                )
                        )
        );
    }
}