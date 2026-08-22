package com.cogworks.deltarunic.battle.data;

import org.joml.Vector3f;

/**
 * A single keyframe in an attack object's animation timeline.
 * Describes position, size, rotation, and sprite at a specific time.
 */
public record AnimationKeyframe(
    float timeInSeconds,
    Vector3f position,
    Vector3f size,
    float rotation,
    String sprite  // can change sprite mid-animation
) {}
