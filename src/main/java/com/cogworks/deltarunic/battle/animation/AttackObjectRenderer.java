package com.cogworks.deltarunic.battle.animation;

import com.cogworks.deltarunic.battle.data.AttackDefinition;
import com.cogworks.deltarunic.battle.data.AttackObject;
import com.cogworks.deltarunic.battle.data.EntityBattleConfig;
import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

public class AttackObjectRenderer {

    public static void renderAttackObjects(
            GuiGraphics guiGraphics,
            EntityBattleConfig config,
            String attackId,
            float animationTime
    ) {
        if (config == null || config.attacks() == null) {
            return;
        }

        AttackDefinition attack = config.attacks().stream()
                .filter(a -> a.id().equals(attackId))
                .findFirst()
                .orElse(null);

        if (attack == null || attack.attackObjects() == null) {
            return;
        }

        for (AttackObject attackObj : attack.attackObjects()) {
            renderAttackObject(guiGraphics, attackObj, animationTime);
        }
    }

    private static void renderAttackObject(
            GuiGraphics guiGraphics,
            AttackObject attackObj,
            float animationTime
    ) {
        if (attackObj.animationPath() == null || attackObj.animationPath().isEmpty()) {
            return;
        }

        Vector3f position = TimelineInterpolator.getPositionAtTime(attackObj.animationPath(), animationTime);
        Vector3f size = TimelineInterpolator.getSizeAtTime(attackObj.animationPath(), animationTime);
        float rotation = TimelineInterpolator.getRotationAtTime(attackObj.animationPath(), animationTime);
        String sprite = TimelineInterpolator.getSpriteAtTime(attackObj.animationPath(), animationTime);

        float screenX = position.x - (size.x / 2);
        float screenY = position.y - (size.y / 2);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(screenX, screenY, 0.0f);

        if (rotation != 0.0f) {
            guiGraphics.pose().translate(size.x / 2, size.y / 2, 0.0f);
            guiGraphics.pose().mulPose(Axis.ZP.rotation(rotation));
            guiGraphics.pose().translate(-size.x / 2, -size.y / 2, 0.0f);
        }

        if (sprite != null) {
            ResourceLocation spriteLocation = ResourceLocation.tryParse(sprite);
            if (spriteLocation != null) {
                try {
                    guiGraphics.blit(spriteLocation, 0, 0, 0, 0, (int) size.x, (int) size.y, (int) size.x, (int) size.y);
                } catch (Exception ignored) {
                    guiGraphics.fill(0, 0, (int) size.x, (int) size.y, 0xFFFF0000);
                }
            }
        } else {
            guiGraphics.fill(0, 0, (int) size.x, (int) size.y, 0xFFFFAA00);
        }

        guiGraphics.pose().popPose();
    }

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
            if (!TimelineInterpolator.isInCollisionWindow(
                    animationTime,
                    attackObj.collisionActivateTime(),
                    attackObj.collisionDeactivateTime()
            )) {
                continue;
            }

            Vector3f objPos = TimelineInterpolator.getPositionAtTime(attackObj.animationPath(), animationTime);
            Vector3f objSize = TimelineInterpolator.getSizeAtTime(attackObj.animationPath(), animationTime);

            float objLeft = objPos.x - (objSize.x / 2);
            float objRight = objPos.x + (objSize.x / 2);
            float objTop = objPos.y - (objSize.y / 2);
            float objBottom = objPos.y + (objSize.y / 2);

            float playerRight = playerX + playerWidth;
            float playerBottom = playerY + playerHeight;

            if (objLeft < playerRight && objRight > playerX &&
                    objTop < playerBottom && objBottom > playerY) {

                totalDamage += attackObj.damage();
            }
        }

        return totalDamage;
    }
}