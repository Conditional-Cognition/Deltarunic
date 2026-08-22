package com.cogworks.deltarunic.battle.data;

import java.util.List;
import java.util.Map;

/**
 * Complete battle configuration for an entity (mob or player).
 * Includes available attacks, battlebox, dialogues, and pacification options.
 * This is the master data structure — everything else is nested within.
 */
public record EntityBattleConfig(
    String entityId,
    BattleboxDefinition battlebox,  // the dodge zone
    List<AttackDefinition> attacks,
    Map<String, String> defaultDialogues,  // state-based dialogues: "greeting", "pacified", "defeated", etc.
    List<String> pacificationMethods,  // ACT options available: "Check", "Spare", "Talk", etc.
    String defaultAttack,  // fallback attack ID if none specified
    boolean canBePacified
) {}
