package com.cogworks.deltarunic.battle.data;

import org.joml.Vector3f;

/**
 * Keyframe for the entity's sprite animation during an attack.
 * Allows sprite changes synchronized with the attack timeline.
 */
public record SpriteKeyframe(
    float timeInSeconds,
    String sprite
) {}
