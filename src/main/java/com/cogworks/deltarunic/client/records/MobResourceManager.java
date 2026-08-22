package com.cogworks.deltarunic.client.records;

import com.google.gson.Gson;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.entity.LivingEntity;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MobResourceManager {
    private static final Gson GSON = new Gson();

    public static MobBattleResource loadResourceForEntity(LivingEntity entity) {
        ResourceLocation entityTypeId = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()); // e.g. "minecraft:zombie"
        
        // Path resolves to assets/minecraft/zombie/resources.json
        ResourceLocation configPath = ResourceLocation.fromNamespaceAndPath(
            entityTypeId.getNamespace(),
            entityTypeId.getPath() + "/resources.json"
        );

        Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(configPath);
        if (resource.isPresent()) {
            try (Reader reader = new InputStreamReader(resource.get().open(), StandardCharsets.UTF_8)) {
                return GSON.fromJson(reader, MobBattleResource.class);
            } catch (Exception ignored) {}
        }
        return null;
    }

    public static String getRandomDialogue(MobBattleResource resource, boolean firstTurn) {
        if (resource == null || resource.dialogues() == null || resource.dialogues().isEmpty()) {
            return "stands menacingly.";
        }
        
        List<String> keys = new ArrayList<>(resource.dialogues().keySet());
        String randomKey = keys.get((int) (Math.random() * keys.size()));
        String text = resource.dialogues().get(randomKey);

        String prefix = "The";
        if (resource.dialogue_prefixes() != null) {
            prefix = firstTurn ? resource.dialogue_prefixes().getOrDefault("before", "A") 
                               : resource.dialogue_prefixes().getOrDefault("after", "The");
        }

        return prefix + text;
    }
}