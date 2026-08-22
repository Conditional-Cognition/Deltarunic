package com.cogworks.deltarunic.battle;

public class BattleAttackData {
    private final int boxX;
    private final int boxY;
    private final int boxWidth;
    private final int boxHeight;
    private final float scaleX;
    private final float scaleY;
    private final float translationX;
    private final float translationY;
    private final boolean hasSpriteChanges;
    private final String spritePath;

    public BattleAttackData(int boxX, int boxY, int boxWidth, int boxHeight, float scaleX, float scaleY, float translationX, float translationY, boolean hasSpriteChanges, String spritePath) {
        this.boxX = boxX;
        this.boxY = boxY;
        this.boxWidth = boxWidth;
        this.boxHeight = boxHeight;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.translationX = translationX;
        this.translationY = translationY;
        this.hasSpriteChanges = hasSpriteChanges;
        this.spritePath = spritePath;
    }

    public int getBoxX() {
        return boxX;
    }

    public int getBoxY() {
        return boxY;
    }

    public int getBoxWidth() {
        return boxWidth;
    }

    public int getBoxHeight() {
        return boxHeight;
    }

    public float getScaleX() {
        return scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }

    public float getTranslationX() {
        return translationX;
    }

    public float getTranslationY() {
        return translationY;
    }

    public boolean hasSpriteChanges() {
        return hasSpriteChanges;
    }

    public String getSpritePath() {
        return spritePath;
    }
}