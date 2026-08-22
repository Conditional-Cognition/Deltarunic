package com.cogworks.deltarunic.battle.data;

import org.joml.Vector3f;

/**
 * Defines the shape and size of the player's dodge zone (or entity's own hitbox in PvP).
 * Fully data-driven and separate from attacks.
 */
public record BattleboxDefinition(
    String id,
    int width,
    int height,
    Vector3f positionOffset,
    float rotationYaw,
    float rotationPitch,
    Vector3f scale,
    String shapeType  // "rectangle", "circle", "polygon", etc.
) {}
