package org.xdg.p4j.core;

/**
 * TODO javadoc
 */

public class Body {
    public float x;
    public float y;
    public float angle;

    public float vx = 0;
    public float vy = 0;
    public float angularVelocity = 0;

    public final int maskWidth;
    public final int maskHeight;
    public final byte[] pixels;

    public boolean isSettled = false;

    public Body(float x, float y, int maskWidth, int maskHeight, byte[] pixels) {
        this.x = x;
        this.y = y;
        this.maskWidth = maskWidth;
        this.maskHeight = maskHeight;
        this.pixels = pixels;
    }

    public void update(float dt) {
        if (isSettled) return;
        vy += (float) (Constants.GRAVITY * dt * Constants.TICKS_PER_SECOND);
        x += vx * dt;
        y += vy * dt;
        angle += angularVelocity * dt;

        vx *= Constants.BODY_VELOCITY_X;
        vy *= Constants.BODY_VELOCITY_Y;
        angularVelocity *= Constants.BODY_ANGULAR_VELOCITY;
    }
}