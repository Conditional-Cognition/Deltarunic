package com.cogworks.deltarunic.battle;

import net.minecraft.world.entity.LivingEntity;

public class BattleDamageTracker {
    private final LivingEntity entity;
    private final float maxHp;
    private float currentBattleHp;
    private float accumulatedDamageThisTurn = 0.0f;

    public BattleDamageTracker(LivingEntity entity) {
        this.entity = entity;
        this.maxHp = entity.getMaxHealth();
        this.currentBattleHp = entity.getHealth();
    }

    public void addDamage(float amount) {
        this.accumulatedDamageThisTurn += amount;
        this.currentBattleHp = Math.max(0.0f, this.currentBattleHp - amount);
    }

    public void heal(float amount) {
        this.currentBattleHp = Math.min(this.maxHp, this.currentBattleHp + amount);
    }

    public float getCurrentBattleHp() {
        return currentBattleHp;
    }

    public float getMaxHp() {
        return maxHp;
    }

    public boolean isDefeated() {
        return currentBattleHp <= 0.0f;
    }

    public float getAccumulatedDamageThisTurn() {
        return accumulatedDamageThisTurn;
    }

    public void resetTurnDamage() {
        this.accumulatedDamageThisTurn = 0.0f;
    }

    
    public void applyFinalDamageToEntity() {
        entity.setHealth(currentBattleHp);
        if (currentBattleHp <= 0.0f) {
            entity.hurt(entity.damageSources().generic(), Float.MAX_VALUE);
        }
    }
}