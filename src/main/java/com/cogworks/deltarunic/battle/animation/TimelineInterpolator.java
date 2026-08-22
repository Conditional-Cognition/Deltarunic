package com.cogworks.deltarunic.battle.animation;

import com.cogworks.deltarunic.battle.data.AnimationKeyframe;
import org.joml.Vector3f;

import java.util.List;

public class TimelineInterpolator {

    public static Vector3f getPositionAtTime(List<AnimationKeyframe> keyframes, float time) {
        if (keyframes == null || keyframes.isEmpty()) {
            return new Vector3f(0, 0, 0);
        }

        AnimationKeyframe[] bracketing = findBracketingKeyframes(keyframes, time);
        AnimationKeyframe before = bracketing[0];
        AnimationKeyframe after = bracketing[1];

        if (before == null) return after.position();
        if (after == null) return before.position();
        if (before == after) return before.position();

        float t = calculateProgress(before.timeInSeconds(), after.timeInSeconds(), time);
        Vector3f result = new Vector3f();
        before.position().lerp(after.position(), t, result);
        return result;
    }

    public static Vector3f getSizeAtTime(List<AnimationKeyframe> keyframes, float time) {
        if (keyframes == null || keyframes.isEmpty()) {
            return new Vector3f(1, 1, 1);
        }

        AnimationKeyframe[] bracketing = findBracketingKeyframes(keyframes, time);
        AnimationKeyframe before = bracketing[0];
        AnimationKeyframe after = bracketing[1];

        if (before == null) return after.size();
        if (after == null) return before.size();
        if (before == after) return before.size();

        float t = calculateProgress(before.timeInSeconds(), after.timeInSeconds(), time);
        Vector3f result = new Vector3f();
        before.size().lerp(after.size(), t, result);
        return result;
    }

    public static float getRotationAtTime(List<AnimationKeyframe> keyframes, float time) {
        if (keyframes == null || keyframes.isEmpty()) {
            return 0.0f;
        }

        AnimationKeyframe[] bracketing = findBracketingKeyframes(keyframes, time);
        AnimationKeyframe before = bracketing[0];
        AnimationKeyframe after = bracketing[1];

        if (before == null) return after.rotation();
        if (after == null) return before.rotation();
        if (before == after) return before.rotation();

        float t = calculateProgress(before.timeInSeconds(), after.timeInSeconds(), time);
        return before.rotation() + (after.rotation() - before.rotation()) * t;
    }

    public static String getSpriteAtTime(List<AnimationKeyframe> keyframes, float time) {
        if (keyframes == null || keyframes.isEmpty()) {
            return null;
        }

        AnimationKeyframe activeFrame = keyframes.getFirst();
        for (AnimationKeyframe frame : keyframes) {
            if (frame.timeInSeconds() <= time) {
                activeFrame = frame;
            } else {
                break;
            }
        }
        return activeFrame.sprite();
    }

    private static AnimationKeyframe[] findBracketingKeyframes(List<AnimationKeyframe> keyframes, float time) {
        AnimationKeyframe before = null;
        AnimationKeyframe after = null;

        for (AnimationKeyframe frame : keyframes) {
            if (frame.timeInSeconds() <= time) {
                before = frame;
            } else {
                after = frame;
                break;
            }
        }
        return new AnimationKeyframe[] { before, after };
    }

    private static float calculateProgress(float startTime, float endTime, float currentTime) {
        float duration = endTime - startTime;
        if (duration <= 0.0001f) {
            return 0.0f;
        }
        float rawT = (currentTime - startTime) / duration;
        return Math.clamp(rawT, 0.0f, 1.0f);
    }

    public static boolean isInCollisionWindow(float currentTime, float activateTime, float deactivateTime) {
        return currentTime >= activateTime && currentTime <= deactivateTime;
    }
}