package com.cogworks.deltarunic.player;

import com.google.gson.JsonObject;

public record PlayerResourceProfile(
        String customSoulTexturePath,
        String customBoxBorderTexturePath,
        boolean useAnimatedSprites
) {
    public static PlayerResourceProfile fromJson(JsonObject json) {
        String soulTex = json.has("customSoulTexturePath") ? json.get("customSoulTexturePath").getAsString() : "";
        String borderTex = json.has("customBoxBorderTexturePath") ? json.get("customBoxBorderTexturePath").getAsString() : "";
        boolean animated = json.has("useAnimatedSprites") && json.get("useAnimatedSprites").getAsBoolean();

        return new PlayerResourceProfile(soulTex, borderTex, animated);
    }
}