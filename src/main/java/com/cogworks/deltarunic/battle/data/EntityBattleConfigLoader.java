package com.cogworks.deltarunic.battle.data;

import com.google.gson.Gson;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.entity.LivingEntity;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Loads EntityBattleConfig from JSON resource files.
 * Expected path: assets/{namespace}/{entity_id}/resources.json
 */
public class EntityBattleConfigLoader {
    private static final Gson GSON = new Gson();

    /**
     * Load the full battle configuration for an entity.
     */
    public static EntityBattleConfig loadConfigForEntity(LivingEntity entity) {
        ResourceLocation entityTypeId = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        if (entityTypeId == null) {
            return null;
        }

        // Path resolves to assets/minecraft/zombie/resources.json (or similar)
        ResourceLocation configPath = ResourceLocation.fromNamespaceAndPath(
            entityTypeId.getNamespace(),
            entityTypeId.getPath() + "/resources.json"
        );

        Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(configPath);
        if (resource.isPresent()) {
            try (Reader reader = new InputStreamReader(resource.get().open(), StandardCharsets.UTF_8)) {
                return GSON.fromJson(reader, EntityBattleConfig.class);
            } catch (Exception ignored) {}
        }

        return null;
    }

    /**
     * Load a battle config by entity ID string (e.g., "minecraft:zombie").
     */
    public static EntityBattleConfig loadConfigByEntityId(String entityId) {
        ResourceLocation configPath = ResourceLocation.tryParse(entityId + "/resources.json");
        if (configPath == null) {
            return null;
        }

        Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(configPath);
        if (resource.isPresent()) {
            try (Reader reader = new InputStreamReader(resource.get().open(), StandardCharsets.UTF_8)) {
                return GSON.fromJson(reader, EntityBattleConfig.class);
            } catch (Exception ignored) {}
        }

        return null;
    }
}
