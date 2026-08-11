package org.p4j.core;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private final Vector3f position;
    private final Vector3f up;
    private final Vector3f front;
    private final Vector3f right;

    private float yaw;
    private float pitch;

    public Camera(Vector3f position) {
        this.position = new Vector3f(position);
        this.up = new Vector3f(0.0f, 1.0f, 0.0f);
        this.front = new Vector3f(0.0f, 0.0f, -1.0f);
        this.right = new Vector3f();
        this.yaw = -90.0f;
        this.pitch = 0.0f;
        update();
    }

    public Matrix4f getViewMatrix() {
        Vector3f target = new Vector3f(position).add(front);
        return new Matrix4f().lookAt(position, target, up);
    }

    public void mouse(float xOffset, float yOffset) {
        xOffset *= K.CAMERA_MOUSE_SENSITIVITY;
        yOffset *= K.CAMERA_MOUSE_SENSITIVITY;
        yaw += xOffset;
        pitch -= yOffset;
        pitch = Math.clamp(pitch, K.CAMERA_MIN_PITCH,
                K.CAMERA_MAX_PITCH);
        update();
    }

    public void moveForward(float deltaTime) {
        float velocity = K.CAMERA_MOVEMENT_SPEED * deltaTime;
        position.add(new Vector3f(front).mul(velocity));
    }

    public void moveBackward(float deltaTime) {
        float velocity = K.CAMERA_MOVEMENT_SPEED * deltaTime;
        position.sub(new Vector3f(front).mul(velocity));
    }

    public void moveLeft(float deltaTime) {
        float velocity = K.CAMERA_MOVEMENT_SPEED * deltaTime;
        position.sub(new Vector3f(right).mul(velocity));
    }

    public void moveRight(float deltaTime) {
        float velocity = K.CAMERA_MOVEMENT_SPEED * deltaTime;
        position.add(new Vector3f(right).mul(velocity));
    }

    public void moveUp(float deltaTime) {
        float velocity = K.CAMERA_MOVEMENT_SPEED * deltaTime;
        position.add(new Vector3f(up).mul(velocity));
    }

    public void moveDown(float deltaTime) {
        float velocity = K.CAMERA_MOVEMENT_SPEED * deltaTime;
        position.sub(new Vector3f(up).mul(velocity));
    }

    private void update() {
        Vector3f direction = new Vector3f();
        direction.x = (float) (Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        direction.y = (float) Math.sin(Math.toRadians(pitch));
        direction.z = (float) (Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        
        front.set(direction).normalize();
        front.cross(up, right).normalize();
    }

    public Vector3f getPosition() {
        return position;
    }
}