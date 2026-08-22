package com.cogworks.deltarunic.battle.data;

import org.joml.Vector3f;

public record AnimationKeyframe(
    float timeInSeconds,
    Vector3f position,
    Vector3f size,
    float rotation,
    String sprite
) {}
