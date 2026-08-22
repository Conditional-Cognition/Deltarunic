package com.cogworks.deltarunic.battle.data;

import java.util.List;
import java.util.Map;

/**
 * Defines a single attack from an entity.
 * Contains all attack objects, sprite animations, and interaction responses.
 * Fully data-driven — all properties load from JSON.
 */
public record AttackDefinition(
    String id,
    String preAttackDialogue,
    List<AttackObject> attackObjects,  // what's actually attacking
    List<SpriteKeyframe> spriteAnimation,  // entity's sprite changes during attack
    float totalAnimationDuration,
    float playerReactionTime,  // when can the player start dodging?
    Map<String, String> pacificationDialogue,  // responses for each ACT option
    List<String> vulnerableToMethods  // which ACT options can pacify this attack
) {}
