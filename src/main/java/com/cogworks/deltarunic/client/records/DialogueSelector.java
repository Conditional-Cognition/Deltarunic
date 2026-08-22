package com.cogworks.deltarunic.client.records;

import com.cogworks.deltarunic.battle.data.EntityBattleConfig;
import com.cogworks.deltarunic.battle.data.SpriteKeyframe;

/**
 * Selects dialogue from an EntityBattleConfig based on current battle state.
 * Provides the "glue" between game mechanics and data-driven dialogue.
 */
public class DialogueSelector {
    private static final String DEFAULT_DIALOGUE = "stands menacingly.";

    /**
     * Select a dialogue string for the entity based on the current game state.
     * Falls back to a default if the state dialogue doesn't exist.
     */
    public static String selectDialogue(EntityBattleConfig config, DialogueState state) {
        if (config == null || config.defaultDialogues() == null) {
            return DEFAULT_DIALOGUE;
        }

        String key = state.toString().toLowerCase();
        String dialogue = config.defaultDialogues().get(key);
        
        if (dialogue != null && !dialogue.isEmpty()) {
            return dialogue;
        }

        // Fallback to generic dialogue if state-specific doesn't exist
        return config.defaultDialogues().getOrDefault("default", DEFAULT_DIALOGUE);
    }

    /**
     * Select dialogue for a specific attack's pre-attack phase.
     */
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

    /**
     * Select dialogue for a pacification response.
     */
    public static String selectPacificationDialogue(EntityBattleConfig config, String pacificationMethod) {
        if (config == null || config.attacks() == null) {
            return DEFAULT_DIALOGUE;
        }

        // Check if any attack has a specific response for this pacification method
        return config.attacks().stream()
                .flatMap(attack -> attack.pacificationDialogue().entrySet().stream())
                .filter(entry -> entry.getKey().equals(pacificationMethod))
                .map(java.util.Map.Entry::getValue)
                .findFirst()
                .orElse(selectDialogue(config, DialogueState.PACIFIED));
    }

    /**
     * Get the sprite for the entity at a specific time during an attack.
     * Interpolates between keyframes.
     */
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
