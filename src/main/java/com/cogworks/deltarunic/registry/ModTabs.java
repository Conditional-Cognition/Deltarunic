package com.cogworks.deltarunic.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
@SuppressWarnings("unused")
public class ModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, com.cogworks.deltarunic.Deltarunic.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> DELTARUNIC_TAB = CREATIVE_MODE_TABS.register(
            "deltarunic_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.deltarunic"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> {
                        Item iconItem = ModItems.UNOBTAINABLE.get();
                        return iconItem.getDefaultInstance();
                    })
                    .displayItems((parameters, output) -> {
                        ModItems.UNOBTAINABLE.get();
                    }).build());
}
