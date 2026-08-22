package com.cogworks.deltarunic.client;

import com.cogworks.deltarunic.network.BattleInvitationManager;
import com.cogworks.deltarunic.network.ServerBattleManager;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.UUID;

@EventBusSubscriber(modid = "deltarunic") // Automatically registers to NeoForge's common event bus
public class ClientCommandRegistry {

    private static final double MAX_DISTANCE = 30.0;

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(
                Commands.literal("deltarunic")
                        // 1. Subcommand: /deltarunic testbbox [<target>]
                        .then(Commands.literal("testbbox")
                                .executes(context -> {
                                    ServerPlayer player = context.getSource().getPlayerOrException();
                                    // Default fallback to self if no target is provided
                                    ServerBattleManager.startBattleSession(player, player);
                                    return 1;
                                })
                                .then(Commands.argument("target", EntityArgument.entity())
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            Entity targetEntity = EntityArgument.getEntity(context, "target");

                                            if (!(targetEntity instanceof LivingEntity livingTarget)) {
                                                player.sendSystemMessage(Component.literal("§c[Deltarunic] Target must be a living entity!"));
                                                return 0;
                                            }

                                            // Check 30 block distance limit
                                            if (player.distanceToSqr(livingTarget) > MAX_DISTANCE * MAX_DISTANCE) {
                                                player.sendSystemMessage(Component.literal("§c[Deltarunic] Subject of command is more than 30 blocks away"));
                                                return 0;
                                            }

                                            ServerBattleManager.startBattleSession(player, livingTarget);
                                            return 1;
                                        })
                                )
                        )
                        // 2. Subcommand: /deltarunic accept <hostUuid>
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