package com.cogworks.deltarunic.client.records;

import com.cogworks.deltarunic.battle.data.EntityBattleConfig;

public class DialogueSelector {
    private static final String DEFAULT_DIALOGUE = "stands menacingly.";

    public static String selectDialogue(EntityBattleConfig config, DialogueState state) {
        if (config == null || config.defaultDialogues() == null) {
            return DEFAULT_DIALOGUE;
        }

        String key = state.toString().toLowerCase();
        String dialogue = config.defaultDialogues().get(key);
        
        if (dialogue != null && !dialogue.isEmpty()) {
            return dialogue;
        }

        return config.defaultDialogues().getOrDefault("default", DEFAULT_DIALOGUE);
    }

    public static String selectPreAttackDialogue(EntityBattleConfig config, String attackId) {
        if (config == null || config.attacks() == null) {
            return DEFAULT_DIALOGUE;
        }

        return config.attacks().stream()
                .filter(attack -> attack.id().equals(attackId))
                .map(attack -> attack.preAttackDialogue())
                .findFirst()
                .orElse(selectDialogue(config, DialogueState.PRE_ATTACK));
    }

    public static String selectPacificationDialogue(EntityBattleConfig config, String pacificationMethod) {
        if (config == null || config.attacks() == null) {
            return DEFAULT_DIALOGUE;
        }

        return config.attacks().stream()
                .flatMap(attack -> attack.pacificationDialogue().entrySet().stream())
                .filter(entry -> entry.getKey().equals(pacificationMethod))
                .map(java.util.Map.Entry::getValue)
                .findFirst()
                .orElse(selectDialogue(config, DialogueState.PACIFIED));
    }

    public static String getSpriteAtTime(EntityBattleConfig config, String attackId, float timeInSeconds) {
        if (config == null || config.attacks() == null) {
            return null;
        }

        return config.attacks().stream()
                .filter(attack -> attack.id().equals(attackId))
                .map(attack -> attack.spriteAnimation())
                .flatMap(java.util.List::stream)
                .filter(keyframe -> keyframe.timeInSeconds() <= timeInSeconds)
                .max((a, b) -> Float.compare(a.timeInSeconds(), b.timeInSeconds()))
                .map(com.cogworks.deltarunic.battle.data.SpriteKeyframe::sprite)
                .orElse(null);
    }
}
