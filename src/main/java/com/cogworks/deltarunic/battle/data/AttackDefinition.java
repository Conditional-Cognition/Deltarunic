package com.cogworks.deltarunic.battle.data;

import java.util.List;
import java.util.Map;

public record AttackDefinition(
    String id,
    String preAttackDialogue,
    List<AttackObject> attackObjects,
    List<SpriteKeyframe> spriteAnimation,
    float totalAnimationDuration,
    float playerReactionTime,
    Map<String, String> pacificationDialogue,
    List<String> vulnerableToMethods
) {}
