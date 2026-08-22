package com.cogworks.deltarunic.battle;

import net.minecraft.resources.ResourceLocation;

public class BattleHeartManager {
    private static final ResourceLocation STATIC_HEART = ResourceLocation.fromNamespaceAndPath("deltarunic", "textures/gui/heart.png");
    private static final ResourceLocation ANIMATED_HEART = ResourceLocation.fromNamespaceAndPath("deltarunic", "textures/gui/heart_hurt.png");
    private int iframeTimer = 0;

    public void update() {
        if (iframeTimer > 0) {
            iframeTimer--;
        }
    }

    public void triggerDamage(int iframeDuration) {
        this.iframeTimer = iframeDuration;
    }

    public ResourceLocation getCurrentSprite() {
        return iframeTimer > 0 ? ANIMATED_HEART : STATIC_HEART;
    }

    public boolean isInvulnerable() {
        return iframeTimer > 0;
    }
}