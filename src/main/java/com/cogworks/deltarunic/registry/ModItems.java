package com.cogworks.deltarunic.registry;

import com.cogworks.ampersandlib.items.*;
import com.cogworks.deltarunic.items.*;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(com.cogworks.deltarunic.Deltarunic.MODID);

    @SuppressWarnings("unused")
    public static final DeferredItem<UnobtainableItem> UNOBTAINABLE = ITEMS.register(
            "unobtainable",
            () -> new UnobtainableItem(new Item.Properties().stacksTo(1))
    );
}