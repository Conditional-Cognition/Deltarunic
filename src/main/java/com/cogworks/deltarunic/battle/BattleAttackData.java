package com.cogworks.deltarunic.battle;

public class BattleAttackData {
    private final int boxWidth;
    private final int boxHeight;
    private final String boxTransformType;
    private final float boxTransformStartTime;
    private final float boxTransformSpeed;

    public BattleAttackData(int boxWidth, int boxHeight, String boxTransformType, float boxTransformStartTime, float boxTransformSpeed) {
        this.boxWidth = boxWidth;
        this.boxHeight = boxHeight;
        this.boxTransformType = boxTransformType != null ? boxTransformType : "";
        this.boxTransformStartTime = boxTransformStartTime;
        this.boxTransformSpeed = boxTransformSpeed;
    }

    public BattleAttackData(int boxWidth, int boxHeight) {
        this(boxWidth, boxHeight, "", 0.0f, 0.0f);
    }

    public int getBoxWidth() {
        return boxWidth;
    }

    public int getBoxHeight() {
        return boxHeight;
    }

    public String getBoxTransformType() {
        return boxTransformType;
    }

    public float getBoxTransformStartTime() {
        return boxTransformStartTime;
    }

    public float getBoxTransformSpeed() {
        return boxTransformSpeed;
    }
}