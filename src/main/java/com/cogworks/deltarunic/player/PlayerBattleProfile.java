package com.cogworks.deltarunic.player;

import com.google.gson.JsonObject;

public record PlayerBattleProfile(
        String playerName,
        int maxHp,
        int attackPower,
        int defensePower,
        String themeSongId
) {
    public static PlayerBattleProfile fromJson(JsonObject json) {
        String name = json.has("playerName") ? json.get("playerName").getAsString() : "Unknown";
        int hp = json.has("maxHp") ? json.get("maxHp").getAsInt() : 90;
        int atk = json.has("attackPower") ? json.get("attackPower").getAsInt() : 10;
        int def = json.has("defensePower") ? json.get("defensePower").getAsInt() : 0;
        String theme = json.has("themeSongId") ? json.get("themeSongId").getAsString() : "";

        return new PlayerBattleProfile(name, hp, atk, def, theme);
    }
}