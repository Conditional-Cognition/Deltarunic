package com.cogworks.deltarunic.battle.animation;

import com.cogworks.deltarunic.battle.data.AttackDefinition;
import com.cogworks.deltarunic.battle.data.AttackObject;
import com.cogworks.deltarunic.battle.data.EntityBattleConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

/**
 * Renders attack objects during battle, with smooth animation along keyframe timelines.
 * Handles sprite loading, position/size interpolation, and collision visualization.
 */
public class AttackObjectRenderer {

    /**
     * Render all attack objects for the current attack.
     * Called every frame during BATTLEBOX_PHASE.
     */
    public static void renderAttackObjects(
            GuiGraphics guiGraphics,
            EntityBattleConfig config,
            String attackId,
            float animationTime,
            int screenWidth,
            int screenHeight
    ) {
        if (config == null || config.attacks() == null) {
            return;
        }

        // Find the attack definition
        AttackDefinition attack = config.attacks().stream()
                .filter(a -> a.id().equals(attackId))
                .findFirst()
                .orElse(null);

        if (attack == null || attack.attackObjects() == null) {
            return;
        }

        // Render each attack object in this attack
        for (AttackObject attackObj : attack.attackObjects()) {
            renderAttackObject(guiGraphics, attackObj, animationTime, screenWidth, screenHeight);
        }
    }

    /**
     * Render a single attack object at the current animation time.
     */
    private static void renderAttackObject(
            GuiGraphics guiGraphics,
            AttackObject attackObj,
            float animationTime,
            int screenWidth,
            int screenHeight
    ) {
        if (attackObj.animationPath() == null || attackObj.animationPath().isEmpty()) {
            return;
        }

        // Get interpolated state at this time
        Vector3f position = TimelineInterpolator.getPositionAtTime(attackObj.animationPath(), animationTime);
        Vector3f size = TimelineInterpolator.getSizeAtTime(attackObj.animationPath(), animationTime);
        float rotation = TimelineInterpolator.getRotationAtTime(attackObj.animationPath(), animationTime);
        String sprite = TimelineInterpolator.getSpriteAtTime(attackObj.animationPath(), animationTime);

        // Calculate screen position (center origin)
        float screenX = position.x - (size.x / 2);
        float screenY = position.y - (size.y / 2);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(screenX, screenY, 0.0f);

        if (rotation != 0.0f) {
            guiGraphics.pose().translate(size.x / 2, size.y / 2, 0.0f);
            guiGraphics.pose().rotateZ(rotation);
            guiGraphics.pose().translate(-size.x / 2, -size.y / 2, 0.0f);
        }

        // Draw the sprite (if available)
        if (sprite != null) {
            ResourceLocation spriteLocation = ResourceLocation.tryParse(sprite);
            if (spriteLocation != null) {
                try {
                    guiGraphics.blit(spriteLocation, 0, 0, 0, 0, (int) size.x, (int) size.y, (int) size.x, (int) size.y);
                } catch (Exception ignored) {
                    // Sprite not found, render placeholder
                    guiGraphics.fill(0, 0, (int) size.x, (int) size.y, 0xFFFF0000);
                }
            }
        } else {
            // No sprite, render colored rectangle for debugging
            guiGraphics.fill(0, 0, (int) size.x, (int) size.y, 0xFFFFAA00);
        }

        guiGraphics.pose().popPose();
    }

    /**
     * Check if a point (player cursor or hitbox center) collides with any active attack objects.
     * Returns the damage to take, or 0 if no collision.
     */
    public static float checkCollisionWithAttacks(
            EntityBattleConfig config,
            String attackId,
            float animationTime,
            float playerX,
            float playerY,
            float playerWidth,
            float playerHeight
    ) {
        if (config == null || config.attacks() == null) {
            return 0.0f;
        }

        AttackDefinition attack = config.attacks().stream()
                .filter(a -> a.id().equals(attackId))
                .findFirst()
                .orElse(null);

        if (attack == null || attack.attackObjects() == null) {
            return 0.0f;
        }

        float totalDamage = 0.0f;

        for (AttackObject attackObj : attack.attackObjects()) {
            // Check if this attack object is in its collision window
            if (!TimelineInterpolator.isInCollisionWindow(
                    animationTime,
                    attackObj.collisionActivateTime(),
                    attackObj.collisionDeactivateTime()
            )) {
                continue;  // Not hurting yet
            }

            // Get the attack object's current position and size
            Vector3f objPos = TimelineInterpolator.getPositionAtTime(attackObj.animationPath(), animationTime);
            Vector3f objSize = TimelineInterpolator.getSizeAtTime(attackObj.animationPath(), animationTime);

            // Simple AABB collision check
            float objLeft = objPos.x - (objSize.x / 2);
            float objRight = objPos.x + (objSize.x / 2);
            float objTop = objPos.y - (objSize.y / 2);
            float objBottom = objPos.y + (objSize.y / 2);

            float playerLeft = playerX;
            float playerRight = playerX + playerWidth;
            float playerTop = playerY;
            float playerBottom = playerY + playerHeight;

            // Check AABB overlap
            if (objLeft < playerRight && objRight > playerLeft &&
                objTop < playerBottom && objBottom > playerTop) {
                // Collision detected
                totalDamage += attackObj.damage();
            }
        }

        return totalDamage;
    }
}
