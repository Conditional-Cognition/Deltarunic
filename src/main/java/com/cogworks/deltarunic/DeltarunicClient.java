package com.cogworks.deltarunic;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = Deltarunic.MODID, dist = Dist.CLIENT) @SuppressWarnings("removal")
@EventBusSubscriber(modid = Deltarunic.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD) // Added explicit MOD bus targeting
public class DeltarunicClient {
    public DeltarunicClient(IEventBus modEventBus, ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        Deltarunic.LOGGER.info("HELLO FROM CLIENT SETUP!");
        Deltarunic.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }
}
