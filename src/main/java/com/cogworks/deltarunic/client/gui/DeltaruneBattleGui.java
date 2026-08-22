package com.cogworks.deltarunic.client.gui;

import com.cogworks.deltarunic.battle.BattleAttackData;
import com.cogworks.deltarunic.battle.BattleDamageTracker;
import com.cogworks.deltarunic.battle.animation.AttackObjectRenderer;
import com.cogworks.deltarunic.battle.data.EntityBattleConfig;
import com.cogworks.deltarunic.battle.data.EntityBattleConfigLoader;
import com.cogworks.deltarunic.player.PlayerBattleProfile;
import com.cogworks.deltarunic.player.PlayerProfileManager;
import com.cogworks.deltarunic.player.PlayerResourceProfile;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class DeltaruneBattleGui extends Screen {

    public enum SessionRole {
        HOST_CONTROLLER,    
        OPPOSITION_ATTACKER 
    }

    private enum TurnState {
        COMMAND_PHASE,
        BULLET_HELL_PHASE
    }

    private final SessionRole localRole;
    private final EntityBattleConfig oppositionConfig;
    private final BattleAttackData activeAttackData;
    
    private final PlayerBattleProfile hostProfile;
    private final PlayerResourceProfile hostResourceProfile;

    private final TurnState currentState = TurnState.COMMAND_PHASE;
    private final String currentAttackId = "";
    private float battleboxTimeInSeconds = 0.0f;

    private float soulX = 0.0f;
    private float soulY = 0.0f;
    private final float soulSpeed = 2.5f;

    private final List<BoxPolygon> activeBoxPolygons = new ArrayList<>();
    private final BattleDamageTracker hostDamageTracker;

    public DeltaruneBattleGui(LivingEntity host, LivingEntity opposition, SessionRole role, BattleAttackData fallbackData) {
        super(Component.literal("Deltarunic PvP Session"));
        this.localRole = role;
        this.activeAttackData = fallbackData;

        if (host instanceof Player playerHost) {
            this.hostProfile = PlayerProfileManager.loadOrCreateProfile(playerHost);
            this.hostResourceProfile = PlayerProfileManager.loadOrCreateResourceProfile(playerHost);
        } else {
            this.hostProfile = new PlayerBattleProfile("Host", 90, 10, 0, "");
            this.hostResourceProfile = new PlayerResourceProfile("", "", false);
        }

        String entityId = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(opposition.getType()).toString();
        this.oppositionConfig = EntityBattleConfigLoader.loadConfigByEntityId(entityId);

        this.hostDamageTracker = new BattleDamageTracker(host);

        resetBoxGeometryToDefault(fallbackData);
    }

    private void resetBoxGeometryToDefault(BattleAttackData fallback) {
        activeBoxPolygons.clear();
        float w = 200;
        float h = 150;
        if (oppositionConfig != null && oppositionConfig.battlebox() != null) {
            w = oppositionConfig.battlebox().width();
            h = oppositionConfig.battlebox().height();
        } else if (fallback != null) {
            w = fallback.getBoxWidth();
            h = fallback.getBoxHeight();
        }

        activeBoxPolygons.add(new BoxPolygon(
                new Vector2f(0, 0),
                new Vector2f(w, 0),
                new Vector2f(w, h),
                new Vector2f(0, h)
        ));

        this.soulX = w / 2.0f - 4.0f;
        this.soulY = h / 2.0f - 4.0f;
    }

    @Override
    public void tick() {
        super.tick();
        if (currentState == TurnState.BULLET_HELL_PHASE) {
            this.battleboxTimeInSeconds += 1.0f / 20.0f;

            handleSoulMovementInputs();
            applyDynamicBoxTransformations(battleboxTimeInSeconds);

            if (oppositionConfig != null && !currentAttackId.isEmpty()) {
                float damage = AttackObjectRenderer.checkCollisionWithAttacks(
                        oppositionConfig, currentAttackId, battleboxTimeInSeconds, soulX, soulY, 8.0f, 8.0f
                );
                if (damage > 0.0f) {
                    hostDamageTracker.addDamage(damage);
                }
            }
        }
    }

    private void handleSoulMovementInputs() {
        float dx = 0;
        float dy = 0;

        if (net.minecraft.client.Minecraft.getInstance().options.keyUp.isDown()) dy -= soulSpeed;
        if (net.minecraft.client.Minecraft.getInstance().options.keyDown.isDown()) dy += soulSpeed;
        if (net.minecraft.client.Minecraft.getInstance().options.keyLeft.isDown()) dx -= soulSpeed;
        if (net.minecraft.client.Minecraft.getInstance().options.keyRight.isDown()) dx += soulSpeed;

        float targetX = soulX + dx;
        float targetY = soulY + dy;

        if (isPointInsideAnyPolygon(targetX, targetY)) {
            soulX = targetX;
            soulY = targetY;
        }
    }

    private boolean isPointInsideAnyPolygon(float px, float py) {
        for (BoxPolygon poly : activeBoxPolygons) {
            if (poly.contains(px, py)) return true;
        }
        return false;
    }

    private void applyDynamicBoxTransformations(float time) {
        if (activeAttackData == null) return;

        String transformType = activeAttackData.getBoxTransformType();
        float startTime = activeAttackData.getBoxTransformStartTime();
        float speed = activeAttackData.getBoxTransformSpeed();

        if ("split_horizontal".equals(transformType) && time >= startTime) {
            float elapsed = time - startTime;
            for (BoxPolygon poly : activeBoxPolygons) {
                if (poly.isSplitable) {
                    poly.offsetBottomHalf(elapsed * speed);
                }
            }
        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        renderPolygonalBattleboxes(guiGraphics);
        renderPvPHud(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void renderPolygonalBattleboxes(GuiGraphics guiGraphics) {
        int centeredBaseX = (this.width - 200) / 2;
        int centeredBaseY = (this.height - 150) / 2;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(centeredBaseX, centeredBaseY, 0.0f);

        for (BoxPolygon poly : activeBoxPolygons) {
            poly.renderPolygon(guiGraphics);
        }

        if (currentState == TurnState.BULLET_HELL_PHASE && !currentAttackId.isEmpty()) {
            AttackObjectRenderer.renderAttackObjects(guiGraphics, oppositionConfig, currentAttackId, battleboxTimeInSeconds);
            guiGraphics.fill((int) soulX, (int) soulY, (int) soulX + 8, (int) soulY + 8, 0xFFFF0000);
        }

        guiGraphics.pose().popPose();
    }

    private void renderPvPHud(GuiGraphics guiGraphics) {
        String roleText = "Role: " + localRole.name() + " | Profile: " + hostProfile.playerName() + " (HP: " + hostProfile.maxHp() + ")";
        guiGraphics.drawString(this.font, roleText, 20, 20, 0xFF55FF55);

        if (localRole == SessionRole.HOST_CONTROLLER) {
            guiGraphics.drawString(this.font, "[F]IGHT  [A]CT  [I]TEM  [S]HIELD", 30, this.height - 40, 0xFFFFAA00);
        } else {
            guiGraphics.drawString(this.font, "Opposition Attacking Wave...", 30, this.height - 40, 0xFFFF5555);
        }
    }

    public static class BoxPolygon {
        public Vector2f topLeft, topRight, bottomRight, bottomLeft;
        public boolean isSplitable = true;

        public BoxPolygon(Vector2f tl, Vector2f tr, Vector2f br, Vector2f bl) {
            this.topLeft = tl;
            this.topRight = tr;
            this.bottomRight = br;
            this.bottomLeft = bl;
        }

        public boolean contains(float x, float y) {
            return x >= topLeft.x && x <= topRight.x && y >= topLeft.y && y <= bottomLeft.y;
        }

        public void offsetBottomHalf(float amount) {
            bottomLeft.y += amount;
            bottomRight.y += amount;
        }

        public void renderPolygon(GuiGraphics graphics) {
            graphics.renderOutline((int) topLeft.x, (int) topLeft.y, (int)(topRight.x - topLeft.x), (int)(bottomLeft.y - topLeft.y), 0xFFFFFFFF);
            graphics.fill((int) topLeft.x, (int) topLeft.y, (int) topRight.x, (int) bottomRight.y, 0xFF000000);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}