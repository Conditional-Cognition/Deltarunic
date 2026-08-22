package com.cogworks.deltarunic.player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.world.entity.player.Player;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class PlayerProfileManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    private static Path getPlayerDirectory(UUID playerUuid) {
        File configDir = net.neoforged.fml.loading.FMLPaths.CONFIGDIR.get().toFile();
        return new File(configDir, "deltarunic/players/" + playerUuid.toString()).toPath();
    }

    public static PlayerBattleProfile loadOrCreateProfile(Player player) {
        UUID uuid = player.getUUID();
        Path dir = getPlayerDirectory(uuid);
        Path dataFile = dir.resolve("data.json");

        if (Files.exists(dataFile)) {
            try (FileReader reader = new FileReader(dataFile.toFile())) {
                JsonObject json = GSON.fromJson(reader, JsonObject.class);
                return PlayerBattleProfile.fromJson(json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        PlayerBattleProfile defaultProfile = new PlayerBattleProfile(
                player.getName().getString(),
                90,
                12,
                2,
                "deltarunic:track_battle"
        );
        saveProfile(uuid, defaultProfile);
        return defaultProfile;
    }

    public static PlayerResourceProfile loadOrCreateResourceProfile(Player player) {
        UUID uuid = player.getUUID();
        Path dir = getPlayerDirectory(uuid);
        Path resFile = dir.resolve("resources.json");

        if (Files.exists(resFile)) {
            try (FileReader reader = new FileReader(resFile.toFile())) {
                JsonObject json = GSON.fromJson(reader, JsonObject.class);
                return PlayerResourceProfile.fromJson(json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        PlayerResourceProfile defaultRes = new PlayerResourceProfile("", "", false);
        saveResourceProfile(uuid, defaultRes);
        return defaultRes;
    }

    public static void saveProfile(UUID playerUuid, PlayerBattleProfile profile) {
        Path dir = getPlayerDirectory(playerUuid);
        try {
            Files.createDirectories(dir);
            Path dataFile = dir.resolve("data.json");
            try (FileWriter writer = new FileWriter(dataFile.toFile())) {
                GSON.toJson(profile, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveResourceProfile(UUID playerUuid, PlayerResourceProfile resourceProfile) {
        Path dir = getPlayerDirectory(playerUuid);
        try {
            Files.createDirectories(dir);
            Path resFile = dir.resolve("resources.json");
            try (FileWriter writer = new FileWriter(resFile.toFile())) {
                GSON.toJson(resourceProfile, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}