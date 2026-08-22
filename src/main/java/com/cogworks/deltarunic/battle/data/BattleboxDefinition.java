package com.cogworks.deltarunic.battle.data;

import org.joml.Vector3f;


public record BattleboxDefinition(
    String id,
    int width,
    int height,
    Vector3f positionOffset,
    float rotationYaw,
    float rotationPitch,
    Vector3f scale,
    String shapeType
) {}
