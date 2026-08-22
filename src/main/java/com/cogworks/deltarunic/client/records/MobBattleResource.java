package com.cogworks.deltarunic.client.records;

import java.util.Map;

public record MobBattleResource(
    Map<String, String> dialogues,
    Map<String, String> dialogue_prefixes,
    Map<String, String> spriteslist,
    String hurtsprite,
    String defaultsprite
) {}