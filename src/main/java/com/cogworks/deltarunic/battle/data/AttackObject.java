package com.cogworks.deltarunic.battle.data;

import org.joml.Vector3f;
import java.util.List;

public record AttackObject(
    String objectId,
    Vector3f startPosition,
    Vector3f startSize,
    float damage,
    String sprite,
    List<AnimationKeyframe> animationPath,
    float collisionActivateTime,
    float collisionDeactivateTime
) {}
