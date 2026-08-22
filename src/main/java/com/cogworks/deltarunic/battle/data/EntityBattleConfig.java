package com.cogworks.deltarunic.battle.data;

import java.util.List;
import java.util.Map;


public record EntityBattleConfig(
    String entityId,
    BattleboxDefinition battlebox,
    List<AttackDefinition> attacks,
    Map<String, String> defaultDialogues,
    List<String> pacificationMethods,
    String defaultAttack,
    boolean canBePacified
) {}
