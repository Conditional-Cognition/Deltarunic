package com.cogworks.deltarunic;

import com.cogworks.deltarunic.network.OpenBattleGuiPacket;
import com.cogworks.deltarunic.network.ReceiveInvitePacket;
import com.cogworks.deltarunic.network.StartBattleInvitePacket;
import com.cogworks.deltarunic.registry.*;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;

@Mod(com.cogworks.deltarunic.Deltarunic.MODID)
public class Deltarunic {
    public static final String MODID = "deltarunic";
    public static final Logger LOGGER = LogUtils.getLogger();
    @SuppressWarnings("unused")
    public Deltarunic(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerPayloads);

        ModParticles.PARTICLE_TYPES.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModTabs.CREATIVE_MODE_TABS.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1")
                .versioned("1.0");

        registrar.playToClient(
                ReceiveInvitePacket.TYPE,
                ReceiveInvitePacket.STREAM_CODEC,
                ReceiveInvitePacket::handle
        );

        registrar.playToServer(
                StartBattleInvitePacket.TYPE,
                StartBattleInvitePacket.STREAM_CODEC,
                StartBattleInvitePacket::handle
        );

        registrar.playToClient(
                OpenBattleGuiPacket.TYPE,
                OpenBattleGuiPacket.STREAM_CODEC,
                OpenBattleGuiPacket::handle
        );
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }
}