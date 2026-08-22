package com.cogworks.deltarunic.battle.animation;

import com.cogworks.deltarunic.battle.data.AnimationKeyframe;
import org.joml.Vector3f;
import java.util.List;


public class TimelineInterpolator {

    
    public static Vector3f getPositionAtTime(List<AnimationKeyframe> keyframes, float timeInSeconds) {
        if (keyframes == null || keyframes.isEmpty()) {
            return new Vector3f(0, 0, 0);
        }


        AnimationKeyframe before = null;
        AnimationKeyframe after = null;

        for (AnimationKeyframe kf : keyframes) {
            if (kf.timeInSeconds() <= timeInSeconds) {
                before = kf;
            } else if (after == null) {
                after = kf;
                break;
            }
        }


        if (before == null && after != null) {
            return new Vector3f(after.position());
        }


        if (before != null && after == null) {
            return new Vector3f(before.position());
        }


        if (before != null && after != null) {
            float timeDelta = after.timeInSeconds() - before.timeInSeconds();
            float progress = (timeInSeconds - before.timeInSeconds()) / timeDelta;
            progress = Math.max(0, Math.min(1, progress));

            Vector3f pos = new Vector3f(before.position());
            pos.lerp(after.position(), progress);
            return pos;
        }

        return new Vector3f(0, 0, 0);
    }

    
    public static Vector3f getSizeAtTime(List<AnimationKeyframe> keyframes, float timeInSeconds) {
        if (keyframes == null || keyframes.isEmpty()) {
            return new Vector3f(1, 1, 1);
        }

        AnimationKeyframe before = null;
        AnimationKeyframe after = null;

        for (AnimationKeyframe kf : keyframes) {
            if (kf.timeInSeconds() <= timeInSeconds) {
                before = kf;
            } else if (after == null) {
                after = kf;
                break;
            }
        }

        if (before == null && after != null) {
            return new Vector3f(after.size());
        }

        if (before != null && after == null) {
            return new Vector3f(before.size());
        }

        if (before != null && after != null) {
            float timeDelta = after.timeInSeconds() - before.timeInSeconds();
            float progress = (timeInSeconds - before.timeInSeconds()) / timeDelta;
            progress = Math.max(0, Math.min(1, progress));

            Vector3f size = new Vector3f(before.size());
            size.lerp(after.size(), progress);
            return size;
        }

        return new Vector3f(1, 1, 1);
    }

    
    public static float getRotationAtTime(List<AnimationKeyframe> keyframes, float timeInSeconds) {
        if (keyframes == null || keyframes.isEmpty()) {
            return 0.0f;
        }

        AnimationKeyframe before = null;
        AnimationKeyframe after = null;

        for (AnimationKeyframe kf : keyframes) {
            if (kf.timeInSeconds() <= timeInSeconds) {
                before = kf;
            } else if (after == null) {
                after = kf;
                break;
            }
        }

        if (before == null && after != null) {
            return after.rotation();
        }

        if (before != null && after == null) {
            return before.rotation();
        }

        if (before != null && after != null) {
            float timeDelta = after.timeInSeconds() - before.timeInSeconds();
            float progress = (timeInSeconds - before.timeInSeconds()) / timeDelta;
            progress = Math.max(0, Math.min(1, progress));

            return before.rotation() + (after.rotation() - before.rotation()) * progress;
        }

        return 0.0f;
    }

    
    public static String getSpriteAtTime(List<AnimationKeyframe> keyframes, float timeInSeconds) {
        if (keyframes == null || keyframes.isEmpty()) {
            return null;
        }

        AnimationKeyframe mostRecent = null;
        for (AnimationKeyframe kf : keyframes) {
            if (kf.timeInSeconds() <= timeInSeconds) {
                mostRecent = kf;
            } else {
                break;
            }
        }

        return mostRecent != null ? mostRecent.sprite() : null;
    }

    
    public static boolean isInCollisionWindow(float currentTime, float collisionActivateTime, float collisionDeactivateTime) {
        return currentTime >= collisionActivateTime && currentTime <= collisionDeactivateTime;
    }
}
