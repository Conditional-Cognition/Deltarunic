package com.cogworks.deltarunic.client.gui;

import com.cogworks.deltarunic.battle.BattleAttackData;
import com.cogworks.deltarunic.battle.BattleDamageTracker;
import com.cogworks.deltarunic.battle.animation.AttackObjectRenderer;
import com.cogworks.deltarunic.battle.data.EntityBattleConfig;
import com.cogworks.deltarunic.battle.data.EntityBattleConfigLoader;
import com.cogworks.deltarunic.client.records.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DeltaruneBattleGui extends Screen {

    private enum TurnState {
        DIALOGUE_PHASE,
        BATTLEBOX_PHASE
    }

    private final LivingEntity playerEntity;
    private final LivingEntity opponentEntity;
    private final BattleAttackData currentAttackData;
    private final EntityBattleConfig opponentConfig;

    private final MobBattleResource mobBattleResource;
    private String currentAttackId = "";
    private String currentDialogueText;
    private TurnState currentState = TurnState.DIALOGUE_PHASE;
    private final List<String> actList;
    
    private float battleboxTimeInSeconds = 0.0f;
    private float soulX = 0.0f;
    private float soulY = 0.0f;

    private final BattleDamageTracker playerDamageTracker;
    private final BattleDamageTracker opponentDamageTracker;

    public DeltaruneBattleGui(LivingEntity player, LivingEntity opponent, BattleAttackData attackData) {
        super(Component.literal("Deltarune Battle"));
        this.playerEntity = player;
        this.opponentEntity = opponent;
        this.currentAttackData = attackData;


        String entityId = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(opponent.getType()).toString();
        this.opponentConfig = EntityBattleConfigLoader.loadConfigByEntityId(entityId);
        this.mobBattleResource = MobResourceManager.loadResourceForEntity(opponent);


        this.playerDamageTracker = new BattleDamageTracker(player);
        this.opponentDamageTracker = new BattleDamageTracker(opponent);



        if (opponentConfig != null) {
            actList = opponentConfig.pacificationMethods();
            this.currentAttackId = opponentConfig.defaultAttack() != null ? opponentConfig.defaultAttack() : "";
            this.currentDialogueText = DialogueSelector.selectDialogue(opponentConfig, DialogueState.GREETING);
        } else {
            actList = List.of("Check", "Spare");
            this.currentDialogueText = "* " + opponent.getDisplayName().getString() + " stands menacingly...";
        }

        if (this.mobBattleResource != null) {
            this.currentDialogueText = MobResourceManager.getRandomDialogue(this.mobBattleResource, false);
        }


        resetSoulPosition();
    }

    private void resetSoulPosition() {
        int boxWidth = 200;
        int boxHeight = 150;

        if (opponentConfig != null && opponentConfig.battlebox() != null) {
            boxWidth = opponentConfig.battlebox().width();
            boxHeight = opponentConfig.battlebox().height();
        } else if (currentAttackData != null) {
            boxWidth = currentAttackData.getBoxWidth();
            boxHeight = currentAttackData.getBoxHeight();
        }

        this.soulX = (boxWidth / 2.0f) - 4.0f;
        this.soulY = (boxHeight / 2.0f) - 4.0f;
    }

    private void advanceTurnState() {
        if (this.mobBattleResource != null) {
            this.currentDialogueText = MobResourceManager.getRandomDialogue(this.mobBattleResource, false);
        } else if (opponentConfig != null && currentAttackId != null) {
            this.currentDialogueText = DialogueSelector.selectPreAttackDialogue(opponentConfig, currentAttackId);
        } else {
            this.currentDialogueText = "* " + opponentEntity.getDisplayName().getString() + " prepares an attack.";
        }
        
        this.currentState = TurnState.BATTLEBOX_PHASE;
        this.battleboxTimeInSeconds = 0.0f;
        resetSoulPosition();
    }

    @Override
    public void tick() {
        super.tick();
        if (currentState == TurnState.BATTLEBOX_PHASE) {
            this.battleboxTimeInSeconds += 1.0f / 20.0f;


            if (opponentConfig != null && currentAttackId != null && !currentAttackId.isEmpty()) {
                float incomingDamage = AttackObjectRenderer.checkCollisionWithAttacks(
                        opponentConfig,
                        currentAttackId,
                        battleboxTimeInSeconds,
                        soulX,
                        soulY,
                        8.0f,
                        8.0f
                );
                
                if (incomingDamage > 0.0f) {
                    playerDamageTracker.addDamage(incomingDamage);
                }
            }
        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        

        renderBattlebox(guiGraphics);


        ItemStack opponentIcon = EntityIconResolver.getEntityIcon(opponentEntity);
        guiGraphics.renderItem(opponentIcon, 20, 20);
        guiGraphics.drawString(this.font, opponentEntity.getDisplayName().getString() + "  HP: " + (int) opponentDamageTracker.getCurrentBattleHp(), 45, 24, 0xFFFFFF);


        ItemStack playerIcon = EntityIconResolver.getEntityIcon(playerEntity);
        guiGraphics.renderItem(playerIcon, 20, this.height - 40);
        guiGraphics.drawString(this.font, "HP: " + (int) playerDamageTracker.getCurrentBattleHp() + " / " + (int) playerDamageTracker.getMaxHp(), 45, this.height - 36, 0xFFFFFF);


        guiGraphics.drawString(this.font, currentDialogueText, 30, this.height - 80, 0xFFFFFF);



        if (currentState == TurnState.DIALOGUE_PHASE) {
            int actStartX = this.width - 120;
            int actStartY = this.height - 80;

            guiGraphics.drawString(this.font, "Available ACTs:", actStartX, actStartY - 14, 0xFFFFAA00);
            for (int i = 0; i < actList.size(); i++) {
                guiGraphics.drawString(this.font, "* " + actList.get(i), actStartX, actStartY + (i * 12), 0xFFFFFF);
            }
        }

        if (currentState == TurnState.BATTLEBOX_PHASE) {
            float turnDamage = playerDamageTracker.getAccumulatedDamageThisTurn();
            if (turnDamage > 0) {

                guiGraphics.drawString(this.font, "Damage: " + (int) turnDamage, (this.width / 2) - 30, (this.height / 2) - 90, 0xFFFF5555);
            }
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void renderBattlebox(GuiGraphics guiGraphics) {
        int boxWidth = 200;
        int boxHeight = 150;
        float offsetX = 0.0f;
        float offsetY = 0.0f;
        float scaleX = 1.0f;
        float scaleY = 1.0f;

        if (opponentConfig != null && opponentConfig.battlebox() != null) {
            var battlebox = opponentConfig.battlebox();
            boxWidth = battlebox.width();
            boxHeight = battlebox.height();
            offsetX = battlebox.positionOffset().x;
            offsetY = battlebox.positionOffset().y;
            scaleX = battlebox.scale().x;
            scaleY = battlebox.scale().y;
        } else if (currentAttackData != null) {
            boxWidth = currentAttackData.getBoxWidth();
            boxHeight = currentAttackData.getBoxHeight();
            offsetX = currentAttackData.getTranslationX();
            offsetY = currentAttackData.getTranslationY();
            scaleX = currentAttackData.getScaleX();
            scaleY = currentAttackData.getScaleY();
        }

        int centeredBoxX = (this.width - boxWidth) / 2;
        int centeredBoxY = (this.height - boxHeight) / 2;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(centeredBoxX + offsetX, centeredBoxY + offsetY, 0.0f);
        guiGraphics.pose().scale(scaleX, scaleY, 1.0f);


        guiGraphics.fill(0, 0, boxWidth, boxHeight, 0xFF000000);
        guiGraphics.renderOutline(0, 0, boxWidth, boxHeight, 0xFFFFFFFF);


        if (currentAttackId != null && !currentAttackId.isEmpty()) {
            AttackObjectRenderer.renderAttackObjects(
                guiGraphics,
                opponentConfig,
                currentAttackId,
                battleboxTimeInSeconds
            );
        }


        if (currentState == TurnState.BATTLEBOX_PHASE) {
            guiGraphics.fill((int) soulX, (int) soulY, (int) soulX + 8, (int) soulY + 8, 0xFFFF0000);
        }

        guiGraphics.pose().popPose();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

        if (keyCode == 72) {
            playerDamageTracker.heal(10.0f);
            return true;
        }


        if (keyCode == 257 || keyCode == 32) {

            if (playerDamageTracker.isDefeated() || opponentDamageTracker.isDefeated()) {
                this.onClose();
                return true;
            }

            if (currentState == TurnState.DIALOGUE_PHASE) {
                advanceTurnState();
            } else {
                currentState = TurnState.DIALOGUE_PHASE;

                playerDamageTracker.resetTurnDamage();
                opponentDamageTracker.resetTurnDamage();
            }
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {

        playerDamageTracker.applyFinalDamageToEntity();
        opponentDamageTracker.applyFinalDamageToEntity();
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}