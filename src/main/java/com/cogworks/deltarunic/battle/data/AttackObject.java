package com.cogworks.deltarunic.battle.data;

import org.joml.Vector3f;
import java.util.List;

/**
 * Represents a single object in an attack (e.g., a fist, projectile, AoE zone).
 * Each attack can have multiple attack objects, each with its own position, damage, and animation.
 */
public record AttackObject(
    String objectId,
    Vector3f startPosition,
    Vector3f startSize,
    float damage,
    String sprite,
    List<AnimationKeyframe> animationPath,  // position, size, rotation, sprite over time
    float collisionActivateTime,  // when does it start hurting?
    float collisionDeactivateTime  // when does it stop hurting?
) {}
