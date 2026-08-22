package com.cogworks.deltarunic.battle.hurtbox;

import org.joml.Vector3f;

public class RotatableHurtbox {
    private Vector3f center;
    private Vector3f size;
    private float rotationYaw;
    private float rotationPitch;

    public RotatableHurtbox(Vector3f center, Vector3f size) {
        this.center = new Vector3f(center);
        this.size = new Vector3f(size);
        this.rotationYaw = 0.0f;
        this.rotationPitch = 0.0f;
    }

    public void setRotation(float yaw, float pitch) {
        this.rotationYaw = yaw;
        this.rotationPitch = pitch;
    }

    public void scale2D(float scaleX, float scaleY) {
        this.size.x *= scaleX;
        this.size.y *= scaleY;
    }

    public boolean intersects(Vector3f point) {
        Vector3f relPoint = new Vector3f(point).sub(center);
        float radYaw = (float) Math.toRadians(-rotationYaw);
        float radPitch = (float) Math.toRadians(-rotationPitch);

        float rotX = (float) (relPoint.x * Math.cos(radYaw) - relPoint.z * Math.sin(radYaw));
        float rotZ = (float) (relPoint.x * Math.sin(radYaw) + relPoint.z * Math.cos(radYaw));
        float rotY = (float) (relPoint.y * Math.cos(radPitch) - rotZ * Math.sin(radPitch));

        return Math.abs(rotX) <= size.x / 2.0f &&
               Math.abs(rotY) <= size.y / 2.0f &&
               Math.abs(rotZ) <= size.z / 2.0f;
    }
}