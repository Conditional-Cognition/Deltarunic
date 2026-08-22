package com.cogworks.deltarunic.client.records;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class EntityIconResolver {

    public static ItemStack getEntityIcon(LivingEntity entity) {
        ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        String path = entityId.getPath();

        Item skullItem = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("minecraft", path + "_head"));
        if (skullItem != Items.AIR) {
            return new ItemStack(skullItem);
        }
        if (entity.getType() == EntityType.PLAYER) {
            return new ItemStack(Items.PLAYER_HEAD);
        }

        Item spawnEgg = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("minecraft", path + "_spawn_egg"));
        if (spawnEgg != Items.AIR) {
            return new ItemStack(spawnEgg);
        }

        for (Item item : BuiltInRegistries.ITEM) {
            if (BuiltInRegistries.ITEM.getKey(item).getPath().contains(path)) {
                return new ItemStack(item);
            }
        }

        return new ItemStack(Items.GRASS_BLOCK);
    }
}