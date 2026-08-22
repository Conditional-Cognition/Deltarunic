package com.cogworks.deltarunic.client.gui;

import com.cogworks.deltarunic.battle.BattleAttackData;
import com.cogworks.deltarunic.client.records.MobBattleResource;
import com.cogworks.deltarunic.client.records.MobResourceManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class DeltaruneBattleGui extends Screen {
    private static final ResourceLocation HOTBAR_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar");
    private static final ResourceLocation HOTBAR_SELECTION_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar_selection");

    private final LivingEntity playerEntity;
    private final LivingEntity opponentEntity;
    private final BattleAttackData currentAttackData;

    private TurnState currentState = TurnState.ACTION_SELECT;
    private int selectedActionIndex = 0; // 0: FIGHT, 1: ACT, 2: ITEM, 3: SHIELD
    private int selectedGridIndex = 0;   // WASD navigation for hotbar/ACT sub-menus
    private String currentDialogueText;

    private final List<String> actList = List.of("Check", "Spare");

    public enum TurnState {
        ACTION_SELECT,
        SUB_MENU,
        DIALOGUE_RESULT,
        BATTLEBOX_PHASE
    }

    public DeltaruneBattleGui(LivingEntity player, LivingEntity opponent, List<String> resources, BattleAttackData attackData) {
        super(Component.translatable("gui.deltarunic_battle"));
        this.playerEntity = player;
        this.opponentEntity = opponent;
        this.currentAttackData = attackData;
        MobBattleResource mobResource = MobResourceManager.loadResourceForEntity(opponent);
        
        this.currentDialogueText = MobResourceManager.getRandomDialogue(mobResource, true);
    }

    @Override
    protected void init() {
        super.init();
        // Exit button
        this.addRenderableWidget(
            Button.builder(Component.literal("Exit"), b -> this.onClose())
                .bounds(10, 10, 50, 20)
                .build()
        );
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (currentState == TurnState.ACTION_SELECT) {
            if (keyCode == GLFW.GLFW_KEY_A || keyCode == GLFW.GLFW_KEY_LEFT) {
                selectedActionIndex = (selectedActionIndex - 1 + 4) % 4;
                return true;
            } else if (keyCode == GLFW.GLFW_KEY_D || keyCode == GLFW.GLFW_KEY_RIGHT) {
                selectedActionIndex = (selectedActionIndex + 1) % 4;
                return true;
            } else if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_SPACE) {
                this.currentState = TurnState.SUB_MENU;
                this.selectedGridIndex = 0;
                return true;
            }
        } else if (currentState == TurnState.SUB_MENU) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                this.currentState = TurnState.ACTION_SELECT;
                return true;
            }
            handleSubMenuNavigation(keyCode);
            return true;
        } else if (currentState == TurnState.DIALOGUE_RESULT) {
            if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_SPACE) {
                advanceTurnState();
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void handleSubMenuNavigation(int keyCode) {
        if (selectedActionIndex == 0 || selectedActionIndex == 2) { // Hotbar navigation
            if (keyCode == GLFW.GLFW_KEY_A || keyCode == GLFW.GLFW_KEY_LEFT) {
                selectedGridIndex = (selectedGridIndex - 1 + 9) % 9;
            } else if (keyCode == GLFW.GLFW_KEY_D || keyCode == GLFW.GLFW_KEY_RIGHT) {
                selectedGridIndex = (selectedGridIndex + 1) % 9;
            } else if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_SPACE) {
                executeSelectedHotbarAction();
            }
        } else if (selectedActionIndex == 1) { // ACT 2x4 Grid Navigation
            if (keyCode == GLFW.GLFW_KEY_W || keyCode == GLFW.GLFW_KEY_UP) {
                selectedGridIndex = Math.max(0, selectedGridIndex - 1);
            } else if (keyCode == GLFW.GLFW_KEY_S || keyCode == GLFW.GLFW_KEY_DOWN) {
                selectedGridIndex = Math.min(actList.size() - 1, selectedGridIndex + 1);
            } else if (keyCode == GLFW.GLFW_KEY_A || keyCode == GLFW.GLFW_KEY_LEFT || keyCode == GLFW.GLFW_KEY_D || keyCode == GLFW.GLFW_KEY_RIGHT) {
                selectedGridIndex = (selectedGridIndex ^ 4) < actList.size() ? (selectedGridIndex ^ 4) : selectedGridIndex;
            } else if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_SPACE) {
                executeActAction(actList.get(selectedGridIndex));
            }
        }
    }

    private void executeSelectedHotbarAction() {
        this.currentDialogueText = "* Used item slot " + (selectedGridIndex + 1) + ".";
        this.currentState = TurnState.DIALOGUE_RESULT;
    }

    private void executeActAction(String act) {
        this.currentDialogueText = "* You performed " + act + "!";
        this.currentState = TurnState.DIALOGUE_RESULT;
    }

    private void advanceTurnState() {
        if (currentAttackData == null) {
            this.currentDialogueText = "* " + opponentEntity.getDisplayName().getString() + " has no attacks. :(";
            this.currentState = TurnState.ACTION_SELECT;
        } else {
            this.currentState = TurnState.BATTLEBOX_PHASE;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (currentState == TurnState.DIALOGUE_RESULT) {
            advanceTurnState();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldCloseOnEsc() { return false; }

    @Override
    public boolean isPauseScreen() { return false; }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 100.0f);

        if (currentState == TurnState.BATTLEBOX_PHASE && currentAttackData != null) {
            renderBattlebox(guiGraphics);
        }

        renderStatusBox(guiGraphics);

        int startX = (this.width - 240) / 2;
        int menuY = this.height - 80;

        renderActionButtons(guiGraphics, startX, menuY);

        if (currentState == TurnState.SUB_MENU) {
            if (selectedActionIndex == 0 || selectedActionIndex == 2) {
                renderNativeHotbar(guiGraphics, startX, this.height - 55);
            } else if (selectedActionIndex == 1) {
                renderActGrid(guiGraphics, startX, this.height - 55);
            }
        } else {
            renderDialogueArea(guiGraphics, startX, this.height - 50);
        }

        guiGraphics.pose().popPose();
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void renderNativeHotbar(GuiGraphics guiGraphics, int startX, int y) {
        if (!(playerEntity instanceof Player player)) return;

        // Draw Vanilla Hotbar Container
        guiGraphics.blitSprite(HOTBAR_SPRITE, startX + 28, y, 182, 22);

        // Render Selector
        int selectorX = startX + 28 - 1 + (selectedGridIndex * 20);
        guiGraphics.blitSprite(HOTBAR_SELECTION_SPRITE, selectorX, y - 1, 24, 23);

        // Render Hotbar Items
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().items.get(i);
            if (!stack.isEmpty()) {
                int itemX = startX + 28 + 3 + (i * 20);
                guiGraphics.renderItem(stack, itemX, y + 3);
                guiGraphics.renderItemDecorations(this.font, stack, itemX, y + 3);
            }
        }
    }

    private void renderActGrid(GuiGraphics guiGraphics, int startX, int subMenuY) {
        for (int i = 0; i < actList.size(); i++) {
            int col = i / 4;
            int row = i % 4;
            int x = startX + (col * 110);
            int y = subMenuY + (row * 12);

            String prefix = (i == selectedGridIndex) ? "❤ " : "  ";
            int color = (i == selectedGridIndex) ? 0xFFFF5555 : 0xFFFFFFFF;
            guiGraphics.drawString(this.font, prefix + actList.get(i), x, y, color, false);
        }
    }

    private void renderDialogueArea(GuiGraphics guiGraphics, int startX, int subMenuY) {
        guiGraphics.drawString(this.font, currentDialogueText, startX + 10, subMenuY, 0xFFFFFFFF, false);
    }

    private void renderActionButtons(GuiGraphics guiGraphics, int startX, int menuY) {
        String[] labels = {"⚔", "✦", "💼", "🛡"};
        for (int i = 0; i < 4; i++) {
            int x = startX + (i * 60);
            int color = (i == selectedActionIndex) ? 0xFFFFFF00 : 0xFF888888;
            guiGraphics.fill(x, menuY, x + 55, menuY + 20, 0xFF000000);
            guiGraphics.renderOutline(x, menuY, 55, 20, color);
            guiGraphics.drawString(this.font, labels[i], x + 5, menuY + 6, color, false);
        }
    }

    private void renderStatusBox(GuiGraphics guiGraphics) {
        int menuWidth = 240;
        int startX = (this.width - menuWidth) / 2;
        int statusY = this.height - 105;

        guiGraphics.fill(startX, statusY, startX + menuWidth, statusY + 22, 0xFF000000);
        guiGraphics.renderOutline(startX, statusY, menuWidth, 22, 0xFF00FFFF);

        String playerName = playerEntity != null ? playerEntity.getName().getString() : "Player";
        String initial = playerName.isEmpty() ? "P" : playerName.substring(0, 1).toUpperCase();

        float maxHp = playerEntity != null ? playerEntity.getMaxHealth() : 20.0f;
        float currentHp = playerEntity != null ? playerEntity.getHealth() : 20.0f;
        int hpPercent = (int) ((currentHp / maxHp) * 100);

        Component betweenText = Component.translatable("gui.deltarunic_battle.username_health_between");
        Component statusComponent = Component.literal(initial).append(betweenText).append(hpPercent + "%");

        guiGraphics.drawString(this.font, statusComponent, startX + 10, statusY + 7, 0xFFFFFFFF, false);
    }

    private void renderBattlebox(GuiGraphics guiGraphics) {
        int boxWidth = currentAttackData.getBoxWidth();
        int boxHeight = currentAttackData.getBoxHeight();
        int centeredBoxX = (this.width - boxWidth) / 2;
        int boxY = currentAttackData.getBoxY();

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(currentAttackData.getTranslationX(), currentAttackData.getTranslationY(), 0.0f);
        guiGraphics.pose().scale(currentAttackData.getScaleX(), currentAttackData.getScaleY(), 1.0f);

        guiGraphics.fill(centeredBoxX, boxY, centeredBoxX + boxWidth, boxY + boxHeight, 0xFF000000);
        guiGraphics.renderOutline(centeredBoxX, boxY, boxWidth, boxHeight, 0xFFFFFFFF);

        guiGraphics.pose().popPose();
    }
}